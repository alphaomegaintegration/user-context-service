package com.alpha.omega.user.batch;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;

import static com.alpha.omega.user.batch.BatchConstants.PROMOTE_USER_LOAD_CHUNK_KEY;
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLoadPromotionItemWriter implements ItemWriter<UserLoad>  {


    private StepExecution stepExecution;

    @BeforeStep
    public void saveStepExecution(StepExecution stepExecution) {
        this.stepExecution = stepExecution;
    }

    @Override
    public void write(Chunk<? extends UserLoad> chunk) throws Exception {
        ExecutionContext stepContext = this.stepExecution.getExecutionContext();
        stepContext.put(PROMOTE_USER_LOAD_CHUNK_KEY, chunk.getItems());
    }
}
