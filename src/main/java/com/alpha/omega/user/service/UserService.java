package com.alpha.omega.user.service;

import com.alpha.omega.user.batch.UserLoad;
import reactor.core.publisher.Mono;

public interface UserService {
    Mono<UserLoad> findUserByUserId(String userId);
}
