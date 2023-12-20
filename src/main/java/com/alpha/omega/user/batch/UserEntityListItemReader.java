package com.alpha.omega.user.batch;

import com.alpha.omega.user.repository.UserEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.*;
import org.springframework.batch.item.support.IteratorItemReader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

import static com.alpha.omega.user.batch.BatchConstants.PROMOTE_USER_ENTITY_CHUNK_KEY;

public class UserEntityListItemReader implements ItemReader<UserEntity>, StepExecutionListener {
    private static final Logger logger = LoggerFactory.getLogger(UserEntityListItemReader.class);


    private List<UserEntity> userEntities;
    private IteratorItemReader<UserEntity> iteratorItemReader;

    @Override
    public void beforeStep(StepExecution stepExecution) {
        JobExecution jobExecution = stepExecution.getJobExecution();
        ExecutionContext jobContext = jobExecution.getExecutionContext();
        this.userEntities = (List<UserEntity>)jobContext.get(PROMOTE_USER_ENTITY_CHUNK_KEY);
        this.iteratorItemReader = new IteratorItemReader<>(this.userEntities);
    }

    @Override
    public UserEntity read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        return iteratorItemReader.read();
    }
}
