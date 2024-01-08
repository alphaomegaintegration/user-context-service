package com.alpha.omega.user.service;

import com.alpha.omega.user.batch.BatchUserRequest;
import com.alpha.omega.user.batch.UserBatchException;
import com.alpha.omega.user.batch.UserLoad;
import com.alpha.omega.user.batch.UsersFromRequestPayloadBatchJobFactory;
import com.alpha.omega.user.model.Context;
import com.alpha.omega.user.utils.Constants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.function.FunctionItemProcessor;
import org.springframework.batch.item.support.IteratorItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.time.Clock;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContextLoader {

    private static final Logger logger = LoggerFactory.getLogger(ContextLoader.class);
    final public static String CONTEXT_LOADER_STEP_NAME = "context.loader.step";
    final public static String CONTEXT_LOADER_JOB_NAME = "context.loader.job";
    ContextService contextService;
    @Builder.Default
    String contextsToLoad = "classpath:contexts/user-context-service.json,classpath:contexts/unhrc-bims-context.json";
    String name = "user.load.to.id.provider";
    JobLauncher jobLauncher;
    JobRepository jobRepository;
    PlatformTransactionManager transactionManager;
    ApplicationEventPublisher applicationEventPublisher;

    public Step createContextLoaderStep() {
        final SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();
        taskExecutor.setThreadNamePrefix(CONTEXT_LOADER_JOB_NAME);

        ContextLoaderTasklet contextLoaderTasklet = ContextLoaderTasklet.builder()
                .contextService(contextService)
                .taskExecutor(taskExecutor)
                .contextsToLoad(contextsToLoad)
                .applicationEventPublisher(applicationEventPublisher)
                .build();

        contextLoaderTasklet.init();

        return new StepBuilder(CONTEXT_LOADER_STEP_NAME, jobRepository)
                .tasklet(contextLoaderTasklet, transactionManager)
                .taskExecutor(taskExecutor)
                .startLimit(1).throttleLimit(1)
                .build();
    }

    public Job createContextLoaderJob() {
        return new JobBuilder(CONTEXT_LOADER_JOB_NAME, jobRepository)
                .start(createContextLoaderStep())
                .build();
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContextLoaderTasklet implements Tasklet {

        ContextService contextService;
        Scheduler scheduler;
        TaskExecutor taskExecutor;
        String contextsToLoad;
        ApplicationEventPublisher applicationEventPublisher;
        final AtomicLong counter = new AtomicLong();

        public ContextLoaderTasklet(ContextService contextService, TaskExecutor taskExecutor, String contextsToLoad) {
            this.contextService = contextService;
            this.taskExecutor = taskExecutor;
            this.contextsToLoad = contextsToLoad;
            this.scheduler = Schedulers.fromExecutor(taskExecutor);
        }

        @Override
        public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
            logger.debug("How many times is ContextLoaderTasklet called? {}",counter.incrementAndGet());
            contextService.loadContexts(scheduler, contextsToLoad)
                    .subscribe(ctx -> {
                        logger.debug("Created context {} ",ctx);
                        applicationEventPublisher.publishEvent(new ContextLoadedEvent(this, ctx));
                    });
            return RepeatStatus.FINISHED;
        }

        public void init(){
            this.scheduler = Schedulers.fromExecutor(this.taskExecutor);
        }
    }

    public static class ContextLoaderRunner implements ApplicationListener<ApplicationReadyEvent> {

        JobLauncher jobLauncher;
        Job job;
        String jobStr;
        final static AtomicLong counter = new AtomicLong();

        public ContextLoaderRunner(JobLauncher jobLauncher, Job job, String jobStr) {
            this.jobLauncher = jobLauncher;
            this.job = job;
            this.jobStr = jobStr;
        }

        @Override
        public void onApplicationEvent(ApplicationReadyEvent event) {
            logger.debug("How many times is ApplicationListener<ApplicationReadyEvent> called? {}",counter.incrementAndGet());
            try {
                Map<String, JobParameter<?>> jobsMap = new HashMap<>();
                jobsMap.put(Constants.CORRELATION_ID, new JobParameter<>(jobStr, String.class));
                JobExecution jobExecution = jobLauncher.run(job, new JobParameters(jobsMap));
            } catch (JobExecutionAlreadyRunningException e) {
                throw new RuntimeException(e);
            } catch (JobRestartException e) {
                throw new RuntimeException(e);
            } catch (JobInstanceAlreadyCompleteException e) {
                throw new RuntimeException(e);
            } catch (JobParametersInvalidException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Getter
    public static class ContextLoadedEvent extends ApplicationEvent{

        Context context;

        public ContextLoadedEvent(Object source, Context context) {
            super(source);
            this.context = context;
        }

        public ContextLoadedEvent(Object source, Clock clock, Context context) {
            super(source, clock);
            this.context = context;
        }

    }


}
