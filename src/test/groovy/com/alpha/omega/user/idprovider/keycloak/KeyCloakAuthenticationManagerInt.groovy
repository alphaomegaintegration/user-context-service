package com.alpha.omega.user.idprovider.keycloak

import com.alpha.omega.security.SecurityUtils
import com.alpha.omega.security.idprovider.keycloak.KeyCloakAuthenticationManager
import com.alpha.omega.user.AbstractIntegrationTest
import com.alpha.omega.user.AppConfig
import com.alpha.omega.user.SecurityConfig
import com.alpha.omega.user.batch.BatchUtil
import com.alpha.omega.user.batch.UserLoad
import com.alpha.omega.user.client.auth.Authentication
import com.alpha.omega.user.config.DefaultRedisCacheConfiguration
import com.alpha.omega.user.repository.ContextRepository
import com.alpha.omega.user.repository.UserContextRepository
import com.alpha.omega.user.repository.UserEntity
import com.alpha.omega.user.service.UserContextService
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.core.env.Environment
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.util.Assert
import reactor.core.scheduler.Schedulers
import reactor.test.StepVerifier
import spock.lang.Specification
import spock.mock.DetachedMockFactory

import java.util.function.Function

@ContextConfiguration(
        classes = [TestConfig.class, DefaultRedisCacheConfiguration.class, AppConfig.class, KeyCloakConfig.class])
class KeyCloakAuthenticationManagerInt extends Specification {
    private static final Logger logger = LoggerFactory.getLogger(KeyCloakAuthenticationManagerInt.class);

    /*
    ./mvnw clean test -Dtest=KeyCloakAuthenticationManagerInt
     */

    def setup() {

    }          // run before every feature method
    def cleanup() {}        // run after every feature method
    def setupSpec() {

    }     // run before the first feature method
    def cleanupSpec() {

    }   // run after

    @Autowired
    Environment environment;

    @Autowired
    KeyCloakUserService keyCloakUserService;

    @Autowired
    UserContextService userContextService

    @Autowired
    UserContextRepository userContextRepository;

    @Autowired
    ContextRepository contextRepository;

    def sanity() {
        expect:
        environment

    }


    def getPropertyFrom(String key) {
        if (environment != null) {
            return environment.getProperty(key)
        } else {
            return System.getenv(key);
        }
    }


    def basic_auth_login() {

        logger.info("uce => {}", userContextRepository.findByContextId("unhrc-bims").get(0))
        logger.info("ce => {}", contextRepository.findByContextId("unhrc-bims"))

        given:
        String username = getPropertyFrom("REALM_USER");
        String password = getPropertyFrom("REALM_PASSWORD");
        String baseUrl = getPropertyFrom("REALM_BASE_URL");
        String tokenUri = getPropertyFrom("REALM_TOKEN_URI");
        String jwkSet = getPropertyFrom("REALM_JWKSET_URI");
        String issuerUrl = getPropertyFrom("REALM_ISSUER");
        String clientId = getPropertyFrom("REAlM_CLIENT_ID");
        String clientSecret = getPropertyFrom("REAlM_CLIENT_SECRET");
        // String baseUrl = environment.getProperty("REALM_PASSWORD");

        logger.info("Got jwkSet {}", jwkSet)
        logger.info("Got baseUrl {}", baseUrl)
        KeyCloakAuthenticationManager authenticationManager = KeyCloakAuthenticationManager.builder()
                .defaultContext(KeyCloakUtils.KEY_CLOAK_DEFAULT_CONTEXT)
                .issuerURL(issuerUrl)
                .realmBaseUrl(baseUrl)
                .realmClientId(clientId)
                .realmClientSecret(clientSecret)
                .realmJwkSetUri(jwkSet)
                .realmTokenUri(tokenUri)
                .userContextService(userContextService)
                .build()
        authenticationManager.init()

        def basicAuth = SecurityUtils.basicAuthCredsFrom(username, password)
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);

        when:
        def result = authenticationManager.authenticate(authenticationToken);

        then:
        true
        result
        StepVerifier
                .create(result)
               // .expectNext("JOHN")
               // .expectNextMatches(name -> name.startsWith("MA"))
               // .expectNext("CLOE", "CATE")
                .expectNextMatches (auth -> auth.authenticated)
                .expectComplete()
                .verify();

    }


    @Configuration
    public static class TestConfig {
        @Bean("defaultUserLoadUserEntityFunction")
        Function<UserLoad, UserEntity> defaultUserLoadUserEntityFunction() {
            return BatchUtil.defaultUserLoadUserEntityFunction();
        }

        @Bean
        ObjectMapper objectMapper() {
            return new ObjectMapper();
        }

        DetachedMockFactory mockFactory = new DetachedMockFactory()


        /*
        @Bean
        @Primary
        UserContextService userContextService() {
            return mockFactory.Mock(UserContextService.class)
            //return mockFactory.Stub(UserContextService)
        }

         */


    }
}
