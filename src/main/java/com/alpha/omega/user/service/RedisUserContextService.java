package com.alpha.omega.user.service;

import com.alpha.omega.user.model.*;
import com.alpha.omega.user.repository.ContextEntity;
import com.alpha.omega.user.repository.ContextRepository;
import com.alpha.omega.user.repository.UserContextEntity;
import com.alpha.omega.user.repository.UserContextRepository;
import com.alpha.omega.user.utils.Constants;
import com.alpha.omega.user.validator.ServiceError;
import com.alpha.omega.user.validator.UserContextValidator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SynchronousSink;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.alpha.omega.user.service.RedisContextService.*;
import static com.alpha.omega.user.validator.UserContextValidator.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RedisUserContextService implements UserContextService{
    private static final Logger logger = LoggerFactory.getLogger(RedisUserContextService.class);


    final static int TEN_MINUTES_MILLIS = 1000 * 60 * 10;
    public static final String CONTEXT_DOES_NOT_EXIST = "Context does not exist ";
    public static final String COULD_NOT_CREATE_USERCONTEXT = "Could not create usercontext";

    ContextService contextService;
    ReactiveRedisTemplate<String, UserContextEntity> userContextTemplate;
    UserContextRepository userContextRepository;
    UserContextValidator userContextValidator;
    ContextRepository contextRepository;

    public RedisUserContextService(ContextService contextService, ReactiveRedisTemplate<String, UserContextEntity> userContextTemplate) {
        this.contextService = contextService;
        this.userContextTemplate = userContextTemplate;
    }

    final static String USER_CONTEXT_KEY_PREFIX = "usercontext:";

    String calculateKey(String contextId){
        return new StringBuilder(USER_CONTEXT_KEY_PREFIX).append(contextId).toString();
    }


    final static Function<UserContext, UserContextEntity> convertUserContextToContextEntity() {
        return (userContext) -> {
            Date now = new Date();
            logger.trace("Got context in convertUserContextToContextEntity => {}", userContext);
            UserContextEntity userContextEntity = new UserContextEntity();
            BeanUtils.copyProperties(userContext, userContextEntity, new String[]{"enabled"});
            //contextEntity.setCreatedBy(inContextDto.getCreatedBy());
            //contextEntity.setCreatedDate(Date.from(inContextDto.getCreatedDate().toInstant()));
            userContextEntity.setCreatedDate(now);
            //contextEntity.setLastModifiedBy(inContextDto.getLastModifiedBy());
            userContextEntity.setLastModifiedByDate(now);
            return userContextEntity;
        };
    }

    BiConsumer<UserContext, SynchronousSink<UserContext>> validateUserContext() {
        return (userContext, sink) -> {
            List<ServiceError> errors = userContextValidator.validate(userContext);
            if (errors.isEmpty()) {
                sink.next(userContext);
            } else {
                String message = ServiceUtils.formatValidationErrors(errors, "context");
                sink.error(new IllegalArgumentException(message));
            }
        };
    }

    Mono<UserContext> checkContextAndRoleExists(UserContext uc) {
        return Mono.just(contextRepository.findByContextId(uc.getContextId()))
                .switchIfEmpty(Mono.just(new ContextEntity()))
                .handle((ctx, sink) -> {
                    List<ServiceError> ServiceErrors = new ArrayList<>();

                    logger.info("checkContextAndRoleExists Got UserContext => {}",uc);
                    logger.info("checkContextAndRoleExists Got ContextEntity => {}",ctx);

                    boolean contextExists = uc.getContextId().equals(ctx.getContextId());
                    if (!contextExists) {
                        ServiceErrors.add(ServiceError.builder()
                                .property(Constants.CONTEXT_ID)
                                .message(CONTEXT_DOES_NOT_EXIST + uc.getContextId())
                                .build());

                    }

                    if (contextExists){
                        if (!ucRoleInContextRoles(uc, ctx)) {
                            ServiceErrors.add(ServiceError.builder()
                                    .property("roleId")
                                    .message("Role does not exist " + uc.getRoleId() + " in context " + uc.getContextId())
                                    .build());
                        }

                        if (!additionalPermissionsInContextPermissions(uc, ctx)) {
                            ServiceErrors.add(ServiceError.builder()
                                    .property("additionalPermissions")
                                    .message("UserContext Permissions " + uc.getAdditionalPermissions() + " are not in context " + uc.getContextId())
                                    .build());
                        }

                        if (!additionalRolesInContextRoles(uc, ctx)) {
                            ServiceErrors.add(ServiceError.builder()
                                    .property("additionalRoles")
                                    .message("UserContext Roles " + uc.getAdditionalRoles() + " are not in context " + uc.getContextId())
                                    .build());
                        }
                    }

                    if (!ServiceErrors.isEmpty()) {
                        String message = ServiceUtils.formatValidationErrors(ServiceErrors, "usercontext");
                        sink.error(new IllegalArgumentException(message));
                    } else {
                        sink.next(ctx);
                    }

                })
                .map(ctx -> uc);
    }

    @Override
    public Mono<UserContext> createUserContext(Mono<UserContext> userContext, String modifiedBy, String tranasactionId, Date createdDate) {

        return Mono.from(userContext)
                .handle(validateUserContext())
                .flatMap(uc -> checkContextAndRoleExists(uc))
                .flatMap(uc -> Mono.just(userContextRepository.findByUserIdAndContextIdAndRoleId(uc.getUserId(),
                                uc.getContextId(), uc.getRoleId()))
                        .switchIfEmpty(Mono.defer(() -> Mono.just(new UserContextEntity())))
                        .map(uce -> Tuples.of(uc, uce)))
                .map(tuple -> {
                    UserContext uc = tuple.getT1();
                    if (StringUtils.isNotBlank(tuple.getT2().getId())){
                        uc.setId(tuple.getT2().getId());
                    }
                    return uc;
                })
                .map(userContextToUserContextEntity(modifiedBy, tranasactionId, createdDate))
                .doOnNext(userContextEntity -> logger.debug("Creating userContextEntity => {}", userContextEntity))
                .map(userContextEntity -> userContextRepository.save(userContextEntity))
                .map(userContextEntityToUserContext);


    }

    @Override
    public Mono<UserContext> createUserContext(UserContext context) {
        return Mono.just(context)
                .publishOn(Schedulers.parallel())
                .map(convertUserContextToContextEntity())
                .flatMap(ctx -> userContextTemplate.opsForValue().set(calculateKey(ctx.getContextId()), ctx))
                .map(bool -> bool ? context : new UserContext());
    }

    @Override
    public Mono<UserContext> updateUserContext(UserContext context) {
        return Mono.just(context)
                .publishOn(Schedulers.parallel())
                .map(convertUserContextToContextEntity())
                .flatMap(ctx -> userContextTemplate.opsForValue().set(calculateKey(ctx.getContextId()), ctx))
                .map(bool -> bool ? context : new UserContext());
    }

    @Override
    public Flux<UserContext> findByContextId(String contextId) {
        return Flux.just(contextId)
                .flatMapIterable(id -> userContextRepository.findByContextId(id))
                .map(userContextEntityToUserContext);
    }

    @Override
    public Mono<Void> deleteUserContextByContextId(String contextId) {
        return Mono.just(contextId)
                .map(id -> userContextRepository.deleteByContextId(id))
                .doOnNext(val -> logger.info("Deleted {} records ",val))
                .then();

    }


    @Override
    public Mono<UserContextPage> getAllUserContextEntities(PageRequest pageRequest) {
        return Mono.just(pageRequest)
                .publishOn(Schedulers.parallel())
                .doOnNext(pageRequest1 -> logger.info("Got page request => {}", pageRequest1))
                //.map(request -> contextRepository.findAll(example,request))
                .flatMap(request -> Mono.zip(userContextTemplate.keys(USER_CONTEXT_KEY_PREFIX + "*").count(),
                        userContextTemplate.keys(USER_CONTEXT_KEY_PREFIX + "*")
                                .flatMap(userContextTemplate.opsForValue()::get)
                                .skip(calculateSkip.apply(pageRequest))
                                .take(request.getPageSize()).collectList(), (t1, t2) -> Tuples.of(t1, t2)))
                .elapsed()
                .map(tuplePage -> {
                    //Page<ContextEntity> page = tuplePage.getT2();
                    Tuple2<Long, List<UserContextEntity>> tupleList = tuplePage.getT2();
                    List<UserContextEntity> list = tupleList.getT2();
                    UserContextPage contextPage = new UserContextPage();
                    contextPage.setPage(pageRequest.getPageNumber());
                    contextPage.setTotal(tupleList.getT1().intValue());
                    contextPage.setPageSize(list.size());
                    contextPage.setContent(list.stream().map(userContextEntityToUserContext).collect(Collectors.toList()));
                    contextPage.setElapsed(tuplePage.getT1().toString());
                    return contextPage;
                });
    }

    @Override
    public Flux<UserContext> getUserContextByContextId(String contextId) {
        return Flux.just(contextId)
                .publishOn(Schedulers.parallel())
                .flatMap(ctxId -> userContextTemplate.opsForValue().get(USER_CONTEXT_KEY_PREFIX))
                .map(userContextEntityToUserContext);
    }

    @Override
    public Mono<UserContext> updateUserContextByContextId(String contextId, Mono<UserContext> context) {

        return null;
    }

    @Override
    public Mono<UserContextPage> createUserContextBatch(UserContextBatchRequest batchRequest, String auditUser, String transactionId) {
        return null;
    }

    @Override
    public Mono<UserContext> addRoleToUserContext(String userId, String contextId, String roleIds, String auditUser) {
        return null;
    }

    @Override
    public Mono<UserContext> updateUserContextByUserContextId(Mono<UserContext> userContext, String auditUser, String transactionId) {
        return null;
    }

    @Override
    public Mono<Void> deleteUserContextByUserContextId(String userContextId) {
        return null;
    }

    @Override
    public Mono<UserContextPage> getAllUserContexts(PageRequest pageRequest) {
        return null;
    }

    @Override
    public Mono<UserContextPage> getUserContextByContextId(PageRequest pageRequest, String contextId) {
        return null;
    }

    @Override
    public Mono<UserContextPage> getUserContextByUserId(PageRequest pageRequest, String userId) {
        return null;
    }

    @Override
    public Mono<UserContext> getUserContextByUserContextId(String userContextId) {
        return null;
    }

    @Override
    public Mono<UserContextPermissions> getUserContextByUserIdAndContextId(UserContextRequest userContextRequest) {
        return null;
    }
}
