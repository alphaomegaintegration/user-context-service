package com.alpha.omega.user.batch;

import com.alpha.omega.user.repository.UserEntity;
import com.alpha.omega.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.repeat.RepeatStatus;

import java.util.function.Function;
import java.util.function.Supplier;

import static com.alpha.omega.user.utils.Constants.COLON;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLoadToUserEntityItemProcessor implements ItemProcessor<UserLoad, UserEntity> {
    private static final Logger logger = LoggerFactory.getLogger(UserLoadToUserEntityItemProcessor.class);

    private UserRepository userRepository;
    IdempotentConsumer idempotentConsumer;
    Function<UserEntity, String> uniqueIdFunction;
    Function<UserLoad, UserEntity> userLoadUserEntityFunction = BatchUtil.defaultUserLoadUserEntityFunction();

    public static final String calculateId(UserEntity userEntity) {
        return new StringBuilder(userEntity.getFirstName())
                .append(COLON)
                .append(userEntity.getLastName())
                .append(COLON)
                .append(userEntity.getMailCode())
                .toString();
    }

    @Override
    public UserEntity process(UserLoad item) throws Exception {
        UserEntity userEntity = userLoadUserEntityFunction.apply(item);
        String uniqueId = uniqueIdFunction.apply(userEntity);
        boolean isProcessed = idempotentConsumer.isProcessed(uniqueId);
        logger.debug("Checking uniqueId => {} is processed => {}",uniqueId,isProcessed);
        if (isProcessed){
            return null;
        }
        Long consumableResult = idempotentConsumer.consume(uniqueId);
        return userEntity;
    }

}
