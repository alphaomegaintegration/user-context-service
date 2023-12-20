package com.alpha.omega.user;

import com.alpha.omega.user.delegate.ContextsDelegate;
import com.alpha.omega.user.repository.ContextEntity;
import com.alpha.omega.user.repository.ContextRepository;
import com.alpha.omega.user.repository.UserContextEntity;
import com.alpha.omega.user.server.ContextsApiController;
//import com.alpha.omega.user.server.UsersApiController;
import com.alpha.omega.user.service.ContextService;
import com.alpha.omega.user.service.RedisContextService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.util.UUID;

@Configuration
@ComponentScan(basePackages={"com.alpha.omega.user.server"})
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



/*
https://spring.io/guides/gs/spring-data-reactive-redis/
 */
    @Bean
    ContextService contextService(ContextRepository contextRepository, ReactiveRedisTemplate<String, ContextEntity> reactiveContextRedisTemplate){
        return new RedisContextService(contextRepository,reactiveContextRedisTemplate);
    }

    @Bean
    ContextsDelegate contextsDelegate(ContextService contextService){
        return new ContextsDelegate(contextService);
    }

    /*
    @Bean
    ContextsApiController contextsApiController(ContextsDelegate contextsDelegate){
        ContextsApiController controller = new ContextsApiController(contextsDelegate);
        return controller;
    }

     */


    @PostConstruct
    public void init(){
        final String uuid = UUID.randomUUID().toString();
        logger.info("Testing Redis with {} as val",uuid);
        //redisTemplate.opsForZSet().range("")
        //redisTemplate.getConnectionFactory().getReactiveConnection().serverCommands().save();

        redisTemplate.opsForHash().put(TEST_NAMESPACE, TEST_KEY, uuid).subscribe();
        redisTemplate.opsForHash().get(TEST_NAMESPACE, TEST_KEY).subscribe((val) -> {
            logger.info("Got val {}",val);
            Assert.isTrue(val.equals(uuid), "Values should be equal");
        });

    }

}
