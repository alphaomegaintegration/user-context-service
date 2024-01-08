package com.alpha.omega.user.idprovider.keycloak;

import com.alpha.omega.user.batch.BatchUtil;
import com.alpha.omega.user.batch.UserLoad;
import com.alpha.omega.user.repository.UserEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Function;

@Configuration
@EnableConfigurationProperties(value = {KeyCloakIdpProperties.class})
public class KeyCloakConfig {

    @Bean({"idProviderUserPersistence", "keyCloakUserItemWriter"})
    KeyCloakUserService keyCloakUserItemWriter(KeyCloakIdpProperties keyCloakIdpProperties,
                                               ObjectMapper objectMapper,
                                               @Qualifier("defaultUserLoadUserEntityFunction") Function<UserLoad, UserEntity> defaultUserLoadUserEntityFunction) {
        return KeyCloakUserService.builder()
                .keyCloakIdpProperties(keyCloakIdpProperties)
                .objectMapper(objectMapper)
                .userLoadUserEntityFunction(defaultUserLoadUserEntityFunction)
                .build();
    }
}
