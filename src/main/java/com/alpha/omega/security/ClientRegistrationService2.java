package com.alpha.omega.security;

import reactor.core.publisher.Mono;

public interface ClientRegistrationService2 {
    Mono<ClientRegistrationEntity2> upsertClientRegistrationEntity(ClientRegistrationEntity2 clientRegistration);
    Mono<ClientRegistrationEntity2> deleteClientRegistrationEntity(ClientRegistrationEntity2 clientRegistration);
}
