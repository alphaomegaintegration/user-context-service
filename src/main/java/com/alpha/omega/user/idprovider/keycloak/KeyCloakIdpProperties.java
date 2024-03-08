package com.alpha.omega.user.idprovider.keycloak;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@ConfigurationProperties(prefix = "idp.provider.keycloak")
public record KeyCloakIdpProperties(/*String clientId, String clientSecret,*/ String baseUrl, String tokenUri,
                                    String userUri, String realm, String adminTokenUri, String adminUsername,
                                    String adminPassword, String adminClientId, String adminClientSecret,
                                    String jwksetUri, String issuerUrl, String adminRealmUri, Integer accessCodeLifespan) {
}
