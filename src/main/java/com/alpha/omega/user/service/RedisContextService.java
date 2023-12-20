package com.alpha.omega.user.service;

import com.alpha.omega.user.model.*;
import com.alpha.omega.user.repository.*;
import com.alpha.omega.user.validator.ServiceError;
import com.alpha.omega.user.validator.UserContextValidator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SynchronousSink;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.io.NotActiveException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.alpha.omega.user.service.ServiceUtils.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RedisContextService implements ContextService {

    private static final Logger logger = LoggerFactory.getLogger(RedisContextService.class);


    public static final String CONTEXT_ID_MUST_THE_SAME_AS_IN_PAYLOAD = "ContextId must the same as in payload.";


    /*
    final static Function<ContextEntity, ContextDto> convertContextEntityToDto = (contextEntity) -> {
        logger.info("Got context in convertContextToDto => {}", contextEntity);
        ContextDto contextDto = new ContextDto();
        BeanUtils.copyProperties(contextEntity, contextDto);
        //contextDto.setPermissions(new ArrayList<>(contextEntity.getPermissions()));
        //contextDto.setRoles(contextEntity.getRoles());
        return contextDto;
    };

     */

    UserContextValidator userContextValidator;

    final static Function<Role, RoleDto> roleToDto = (role) -> {

        RoleDto roleDto = new RoleDto();
        BeanUtils.copyProperties(role, roleDto);
        return roleDto;
    };

    final static Function<RoleDto, Role> roleDtoToRole = (roleDto) -> {

        Role role = new Role();
        role.setRoleId(roleDto.getRoleId());
        role.setRoleName(roleDto.getRoleName());
        role.setPermissions(new HashSet<>(roleDto.getPermissions()));
        return role;
    };

    final static Function<Role, RoleDto> roleToRoleDto = (role) -> {

        RoleDto roleDto = new RoleDto();
        roleDto.setRoleId(role.getRoleId());
        roleDto.setRoleName(role.getRoleName());
        roleDto.setPermissions(new ArrayList<>(role.getPermissions()));
        return roleDto;
    };

    final static Function<ContextEntity, Context> convertContextEntityToContext = (contextEntity) -> {
        logger.info("Got contextEntity in convertContextEntityToContext => {}", contextEntity);

        if (contextEntity != null && !StringUtils.isBlank(contextEntity.getContextId())) {
            Date now = new Date();
            String createdOffsetDate = null;
            String modifiedOffsetDate = null;
            try {
                if (contextEntity.getCreatedDate() != null) {
                    createdOffsetDate = SERVICE_DATETIME_FORMATTER.format(contextEntity.getCreatedDate().toInstant());
                }

            } catch (Exception e) {
                logger.warn("Could not parse getCreatedDate {} for context {}" + contextEntity.getCreatedDate(),
                        contextEntity.getContextId(), e);
            }

            try {
                if (contextEntity.getLastModifiedByDate() != null) {
                    modifiedOffsetDate = SERVICE_DATETIME_FORMATTER.format(contextEntity.getLastModifiedByDate().toInstant());
                }

            } catch (Exception e) {
                logger.warn("Could not parse getLastModifiedByDate {} for context {}" + contextEntity.getLastModifiedByDate(),
                        contextEntity.getContextId(), e);
            }

            Context context = new Context();
            context.setContextId(contextEntity.getContextId());
            context.setContextName(contextEntity.getContextName());
            context.setDescription(contextEntity.getDescription());
            context.setCreatedBy(contextEntity.getCreatedBy());
            context.setModifiedBy(contextEntity.getLastModifiedBy());
            context.setId(contextEntity.getId());
            context.setEnabled(contextEntity.isEnabled());
            context.setTransactionId(contextEntity.getTransactionId());
            context.setCreatedTime(createdOffsetDate);
            context.setModifiedTime(modifiedOffsetDate);
            context.setPermissions(new HashSet<>(contextEntity.getPermissions()));
            context.setRoles(contextEntity.getRoles().stream().map(roleEntityToRole).collect(Collectors.toList()));
            return context;
        } else {
            return new Context();
        }

    };

    /*
    final static Function<ContextDto, Context> convertContextDTOToContext = (contextEntity) -> {
        logger.info("Got context in convertContextToDto => {}", contextEntity);
        Context context = new Context();
        context.setContextId(contextEntity.getContextId());
        context.setContextName(contextEntity.getContextName());
        context.setDescription(contextEntity.getDescription());
        context.setCreatedBy(contextEntity.getCreatedBy());
        context.setModifiedBy(contextEntity.getLastModifiedBy());
        context.setId(contextEntity.getId());
        context.setEnabled(contextEntity.isEnabled());
        context.setTransactionId(contextEntity.getTransactionId());
        context.setCreatedTime(AUTZ_DATETIME_FORMATTER.format(contextEntity.getCreatedDate().toInstant()));
        context.setModifiedTime(AUTZ_DATETIME_FORMATTER.format(contextEntity.getLastModifiedByDate().toInstant()));
        context.setPermissions(new HashSet<>(contextEntity.getPermissions()));
        context.setRoles(contextEntity.getRoles().stream().map(roleDtoToRole).collect(Collectors.toList()));
        return context;
    };

     */


    final static Function<Context, ContextEntity> convertContextToContextEntity() {
        return (context) -> {
            Date now = new Date();
            logger.trace("Got context in convertContextToContextEntity => {}", context);
            ContextEntity contextEntity = new ContextEntity();
            BeanUtils.copyProperties(context, contextEntity, new String[]{"enabled"});
            //contextEntity.setCreatedBy(inContextDto.getCreatedBy());
            //contextEntity.setCreatedDate(Date.from(inContextDto.getCreatedDate().toInstant()));
            contextEntity.setCreatedDate(now);
            //contextEntity.setLastModifiedBy(inContextDto.getLastModifiedBy());
            contextEntity.setLastModifiedByDate(now);
            contextEntity.setPermissions(new ArrayList<>(context.getPermissions()));
            contextEntity.setRoles(context.getRoles().stream().map(roleToRoleEntity).collect(Collectors.toSet()));
            return contextEntity;
        };
    }


    ContextRepository contextRepository;

    ReactiveRedisOperations<String, ContextEntity> contextOps;

    public RedisContextService(ContextRepository contextRepository, ReactiveRedisTemplate<String, ContextEntity> reactiveContextRedisTemplate) {
        this.contextRepository = contextRepository;
        this.contextOps = reactiveContextRedisTemplate;
    }

    final static String CONTEXT_KEY_PREFIX = "context:";

    String calculateKey(String contextId) {
        return new StringBuilder(CONTEXT_KEY_PREFIX).append(contextId).toString();
    }



    @Override
    public Mono<Context> createContext(Context context) {
        return Mono.just(context)
                .publishOn(Schedulers.parallel())
                .map(convertContextToContextEntity())
                .flatMap(ctx -> contextOps.opsForValue().set(calculateKey(ctx.getContextId()), ctx))
                .map(bool -> bool ? context : new Context());
    }

    final static Function<UserContext, UserContextEntity> userContextToUserContextEntity(String modifiedBy,
                                                                                         String transactionId,
                                                                                         Date createdDate) {
        return (userContext) -> {
            OffsetDateTime now = OffsetDateTime.now();
            OffsetDateTime createdOffsetDate = now;
            OffsetDateTime modifiedOffsetDate = now;
            try {
                if (StringUtils.isNotBlank(userContext.getCreatedTime())) {
                    createdOffsetDate = OffsetDateTime.parse(userContext.getCreatedTime(),
                            DateTimeFormatter.ISO_DATE_TIME);
                }

            } catch (Exception e) {
                logger.warn("Could not parse getCreatedTime " + userContext.getCreatedTime(), e);
            }

            try {
                if (StringUtils.isNotBlank(userContext.getModifiedTime())) {
                    modifiedOffsetDate = OffsetDateTime.parse(userContext.getModifiedTime(),
                            DateTimeFormatter.ISO_DATE_TIME);
                }

            } catch (Exception e) {
                logger.warn("Could not parse getModifiedTime " + userContext.getModifiedTime(), e);
            }

            UserContextEntity userContextEntity = new UserContextEntity();
            if (StringUtils.isNotBlank(userContext.getId())){
                userContextEntity.setId(userContext.getId());
            }
            userContextEntity.setContextId(userContext.getContextId());
            userContextEntity.setUserId(userContext.getUserId());
            userContextEntity.setRoleId(userContext.getRoleId());
            userContextEntity.setTransactionId(transactionId);
            userContextEntity.setCreatedDate(Date.from(createdOffsetDate.toInstant()));
            userContextEntity.setLastModifiedByDate(Date.from(modifiedOffsetDate.toInstant()));
            userContextEntity.setCreatedBy(userContext.getCreatedBy() != null ? userContext.getCreatedBy() : modifiedBy);
            userContextEntity.setLastModifiedBy(userContext.getModifiedBy() != null ? userContext.getModifiedBy() : modifiedBy);
            userContextEntity.setEnabled(userContext.getEnabled() != null ? userContext.getEnabled():Boolean.TRUE);
            userContextEntity.setAdditionalPermissions(userContext.getAdditionalPermissions());
            userContextEntity.setAdditionalRoles(userContext.getAdditionalRoles());
            return userContextEntity;
        };
    }

    final static Function<UserContextEntity, UserContext> userContextEntityToUserContext = (userContextEntity) -> {
        UserContext userContext = new UserContext();
        userContext.setUserId(userContextEntity.getUserId());
        userContext.setContextId(userContextEntity.getContextId());
        userContext.setRoleId(userContextEntity.getRoleId());
        userContext.setEnabled(userContextEntity.isEnabled());
        userContext.setId(userContextEntity.getId());
        if (userContextEntity.getAdditionalPermissions() != null) {
            userContext.setAdditionalPermissions(new HashSet<>(userContextEntity.getAdditionalPermissions()));
        }

        if (userContextEntity.getAdditionalRoles() != null) {
            userContext.setAdditionalRoles(new HashSet<>(userContextEntity.getAdditionalRoles()));
        }

        userContext.setCreatedBy(userContextEntity.getCreatedBy());
        userContext.setModifiedBy(userContextEntity.getLastModifiedBy());
        try {
            userContext.setCreatedTime(ServiceUtils.SERVICE_DATETIME_FORMATTER.format(userContextEntity.getCreatedDate().toInstant()));
            userContext.setModifiedTime(ServiceUtils.SERVICE_DATETIME_FORMATTER.format(userContextEntity.getCreatedDate().toInstant()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        userContext.setTransactionId(userContextEntity.getTransactionId());

        return userContext;
    };

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

    /*
    //@Override
    public Mono<Context> createContext2(Context context) {

        return Mono.just(context)
                .doOnNext(ctx -> logger.trace("using context => {}", ctx))
                .handle(validateContext())
                .map(convertContextToContextEntity(inputContextDto))
                .flatMap(ctx -> contextRepository.findByContextId(ctx.getContextId()).switchIfEmpty(Mono.just(ctx)).zipWith(Mono.just(ctx)))
                .flatMap(tpctx -> contextRepository.deleteByContextId(tpctx.getT1().getContextId()).thenReturn(tpctx))
                .doOnNext(ctx -> logger.info("using tuple context => {}", ctx))
                .flatMap(tpctx -> {
                    ContextEntity inputCtx = tpctx.getT2();
                    ContextEntity foundCtx = tpctx.getT1();
                    inputCtx.setTransactionId(inputContextDto.getTransactionId());
                    if (foundCtx != null) {
                        if (StringUtils.isNotBlank(foundCtx.getCreatedBy())){
                            inputCtx.setCreatedBy(foundCtx.getCreatedBy());
                        }

                        if (foundCtx.getCreatedDate() != null){
                            logger.trace("found created date => {} input created date => {}",foundCtx.getCreatedDate(),
                                    inputCtx.getCreatedDate());
                            inputCtx.setCreatedDate(foundCtx.getCreatedDate());
                        }
                    }
                    logger.trace("persisting ctx => {}",inputCtx);
                    return contextRepository.save(inputCtx);
                })
                .map(convertContextEntityToContext);
    }

     */



    @Override
    public Mono<Context> updateContext(Context context) {
        return Mono.just(context)
                .publishOn(Schedulers.parallel())
                .map(convertContextToContextEntity())
                .flatMap(ctx -> contextOps.opsForValue().set(calculateKey(ctx.getContextId()), ctx))
                .map(bool -> bool ? context : new Context());
    }

    static final Mono<ContextEntity> EMPTY_CONTEXT_ENTITY = Mono.just(new ContextEntity());

    public Mono<ContextEntity> findContextEntity(String contextId) {
        return Mono.just(contextId)
                .publishOn(Schedulers.parallel())
                //.map(ctxId -> contextRepository.findByContextId(ctxId))
                .flatMap(ctxId -> contextOps.opsForValue().get(calculateKey(ctxId)))
                .switchIfEmpty(EMPTY_CONTEXT_ENTITY)
                .doOnNext(ctx -> logger.info("-------- Got context entity -> {}", ctx));
    }

    @Override
    public Mono<Context> findByContextId(String contextId) {
        return findContextEntity(contextId)
                .map(convertContextEntityToContext);
    }

    @Override
    public Mono<Context> addAdditionalRolesByContextId(String contextId, Mono<Role> role) {
        return findContextEntity(contextId)
                .flatMap(ctx -> {
                    return Mono.from(role)
                            .map(roleToRoleEntity)
                            .map(roleEntity -> {
                                ctx.getRoles().add(roleEntity);
                                return ctx;
                            });
                })
                .map(convertContextEntityToContext);
    }

    @Override
    public Mono<Void> deleteContextByContextId(String contextId) {
        return Mono.just(contextId)
                .publishOn(Schedulers.parallel())
                //.map(ctxId -> contextRepository.deleteByContextId(ctxId))
                .flatMap(ctxId -> contextOps.opsForValue().delete(calculateKey(ctxId)))
                .and(Mono.empty());
    }

    public static Function<PageRequest, Integer> calculateSkip = pageRequest -> {
        return (pageRequest.getPageNumber() - 1) * pageRequest.getPageSize();
    };

    /*
    https://spring.io/guides/gs/spring-data-reactive-redis/
     */
    @Override
    public Mono<ContextPage> getAllContextEntities(PageRequest pageRequest) {


        return Mono.just(pageRequest)
                .publishOn(Schedulers.parallel())
                .doOnNext(pageRequest1 -> logger.info("Got page request => {}", pageRequest1))
                //.map(request -> contextRepository.findAll(example,request))
                .flatMap(request -> Mono.zip(contextOps.keys(CONTEXT_KEY_PREFIX + "*").count(),
                        contextOps.keys(CONTEXT_KEY_PREFIX + "*")
                                .flatMap(contextOps.opsForValue()::get)
                                .skip(calculateSkip.apply(pageRequest))
                                .take(request.getPageSize()).collectList(), (t1, t2) -> Tuples.of(t1, t2)))
                .elapsed()
                .map(tuplePage -> {
                    //Page<ContextEntity> page = tuplePage.getT2();
                    Tuple2<Long, List<ContextEntity>> tupleList = tuplePage.getT2();
                    List<ContextEntity> list = tupleList.getT2();
                    ContextPage contextPage = new ContextPage();
                    contextPage.setPage(pageRequest.getPageNumber());
                    contextPage.setTotal(tupleList.getT1().intValue());
                    contextPage.setPageSize(list.size());
                    contextPage.setContent(list.stream().map(convertContextEntityToContext).collect(Collectors.toList()));
                    contextPage.setElapsed(tuplePage.getT1().toString());
                    return contextPage;
                });
    }

    @Override
    public Mono<RolePage> getRolesByContextId(String contextId) {
        return this.getContextByContextId(contextId)
                .map(ctx -> ctx.getRoles())
                .elapsed()
                .map(rolesTuple -> {
                    List<Role> roles = rolesTuple.getT2();
                    RolePage rolePage = new RolePage();
                    rolePage.setContent(roles);
                    rolePage.setPage(1);
                    rolePage.setPageSize(roles.size());
                    rolePage.setTotal(roles.size());
                    rolePage.setElapsed(calculateElapsedMessage().apply(rolesTuple.getT1()));
                    return rolePage;
                });
    }

    @Override
    public Mono<Context> getContextByContextId(String contextId) {
        return findByContextId(contextId)
                .handle(validateContext(contextId));
    }

    BiConsumer<Context, SynchronousSink<Context>> validateContext(String contextId) {
        return (context, sink) -> {

            if (context == null) {
                sink.error(new ChangeSetPersister.NotFoundException());
            }

            if (contextId.equals(context.getContextId())) {
                sink.next(context);
            } else {
                sink.error(new IllegalArgumentException(CONTEXT_ID_MUST_THE_SAME_AS_IN_PAYLOAD));
            }
        };
    }

    @Override
    public Mono<Context> updateContextByContextId(String contextId, Mono<Context> context) {
        return Mono.from(context)
                .flatMap(ctx -> createContext(ctx));
    }

    @Override
    public Mono<Role> getRoleByContextIdAndRoleId(String contextId, String roleId) {
        return findContextEntity(contextId)
                .flatMapIterable(ctx -> ctx.getRoles())
                .filter(re -> re.getRoleId().equals(roleId))
                .distinct()
                .collectList()
                .map(list -> list.get(0))
                .map(roleEntityToRole);
    }
}
