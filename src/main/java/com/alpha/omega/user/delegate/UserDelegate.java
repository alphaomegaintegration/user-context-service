package com.alpha.omega.user.delegate;

import com.alpha.omega.user.batch.BatchJobService;
import com.alpha.omega.user.batch.BatchUserRequest;
import com.alpha.omega.user.batch.BatchUserResponse;
import com.alpha.omega.user.batch.UserLoad;
import com.alpha.omega.user.server.UsersApiDelegate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDelegate implements UsersApiDelegate {
    private static final Logger logger = LoggerFactory.getLogger(UserDelegate.class);

    private BatchJobService batchJobService;

    @Override
    public Mono<ResponseEntity<BatchUserResponse>> createUsers(Mono<BatchUserRequest> body, ServerWebExchange exchange) {
        return Mono.from(body)
                .publishOn(Schedulers.boundedElastic())
                .doOnNext(batchUserRequest -> logger.debug("Got job request => {}",batchUserRequest))
                .map(request -> batchJobService.startJob(request))
                .map(response -> ResponseEntity.ok(response));
    }

    @Override
    public Mono<ResponseEntity<BatchUserResponse>> getBatchJobStatus(String jobName, String correlationId, ServerWebExchange exchange) {
        return Mono.just(BatchUserRequest.builder()
                        .correlationId(correlationId)
                        .jobName(jobName)
                        .build())
                .publishOn(Schedulers.boundedElastic())
                .doOnNext(batchUserRequest -> logger.debug("Got job request => {}",batchUserRequest))
                .map(request -> batchJobService.getJobExecution(request))
                .map(response -> ResponseEntity.ok(response));
    }
}
