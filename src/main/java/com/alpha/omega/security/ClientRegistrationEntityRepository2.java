package com.alpha.omega.security;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ClientRegistrationEntityRepository2 extends CrudRepository<ClientRegistrationEntity2, String> {
    Optional<ClientRegistrationEntity2> findByRegistrationId(String registrationId);
}
