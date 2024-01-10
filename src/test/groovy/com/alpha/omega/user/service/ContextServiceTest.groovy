package com.alpha.omega.user.service


import com.alpha.omega.user.batch.BatchUtil
import com.alpha.omega.user.batch.UserLoad
import com.alpha.omega.user.model.Context
import com.alpha.omega.user.repository.ContextEntity
import com.alpha.omega.user.repository.ContextRepository
import com.alpha.omega.user.repository.PagingAndSortingContextRepository
import com.alpha.omega.user.repository.RoleEntity
import com.alpha.omega.user.repository.RoleRepository
import com.alpha.omega.user.repository.UserEntity
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.DefaultResourceLoader
import org.springframework.core.io.Resource
import org.springframework.core.io.ResourceLoader
import org.springframework.data.redis.core.ReactiveRedisOperations
import org.springframework.test.context.ContextConfiguration
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import spock.lang.Specification
import spock.mock.DetachedMockFactory

import java.util.function.BiFunction
import java.util.function.Function
import java.util.stream.Collectors

import static com.alpha.omega.user.service.RedisContextService.convertContextToContextEntity
import static com.alpha.omega.user.service.ServiceUtils.extractFileAsStringFromResource

@ContextConfiguration(
        classes = [TestConfig.class])
class ContextServiceTest extends Specification {
    private static final Logger logger = LoggerFactory.getLogger(ContextServiceTest.class);

    /*
    ./mvnw clean test -Dtest=ContextServiceTest
     */

    static ResourceLoader resourceLoader = new DefaultResourceLoader();

    def setup() {

    }          // run before every feature method
    def cleanup() {}        // run after every feature method
    def setupSpec() {

    }     // run before the first feature method
    def cleanupSpec() {

    }   // run after

    @Autowired
    PagingAndSortingContextRepository pagingAndSortingContextRepository;

    @Autowired
    ContextRepository contextRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    ObjectMapper objectMapper;

    def test_config() {
        expect:
        pagingAndSortingContextRepository
        contextRepository
        roleRepository
        objectMapper
    }

    BiFunction<Context, Context, Boolean> checkContextResult = (result, userContextService) -> {
        return result.getContextId().equals(userContextService.getContextId()) &
                result.getRoles().containsAll(userContextService.getRoles()) &
                result.getCreatedTime() != null &
              result.getModifiedTime() != null;
    }

    BiFunction<Throwable, String, Boolean> illegalArgumentContainsMessage = (ex, message) -> {
          return ex instanceof IllegalArgumentException & ex.getMessage().contains(message);
    }


    def create_context() {
        given:

        Context userContextService = loadContext("classpath:contexts/user-context-service.json")
        int rolesSize = userContextService.getRoles().size()
        List<RoleEntity> roleEntities = userContextService.getRoles().stream()
                .map(ServiceUtils.roleToRoleEntity)
                .collect(Collectors.toList())
        //logger.info("Got userContextService => {}",userContextService.toString())
        roleEntities.size() * roleRepository.save(_) >>> roleEntities
        1 * contextRepository.save(_) >> convertContextToContextEntity().apply(userContextService)
        ContextService contextService = RedisContextService.builder()
                .contextRepository(contextRepository)
                .roleRepository(roleRepository)
                .pagingAndSortingContextRepository(pagingAndSortingContextRepository)
                .objectMapper(objectMapper)
                .build()
        when:
        Mono<Context> result = contextService.createContext(userContextService)

        then:
        StepVerifier
                .create(result)
                .expectNextMatches(context -> checkContextResult.apply(context, userContextService))
                .expectComplete()
                .verify();

    }

    def create_context_no_description() {
        given:

        Context userContextService = loadContext("classpath:contexts/user-context-service.json")
        userContextService.setDescription(null)
        int rolesSize = userContextService.getRoles().size()
        List<RoleEntity> roleEntities = userContextService.getRoles().stream()
                .map(ServiceUtils.roleToRoleEntity)
                .collect(Collectors.toList())
        //logger.info("Got userContextService => {}",userContextService.toString())
        0 * roleRepository.save(_) >>> roleEntities
        0 * contextRepository.save(_) >> convertContextToContextEntity().apply(userContextService)
        ContextService contextService = RedisContextService.builder()
                .contextRepository(contextRepository)
                .roleRepository(roleRepository)
                .pagingAndSortingContextRepository(pagingAndSortingContextRepository)
                .objectMapper(objectMapper)
                .build()
        when:
        Mono<Context> result = contextService.createContext(userContextService)

        then:
        StepVerifier
                .create(result)
                .expectError(IllegalArgumentException.class)
                //.expectNextMatches(context -> checkContextResult.apply(context, userContextService))
                //.expectComplete()
                .verify();

    }

    def create_context_no_context_id() {
        given:

        Context userContextService = loadContext("classpath:contexts/user-context-service.json")
        userContextService.setContextId(null)
        int rolesSize = userContextService.getRoles().size()
        List<RoleEntity> roleEntities = userContextService.getRoles().stream()
                .map(ServiceUtils.roleToRoleEntity)
                .collect(Collectors.toList())
        //logger.info("Got userContextService => {}",userContextService.toString())
        0 * roleRepository.save(_) >>> roleEntities
        0 * contextRepository.save(_) >> convertContextToContextEntity().apply(userContextService)
        ContextService contextService = RedisContextService.builder()
                .contextRepository(contextRepository)
                .roleRepository(roleRepository)
                .pagingAndSortingContextRepository(pagingAndSortingContextRepository)
                .objectMapper(objectMapper)
                .build()
        when:
        Mono<Context> result = contextService.createContext(userContextService)

        then:
        StepVerifier
                .create(result)
                .expectError(IllegalArgumentException.class)
        //.expectNextMatches(context -> checkContextResult.apply(context, userContextService))
        //.expectComplete()
                .verify();

    }

    def create_context_no_permissions() {
        given:

        Context userContextService = loadContext("classpath:contexts/user-context-service.json")
        userContextService.setPermissions(Collections.emptySet())
        int rolesSize = userContextService.getRoles().size()
        List<RoleEntity> roleEntities = userContextService.getRoles().stream()
                .map(ServiceUtils.roleToRoleEntity)
                .collect(Collectors.toList())
        //logger.info("Got userContextService => {}",userContextService.toString())
        0 * roleRepository.save(_) >>> roleEntities
        0 * contextRepository.save(_) >> convertContextToContextEntity().apply(userContextService)
        ContextService contextService = RedisContextService.builder()
                .contextRepository(contextRepository)
                .roleRepository(roleRepository)
                .pagingAndSortingContextRepository(pagingAndSortingContextRepository)
                .objectMapper(objectMapper)
                .build()
        when:
        Mono<Context> result = contextService.createContext(userContextService)

        then:
        StepVerifier
                .create(result)
                .expectErrorMatches(ex -> illegalArgumentContainsMessage.apply(ex, "Permissions cannot be empty or null"))
                .verify();

    }


    def "UpdateContext"() {
    }

    def Context loadContext(String contextPath) {
        Resource resource = resourceLoader.getResource(contextPath);
        Optional<String> fileAsStr = extractFileAsStringFromResource(resource);
        try {

            return objectMapper.readValue(fileAsStr.orElseThrow(), Context.class)
        } catch (Exception e) {
            e.printStackTrace()
            throw new RuntimeException(e);
        }
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

        @Bean
        ContextRepository contextRepository() {
            return mockFactory.Mock(ContextRepository.class)

        }

        @Bean
        PagingAndSortingContextRepository pagingAndSortingContextRepository() {
            return mockFactory.Mock(PagingAndSortingContextRepository.class)

        }

        @Bean
        RoleRepository roleRepository() {
            return mockFactory.Mock(RoleRepository.class)

        }

        @Bean
        ReactiveRedisOperations<String, ContextEntity> reactiveRedisOperations() {
            return (ReactiveRedisOperations<String, ContextEntity>) mockFactory.Mock(ReactiveRedisOperations.class)

        }

    }
}
