package com.alpha.omega.user.batch;

import com.alpha.omega.user.repository.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.core.io.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StringArrayToUserEntityTasklet implements Tasklet, StepExecutionListener, JobExecutionListener {

 /*
    https://www.baeldung.com/spring-batch-tasklet-chunk
     */

    private static final Logger logger = LoggerFactory.getLogger(StringArrayToUserLoadItemProcessor.class);
    private Function<String[], UserEntity> stringArrayToUserEntityFunction;

    private Resource inputResource;
    private BatchFileHelper fileHelper;

    @Override
    public void beforeJob(JobExecution jobExecution) {
        JobExecutionListener.super.beforeJob(jobExecution);
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        JobExecutionListener.super.afterJob(jobExecution);
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        StepExecutionListener.super.beforeStep(stepExecution);
        fileHelper = BatchFileHelper.builder()
                .inputResource(inputResource)
                .build();
        logger.debug("Initializing BatchFileHelper in beforeStep");
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        fileHelper.closeReader();
        logger.debug("BatchFileHelper.closeReader() in afterStep");
        return ExitStatus.COMPLETED;
    }


    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        //chunkContext.
        List<String[]> lines = new ArrayList<>();
        String [] line = fileHelper.readLine();
        logger.debug(" Got line ");
        return null;
    }
}
