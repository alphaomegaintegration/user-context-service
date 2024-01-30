package com.alpha.omega.user.service;

import com.alpha.omega.user.model.*;
import com.alpha.omega.user.repository.*;
import com.alpha.omega.user.validator.ContextValidator;
import com.alpha.omega.user.validator.ServiceError;
import com.alpha.omega.user.validator.UserContextValidator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SynchronousSink;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.alpha.omega.user.service.ServiceUtils.*;
import static com.alpha.omega.user.utils.Constants.COMMA;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RedisContextService implements ContextService {

    static final Logger logger = LoggerFactory.getLogger(RedisContextService.class);


    public static final String CONTEXT_ID_MUST_THE_SAME_AS_IN_PAYLOAD = "ContextId must the same as in payload.";
    @Builder.Default
    UserContextValidator userContextValidator = new UserContextValidator();
    ContextRepository contextRepository;
    PagingAndSortingContextRepository pagingAndSortingContextRepository;
    RoleRepository roleRepository;
    ReactiveRedisOperations<String, ContextEntity> contextOps;
    ObjectMapper objectMapper;
    @Builder.Default
    ResourceLoader resourceLoader = new DefaultResourceLoader();
    @Builder.Default
    Scheduler scheduler = Schedulers.boundedElastic();
    @Builder.Default
    ContextValidator contextValidator = new ContextValidator();
    ApplicationEventPublisher eventPublisher;



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
        logger.debug("Got contextEntity in convertContextEntityToContext => {}", contextEntity);

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
            context.setRoles(new ArrayList<>(contextEntity.getRoles().stream().map(roleEntityToRole).collect(Collectors.toSet())));
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
            if (context == null){
                throw new IllegalArgumentException("Context cannot be null in convertContextToContextEntity");
            }
            Date now = new Date();
            logger.info("Got context in convertContextToContextEntity => {}", context);
            ContextEntity contextEntity = new ContextEntity();
            BeanUtils.copyProperties(context, contextEntity, new String[]{"enabled"});
            //contextEntity.setCreatedBy(inContextDto.getCreatedBy());
            //contextEntity.setCreatedDate(Date.from(inContextDto.getCreatedDate().toInstant()));
            contextEntity.setCreatedDate(now);
            //contextEntity.setLastModifiedBy(inContextDto.getLastModifiedBy());
            contextEntity.setLastModifiedByDate(now);
            contextEntity.setPermissions(new ArrayList<>(context.getPermissions()));
            contextEntity.setRoles(new HashSet<>(context.getRoles().stream().map(roleToRoleEntity).collect(Collectors.toSet())));
            logger.info("Got contextEntity in convertContextToContextEntity => {}", contextEntity);
            return contextEntity;
        };
    }


    public RedisContextService(ContextRepository contextRepository, ReactiveRedisTemplate<String, ContextEntity> reactiveContextRedisTemplate) {
        this.contextRepository = contextRepository;
        this.contextOps = reactiveContextRedisTemplate;
    }


    @Override
    public Mono<Context> createContext(Context context) {

        return Mono.just(context)
                .publishOn(scheduler)
                .handle(validateContext())
                .map(convertContextToContextEntity())
                .map(ctx -> {

                    List<RoleEntity> roles = ctx.getRoles().stream()
                            //.map(persistRoleEntity)
                            .map(role -> roleRepository.save(role))
                            .collect(Collectors.toList());
                    ctx.setRoles(roles);
                    return contextRepository.save(ctx);
                })
                .map(convertContextEntityToContext)
                .doOnNext(ctx -> eventPublisher.publishEvent(new ContextCreated(this, ctx)));

    }


    /*
    @Override
    public Mono<Context> createContext_original(Context context) {

        return Mono.just(context)
                .publishOn(Schedulers.parallel())
                .map(convertContextToContextEntity())
                .flatMap(ctx -> Mono.zip(contextOps.opsForValue()
                                .set(calculateContextKey(ctx.getContextId()), ctx),
                        Mono.just(ctx)))
                .map(tuple -> ServiceUtils.convertContextEntityToContext.apply(tuple.getT2()));

    }

     */

    private Map<?, ?> convertContextEntityToMap(ContextEntity ctx) {
        return objectMapper.convertValue(ctx, Map.class);
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
            if (StringUtils.isNotBlank(userContext.getId())) {
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
            userContextEntity.setEnabled(userContext.getEnabled() != null ? userContext.getEnabled() : Boolean.TRUE);
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


    @Override
    public Mono<Context> updateContext(Context context) {
        return Mono.just(context)
                .publishOn(scheduler)
                .map(convertContextToContextEntity())
                .flatMap(ctx -> contextOps.opsForValue().set(calculateContextKey(ctx.getContextId()), ctx))
                .map(bool -> bool ? context : new Context());
    }

    static final Mono<ContextEntity> EMPTY_CONTEXT_ENTITY = Mono.just(new ContextEntity());

    public Mono<ContextEntity> findContextEntity(String contextId) {
        return Mono.just(contextId)
                .publishOn(scheduler)
                .doOnNext(ctx -> logger.debug("-------- Got context id -> {}", ctx))
                //.map(ctxId -> contextRepository.findByContextId(ctxId))
                .map(ctxId -> contextRepository.findByContextId(ctxId))
                .doOnNext(ctx -> logger.debug("-------- Got context entity -> {}", ctx))
                .switchIfEmpty(EMPTY_CONTEXT_ENTITY);
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
                .flatMap(ctxId -> contextOps.opsForValue().delete(calculateContextKey(ctxId)))
                .and(Mono.empty());
    }

    public static Function<PageRequest, Integer> calculateSkip = pageRequest -> {
        return (pageRequest.getPageNumber() - 1) * pageRequest.getPageSize();
    };

    Function<List<Map.Entry<Object, Object>>, ContextEntity> mapEntrieToContextEntityFunction(){
        return entries -> {
            Map<Object, Object> map = entries.stream().collect(Collectors.toMap(entry -> entry.getKey(),
                    entry -> entry.getValue()));
            return objectMapper.convertValue(map, ContextEntity.class);
        };
    }

    Function<String,Mono<Boolean>> dataTypeHash(){
        return ky -> {
            return contextOps.type(ky)
                    .filter(dataType -> dataType.equals(DataType.HASH))
                    .map(dt -> dt.equals(DataType.HASH));
        };
    }

    Flux<String> getOnlyHashKeys(Flux<String> keys){
        return Flux.from(keys)
                .filterWhen(dataTypeHash());
    }

    /*
    https://spring.io/guides/gs/spring-data-reactive-redis/
     */
    @Override
    public Mono<ContextPage> getAllContextEntities(PageRequest pageRequest) {
        //contextOps.opsForHash().

        return Mono.just(pageRequest)
                .publishOn(scheduler)
                .doOnNext(pageRequest1 -> logger.debug("Got page request => {}", pageRequest1))
                .doOnNext(pageRequest1 -> logger.debug("Got total count => {}", contextRepository.count()))
                .flatMap(request -> Mono.zip(Mono.just(contextRepository.count()),
                        Flux.fromIterable (contextRepository.findAll())
                                .skip(calculateSkip.apply(pageRequest))
                                .take(request.getPageSize()).collectList(),
                        (t1, t2) -> Tuples.of(t1, t2)))
                .elapsed()
                .map(tuplePage -> {
                    //Tuple2<Long, Iterable<ContextEntity>> tupleList = tuplePage.getT2();
                    //List<ContextEntity> list = IterableUtils.toList(tupleList.getT2());
                    Tuple2<Long, List<ContextEntity>> tupleList = tuplePage.getT2();
                    List<ContextEntity> list = tupleList.getT2();
                    //Tuple2<Long, Page<ContextEntity>> tupleList = tuplePage.getT2();
                    //List<ContextEntity> list = tupleList.getT2().getContent();
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
                .handle(validatePersistedContext(contextId));
    }

    BiConsumer<Context, SynchronousSink<Context>> validatePersistedContext(String contextId) {
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

    BiConsumer<Context, SynchronousSink<Context>> validateContext() {
        logger.debug("In validateContext()");
        return (context, sink) -> {
            List<ServiceError> errors = contextValidator.validate(context);
            if (errors.isEmpty()) {
                sink.next(context);
            } else {
                String message = ServiceUtils.formatValidationErrors(errors, "context");
                sink.error(new IllegalArgumentException(message));
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

    @Override
    public Flux<Role> getRolesByContextIdAndRoleIdIn(String contextId, List<String> roleIds, boolean allRoles) {

        logger.info("################ contextId => {}, roleids => {}, allRoles => {}",
                new Object[]{contextId, roleIds, allRoles});
        return Mono.just(Tuples.of(contextId, roleIds))
                .publishOn(scheduler)
                .flatMapMany(tuple -> findContextEntity(tuple.getT1()).map(ce -> ce.getRoles()))
                .flatMapIterable(col -> col)
                .distinct()
                //.flatMap(col -> Flux.fromIterable(col))
                .filter(role -> allRoles ? Boolean.TRUE : roleIds.contains(role.getRoleId()))
                .map(roleEntityToRole);
    }

    @Override
    public Mono<Boolean> roleExistsInContext(String roleId, String contextId) {
        return Mono.just(Tuples.of(roleId,contextId))
                .flatMap(tuple -> findContextEntity(tuple.getT1())
                        .map(ce -> ce.getRoles().stream()
                                .filter(roleEntity -> roleEntity.getRoleId().equals(tuple.getT1()))
                                .findFirst()))
                .map(Optional::isPresent);

    }

    Function<Optional<String>, Optional<Context>> extractContextFromJson(){
        return json -> {
            Optional<Context> context = Optional.empty();
            if (json.isPresent()){
                try {
                    context = Optional.of(objectMapper.readValue(json.get(), Context.class));
                } catch (JsonProcessingException e) {
                    logger.warn("Could not extractContextFromJson {}",json.get(),e);
                }
            }
            return context;
        };
    }

    final AtomicLong counter = new AtomicLong();

    @Override
    public Flux<Context> loadContexts(Scheduler scheduler, String contextsStr) {
        logger.debug("How many times is this called? {}",counter.incrementAndGet());
        return Flux.fromArray(contextsStr.split(COMMA))
                .publishOn(scheduler)
                .map(resourcePath -> resourceLoader.getResource(resourcePath))
                .map(resource -> extractFileAsStringFromResource(resource))
                .map(extractContextFromJson())
                .flatMap(optionalContext -> this.createContext(optionalContext.get()));
    }
}
