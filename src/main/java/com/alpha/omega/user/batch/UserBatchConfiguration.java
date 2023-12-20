package com.alpha.omega.user.batch;

import com.alpha.omega.user.repository.UserEntity;
import com.alpha.omega.user.repository.UserRepository;
import com.alpha.omega.user.utils.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.CommandLineJobRunner;
import org.springframework.batch.core.listener.ExecutionContextPromotionListener;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.*;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.ArrayFieldSetMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.batch.item.support.IteratorItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.couchbase.CouchbaseProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import javax.crypto.SecretKey;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static com.alpha.omega.user.batch.BatchConstants.PROMOTE_USER_ENTITY_CHUNK_KEY;
import static com.alpha.omega.user.utils.Constants.COMMA;

@Configuration
@EnableConfigurationProperties(value = {KeyCloakUserItemWriter.KeyCloakIdpProperties.class})
@EnableBatchProcessing
public class UserBatchConfiguration extends CommandLineJobRunner {

    private static final Logger logger = LoggerFactory.getLogger(UserBatchConfiguration.class);

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
        listener.setKeys(new String[] {PROMOTE_USER_ENTITY_CHUNK_KEY});
        return listener;
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


    @ConditionalOnProperty(prefix="user.batch.load", name="name", havingValue="array", matchIfMissing = false)
    @Configuration
    public static class StringArrayLoadConfig{

        @Bean
        LineMapper<String[]> lineMapper(){
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
        Supplier<String> keyGenerator(){
            return new Supplier<String>() {
                @Override
                public String get() {
                    String salt  = "pepper";
                    String password = "fakePassword";
                    String secretKey = BatchUtil.generateSecretKeyString(password, salt);
                    return secretKey;
                }
            };
        }

        @Bean
        public Step stepStringArrayLoad(IdempotentConsumer idempotentConsumer,
                                        FlatFileItemReader<String[]> reader,
                                 CompositeItemWriter<UserEntity> userCompositeItemWriter,
                                        UserEntityItemWriter userEntityItemWriter,
                                 PlatformTransactionManager transactionManager,
                                 JobRepository jobRepository,
                                        Supplier<String> keyGenerator,
                                        ExecutionContextPromotionListener promotionListener) {

            String name = "csv.file.to.redis";
            return new StepBuilder(name, jobRepository)
                    .<String[], UserEntity>chunk(100, transactionManager)
                    .reader(reader)
                    .processor(StringArrayToUserEntityItemProcessor.builder()
                            .idempotentConsumer(idempotentConsumer)
                            .uniqueIdFunction(userEntity -> userEntity.getEmail())
                            .passwordSupplier(keyGenerator)
                            .build())
                    .writer(userEntityItemWriter)
                    .listener(promotionListener)
                    .build();
        }

        @Bean
        KeyCloakUserItemWriter keyCloakUserItemWriter(KeyCloakUserItemWriter.KeyCloakIdpProperties keyCloakIdpProperties,
                                                      ObjectMapper objectMapper){
            return KeyCloakUserItemWriter.builder()
                    .keyCloakIdpProperties(keyCloakIdpProperties)
                    .objectMapper(objectMapper)
                    .build();
        }

        @Bean
        UserEntityListItemReader userEntityListItemReader(){
            return new UserEntityListItemReader();
        }

        @Bean
        public Job importToStringArrayJob(JobRepository jobRepository, Step stepStringArrayLoad,
                                          PlatformTransactionManager transactionManager,
                                          KeyCloakUserItemWriter keyCloakUserItemWriter,
                                          UserEntityListItemReader userEntityListItemReader) {
            return new JobBuilder("importToStringArrayJob", jobRepository)
                    .start(stepStringArrayLoad)
                    .next(new StepBuilder("write.to.idprovider", jobRepository)
                            .<UserEntity, UserEntity>chunk(100, transactionManager)

                                    .reader(userEntityListItemReader)
//                            .processor(new ItemProcessor<UserEntity, UserEntity>() {
//                                @Override
//                                public UserEntity process(UserEntity item) throws Exception {
//                                    return null;
//                                }
//                            })
                            .writer(keyCloakUserItemWriter)
                            .build())
                    //.listener()
                    .build();
        }
    }

    @ConditionalOnProperty(prefix="user.batch.load", name="env", havingValue="local", matchIfMissing = true)
    @Configuration
    public static class EnvLoadConfig{
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



    @ConditionalOnProperty(prefix="user.batch.load", name="name", havingValue="userload", matchIfMissing = true)
    @Configuration
    public static class UserLoadConfig{

        @Bean
        String[] csvHeaderNames(){
            return new String[]{
                    "title", "first", "last", "streetNumber", "streetName", "city", "state",
                    "country", "postcode", "latitude", "longitude", "offset", "description", "email"
            };
        }

        @Bean
        public FlatFileItemReader<UserLoad> reader(
                LineMapper<UserLoad> lineMapper, Resource sourceResource) {
            var itemReader = new FlatFileItemReader<UserLoad>();
            itemReader.setLineMapper(lineMapper);
            itemReader.setResource(sourceResource);
            itemReader.setLinesToSkip(1);
            return itemReader;
        }

        @Bean
        public DefaultLineMapper<UserLoad> lineMapper(LineTokenizer tokenizer,
                                                      FieldSetMapper<UserLoad> fieldSetMapper) {
            var lineMapper = new DefaultLineMapper<UserLoad>();
            lineMapper.setLineTokenizer(tokenizer);
            lineMapper.setFieldSetMapper(fieldSetMapper);
            return lineMapper;
        }

        @Bean
        public BeanWrapperFieldSetMapper<UserLoad> fieldSetMapper() {
            var fieldSetMapper = new BeanWrapperFieldSetMapper<UserLoad>();
            fieldSetMapper.setTargetType(UserLoad.class);
            return fieldSetMapper;
        }

        @Bean
        public DelimitedLineTokenizer tokenizer(String[] csvHeaderNames) {
            var tokenizer = new DelimitedLineTokenizer();
            tokenizer.setStrict(false);
            tokenizer.setQuoteCharacter(Constants.DOUBLE_QUOTES);
            tokenizer.setDelimiter(COMMA);
            tokenizer.setNames(csvHeaderNames);
            return tokenizer;
        }

        @Bean
        UserLoadToUserEntityItemLifecycleListener userEntityItemLifecycleListener(UserRepository userRepository,
                                                                                  WritableResource errorResource,
                                                                                  WritableResource archiveResource,
                                                                                  WritableResource sourceResource) {

            return UserLoadToUserEntityItemLifecycleListener.builder()
                    .userRepository(userRepository)
                    .errorResource(errorResource)
                    .archiveResource(archiveResource)
                    .sourceResource(sourceResource)
                    .build();
        }

        @Bean
        UserLoadToUserEntityItemProcessor userEntityItemProcessor(IdempotentConsumer idempotentConsumer, UserRepository userRepository) {
            return UserLoadToUserEntityItemProcessor.builder()
                    .userRepository(userRepository)
                    .idempotentConsumer(idempotentConsumer)
                    .uniqueIdFunction(ue -> StringUtils.isNotBlank(ue.getExternalId()) ? ue.getExternalId() : UserLoadToUserEntityItemProcessor.calculateId(ue))
                    .build();
        }

        @Bean
        public Step stepUserLoad(ItemReader<UserLoad> reader,
                                 UserLoadToUserEntityItemProcessor userLoadToUserEntityItemProcessor,
                                 CompositeItemWriter<UserEntity> userCompositeItemWriter,
                                 UserEntityItemWriter userEntityItemWriter,
                                 PlatformTransactionManager transactionManager,
                                 JobRepository jobRepository,
                                 UserLoadToUserEntityItemLifecycleListener listener) {

            String name = "INSERT CSV RECORDS To DB Step";
            return new StepBuilder(name, jobRepository)
                    .<UserLoad, UserEntity>chunk(100, transactionManager)
                    .reader(reader)
                    .processor(userLoadToUserEntityItemProcessor)
                    .writer(userEntityItemWriter)
                    .listener((StepExecutionListener) listener)
                    .build();
        }



        @Bean
        public Job importUserLoadJob(JobRepository jobRepository, Step stepUserLoad,
                                     UserLoadToUserEntityItemLifecycleListener listener) {
            return new JobBuilder("importUserLoadJob", jobRepository)
                    .listener(listener)
                    .start(stepUserLoad)
                    .build();
        }
    }



}
