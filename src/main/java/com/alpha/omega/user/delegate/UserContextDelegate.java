package com.alpha.omega.user.delegate;

import com.alpha.omega.user.model.UserContext;
import com.alpha.omega.user.model.UserContextBatchRequest;
import com.alpha.omega.user.model.UserContextPage;
import com.alpha.omega.user.model.UserContextPermissions;
import com.alpha.omega.user.server.UsercontextsApi;
import com.alpha.omega.user.server.UsercontextsApiDelegate;
import com.alpha.omega.user.service.ContextService;
import com.alpha.omega.user.service.UserContextService;
import io.micrometer.common.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Date;

import static com.alpha.omega.user.service.ServiceUtils.CORRELATION_ID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserContextDelegate implements UsercontextsApi {

    private static final Logger logger = LoggerFactory.getLogger(UserContextDelegate.class);
    UserContextService userContextService;
    ContextService contextService;


    public static final String extractCorrelationId(ServerWebExchange exchange){
        return exchange.getAttribute(CORRELATION_ID);
    }

    @Override
    public Mono<ResponseEntity<UserContext>> addPermissionToUserContext(String userId, String contextId, String permission, String cacheControl, ServerWebExchange exchange) {
        return UsercontextsApi.super.addPermissionToUserContext(userId, contextId, permission, cacheControl, exchange);
    }

    @Override
    public Mono<ResponseEntity<UserContext>> addPermissionsToUserContext(String userId, String contextId, String cacheControl, String additionalPermissions, ServerWebExchange exchange) {
        return UsercontextsApi.super.addPermissionsToUserContext(userId, contextId, cacheControl, additionalPermissions, exchange);
    }

    @Override
    public Mono<ResponseEntity<UserContext>> addRoleToUserContext(String userId, String contextId, String roleId, String cacheControl, String additionalPermissions, ServerWebExchange exchange) {
        return UsercontextsApi.super.addRoleToUserContext(userId, contextId, roleId, cacheControl, additionalPermissions, exchange);
    }

    @Override
    public Mono<ResponseEntity<UserContext>> createUserContext(Mono<UserContext> userContext, ServerWebExchange exchange) {
        /*
            Mono<UserContext> createUserContext(Mono<UserContext> userContext, String modifiedBy, String transactionId, Date createdDate);

         */

        return userContextService.createUserContext(userContext, null, extractCorrelationId(exchange), new Date())
                .doOnNext(ctx -> logger.info("Got context  => {}", ctx))
                .map(val -> {
                    if (StringUtils.isBlank(val.getContextId())) {
                        return ResponseEntity.status(HttpStatus.OK)
                                .headers(headers -> headers.add(CORRELATION_ID, exchange.getAttribute(CORRELATION_ID)))
                                .body(val);
                    } else {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .headers(headers -> headers.add(CORRELATION_ID, exchange.getAttribute(CORRELATION_ID)))
                                .build();
                    }
                });
    }

    @Override
    public Mono<ResponseEntity<UserContextPage>> createUserContextBatch(Mono<UserContextBatchRequest> userContextBatchRequest, ServerWebExchange exchange) {
        return UsercontextsApi.super.createUserContextBatch(userContextBatchRequest, exchange);
    }

    @Override
    public Mono<ResponseEntity<Void>> deleteUserContextByUserContextId(String usercontextId, ServerWebExchange exchange) {
        return UsercontextsApi.super.deleteUserContextByUserContextId(usercontextId, exchange);
    }

    @Override
    public Mono<ResponseEntity<UserContextPage>> getAllUserContexts(Integer page, Integer pageSize, String direction, String cacheControl, ServerWebExchange exchange) {
        return UsercontextsApi.super.getAllUserContexts(page, pageSize, direction, cacheControl, exchange);
    }

    @Override
    public Mono<ResponseEntity<UserContextPage>> getUserContextByContextId(String contextId, String cacheControl, Integer page, Integer pageSize, String direction, ServerWebExchange exchange) {
        return UsercontextsApi.super.getUserContextByContextId(contextId, cacheControl, page, pageSize, direction, exchange);
    }

    @Override
    public Mono<ResponseEntity<UserContext>> getUserContextByUserContextId(String usercontextId, ServerWebExchange exchange) {
        return UsercontextsApi.super.getUserContextByUserContextId(usercontextId, exchange);
    }

    @Override
    public Mono<ResponseEntity<UserContextPage>> getUserContextByUserId(String userId, String cacheControl, Integer page, Integer pageSize, String direction, ServerWebExchange exchange) {
        return UsercontextsApi.super.getUserContextByUserId(userId, cacheControl, page, pageSize, direction, exchange);
    }

    @Override
    public Mono<ResponseEntity<UserContextPermissions>> getUserContextByUserIdAndContextId(String userId, String contextId, Boolean allRoles, String roles, String cacheControl, ServerWebExchange exchange) {
        return UsercontextsApi.super.getUserContextByUserIdAndContextId(userId, contextId, allRoles, roles, cacheControl, exchange);
    }

    @Override
    public Mono<ResponseEntity<UserContext>> updateUserContext(String usercontextId, Mono<UserContext> userContext, ServerWebExchange exchange) {
        return UsercontextsApi.super.updateUserContext(usercontextId, userContext, exchange);
    }
}
