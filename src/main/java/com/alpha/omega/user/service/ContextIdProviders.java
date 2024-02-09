package com.alpha.omega.user.service;

import reactor.core.publisher.Mono;

import java.util.Map;

public interface ContextIdProviders {
    Mono<Map<String, Object>> getContextIdProviders(String contextId);
}
