package com.alpha.omega.user

import com.alpha.omega.user.config.DefaultRedisCacheConfiguration
import org.assertj.core.api.Assertions
import org.junit.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.support.TestPropertySourceUtils
import org.springframework.util.Assert
import org.testcontainers.containers.DockerComposeContainer
import spock.lang.Shared
import spock.lang.Specification

//@Testcontainers
/*
@SpringBootTest(
        classes = TransactionRepositorySpecification.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
        //properties = { "spring.datasource.url=jdbc:tc:postgresql:11-alpine:///databasename" }
)

 */
//@TestPropertySource(properties = ["SPRING_PROFILES_ACTIVE=autz","cosmos.repositories=com.cosmos.repository,com.autz.repository"])
//@TestPropertySource(locations = ["classpath:properties/ServiceContextsControllerTest.yaml"])
@ContextConfiguration(initializers = PropertyOverrideContextInitializer.class,
        classes = [ TestConfig.class, DefaultRedisCacheConfiguration.class])
@WebFluxTest(/*controllers=[ServiceContextsController.class]*/)
class TransactionRepositoryIntegration extends Specification {

    private static final Logger logger = LoggerFactory.getLogger(TransactionRepositoryIntegration.class);
    static String TEST_NAMESPACE = "TransactionRepositorySpecification:namespace";
    static String TEST_KEY = "test:keyer";
    /*
    https://dzone.com/articles/testcontainers-from-zero-to-hero-video
    https://bsideup.github.io/posts/local_development_with_testcontainers/
     */


    /*
    private static final String POSTGRES_IMAGE = "postgres:latest"
    private static final String POSTGRES_DB = "assignment-test"
    private static final String POSTGRES_USER = "postgres"
    private static final String POSTGRES_PASSWORD = "postgrespass"

    @Container
    static PostgreSQLContainer postgresql = new PostgreSQLContainer<>(DockerImageName.parse(POSTGRES_IMAGE))
            .withDatabaseName(POSTGRES_DB)
            .withUsername(POSTGRES_USER)
            .withPassword(POSTGRES_PASSWORD)

    @Shared
    PostgreSQLContainer postgresqlContainer = postgresql

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresql::getJdbcUrl)
        registry.add("spring.datasource.username", postgresql::getUsername)
        registry.add("spring.datasource.password", postgresql::getPassword)
    }

     */

    public static File composeFile() {
        File composeFile = new File("./src/test/resources/docker/compose-test.yml");
        Assertions.assertThat(composeFile.exists()).isTrue();
        return composeFile;
    }

    public static DockerComposeContainer testEnvironment() {
        DockerComposeContainer env =
                new DockerComposeContainer("dockercompose-votes", composeFile())
                        .withExposedService("postgres2_1", 5432)
                        //.withExposedService("keycloak", 8008)
                        .withExposedService("cache_1", 6379)
        return env;
    }

    @Shared
    protected static DockerComposeContainer env = testEnvironment()

    /*
    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        env.start();
        registry.add("spring.redis.host", env.getServiceHost("cache_1", 6379));
        registry.add("spring.redis.port", env.getServicePort("cache_1", 6379));
    }

     */

    @Autowired
    Environment environment;

    @Autowired
    ReactiveStringRedisTemplate reactiveStringRedisTemplate;

    @Configuration
    public static class TestConfig{

    }

    def setupSpec() {
//        env.start()
//        String redisUrl = env.getServiceHost("cache_1", 6379) + ":" +
//                env.getServicePort("cache_1", 6379).toString();
//        logger.info("############# using redisUrl => {}",redisUrl)

    }
    def cleanupSpec() {
        env.stop()
    }

    @Test
    public void sanity() {


        logger.info("Sanity check")
    }

    def sanity2(){
        expect:
        environment

        logger.info("spring.redis.host => {}", environment.getProperty("spring.redis.host"))
        logger.info("spring.redis.port => {}", environment.getProperty("spring.redis.port"))

        final String uuid = UUID.randomUUID().toString();
        logger.info("Testing Redis with {} as val", uuid);

        reactiveStringRedisTemplate.opsForHash().put(TEST_NAMESPACE, TEST_KEY, uuid).subscribe();
        reactiveStringRedisTemplate.opsForHash().get(TEST_NAMESPACE, TEST_KEY).subscribe((val) -> {
            logger.info("Got val {}", val);
            Assert.isTrue(val.equals(uuid), "Values should be equal");
        });

    }

    // some tests
    def "length of Spock's and his friends' names"() {
        expect:
        name.size() == length

        where:
        name     | length
        "Spock"  | 5
        "Kirk"   | 4
        "Scotty" | 6
    }


    static class PropertyOverrideContextInitializer
            implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        PropertyOverrideContextInitializer() {
            logger.info("Starting PropertyOverrideContextInitializer.........")
            env.start();
//
//            System.setProperty("javax.net.ssl.trustStore", keyStoreFile.toString());
//            System.setProperty("javax.net.ssl.trustStorePassword", emulator.getEmulatorKey());
//            System.setProperty("javax.net.ssl.trustStoreType", "PKCS12");
//            System.setProperty("cosmos.repositories","com.pwc.cosmos.repository,com.pwc.autz.repository");
        }

        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {

            logger.info("@@@@@@@@@@@@@@@ Using env => {}",env.toString())


            //registry.add("spring.redis.host", env.getServiceHost("cache_1", 6379));
            //registry.add("spring.redis.port", env.getServicePort("cache_1", 6379));
            TestPropertySourceUtils.addInlinedPropertiesToEnvironment(applicationContext,
                    "cosmos.repositories=com.pwc.cosmos.repository,com.autz.repository",
                    "spring.redis.host="+env.getServiceHost("cache_1", 6379),
                    "REDIS_HOST="+env.getServiceHost("cache_1", 6379),
                    "spring.redis.port="+env.getServicePort("cache_1", 6379),
                    "REDIS_PORT="+env.getServicePort("cache_1", 6379)
            );

            //applicationContext.beanFactory.registerSingleton("CosmosClientBuilder",getCosmosClientBuilder())


        }
    }

}
