package com.alpha.omega.user.idprovider.keycloak;

import com.alpha.omega.user.batch.BatchUtil;
import com.alpha.omega.user.batch.UserLoad;
import com.alpha.omega.user.repository.UserEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.keycloak.admin.client.Keycloak;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.function.Function;

import static com.alpha.omega.user.idprovider.keycloak.KeyCloakService.ADMIN_CLI;
import static com.alpha.omega.user.idprovider.keycloak.KeyCloakService.MASTER;

@Configuration
@EnableConfigurationProperties(value = {KeyCloakIdpProperties.class})
public class KeyCloakConfig {


    @Bean
    Keycloak keycloak(KeyCloakIdpProperties keyCloakIdpProperties){

        return  Keycloak.getInstance(
                keyCloakIdpProperties.baseUrl(),
                MASTER,
                keyCloakIdpProperties.adminUsername(),
                keyCloakIdpProperties.adminPassword(),
                ADMIN_CLI,
                keyCloakIdpProperties.adminClientSecret());
    }


    @Bean
    KeyCloakService keyCloakService(KeyCloakIdpProperties keyCloakIdpProperties,
                                    ObjectMapper objectMapper,Keycloak keycloak){
        return KeyCloakService.builder()
                .objectMapper(objectMapper)
                .keyCloakIdpProperties(keyCloakIdpProperties)
                .keycloak(keycloak)
                .build();
    }

    @Bean({"idProviderUserPersistence", "keyCloakUserItemWriter"})
    KeyCloakUserService keyCloakUserItemWriter(KeyCloakIdpProperties keyCloakIdpProperties,
                                               ObjectMapper objectMapper,
                                               @Qualifier("defaultUserLoadUserEntityFunction") Function<UserLoad, UserEntity> defaultUserLoadUserEntityFunction
                                               ) {
        return KeyCloakUserService.builder()
                .keyCloakIdpProperties(keyCloakIdpProperties)
                .objectMapper(objectMapper)
                .userLoadUserEntityFunction(defaultUserLoadUserEntityFunction)
                .build();
    }

    @Bean
    KeyCloakService.KeyCloakContextListener keyCloakContextListener(Keycloak keycloak, KeyCloakIdpProperties keyCloakIdpProperties,
                                                                    KeyCloakService keyCloakService){
        return KeyCloakService.KeyCloakContextListener.builder()
                .keycloak(keycloak)
                .keyCloakIdpProperties(keyCloakIdpProperties)
                .keyCloakService(keyCloakService)
                .build();
    }
}
