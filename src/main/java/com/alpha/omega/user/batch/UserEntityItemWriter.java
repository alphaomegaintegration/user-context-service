package com.alpha.omega.user.batch;

import com.alpha.omega.user.repository.UserEntity;
import com.alpha.omega.user.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.CompositeItemWriter;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.stream.Collectors;

import static com.alpha.omega.user.batch.BatchConstants.PROMOTE_USER_ENTITY_CHUNK_KEY;

public class UserEntityItemWriter implements ItemWriter<UserEntity> {


    private static final Logger logger = LoggerFactory.getLogger(UserEntityItemWriter.class);

    private UserRepository userRepository;
    private StepExecution stepExecution;
    private ObjectMapper objectMapper = new ObjectMapper();

    public UserEntityItemWriter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void write(Chunk<? extends UserEntity> chunk) throws Exception {

        List<UserEntity> userEntities = chunk.getItems().stream()
                        .map(userEntity -> {
                            UserEntity ue = userRepository.save(userEntity);
                            logger.debug("UserEntity saved => {}", ue);
                            writeToJsonOut(ue);
                            return ue;
                        }).collect(Collectors.toList());
        ExecutionContext stepContext = this.stepExecution.getExecutionContext();
        stepContext.put(PROMOTE_USER_ENTITY_CHUNK_KEY, userEntities);

    }

    void writeToJsonOut(UserEntity userEntity){
        try {
            logger.debug("{}",objectMapper.writeValueAsString(userEntity));
        } catch (JsonProcessingException e) {
            logger.warn("Could not write to json",e);
        }
    }

    @BeforeStep
    public void saveStepExecution(StepExecution stepExecution) {
        this.stepExecution = stepExecution;
    }
}

