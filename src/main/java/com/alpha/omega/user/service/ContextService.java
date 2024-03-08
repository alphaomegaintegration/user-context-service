package com.alpha.omega.user.service;

import com.alpha.omega.user.model.Context;
import com.alpha.omega.user.model.ContextPage;
import com.alpha.omega.user.model.Role;
import com.alpha.omega.user.model.RolePage;
import org.springframework.data.domain.PageRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.util.List;

public interface ContextService {

    Mono<Context> createContext(Context context);
    Mono<Context> updateContext(Context context);
    Mono<Context> findByContextId(String contextId);
    Mono<Context> addAdditionalRolesByContextId(String contextId, Mono<Role> role);
    Mono<Void> deleteContextByContextId(String contextId);
    Mono<ContextPage> getAllContextEntities(PageRequest pageRequest);
    Mono<RolePage> getRolesByContextId(String contextId);
    Mono<Context> getContextByContextId(String contextId);
    Mono<Context> updateContextByContextId(String contextId, Mono<Context> context);
    Mono<Role> getRoleByContextIdAndRoleId(String contextId, String roleId);
    Flux<Role> getRolesByContextIdAndRoleIdIn(String contextId, List<String> roleIds, boolean allRoles);
    Mono<Boolean> roleExistsInContext(String roleId, String contextId);
    Flux<Context> loadContexts(Scheduler scheduler, String contextsStr);
    Flux<Context> getAllContexts();
}
