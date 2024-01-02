package com.alpha.omega.user.batch;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.listener.ExecutionContextPromotionListener;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.function.FunctionItemProcessor;
import org.springframework.batch.item.support.IteratorItemReader;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.function.Function;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsersFromRequestPayloadBatchJobFactory {

    JobRepository jobRepository;
    PlatformTransactionManager transactionManager;

    Step stepUserLoadToUserEntity;
    Step stepUserLoadToIdProvider;
    UserLoadPromotionItemWriter userLoadPromotionItemWriter;
    ExecutionContextPromotionListener promotionListener;

    String stepName = "read.from.batch.request";
    Integer chunkSize = 100;

    Job createJobFromRequest(BatchUserRequest batchUserRequest){
        Step usersFromRequestPayloadStep = usersFromRequestPayload(batchUserRequest);
        return new JobBuilder("users.from.request.payload.user.load.job", jobRepository)
                .listener(promotionListener)
                .start(usersFromRequestPayloadStep)
                .next(stepUserLoadToUserEntity)
                .next(stepUserLoadToIdProvider)
                .build();
    }

    private Step usersFromRequestPayload(BatchUserRequest batchUserRequest) {

        if (batchUserRequest.getUsers() == null || batchUserRequest.getUsers().isEmpty()){
            throw new UserBatchException("Cannot create Step for UsersFromRequestPayloadBatchJobFactory!");
        }

        IteratorItemReader<UserLoad> itemReader = new IteratorItemReader<>(batchUserRequest.getUsers());

        return new StepBuilder(stepName, jobRepository)
                .<UserLoad, UserLoad>chunk(chunkSize, transactionManager)
                .listener(promotionListener)
                .reader(itemReader)
                .processor(new FunctionItemProcessor(Function.identity()))
                .writer(userLoadPromotionItemWriter)
                .build();
    }

}
