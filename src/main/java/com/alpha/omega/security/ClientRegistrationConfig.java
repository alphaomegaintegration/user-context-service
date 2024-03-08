package com.alpha.omega.security;

import com.alpha.omega.security.client.ClientRegistrationEntityRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@Configuration
@EnableRedisRepositories(basePackages = {"com.alpha.omega.security.client"})
public class ClientRegistrationConfig {

    //

    @Bean
    RedisReactiveClientRegistrationRepository redisReactiveClientRegistrationRepository(ClientRegistrationEntityRepository clientRegistrationEntityRepository){
        return RedisReactiveClientRegistrationRepository.builder()
                .clientRegistrationEntityRepository(clientRegistrationEntityRepository)
                .build();
    }
}
