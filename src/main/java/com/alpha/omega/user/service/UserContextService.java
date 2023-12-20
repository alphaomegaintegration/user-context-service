package com.alpha.omega.user.service;

import com.alpha.omega.user.model.UserContext;
import com.alpha.omega.user.model.UserContextBatchRequest;
import com.alpha.omega.user.model.UserContextPage;
import com.alpha.omega.user.model.UserContextPermissions;
import org.springframework.data.domain.PageRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;

public interface UserContextService {
    Mono<UserContext> createUserContext(UserContext context);
    Mono<UserContext> updateUserContext(UserContext context);
    Flux<UserContext> findByContextId(String contextId);
    Mono<Void> deleteUserContextByContextId(String contextId);

    Mono<UserContextPage> getAllUserContextEntities(PageRequest pageRequest);

    Flux<UserContext> getUserContextByContextId(String contextId);

    Mono<UserContext> updateUserContextByContextId(String contextId, Mono<UserContext> context);

    Mono<UserContext> createUserContext(Mono<UserContext> userContext, String modifiedBy, String tranasactionId, Date createdDate);
    Mono<UserContextPage> createUserContextBatch(UserContextBatchRequest batchRequest, String auditUser, String transactionId);
    Mono<UserContext> addRoleToUserContext(String userId, String contextId, String roleIds,String auditUser);
    Mono<UserContext> updateUserContextByUserContextId(Mono<UserContext> userContext, String auditUser, String transactionId);
    Mono<Void> deleteUserContextByUserContextId(String userContextId);
    Mono<UserContextPage> getAllUserContexts(PageRequest pageRequest);
    Mono<UserContextPage> getUserContextByContextId(PageRequest pageRequest, String contextId);
    Mono<UserContextPage> getUserContextByUserId(PageRequest pageRequest, String userId);
    Mono<UserContext> getUserContextByUserContextId(String userContextId);
    Mono<UserContextPermissions> getUserContextByUserIdAndContextId(UserContextRequest userContextRequest);
}
