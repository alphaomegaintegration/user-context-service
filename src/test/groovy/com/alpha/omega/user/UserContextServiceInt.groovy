package com.alpha.omega.user

import com.alpha.omega.user.batch.BatchUtil
import com.alpha.omega.user.batch.UserLoad
import com.alpha.omega.user.config.DefaultRedisCacheConfiguration
import com.alpha.omega.user.idprovider.keycloak.KeyCloakConfig
import com.alpha.omega.user.repository.UserEntity
import org.junit.jupiter.api.extension.ExtendWith
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.util.Assert
import org.testcontainers.containers.DockerComposeContainer
import spock.lang.Shared
import org.springframework.test.context.junit.jupiter.SpringExtension;


import java.util.function.Function

@ExtendWith(SpringExtension.class)
@ContextConfiguration(initializers = AbstractIntegrationTest.Initializer.class,
        classes = [ TestConfig.class, DefaultRedisCacheConfiguration.class, AppConfig.class, KeyCloakConfig.class, SecurityConfig.class])
@WebFluxTest()
class UserContextServiceInt extends AbstractIntegrationTest{

    private static final Logger logger = LoggerFactory.getLogger(UserContextServiceInt.class);
    static String TEST_NAMESPACE = "TransactionRepositorySpecification:namespace";
    static String TEST_KEY = "test:keyer";
    @Shared
    public  static DockerComposeContainer dockerComposeContainer = testEnvironment()

    @Autowired
    Environment environment;

    @Autowired
    ReactiveStringRedisTemplate reactiveStringRedisTemplate;
    @Autowired
    ApplicationContext context;

    WebTestClient webClient;



    def setup() {
        this.webClient = WebTestClient
                .bindToApplicationContext(this.context)
        // add Spring Security test Support
                .apply(SecurityMockServerConfigurers.springSecurity())
                .configureClient()
        // .filter(SecurityMockServerConfigurers.basicAuthentication())
                .build();
    }          // run before every feature method
    def cleanup() {}        // run after every feature method
    def setupSpec() {

    }     // run before the first feature method
    def cleanupSpec() {
        dockerComposeContainer.stop()
    }   // run after the last feature method


    def sanity2(){
        expect:
        environment

        logger.info("spring.redis.host => {}", environment.getProperty("REDIS_HOST"))
        logger.info("spring.redis.port => {}", environment.getProperty("REDIS_PORT"))

        final String uuid = UUID.randomUUID().toString();
        logger.info("Testing Redis with {} as val", uuid);

        reactiveStringRedisTemplate.opsForHash().put(TEST_NAMESPACE, TEST_KEY, uuid).subscribe();
        reactiveStringRedisTemplate.opsForHash().get(TEST_NAMESPACE, TEST_KEY).subscribe((val) -> {
            logger.info("Got val {}", val);
            Assert.isTrue(val.equals(uuid), "Values should be equal");
        });

    }

    def web_request(){
        given:

        expect:
        webClient.get().uri("/contexts", 100)
                .exchange()
                .expectStatus().isUnauthorized()
        /*
                .expectBody()
                .jsonPath("$.name").isNotEmpty()
                .jsonPath("$.id").isEqualTo(100)
                .jsonPath("$.name").isEqualTo("Test")
                .jsonPath("$.salary").isEqualTo(1000);

         */
    }

    @Configuration
    public static class TestConfig{
        @Bean("defaultUserLoadUserEntityFunction")
        Function<UserLoad, UserEntity> defaultUserLoadUserEntityFunction() {
            return BatchUtil.defaultUserLoadUserEntityFunction();
        }
    }
}
