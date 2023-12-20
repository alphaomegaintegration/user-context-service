package com.alpha.omega.user.batch;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;

import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchJobService {

    private static final Logger logger = LoggerFactory.getLogger(BatchJobService.class);

    JobRepository jobRepository;
    JobLauncher jobLauncher;
    Job job;

    public JobExecution startJob(Map<String, String> jobParams){
        Map<String, JobParameter<?>> jobsMap = jobParams.entrySet().stream()
                .collect(Collectors.toMap(entry -> entry.getKey(), entry -> new JobParameter<>(entry.getValue(), String.class)));
        JobExecution jobExecution = null;
        try {
            jobExecution = jobLauncher.run(job, new JobParameters(jobsMap));
        } catch (JobExecutionAlreadyRunningException e) {
            throw new RuntimeException(e);
        } catch (JobRestartException e) {
            throw new RuntimeException(e);
        } catch (JobInstanceAlreadyCompleteException e) {
            throw new RuntimeException(e);
        } catch (JobParametersInvalidException e) {
            throw new RuntimeException(e);
        }
        return jobExecution;
    }

    public String getJobStatus(Map<String, String> jobParams){
        JobExecution jobExecution = this.getJobExecution(jobParams);
        return jobExecution != null ? jobExecution.getExitStatus().getExitCode() : ExitStatus.UNKNOWN.getExitCode();
    }

    public JobExecution getJobExecution(Map<String, String> jobParams){
        Map<String, JobParameter<?>> jobsMap = jobParams.entrySet().stream()
                .collect(Collectors.toMap(entry -> entry.getKey(), entry -> new JobParameter<>(entry.getValue(), String.class)));
        JobExecution jobExecution = jobRepository.getLastJobExecution(job.getName(), new JobParameters(jobsMap));
        return jobExecution;
    }

}
