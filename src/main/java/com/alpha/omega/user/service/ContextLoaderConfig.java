package com.alpha.omega.user.service;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.UUID;

@Configuration
public class ContextLoaderConfig {
    @Bean
    ContextLoader contextLoader(PlatformTransactionManager transactionManager,
                                JobRepository jobRepository, JobLauncher jobLauncher,
                                ContextService contextService, ApplicationEventPublisher applicationEventPublisher){
        return ContextLoader.builder()
                .transactionManager(transactionManager)
                .jobRepository(jobRepository)
                .jobLauncher(jobLauncher)
                .contextService(contextService)
                .applicationEventPublisher(applicationEventPublisher)
                .build();
    }

    @Bean("contextLoaderStep")
    Step contextLoaderStep(ContextLoader contextLoader){
        return contextLoader.createContextLoaderStep();
    }

    @Bean("contextLoaderJob")
    Job contextLoaderJob(ContextLoader contextLoader){
        return contextLoader.createContextLoaderJob();
    }

    @Bean
    ContextLoader.ContextLoaderRunner loaderRunner(JobLauncher jobLauncher, Job contextLoaderJob){
        return new ContextLoader.ContextLoaderRunner(jobLauncher, contextLoaderJob, UUID.randomUUID().toString());
    }

}
