package com.alpha.omega.user.batch;

import com.alpha.omega.user.repository.UserEntity;
import com.alpha.omega.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemProcessor;

import java.util.function.Function;
import java.util.function.Supplier;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StringArrayToUserEntityItemProcessor implements ItemProcessor<String[], UserEntity>, StepExecutionListener {

    private static final Logger logger = LoggerFactory.getLogger(StringArrayToUserEntityItemProcessor.class);
    private Function<String[], UserEntity> stringArrayToUserEntityFunction;
    IdempotentConsumer idempotentConsumer;
    Function<UserEntity, String> uniqueIdFunction;
    Supplier<String> passwordSupplier;

    @Override
    public void beforeStep(StepExecution stepExecution) {

        if (passwordSupplier == null){
            throw new UserBatchException("passwordSupplier cannot be null in StringArrayToUserEntityItemProcessor");
        }

        if (stringArrayToUserEntityFunction == null){
            stringArrayToUserEntityFunction = new StringArrayToUserEntityFunction(passwordSupplier);
        }
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return StepExecutionListener.super.afterStep(stepExecution);
    }

    @Override
    public UserEntity process(String[] item) throws Exception {

        UserEntity userEntity = stringArrayToUserEntityFunction.apply(item);
        String uniqueId = uniqueIdFunction.apply(userEntity);
        boolean isProcessed = idempotentConsumer.isProcessed(uniqueId);
        logger.info("Checking uniqueId => {} is processed => {} with password => {}",uniqueId,isProcessed,userEntity.getPassword());
        if (isProcessed){
            return null;
        }
        Long consumableResult = idempotentConsumer.consume(uniqueId);
        return userEntity;
    }

    public static class StringArrayToUserEntityFunction implements Function<String[], UserEntity>{

        Supplier<String> passwordSupplier;

        public StringArrayToUserEntityFunction(Supplier<String> passwordSupplier) {
            this.passwordSupplier = passwordSupplier;
        }

        @Override
        public UserEntity apply(String[] csvLine) {
            UserEntity userEntity = new UserEntity();
            userEntity.setEmail(csvLine[13]);
            userEntity.setLastName(csvLine[2]);
            userEntity.setFirstName(csvLine[1]);
            userEntity.setMailCode(csvLine[8]);
            userEntity.setCountry(csvLine[7]);
            userEntity.setPassword(passwordSupplier.get());
            return userEntity;
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
