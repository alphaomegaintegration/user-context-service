package com.alpha.omega.user

import org.assertj.core.api.Assertions
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.core.env.MapPropertySource
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.containers.DockerComposeContainer
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.containers.Network
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.lifecycle.Startables
import org.testcontainers.spock.Testcontainers
import spock.lang.Shared
import spock.lang.Specification

import java.util.stream.Stream

//@Testcontainers
//@ContextConfiguration(initializers = AbstractIntegrationTest.Initializer.class)
abstract class AbstractIntegrationTest extends Specification{


    public static String POSTGRES_DB = "batch"
    public static String POSTGRES_USER = "batchuser"
    public static String POSTGRES_PASSWORD = "password"
    public static Integer POSTGRES_PORT = Integer.valueOf(5432)
    public static Integer REDIS_PORT = Integer.valueOf(6379)

    public static Map<String, String> envStart() {
        return Map.of("POSTGRES_DB", POSTGRES_DB,
                "POSTGRES_USER", POSTGRES_USER,
                "POSTGRES_PASSWORD", POSTGRES_PASSWORD);
    }

    public static File composeFile() {
        File composeFile = new File("./src/test/resources/docker/compose-test.yml");
        Assertions.assertThat(composeFile.exists()).isTrue();
        return composeFile;
    }

    public static DockerComposeContainer testEnvironment() {
        DockerComposeContainer env =
                new DockerComposeContainer("dockercompose-votes", composeFile())
                        .withEnv(envStart())
                        .withExposedService("postgres2_1", POSTGRES_PORT)
                //.withExposedService("keycloak", 8008)
                        .withExposedService("cache_1", REDIS_PORT)
        return env;
    }

    @Shared
    public  static DockerComposeContainer dockerComposeContainer = testEnvironment()

    public static Map<String, String> getProperties() {
        // Startables.deepStart(Stream.of(redis, kafka, postgres)).join();
        //Startables.deepStart(Stream.of(dockerComposeContainer)).join();
        dockerComposeContainer.start()

        return Map.of("REDIS_HOST", dockerComposeContainer.getServiceHost("cache_1", REDIS_PORT),
                "REDIS_PORT", dockerComposeContainer.getServicePort("cache_1", REDIS_PORT).toString()
        );
        /*
        return Map.of(
                "spring.datasource.url", postgres.getJdbcUrl(),
                "spring.datasource.username", postgres.getUsername(),
                "spring.datasource.password", postgres.getPassword(),

                "spring.redis.host", redis.getHost(),
                "spring.redis.port", redis.getFirstMappedPort() + "",
                "spring.kafka.bootstrap-servers", kafka.getBootstrapServers()
        );

         */
    }

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        /*
        https://github.com/testcontainers/testcontainers-java/blob/main/modules/postgresql/src/test/java/org/testcontainers/junit/postgresql/SimplePostgreSQLTest.java
         */
        /*
        static Network network = Network.newNetwork();
        static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(TestImages.POSTGRES_TEST_IMAGE)
        .withNetwork(network);

        static GenericContainer<?> redis = new GenericContainer<>("redis:3-alpine")
        .withNetwork(network)
                .withExposedPorts(6379);

        static KafkaContainer kafka = new KafkaContainer();

         */



        @Override
        public void initialize(ConfigurableApplicationContext context) {
            var env = context.getEnvironment();
            env.getPropertySources().addFirst(new MapPropertySource(
                    "testcontainers",
                    (Map) AbstractIntegrationTest.getProperties()
            ));
        }
    }
}