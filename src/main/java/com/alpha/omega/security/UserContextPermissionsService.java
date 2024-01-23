package com.alpha.omega.security;

import com.alpha.omega.user.model.UserContextPermissions;
import reactor.core.publisher.Mono;

public interface UserContextPermissionsService {
    Mono<UserContextPermissions> getUserContextByUserIdAndContextId(UserContextRequest userContextRequest);
}
