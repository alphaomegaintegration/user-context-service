package com.alpha.omega.user;

import com.alpha.omega.user.batch.BatchJobService;
import com.alpha.omega.user.delegate.ContextsDelegate;
import com.alpha.omega.user.delegate.PublicDelegate;
import com.alpha.omega.user.delegate.UserContextDelegate;
import com.alpha.omega.user.exception.ContextNotFoundException;
import com.alpha.omega.user.exception.ReactiveExceptionHandler;
import com.alpha.omega.user.exception.ServiceException;
import com.alpha.omega.user.exception.UserNotFoundException;
import com.alpha.omega.user.repository.*;
import com.alpha.omega.user.server.ContextsApiController;
//import com.alpha.omega.user.server.UsersApiController;
import com.alpha.omega.user.server.UsercontextsApiController;
import com.alpha.omega.user.service.*;
import com.alpha.omega.user.validator.UserContextValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.annotation.Order;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.UUID;

import static org.springframework.http.ResponseEntity.notFound;

@Configuration
//@ComponentScan(basePackages = {"com.alpha.omega.user.server"})
@EnableRedisRepositories(basePackages = "com.alpha.omega.user.repository")
public class AppConfig {
    private static final Logger logger = LoggerFactory.getLogger(AppConfig.class);


    @Autowired
    ReactiveStringRedisTemplate redisTemplate;
    @Autowired
    ReactiveRedisTemplate<String, UserContextEntity> reactiveUserContextRedisTemplate;
    @Autowired
    ReactiveRedisTemplate<String, ContextEntity> reactiveContextRedisTemplate;
    static String TEST_NAMESPACE = "test:namespace";
    static String TEST_KEY = "test:key";

    @Bean(name = "applicationEventMulticaster")
    public ApplicationEventMulticaster simpleApplicationEventMulticaster() {
        SimpleApplicationEventMulticaster eventMulticaster =
                new SimpleApplicationEventMulticaster();

        eventMulticaster.setTaskExecutor(new SimpleAsyncTaskExecutor());
        return eventMulticaster;
    }

    @Bean
    @Order(-2)
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
    public ReactiveExceptionHandler reactiveExceptionHandler(WebProperties webProperties, ApplicationContext applicationContext, ServerCodecConfigurer configurer) {
        ReactiveExceptionHandler exceptionHandler = new ReactiveExceptionHandler(
                new DefaultErrorAttributes(), webProperties.getResources(), applicationContext, exceptionToStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR
        );
        exceptionHandler.setMessageWriters(configurer.getWriters());
        exceptionHandler.setMessageReaders(configurer.getReaders());
        return exceptionHandler;
    }

    @Bean
    public Map<Class<? extends Exception>, HttpStatus> exceptionToStatusCode() {
        return Map.of(
                UserNotFoundException.class, HttpStatus.NOT_FOUND,
                ContextNotFoundException.class, HttpStatus.NOT_FOUND,
                ServiceException.class, HttpStatus.BAD_REQUEST,
                IllegalArgumentException.class, HttpStatus.BAD_REQUEST,
                WebClientResponseException.Unauthorized.class, HttpStatus.UNAUTHORIZED,
                WebClientResponseException.Forbidden.class, HttpStatus.FORBIDDEN,
                WebClientResponseException.BadRequest.class, HttpStatus.BAD_REQUEST,
                WebClientResponseException.Conflict.class, HttpStatus.CONFLICT,
                JwtValidationException.class, HttpStatus.UNAUTHORIZED,
                BadCredentialsException.class, HttpStatus.UNAUTHORIZED

        );
    }

    @Bean
    public HttpStatus defaultStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    /*
    https://spring.io/guides/gs/spring-data-reactive-redis/
     */
    @Bean
    UserContextValidator userContextValidator() {
        return new UserContextValidator();
    }

    @Bean
    ContextService contextService(ContextRepository contextRepository, ReactiveRedisTemplate<String, ContextEntity> reactiveContextRedisTemplate,
                                  RoleRepository roleRepository, ObjectMapper objectMapper,
                                  PagingAndSortingContextRepository pagingAndSortingContextRepository,
                                  ApplicationEventPublisher eventPublisher) {
        return RedisContextService.builder()
                .contextRepository(contextRepository)
                .pagingAndSortingContextRepository(pagingAndSortingContextRepository)
                .contextOps(reactiveContextRedisTemplate)
                .roleRepository(roleRepository)
                .objectMapper(objectMapper)
                .eventPublisher(eventPublisher)
                .build();
    }


    @Bean
    ContextsDelegate contextsDelegate(ContextService contextService) {
        return new ContextsDelegate(contextService);
    }

    @Bean
    UserContextService userContextService(ContextService contextService,
                                          ReactiveRedisTemplate<String, UserContextEntity> userContextTemplate,
                                          UserContextRepository userContextRepository,
                                          UserContextValidator userContextValidator,
                                          ContextRepository contextRepository) {
        return RedisUserContextService.builder()
                .userContextTemplate(userContextTemplate)
                .userContextRepository(userContextRepository)
                .contextRepository(contextRepository)
                .contextService(contextService)
                .userContextValidator(userContextValidator)
                .build();
    }

    @Bean
    UserContextDelegate userContextDelegate(UserContextService userContextService, ContextService contextService){
        return UserContextDelegate.builder()
                .userContextService(userContextService)
                .contextService(contextService)
                .build();
    }

    @Bean
    ContextsApiController contextsApiController(ContextsDelegate contextsDelegate){
        ContextsApiController controller = new ContextsApiController(contextsDelegate);
        return controller;
    }


    @Bean
    UsercontextsApiController usercontextsApiController(UserContextDelegate userContextDelegate){
        UsercontextsApiController controller = new UsercontextsApiController(userContextDelegate);
        return controller;
    }




    @PostConstruct
    public void init() {
        final String uuid = UUID.randomUUID().toString();
        logger.info("Testing Redis with {} as val", uuid);
        //redisTemplate.opsForZSet().range("")
        //redisTemplate.getConnectionFactory().getReactiveConnection().serverCommands().save();

        redisTemplate.opsForHash().put(TEST_NAMESPACE, TEST_KEY, uuid).subscribe();
        redisTemplate.opsForHash().get(TEST_NAMESPACE, TEST_KEY).subscribe((val) -> {
            logger.info("Got val {}", val);
            Assert.isTrue(val.equals(uuid), "Values should be equal");
        });

    }


}
