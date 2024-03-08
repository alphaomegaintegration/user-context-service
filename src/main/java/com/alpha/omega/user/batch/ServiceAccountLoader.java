package com.alpha.omega.user.batch;

import com.alpha.omega.user.model.Context;
import com.alpha.omega.user.service.ContextLoader;
import com.alpha.omega.user.utils.Constants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.alpha.omega.user.service.ServiceUtils.extractFileAsStringFromResource;
import static com.alpha.omega.user.utils.Constants.COMMA;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceAccountLoader implements ApplicationListener<ContextLoader.ContextLoadedEvent> {

    private static final Logger logger = LoggerFactory.getLogger(ServiceAccountLoader.class);
    BatchJobService batchJobService;
    @Builder.Default
    String saBatchUsers = "classpath:users/user-context-service-sa-batch-job-request.json";
    @Builder.Default
    Boolean changePassword = Boolean.TRUE;
    @Builder.Default
    Boolean loadServiceAccounts = Boolean.TRUE;
    @Builder.Default
    String keyGenerator = UUID.randomUUID().toString();
    @Builder.Default
    ResourceLoader resourceLoader = new DefaultResourceLoader();
    @Builder.Default
    ObjectMapper objectMapper = new ObjectMapper();
    //final static AtomicLong counter = new AtomicLong();


    Function<Optional<String>, Optional<BatchUserRequest>> extractBatchUserRequestFromJson() {
        return json -> {
            Optional<BatchUserRequest> batchUserRequest = Optional.empty();
            if (json.isPresent()) {
                try {
                    batchUserRequest = Optional.of(objectMapper.readValue(json.get(), BatchUserRequest.class));
                } catch (JsonProcessingException e) {
                    logger.warn("Could not extractContextFromJson {}", json.get(), e);
                }
            }
            return batchUserRequest;
        };
    }

    final AtomicBoolean hasRun = new AtomicBoolean(Boolean.FALSE);

    @Override
    public void onApplicationEvent(ContextLoader.ContextLoadedEvent event) {

        final Context context = event.getContext();


        if (loadServiceAccounts) {
            if (!hasRun.get()) {
                try {
                    logger.info("Got ContextLoader.ContextLoadedEvent event........");
                    List<String> usersPaths = Arrays.asList(saBatchUsers.split(COMMA));
                    usersPaths.stream()
                            // NOTE make sure service accounts are associated with the context. Use a name that
                            // contains the context eg. user-context-service-sa-batch-job-request.json. Where  user-context-service
                            // is a context.
                            .filter(usersPath -> usersPath.contains(context.getContextId()))
                            .forEach(userPath -> {
                                Resource resource = resourceLoader.getResource(userPath);
                                Optional<String> fileAsStr = extractFileAsStringFromResource(resource);
                                Optional<BatchUserRequest> batchUserRequest = extractBatchUserRequestFromJson().apply(fileAsStr);
                                processBatchRequest(context, batchUserRequest);
                            });

                    hasRun.set(Boolean.TRUE);
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.warn("Could not load service accounts from " + saBatchUsers, e);
                }
            }
        }

    }

    private void processBatchRequest(Context context, Optional<BatchUserRequest> batchUserRequest) {
        batchUserRequest.ifPresent(request -> {
            String jobStr = UUID.randomUUID().toString();
            request.setCorrelationId(jobStr);
            if (changePassword) {
                logger.info("reset with => {}", keyGenerator);
                List<UserLoad> userLoadList = request.getUsers().stream()
                        .filter(user -> user.getContextId().equals(context.getContextId()))
                        .map(user -> {
                            user.setPassword(keyGenerator);
                            return user;
                        })
                        .collect(Collectors.toList());
                request.setUsers(userLoadList);
            }
            batchJobService.startJob(request);
        });
    }
}
