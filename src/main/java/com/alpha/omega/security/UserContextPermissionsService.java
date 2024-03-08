package com.alpha.omega.security;

import com.alpha.omega.security.context.UserContextPermissions;
import com.alpha.omega.security.context.UserContextRequest;
import reactor.core.publisher.Mono;

public interface UserContextPermissionsService {
    Mono<UserContextPermissions> getUserContextByUserIdAndContextId(UserContextRequest userContextRequest);
}
