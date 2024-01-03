package com.alpha.omega.user.batch;

import com.alpha.omega.user.delegate.UserDelegate;
import com.alpha.omega.user.idprovider.keycloak.KeyCloakUserService;
import com.alpha.omega.user.repository.UserContextRepository;
import com.alpha.omega.user.repository.UserEntity;
import com.alpha.omega.user.repository.UserRepository;
import com.alpha.omega.user.service.ServiceUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.CommandLineJobRunner;
import org.springframework.batch.core.listener.ExecutionContextPromotionListener;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.*;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.ArrayFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.alpha.omega.user.batch.BatchConstants.PROMOTE_USER_ENTITY_CHUNK_KEY;
import static com.alpha.omega.user.batch.BatchConstants.PROMOTE_USER_LOAD_CHUNK_KEY;
import static com.alpha.omega.user.utils.Constants.COMMA;

@Configuration
@EnableConfigurationProperties(value = {KeyCloakUserService.KeyCloakIdpProperties.class})
//@EnableBatchProcessing
public class UserBatchConfiguration extends CommandLineJobRunner {

    private static final Logger logger = LoggerFactory.getLogger(UserBatchConfiguration.class);

    @Value("${user.batch.load.chunk.size}")
    Integer chunkSize;

    @Bean("defaultUserLoadUserEntityFunction")
    Function<UserLoad, UserEntity> defaultUserLoadUserEntityFunction() {
        return BatchUtil.defaultUserLoadUserEntityFunction();
    }

    @Bean
    IdempotentConsumer idempotentConsumer(StringRedisTemplate redisTemplate) {
        IdempotentConsumer idempotentConsumer = new IdempotentConsumer(redisTemplate, "user:entities");
        return idempotentConsumer;
    }

    @Bean
    UserEntityItemWriter userEntityItemWriter(UserRepository userRepository) {
        UserEntityItemWriter writer = new UserEntityItemWriter(userRepository);
        return writer;
    }

    @Bean
    CompositeItemWriter<UserEntity> userCompositeItemWriter(UserEntityItemWriter userEntityItemWriter) {
        List<ItemWriter<? super UserEntity>> delegates = new ArrayList<>();
        delegates.add(userEntityItemWriter);
        CompositeItemWriter<UserEntity> compositeItemWriter = new CompositeItemWriter<UserEntity>();
        compositeItemWriter.setDelegates(delegates);
        return compositeItemWriter;
    }

    /*
    https://docs.spring.io/spring-batch/reference/common-patterns.html#passingDataToFutureSteps
     */
    @Bean
    public ExecutionContextPromotionListener promotionListener() {
        ExecutionContextPromotionListener listener = new ExecutionContextPromotionListener();
        listener.setKeys(new String[]{PROMOTE_USER_ENTITY_CHUNK_KEY, PROMOTE_USER_LOAD_CHUNK_KEY});
        return listener;
    }


    @Bean
    UserLoadToUserEntityItemProcessor userLoadToUserEntityItemProcessor(IdempotentConsumer idempotentConsumer, UserRepository userRepository) {
        return UserLoadToUserEntityItemProcessor.builder()
                .userRepository(userRepository)
                .idempotentConsumer(idempotentConsumer)
                .uniqueIdFunction(ue -> StringUtils.isNotBlank(ue.getExternalId()) ? ue.getExternalId() : UserLoadToUserEntityItemProcessor.calculateId(ue))
                .build();
    }

    @Bean
    public Step stepUserLoadToUserEntity(UserRepository userRepository,
                                         UserContextRepository userContextRepository,
                                         PlatformTransactionManager transactionManager,
                                         JobRepository jobRepository,
                                         ExecutionContextPromotionListener promotionListener) {

        String name = "user.load.to.user.entity";

        return new StepBuilder(name, jobRepository)
                .tasklet(UserLoadToEntityTasklet.builder()
                        .userContextRepository(userContextRepository)
                        .userRepository(userRepository)
                        .userLoadUserEntityFunction(BatchUtil.defaultUserLoadUserEntityFunction())
                        .userLoadUserContextEntityFunction(BatchUtil.defaultUserLoadUserContextEntityFunction())
                        .build(), transactionManager)

                .listener(promotionListener)
                .build();
    }


    @Bean
    public Step stepUserLoadToIdProvider(KeyCloakUserService keyCloakUserService,
                                         PlatformTransactionManager transactionManager,
                                         JobRepository jobRepository) {

        String name = "user.load.to.id.provider";

        return new StepBuilder(name, jobRepository)
                .tasklet(keyCloakUserService, transactionManager)
                .build();
    }

    @Bean
    UserLoadPromotionItemWriter userLoadPromotionItemWriter(){
        return UserLoadPromotionItemWriter.builder()
                .build();
    }

    @Bean
    UsersFromRequestPayloadBatchJobFactory inMemoryBatchJobFactory(PlatformTransactionManager transactionManager,
                                                                   JobRepository jobRepository,
                                                                   UserLoadPromotionItemWriter userLoadPromotionItemWriter,
                                                                   ExecutionContextPromotionListener promotionListener,
                                                                   Step stepUserLoadToIdProvider,
                                                                   Step stepUserLoadToUserEntity) {
        return UsersFromRequestPayloadBatchJobFactory.builder()
                .jobRepository(jobRepository)
                .transactionManager(transactionManager)
                .promotionListener(promotionListener)
                .stepUserLoadToUserEntity(stepUserLoadToUserEntity)
                .stepUserLoadToIdProvider(stepUserLoadToIdProvider)
                .chunkSize(chunkSize)
                .stepName("read.from.batch.request")
                .userLoadPromotionItemWriter(userLoadPromotionItemWriter)
                .build();
    }



    /*
    @Bean
    public JobLauncher aysncJobLauncher() {
        final SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
        jobLauncher.setJobRepository(jobRepository);
        final SimpleAsyncTaskExecutor simpleAsyncTaskExecutor = new SimpleAsyncTaskExecutor();
        simpleAsyncTaskExecutor.setConcurrencyLimit(1);
        jobLauncher.setTaskExecutor(simpleAsyncTaskExecutor);
        return jobLauncher;
    }

     */


    @ConditionalOnProperty(prefix = "user.batch.load", name = "name", havingValue = "array", matchIfMissing = false)
    @Configuration
    public static class StringArrayLoadConfig {
        @Autowired
        Environment env;

        @Value("${user.batch.load.chunk.size}")
        Integer chunkSize;

        @Value("${key.generator.salt}")
        String keyGeneratorSalt;

        @Value("${key.generator.password}")
        String keyGeneratorPassword;

        @Bean
        LineMapper<String[]> lineMapper() {
            DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer(COMMA);
            DefaultLineMapper<String[]> lineMapper = new DefaultLineMapper<>();
            lineMapper.setLineTokenizer(lineTokenizer);
            lineMapper.setFieldSetMapper(new ArrayFieldSetMapper());
            return lineMapper;
        }

        @Bean
        public FlatFileItemReader<String[]> reader(
                LineMapper<String[]> lineMapper, Resource sourceResource) {
            FlatFileItemReader itemReader = new FlatFileItemReader<String[]>();
            itemReader.setLineMapper(lineMapper);
            itemReader.setResource(sourceResource);
            itemReader.setLinesToSkip(1);
            return itemReader;
        }

        @Bean
        Supplier<String> keyGenerator() {
            return new Supplier<String>() {
                @Override
                public String get() {
                    String salt = "pepper";
                    String password = "fakePassword";
                    String secretKey = BatchUtil.generateSecretKeyString(keyGeneratorPassword, keyGeneratorSalt);
                    return secretKey;
                }
            };
        }

        @Bean
        StringArrayToUserLoadItemProcessor processorWriter(IdempotentConsumer idempotentConsumer,
                                                           Supplier<String> keyGenerator){
            return StringArrayToUserLoadItemProcessor.builder()
                    .idempotentConsumer(idempotentConsumer)
                    .uniqueIdFunction(userEntity -> userEntity.getEmail())
                    .passwordSupplier(keyGenerator)
                    .build();
        }

        @Bean
        public Step stepStringArrayToUserLoad(StringArrayToUserLoadItemProcessor processorWriter,
                                              FlatFileItemReader<String[]> reader,
                                              PlatformTransactionManager transactionManager,
                                              JobRepository jobRepository,
                                              ExecutionContextPromotionListener promotionListener) {

            String name = "csv.file.to.redis";
            return new StepBuilder(name, jobRepository)
                    .<String[], UserLoad>chunk(chunkSize, transactionManager)
                    .reader(reader)
                    .processor(processorWriter)
                    .writer(processorWriter)
                    .listener(promotionListener)
                    .build();
        }


        @Bean
        UserEntityListItemReader userEntityListItemReader() {
            return new UserEntityListItemReader();
        }

        @Bean("csvJob")
        public Job csvJob(JobRepository jobRepository, Step stepStringArrayToUserLoad,
                          Step stepUserLoadToIdProvider,
                          Step stepUserLoadToUserEntity) {
            return new JobBuilder("import.to.string.array.job", jobRepository)
                    .start(stepStringArrayToUserLoad)
                    .next(stepUserLoadToUserEntity)
                    .next(stepUserLoadToIdProvider)
                    //.next()
                   // .listener()
                    .build();
        }
    }

    @ConditionalOnProperty(prefix = "user.batch.load", name = "env", havingValue = "local", matchIfMissing = true)
    @Configuration
    public static class EnvLoadConfig {
        @Bean
        WritableResource errorResource(Environment env) throws MalformedURLException {
            FileUrlResource resource = new FileUrlResource(env.getProperty("user.batch.error.resource"));
            return resource;
        }


        @Bean
        WritableResource archiveResource(Environment env) throws MalformedURLException {
            FileUrlResource resource = new FileUrlResource(env.getProperty("user.batch.archive.resource"));
            return resource;
        }

        @Bean
        WritableResource sourceResource(Environment env) throws MalformedURLException {
            FileUrlResource resource = new FileUrlResource(env.getProperty("user.batch.source.resource"));
            return resource;
        }
    }

    @Configuration
    public static class BatchJobServiceConfig {

        @Bean
        BatchJobService batchJobService(Job csvJob, UsersFromRequestPayloadBatchJobFactory usersFromRequestPayloadBatchJobFactory,
                                        JobLauncher jobLauncher, JobRepository jobRepository) {
            return BatchJobService.builder()
                    .jobLauncher(jobLauncher)
                    .csvJob(csvJob)
                    .usersFromRequestPayloadBatchJobFactory(usersFromRequestPayloadBatchJobFactory)
                    .jobRepository(jobRepository)
                    .build();
        }

        @Bean
        ServiceAccountLoader serviceAccountLoader(BatchJobService batchJobService){
            return ServiceAccountLoader.builder()
                    .batchJobService(batchJobService)
                    .build();
        }

        @Bean
        UserDelegate UserDelegate(BatchJobService batchJobService) {
            return UserDelegate.builder()
                    .batchJobService(batchJobService)
                    .build();
        }

    }


}
