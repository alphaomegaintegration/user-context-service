package com.alpha.omega.user.batch;

import com.alpha.omega.user.repository.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;

import java.util.function.Function;
import java.util.function.Supplier;

import static com.alpha.omega.user.batch.BatchConstants.PROMOTE_USER_ENTITY_CHUNK_KEY;
import static com.alpha.omega.user.batch.BatchConstants.PROMOTE_USER_LOAD_CHUNK_KEY;
import static com.alpha.omega.user.batch.BatchUtil.getStringArrayValue;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StringArrayToUserLoadItemProcessor implements ItemProcessor<String[], UserLoad>, ItemWriter<UserLoad>,
        StepExecutionListener {

    private static final Logger logger = LoggerFactory.getLogger(StringArrayToUserLoadItemProcessor.class);
    private Function<String[], UserLoad> stringArrayToUserLoadFunction;
    IdempotentConsumer idempotentConsumer;
    Function<UserLoad, String> uniqueIdFunction;
    Supplier<String> passwordSupplier;
    private StepExecution stepExecution;

    @Override
    public void beforeStep(StepExecution stepExecution) {

        if (passwordSupplier == null){
            throw new UserBatchException("passwordSupplier cannot be null in StringArrayToUserEntityItemProcessor");
        }

        if (stringArrayToUserLoadFunction == null){
            stringArrayToUserLoadFunction = new StringArrayToUserLoadFunction(passwordSupplier);
        }
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return StepExecutionListener.super.afterStep(stepExecution);
    }

    @BeforeStep
    public void saveStepExecution(StepExecution stepExecution) {
        this.stepExecution = stepExecution;
    }

    @Override
    public UserLoad process(String[] item) throws Exception {

        UserLoad userLoad = stringArrayToUserLoadFunction.apply(item);

        return userLoad;
    }

    @Override
    public void write(Chunk<? extends UserLoad> chunk) throws Exception {
        ExecutionContext stepContext = this.stepExecution.getExecutionContext();
        stepContext.put(PROMOTE_USER_LOAD_CHUNK_KEY, chunk.getItems());
    }

    public static class StringArrayToUserLoadFunction implements Function<String[], UserLoad>{

        Supplier<String> passwordSupplier;

        public StringArrayToUserLoadFunction(Supplier<String> passwordSupplier) {
            this.passwordSupplier = passwordSupplier;
        }

        @Override
        public UserLoad apply(String[] csvLine) {

            return UserLoad.builder()
                    .first(getStringArrayValue(csvLine, 1))
                    .last(getStringArrayValue(csvLine, 2))
                    .city(getStringArrayValue(csvLine, 5))
                    .state(getStringArrayValue(csvLine, 6))
                    .postcode(getStringArrayValue(csvLine, 8))
                    .email(getStringArrayValue(csvLine, 13))
                    .country(getStringArrayValue(csvLine, 7))
                    .latitude(getStringArrayValue(csvLine, 9))
                    .longitude(getStringArrayValue(csvLine, 10))
                    .description(getStringArrayValue(csvLine, 12))
                    .externalId(getStringArrayValue(csvLine, 16))
                    .contextId(getStringArrayValue(csvLine, 14))
                    .role(getStringArrayValue(csvLine, 15))
                    .password(passwordSupplier.get())
                    .build();
        }



        /*
0        name.title,
1 name.first,
2 name.last,
3 location.street.number,
4 location.street.name,
5 location.city,
6 location.state,
7 location.country,
8 location.postcode,
9 location.coordinates.latitude,
10 location.coordinates.longitude,
11 location.timezone.offset,
12 location.timezone.description,
13 email
         */
    }

}
