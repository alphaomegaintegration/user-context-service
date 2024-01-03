package com.alpha.omega.user.service;

import com.alpha.omega.security.SecurityUtils;
import com.alpha.omega.user.exception.ContextNotFoundException;
import com.alpha.omega.user.exception.UserNotFoundException;
import com.alpha.omega.user.model.*;
import com.alpha.omega.user.repository.*;
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
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SynchronousSink;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuple3;
import reactor.util.function.Tuples;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.alpha.omega.user.service.RedisContextService.*;
import static com.alpha.omega.user.service.ServiceUtils.*;
import static com.alpha.omega.user.utils.Constants.*;
import static com.alpha.omega.user.validator.UserContextValidator.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RedisUserContextService implements UserContextService {
    private static final Logger logger = LoggerFactory.getLogger(RedisUserContextService.class);


    final static int TEN_MINUTES_MILLIS = 1000 * 60 * 10;
    public static final String CONTEXT_DOES_NOT_EXIST = "Context does not exist ";
    public static final String COULD_NOT_CREATE_USERCONTEXT = "Could not create usercontext";
    public static final String USER_NOT_FOUND = "USER NOT FOUND";
    final static Supplier<UserNotFoundException> USER_NOT_FOUND_EXCEPTION_SUPPLIER = () -> new UserNotFoundException(USER_NOT_FOUND);

    ContextService contextService;
    ReactiveRedisTemplate<String, UserContextEntity> userContextTemplate;
    UserContextRepository userContextRepository;
    @Builder.Default
    UserContextValidator userContextValidator = new UserContextValidator();
    ContextRepository contextRepository;
    @Builder.Default
    Scheduler publisherSchedule = Schedulers.boundedElastic();

    public RedisUserContextService(ContextService contextService, ReactiveRedisTemplate<String, UserContextEntity> userContextTemplate) {
        this.contextService = contextService;
        this.userContextTemplate = userContextTemplate;
    }


    final static Function<UserContextEntity, UserContext> convertUserContextEntityToUserContext() {
        return uce -> {
            UserContext userContext = new UserContext();
            BeanUtils.copyProperties(uce, userContext);
            return userContext;
        };
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
        logger.debug("In validateUserContext()");
        return (userContext, sink) -> {
            List<ServiceError> errors = userContextValidator.validate(userContext);
            if (errors.isEmpty()) {
                sink.next(userContext);
            } else {
                String message = ServiceUtils.formatValidationErrors(errors, "usercontext");
                sink.error(new IllegalArgumentException(message));
            }
        };
    }

    // Tuple2<UserContextEntity, ContextEntity>

    BiConsumer<UserContextEntity, SynchronousSink<UserContextEntity>> whenUserContextEntityInPipelineIsNull() {
        return (userContext, sink) -> {
            if (userContext == null) {
                sink.error(new ContextNotFoundException("CONTEXT NOT FOUND"));
            } else {
                sink.next(userContext);
            }

        };
    }

    BiConsumer<Tuple2<UserContextEntity, ContextEntity>, SynchronousSink<Tuple2<UserContextEntity, ContextEntity>>> whenUserContextEntityTupleisEmpty() {
        return (tuple, sink) -> {
            if (StringUtils.isBlank(tuple.getT1().getContextId())) {
                sink.error(new IllegalArgumentException("Permissions not found "));
            } else {
                sink.next(tuple);
            }

        };
    }

    BiConsumer<Tuple2<UserContextEntity, List<Role>>, SynchronousSink<Tuple2<UserContextEntity, List<Role>>>> whenUserContextEntityRolesEmpty() {
        return (tuple, sink) -> {
            if (StringUtils.isBlank(tuple.getT1().getContextId())) {
                sink.error(new IllegalArgumentException("Roles not found "));
            } else {
                sink.next(tuple);
            }

        };
    }

    Mono<UserContext> checkContextAndRoleExists(UserContext userContext) {

        logger.debug("checkContextAndRoleExists Got UserContext => {}", userContext);

        return Mono.just(userContext)
                .publishOn(publisherSchedule)
                .flatMap(uc -> Mono.zip(Mono.just(uc),
                        Mono.just(Optional.ofNullable(contextRepository.findByContextId(uc.getContextId())).orElse(new ContextEntity())),
                        (t1, t2) -> Tuples.of(t1, t2)))
                .handle((tpl, sink) -> {
                    List<ServiceError> ServiceErrors = new ArrayList<>();
                    ContextEntity ctx = tpl.getT2();
                    UserContext uc = tpl.getT1();

                    if (StringUtils.isBlank(ctx.getContextId())) {
                        ServiceErrors.add(ServiceError.builder()
                                .property(Constants.CONTEXT_ID)
                                .message(CONTEXT_DOES_NOT_EXIST + uc.getContextId())
                                .build());
                    }

                    logger.debug("checkContextAndRoleExists Got UserContext => {}", uc);
                    logger.debug("checkContextAndRoleExists Got ContextEntity => {}", ctx);

                    boolean contextExists = uc.getContextId().equals(ctx.getContextId());
                    if (!contextExists) {
                        ServiceErrors.add(ServiceError.builder()
                                .property(Constants.CONTEXT_ID)
                                .message(CONTEXT_DOES_NOT_EXIST + uc.getContextId())
                                .build());

                    }

                    if (contextExists) {
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
                .map(tpl -> userContext);
    }

    @Override
    public Mono<UserContext> createUserContext(Mono<UserContext> userContext, String modifiedBy, String tranasactionId, Date createdDate) {

        return Mono.from(userContext)
                .publishOn(publisherSchedule)
                .handle(validateUserContext())
                .flatMap(uc -> checkContextAndRoleExists(uc))
                .flatMap(uc -> Mono.just(userContextRepository.findByUserIdAndContextIdAndRoleId(uc.getUserId(),
                                uc.getContextId(), uc.getRoleId()).orElse(new UserContextEntity()))
                        .map(uce -> Tuples.of(uc, uce)))
                .map(tuple -> {
                    UserContext uc = tuple.getT1();
                    if (StringUtils.isNotBlank(tuple.getT2().getId())) {
                        uc.setId(tuple.getT2().getId());
                    }
                    return uc;
                })
                .map(userContextToUserContextEntity(modifiedBy, tranasactionId, createdDate))
                .doOnNext(uce -> logger.debug("Created userContextEntity => {}", uce))
                .map(uce -> Tuples.of(uce, userContextRepository.save(uce)))
                .map(tuple -> userContextEntityToUserContext.apply(tuple.getT2()));
    }


    @Override
    public Mono<UserContext> createUserContext(UserContext context) {
        return Mono.just(context)
                .publishOn(Schedulers.parallel())
                .handle(validateUserContext())
                .map(convertUserContextToContextEntity())
                //.flatMap(ctx -> userContextTemplate.opsForValue().set(calculateUserContextKey(ctx), ctx))
                //.map(bool -> bool ? context : new UserContext());
                .map(uce -> {
                    uce.setEnabled(Boolean.TRUE);
                    return userContextRepository.save(uce);
                })
                .map(userContextEntityToUserContext);

    }


    @Override
    public Mono<UserContext> updateUserContext(UserContext context) {
        return Mono.just(context)
                .publishOn(publisherSchedule)
                .handle(validateUserContext())
                .map(convertUserContextToContextEntity())
                .filterWhen(uce -> contextService.roleExistsInContext(uce.getRoleId(), uce.getContextId()))
                .handle(whenUserContextEntityInPipelineIsNull())
                .map(uce -> Tuples.of(uce, userContextRepository.findByUserIdAndContextId(uce.getUserId(), uce.getContextId()).orElseThrow(USER_NOT_FOUND_EXCEPTION_SUPPLIER)))
                .map(tpl -> {
                    UserContextEntity uceInput = tpl.getT1();
                    UserContextEntity uceFound = tpl.getT2();
                    uceInput.setId(uceFound.getId());
                    return userContextRepository.save(uceInput);
                })
                .map(convertUserContextEntityToUserContext());
    }

    @Override
    public Flux<UserContext> findByContextId(String contextId) {
        return Flux.just(contextId)
                .publishOn(publisherSchedule)
                .flatMapIterable(id -> userContextRepository.findByContextId(id))
                .map(userContextEntityToUserContext);
    }

    @Override
    public Mono<Void> deleteUserContextByContextId(String contextId) {
        return Mono.just(contextId)
                .publishOn(publisherSchedule)
                .map(id -> userContextRepository.deleteByContextId(id))
                .doOnNext(val -> logger.debug("Deleted {} records ", val))
                .then();

    }


    @Override
    public Mono<UserContextPage> getAllUserContextEntities(PageRequest pageRequest) {
        return Mono.just(pageRequest)
                .publishOn(publisherSchedule)
                .doOnNext(pageRequest1 -> logger.debug("getAllUserContextEntities Got page request => {}", pageRequest1))
                .flatMap(request -> Mono.zip(Mono.just(userContextRepository.count()),
                        Flux.fromIterable(userContextRepository.findAll())
                                .skip(calculateSkip.apply(pageRequest))
                                .take(request.getPageSize()).collectList(),
                        (t1, t2) -> Tuples.of(t1, t2)))

                .elapsed()
                .map(tuplePage -> {
                    Tuple2<Long, List<UserContextEntity>> tupleList = tuplePage.getT2();
                    List<UserContextEntity> list = tupleList.getT2();
                    UserContextPage contextPage = new UserContextPage();
                    contextPage.setPage(pageRequest.getPageNumber());
                    contextPage.setTotal(tupleList.getT1().intValue());
                    contextPage.setPageSize(list.size());
                    contextPage.setContent(list.stream().map(convertUserContextEntityToUserContext()).collect(Collectors.toList()));
                    contextPage.setElapsed(tuplePage.getT1().toString());
                    return contextPage;
                });
    }

    @Override
    public Flux<UserContext> getUserContextByContextId(String contextId) {
        return Flux.just(contextId)
                .publishOn(Schedulers.parallel())
                .flatMap(ctxId -> userContextTemplate.opsForValue().get(calculateUserContextKey(STAR, ctxId, STAR)))
                .map(userContextEntityToUserContext);
    }

    @Override
    public Mono<UserContextPage> createUserContextBatch(UserContextBatchRequest batchRequest, String auditUser, String transactionId) {
        return Flux.fromIterable(batchRequest.getUsers())
                .flatMap(uc -> this.createUserContext(uc))
                .collectList()
                .elapsed()
                .map(tuplePage -> {
                    List<UserContext> list = tuplePage.getT2();
                    UserContextPage contextPage = new UserContextPage();
                    contextPage.setPage(1);
                    contextPage.setTotal(list.size());
                    contextPage.setPageSize(list.size());
                    contextPage.setContent(list);
                    contextPage.setElapsed(tuplePage.getT1().toString());
                    return contextPage;
                });
    }


    @Override
    public Mono<UserContext> addRoleToUserContext(String userId, String contextId, String roleId, String auditUser) {

        final Tuple3<String, String, String> params = Tuples.of(userId, contextId, roleId);

        return Mono.just(params)
                .publishOn(publisherSchedule)
                .map(tp3 -> userContextRepository.findByUserIdAndContextId(tp3.getT1(), tp3.getT2()).orElseThrow(() -> new UserNotFoundException("User Not Found")))
                .flatMap(uce -> Mono.zip(Mono.just(uce), contextService.getRolesByContextIdAndRoleIdIn(uce.getContextId(), Collections.singletonList(uce.getRoleId()), Boolean.FALSE).collectList(),
                        (t1, t2) -> Tuples.of(t1, t2)))
                .switchIfEmpty(USER_CONTEXT_ENTITY_LIST_TUPLE_2)
                .handle(whenUserContextEntityRolesEmpty())
                .map(tp -> {
                    UserContextEntity uce = tp.getT1();
                    List<String> roleIds = tp.getT2().stream()
                            .filter(role -> !role.getRoleId().equals(uce.getRoleId()))
                            .map(Role::getRoleId)
                            .collect(Collectors.toList());
                    uce.getAdditionalRoles().addAll(roleIds);
                    return userContextRepository.save(uce);
                })
                .map(convertUserContextEntityToUserContext());
    }

    final static Mono<Tuple2<UserContextEntity, List<Role>>> USER_CONTEXT_ENTITY_LIST_TUPLE_2 = Mono.just(Tuples.of(new UserContextEntity(), Collections.emptyList()));

    final static Mono<Tuple2<UserContextEntity, ContextEntity>> USER_CONTEXT_ENTITY_CONTEXT_ENTITY_TUPLE_2 = Mono.just(Tuples.of(new UserContextEntity(), new ContextEntity()));

    @Override
    public Mono<UserContext> addPermissionsToUserContext(String userId, String contextId, String permissionsStr, String auditUser) {

        List<String> permissions = StringUtils.isNotBlank(permissionsStr) ? Arrays.asList(permissionsStr.split(COMMA))
                : Collections.emptyList();

        final Tuple3<String, String, List<String>> params = Tuples.of(userId, contextId, permissions);
        return Mono.just(params)
                .publishOn(publisherSchedule)
                .map(tp3 -> userContextRepository.findByUserIdAndContextId(tp3.getT1(), tp3.getT2()).orElseThrow(() -> new UserNotFoundException("User Not Found")))
                .flatMap(uce -> Mono.zip(Mono.just(uce), Mono.just(contextRepository.findByContextId(uce.getContextId())),
                        (t1, t2) -> Tuples.of(t1, t2)))
                .filter(tp -> additionalPermissionsInContextPermissions(convertUserContextEntityToUserContext().apply(tp.getT1()),
                        tp.getT2()))
                .switchIfEmpty(USER_CONTEXT_ENTITY_CONTEXT_ENTITY_TUPLE_2)
                .handle(whenUserContextEntityTupleisEmpty())
                .map(tp -> {
                    UserContextEntity uce = tp.getT1();
                    uce.getAdditionalPermissions().addAll(params.getT3());
                    return userContextRepository.save(uce);
                })
                .map(convertUserContextEntityToUserContext());
    }

    @Override
    public Mono<Void> deleteUserContextByUserContextId(String userContextId) {
        return Mono.just(userContextId)
                .publishOn(publisherSchedule)
                .map(uctxId -> userContextRepository.findById(uctxId).orElseThrow(() -> new UserNotFoundException("User Not Found for id " + userContextId)))
                .map(uce -> {
                    uce.setEnabled(Boolean.FALSE);
                    userContextRepository.save(uce);
                    return uce;
                }).then();
    }

    @Override
    public Mono<UserContextPage> getAllUserContexts(PageRequest pageRequest) {
        return Mono.just(pageRequest)
                .publishOn(publisherSchedule)
                .doOnNext(pageRequest1 -> logger.debug("getAllUserContextEntities Got page request => {}", pageRequest1))
                .flatMap(request -> Mono.zip(Mono.just(userContextRepository.count()),
                        Flux.fromIterable(userContextRepository.findAll())
                                .skip(calculateSkip.apply(pageRequest))
                                .take(request.getPageSize()).collectList(),
                        (t1, t2) -> Tuples.of(t1, t2)))

                .elapsed()
                .map(tuplePage -> {
                    Tuple2<Long, List<UserContextEntity>> tupleList = tuplePage.getT2();
                    List<UserContextEntity> list = tupleList.getT2();
                    UserContextPage contextPage = new UserContextPage();
                    contextPage.setPage(pageRequest.getPageNumber());
                    contextPage.setTotal(tupleList.getT1().intValue());
                    contextPage.setPageSize(list.size());
                    contextPage.setContent(list.stream().map(convertUserContextEntityToUserContext()).collect(Collectors.toList()));
                    contextPage.setElapsed(tuplePage.getT1().toString());
                    return contextPage;
                });
    }

    @Override
    public Mono<UserContextPage> getUserContextByContextId(PageRequest pageRequest, String contextId) {
        return Mono.just(pageRequest)
                .publishOn(publisherSchedule)
                .doOnNext(pageRequest1 -> logger.debug("getUserContextByUserId Got page request => {}", pageRequest1))
                .flatMap(request -> Mono.zip(Mono.just(userContextRepository.findCountByContextId(contextId)),
                        Flux.fromIterable(userContextRepository.findByContextId(contextId))
                                .skip(calculateSkip.apply(pageRequest))
                                .take(request.getPageSize()).collectList(),
                        (t1, t2) -> Tuples.of(t1, t2)))

                .elapsed()
                .map(tuplePage -> {
                    Tuple2<Long, List<UserContextEntity>> tupleList = tuplePage.getT2();
                    List<UserContextEntity> list = tupleList.getT2();
                    UserContextPage contextPage = new UserContextPage();
                    contextPage.setPage(pageRequest.getPageNumber());
                    contextPage.setTotal(tupleList.getT1().intValue());
                    contextPage.setPageSize(list.size());
                    contextPage.setContent(list.stream().map(convertUserContextEntityToUserContext()).collect(Collectors.toList()));
                    contextPage.setElapsed(tuplePage.getT1().toString());
                    return contextPage;
                });
    }

    @Override
    public Mono<UserContextPage> getUserContextByUserId(PageRequest pageRequest, String userId) {
        return Mono.just(pageRequest)
                .publishOn(publisherSchedule)
                .doOnNext(pageRequest1 -> logger.debug("getUserContextByUserId Got page request => {}", pageRequest1))
                .flatMap(request -> Mono.zip(Mono.just(userContextRepository.findCountByUserId(userId)),
                        Flux.fromIterable(userContextRepository.findByUserId(userId))
                                .skip(calculateSkip.apply(pageRequest))
                                .take(request.getPageSize()).collectList(),
                        (t1, t2) -> Tuples.of(t1, t2)))

                .elapsed()
                .map(tuplePage -> {
                    Tuple2<Long, List<UserContextEntity>> tupleList = tuplePage.getT2();
                    List<UserContextEntity> list = tupleList.getT2();
                    UserContextPage contextPage = new UserContextPage();
                    contextPage.setPage(pageRequest.getPageNumber());
                    contextPage.setTotal(tupleList.getT1().intValue());
                    contextPage.setPageSize(list.size());
                    contextPage.setContent(list.stream().map(convertUserContextEntityToUserContext()).collect(Collectors.toList()));
                    contextPage.setElapsed(tuplePage.getT1().toString());
                    return contextPage;
                });
    }

    @Override
    public Mono<UserContext> getUserContextByUserContextId(String userContextId) {
        return Mono.just(userContextId)
                .map(uctxId -> userContextRepository.findById(uctxId).orElseThrow(() -> new UserNotFoundException("User Not Found for id " + userContextId)))
                .map(convertUserContextEntityToUserContext());
    }

    @Override
    public Mono<UserContextPermissions> getUserContextByUserIdAndContextId(final UserContextRequest userContextRequest) {

        final List<String> roleIds = StringUtils.isBlank(userContextRequest.getRoles()) ? Collections.emptyList()
                : Arrays.asList(userContextRequest.getRoles().split(COMMA));

        /*
        Optional<UserContextEntity> userContextEntity = userContextRepository.findByUserIdAndContextId(userContextRequest.getUserId(), userContextRequest.getContextId());
        if (userContextEntity.isPresent()){
            logger.debug("Got UserContextEntity {}",userContextEntity.get());
        } else {
            logger.debug("UserContextEntity from UserContextRequest {} is not available",userContextRequest);
        }

         */

        logger.debug("userContextRepository is null {}", userContextRepository == null);

        return Mono.just(userContextRequest)
                .publishOn(Schedulers.boundedElastic())
                .doOnNext(request -> logger.debug("getUserContextByUserIdAndContextId request => {}", request))
                .map(request -> userContextRepository.findByUserIdAndContextId(request.getUserId(), request.getContextId()).orElseThrow(() -> new UserNotFoundException("User Not Found", request)))
                .doOnNext(uce -> logger.debug("Got uce => {}", uce))
                .flatMap(uce -> contextService.getRolesByContextIdAndRoleIdIn(uce.getContextId(), roleIds, roleIds.isEmpty()).collectList()
                        .map(roles -> Tuples.of(roles, uce)))
                .elapsed()
                .map(tuple -> {
                    Tuple2<List<Role>, UserContextEntity> tuple2 = tuple.getT2();
                    List<Role> roles = tuple2.getT1();
                    UserContextPermissions userContextPermissions = new UserContextPermissions();
                    userContextPermissions.setContextId(tuple2.getT2().getContextId());
                    userContextPermissions.setRoleId(tuple2.getT2().getRoleId());
                    userContextPermissions.setUserId(tuple2.getT2().getUserId());
                    userContextPermissions.setEnabled(tuple2.getT2().isEnabled());
                    List<String> permissions = roles.stream()
                            .flatMap(role -> role.getPermissions().stream())
                            .collect(Collectors.toList());
                    userContextPermissions.setPermissions(permissions);
                    return userContextPermissions;
                })

                .doOnNext(ucp -> logger.debug("got ucp => {}", ucp.toString()));
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class RedisReactiveUserDetailsService implements ReactiveUserDetailsService{

        String contextId;
        UserContextService userContextService;
        Scheduler scheduler = Schedulers.boundedElastic();

        @Override
        public Mono<UserDetails> findByUsername(String username) {
            final UserContextRequest userContextRequest = UserContextRequest.builder()
                    .contextId(contextId)
                    .userId(username)
                    .build();

            return Mono.just(userContextRequest)
                    .publishOn(scheduler)
                    .flatMap(request -> userContextService.getUserContextByUserIdAndContextId(request))
                    .map(SecurityUtils.convertUserContextPermissionsToUserDetails());
        }
    }
}
