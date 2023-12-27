package com.alpha.omega.user.batch;

import com.alpha.omega.user.repository.UserContextEntity;
import com.alpha.omega.user.repository.UserContextRepository;
import com.alpha.omega.user.repository.UserEntity;
import com.alpha.omega.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.batch.core.*;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.alpha.omega.user.batch.BatchConstants.PROMOTE_USER_LOAD_CHUNK_KEY;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLoadToIdProviderTasklet implements Tasklet, StepExecutionListener, Consumer<UserLoad> {

    private List<UserLoad> userLoadList;
    private Function<UserLoad, UserEntity> userLoadUserEntityFunction = defaultUserLoadUserEntityFunction;
    private Function<UserLoad, UserContextEntity> userLoadUserContextEntityFunction = defaultUserLoadUserContextEntityFunction;
    private UserRepository userRepository;
    private UserContextRepository userContextRepository;


    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        StepExecution stepExecution = chunkContext.getStepContext().getStepExecution();
        JobExecution jobExecution = stepExecution.getJobExecution();
        ExecutionContext jobContext = jobExecution.getExecutionContext();
        List<UserLoad> userLoadList = (List<UserLoad>) jobContext.get(PROMOTE_USER_LOAD_CHUNK_KEY);
        userLoadList.stream().forEach(this::accept);
        return RepeatStatus.FINISHED;
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        StepExecutionListener.super.beforeStep(stepExecution);
        JobExecution jobExecution = stepExecution.getJobExecution();
        ExecutionContext jobContext = jobExecution.getExecutionContext();
        this.userLoadList = (List<UserLoad>) jobContext.get(PROMOTE_USER_LOAD_CHUNK_KEY);
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return StepExecutionListener.super.afterStep(stepExecution);
    }

    @Override
    public void accept(UserLoad userLoad) {
        UserEntity userEntity = userLoadUserEntityFunction.apply(userLoad);
        userRepository.save(userEntity);
        UserContextEntity userContextEntity = userLoadUserContextEntityFunction.apply(userLoad);
        userContextRepository.save(userContextEntity);
    }

    static Function<UserLoad, UserEntity> defaultUserLoadUserEntityFunction = userLoad -> {
        UserEntity userEntity = new UserEntity();
        userEntity.setFirstName(userLoad.getFirst());
        userEntity.setLastName(userLoad.getLast());
        userEntity.setMailCode(userLoad.getPostcode());
        userEntity.setEmail(userLoad.getEmail());
        userEntity.setCountry(userLoad.getCountry());
        return userEntity;
    };

    static Function<UserLoad, UserContextEntity> defaultUserLoadUserContextEntityFunction = userLoad -> {
        UserContextEntity userContextEntity = new UserContextEntity();
        userContextEntity.setContextId(userLoad.getContextId());
        userContextEntity.setUserId(userLoad.getEmail());
        userContextEntity.setRoleId(userLoad.getRole());
        return userContextEntity;
    };

}
