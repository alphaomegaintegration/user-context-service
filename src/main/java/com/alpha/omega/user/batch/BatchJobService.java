package com.alpha.omega.user.batch;

import com.alpha.omega.user.utils.Constants;
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

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchJobService {

    private static final Logger logger = LoggerFactory.getLogger(BatchJobService.class);

    JobRepository jobRepository;
    JobLauncher jobLauncher;
    Job csvJob;
    UsersFromRequestPayloadBatchJobFactory usersFromRequestPayloadBatchJobFactory;

    Function<JobExecution, BatchUserResponse> convertToBatchUserResponse(){
        return jobExecution -> {
            return BatchUserResponse.builder()
                    //.jobExecution(jobExecution)
                    .jobInstance(jobExecution.getJobInstance())
                    .createTime(jobExecution.getCreateTime())
                    .executionContext(jobExecution.getExecutionContext())
                    .exitStatus(jobExecution.getExitStatus())
                    .jobName(jobExecution.getJobInstance().getJobName())
                    .build();
        };
    }

    public BatchUserResponse startJob(BatchUserRequest batchUserRequest){
        Map<String, JobParameter<?>> jobsMap = batchUserRequest.getJobParameters().entrySet().stream()
                .collect(Collectors.toMap(entry -> entry.getKey(), entry -> new JobParameter<>(entry.getValue(), String.class)));
;        JobExecution jobExecution = null;
        try {
            Job job = extractJobFromRequest(batchUserRequest);
            jobsMap.put(Constants.CORRELATION_ID, new JobParameter<>(batchUserRequest.getCorrelationId(), String.class));
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
        logger.debug("JobExecutionId => {}",jobExecution.getJobId());

        return convertToBatchUserResponse().apply(jobExecution);
    }

    private Job extractJobFromRequest(BatchUserRequest batchUserRequest) {
        Job job = null;
        if (batchUserRequest.getUsers() == null || batchUserRequest.getUsers().isEmpty()){
            job = csvJob;
            validateCsvJobRequest(batchUserRequest);
        } else {
            job = usersFromRequestPayloadBatchJobFactory.createJobFromRequest(batchUserRequest);
        }
        return job;
    }

    void validateCsvJobRequest(BatchUserRequest batchUserRequest) {

    }

    public String getJobStatus(BatchUserRequest batchUserRequest){
        BatchUserResponse batchUserResponse = this.getJobExecution(batchUserRequest);
        JobExecution jobExecution = batchUserResponse.getJobExecution();
        return jobExecution != null ? jobExecution.getExitStatus().getExitCode() : ExitStatus.UNKNOWN.getExitCode();
    }

    public BatchUserResponse getJobExecution(BatchUserRequest batchUserRequest){
        Map<String, JobParameter<?>> jobsMap = batchUserRequest.getJobParameters().entrySet().stream()
                .collect(Collectors.toMap(entry -> entry.getKey(), entry -> new JobParameter<>(entry.getValue(), String.class)));
        Job job = extractJobFromRequest(batchUserRequest);
        jobsMap.put(Constants.CORRELATION_ID, new JobParameter<>(batchUserRequest.getCorrelationId(), String.class));
        JobExecution jobExecution = jobRepository.getLastJobExecution(job.getName(), new JobParameters(jobsMap));
        return convertToBatchUserResponse().apply(jobExecution);
    }

}
