package com.alpha.omega.user.config;

import com.alpha.omega.user.repository.ContextEntity;
import com.alpha.omega.user.repository.UserContextEntity;
import com.redislabs.lettusearch.StatefulRediSearchConnection;
import com.redislabs.springredisearch.RediSearchAutoConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.RedisKeyValueAdapter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.convert.KeyspaceConfiguration;
import org.springframework.data.redis.core.convert.MappingConfiguration;
import org.springframework.data.redis.core.index.IndexConfiguration;
import org.springframework.data.redis.core.index.IndexDefinition;
import org.springframework.data.redis.core.index.SimpleIndexDefinition;
import org.springframework.data.redis.core.mapping.RedisMappingContext;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.Collections;

@Configuration
@EnableConfigurationProperties(CacheConfigProperties.class)
@EnableRedisRepositories(basePackages = {"com.alpha.omega.user.repository"})
public class DefaultRedisCacheConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(DefaultRedisCacheConfiguration.class);

    @Autowired
    CacheConfigProperties cacheConfigProperties;

    @Autowired
    Environment env;

    public void setCacheConfigProperties(CacheConfigProperties cacheConfigProperties) {
        this.cacheConfigProperties = cacheConfigProperties;
    }

    private RedisNode populateNode(String host, Integer port) {
        return new RedisNode(host, port);
    }

    @Bean
    public RedisTemplate<?, ?> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<byte[], byte[]> template = new RedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

    @Bean
    RedisStandaloneConfiguration RedisStandaloneConfiguration(CacheConfigProperties cacheConfigProperties) {

        RedisPassword redisPassword = RedisPassword.of(cacheConfigProperties.getPassword());
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(cacheConfigProperties.getHost());
        redisStandaloneConfiguration.setPort(cacheConfigProperties.getPort());
        redisStandaloneConfiguration.setPassword(redisPassword);
        return redisStandaloneConfiguration;
    }

    @Bean
    @Primary
    public ReactiveRedisConnectionFactory connectionFactory() {
        String host = env.getProperty("REDIS_HOST", "localhost");
        Integer port = env.getProperty("REDIS_PORT", Integer.class,6379);
        return new LettuceConnectionFactory(host, port);
    }

    @Bean
    @ConditionalOnMissingBean(
            name = {"reactiveRedisTemplate"}
    )
    @ConditionalOnBean({ReactiveRedisConnectionFactory.class})
    public ReactiveRedisTemplate<Object, Object> reactiveRedisTemplate(ReactiveRedisConnectionFactory reactiveRedisConnectionFactory, ResourceLoader resourceLoader) {
        JdkSerializationRedisSerializer jdkSerializer = new JdkSerializationRedisSerializer(resourceLoader.getClassLoader());
        RedisSerializationContext<Object, Object> serializationContext = RedisSerializationContext.newSerializationContext().key(jdkSerializer).value(jdkSerializer).hashKey(jdkSerializer).hashValue(jdkSerializer).build();
        return new ReactiveRedisTemplate(reactiveRedisConnectionFactory, serializationContext);
    }

    @Bean("reactiveStringRedisTemplate")
    @ConditionalOnMissingBean(
            name = {"reactiveStringRedisTemplate"}
    )
    //@ConditionalOnBean({ReactiveRedisConnectionFactory.class})
    public ReactiveStringRedisTemplate reactiveStringRedisTemplate(ReactiveRedisConnectionFactory reactiveRedisConnectionFactory) {
        return new ReactiveStringRedisTemplate(reactiveRedisConnectionFactory);
    }

	/*
	@Bean("redisson")
	RedissonClient redissonClient(RedisStandaloneConfiguration redisStandaloneConfiguration) {
		Config config = new Config();
		String host = redisStandaloneConfiguration.getHostName();
		Integer port = redisStandaloneConfiguration.getPort();
		String address = String.format("redis://%s:%d", host,port);
		config.useSingleServer().setAddress(address);
		RedissonClient redisson = Redisson.create(config);
		return redisson;
	}

	 */


    @Bean("reactiveUserContextRedisTemplate")
    public ReactiveRedisTemplate<String, UserContextEntity> reactiveUserContextRedisTemplate(
            ReactiveRedisConnectionFactory factory) {
        StringRedisSerializer keySerializer = new StringRedisSerializer();
        Jackson2JsonRedisSerializer<UserContextEntity> valueSerializer =
                new Jackson2JsonRedisSerializer<>(UserContextEntity.class);
        RedisSerializationContext.RedisSerializationContextBuilder<String, UserContextEntity> builder =
                RedisSerializationContext.newSerializationContext(keySerializer);
        RedisSerializationContext<String, UserContextEntity> context =
                builder.value(valueSerializer).build();
        return new ReactiveRedisTemplate<>(factory, context);
    }

    @Bean("reactiveContextRedisTemplate")
    public ReactiveRedisTemplate<String, ContextEntity> reactiveContextRedisTemplate(
            ReactiveRedisConnectionFactory factory) {
        StringRedisSerializer keySerializer = new StringRedisSerializer();
        Jackson2JsonRedisSerializer<ContextEntity> valueSerializer =
                new Jackson2JsonRedisSerializer<>(ContextEntity.class);
        RedisSerializationContext.RedisSerializationContextBuilder<String, ContextEntity> builder =
                RedisSerializationContext.newSerializationContext(keySerializer);
        RedisSerializationContext<String, ContextEntity> context =
                builder.value(valueSerializer).build();
        return new ReactiveRedisTemplate<>(factory, context);
    }

    @Bean
    public RedisMappingContext keyValueMappingContext() {
        //return new RedisMappingContext(new MappingConfiguration(new IndexConfiguration(), new AppKeyspaceConfiguration()));
        return new RedisMappingContext(new MappingConfiguration(new AppIndexConfiguration(), new AppKeyspaceConfiguration()));
    }

    public static class AppKeyspaceConfiguration extends KeyspaceConfiguration {

        @Override
        protected Iterable<KeyspaceSettings> initialConfiguration() {
            return Collections.singleton(new KeyspaceSettings(ContextEntity.class, "contextEntity"));
        }
    }

    public static class AppIndexConfiguration extends IndexConfiguration {

        @Override
        protected Iterable<IndexDefinition> initialConfiguration() {
            return Collections.singleton(new SimpleIndexDefinition("contextEntity", "contextId"));
        }
    }

    @PostConstruct
    public void postInit() {
        logger.info("Configured => {} with properties  => {}", this.getClass().getName(), cacheConfigProperties);
    }


    @Configuration
    @Import({RediSearchAutoConfiguration.class})
    public class PostConfig implements ApplicationListener<ContextRefreshedEvent> {

        @Autowired
        ReactiveStringRedisTemplate redisTemplate;

        @Autowired
        StatefulRediSearchConnection<String, String> searchConnection;


        @Autowired
        Environment environment;

        @Autowired
        RedisKeyValueAdapter redisKeyValueAdapter;

        @Override
        public void onApplicationEvent(ContextRefreshedEvent event) {
            Boolean flushDB = environment.getProperty("user.batch.db.flush", Boolean.class, Boolean.FALSE);
            //redisTemplate.opsForHash().s

            if (flushDB){
                Mono.just(flushDB)
                        .flatMap(flush -> redisTemplate.getConnectionFactory()
                                .getReactiveConnection()
                                .serverCommands()
                                .flushDb(RedisServerCommands.FlushOption.SYNC))
                        .doOnNext(val -> logger.info("Redis flushDB => {} request -> {}",flushDB, val))
                        .subscribe();
            }


        }
    }

}
