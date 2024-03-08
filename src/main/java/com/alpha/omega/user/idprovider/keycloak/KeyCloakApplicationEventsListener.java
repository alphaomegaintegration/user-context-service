package com.alpha.omega.user.idprovider.keycloak;

import com.alpha.omega.user.model.Context;
import com.alpha.omega.user.service.ContextCreated;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.ClientRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class KeyCloakApplicationEventsListener {
    static final Logger logger = LoggerFactory.getLogger(KeyCloakApplicationEventsListener.class);

    KeyCloakService keyCloakService;
    @Builder.Default
    ResourceLoader resourceLoader = new DefaultResourceLoader();
    @Builder.Default
    ObjectMapper objectMapper = new ObjectMapper();
    Keycloak keycloak;
    KeyCloakIdpProperties keyCloakIdpProperties;
    ClientRepresentation template;
    static ConcurrentHashMap<String, Boolean> idempotentGuard = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {

        KeyCloakUtils.loadClientRepresentationTemplate(KeyCloakUtils.DEFAULT_CLIENT_TEMPLATE).ifPresent(cr -> template = cr);
        logger.info("Client template {}", template);
        //template = KeyCloakUtils.loadClientRepresentationTemplate(KeyCloakUtils.DEFAULT_CLIENT_TEMPLATE);
    }

    static AtomicInteger contextStartedEventCount = new AtomicInteger(0);
    static AtomicInteger contextRefreshedEventCount = new AtomicInteger(0);

    @EventListener(classes = { ContextStartedEvent.class})
    public void handleContextStartedEvent(ContextStartedEvent ctxStartEvt) {
        logger.info("contextStartedEventCount => {}",contextStartedEventCount.incrementAndGet());
    }

    @EventListener(classes = { ContextRefreshedEvent.class})
    public void handleContextRefreshEvent(ContextRefreshedEvent ctxStartEvt) {
        logger.info("contextRefreshedEventCount => {}",contextRefreshedEventCount.incrementAndGet());
        keyCloakService.getClientSecret("user-context-service").subscribe(secret -> {
            logger.info("Got new secret => {}",secret);
           //keyCloakIdpProperties.adminClientSecret()
        });
    }

    @EventListener
    public void onApplicationEvent(ContextCreated event) {
        Context context = event.getContext();
        //createRealmFromContext(context);
        if (idempotentGuard.computeIfAbsent(context.getContextId(), val -> Boolean.TRUE)) {
            idempotentGuard.put(context.getContextId(), Boolean.FALSE);
            keyCloakService.createRealmFromContext(context);
        }

    }


    /*
    {
  "id": "720071b8-3a3f-4de2-aef3-16a78a4bff54",
  "createdTimestamp": 1703812598807,
  "username": "service-account-user-context-service",
  "enabled": true,
  "totp": false,
  "emailVerified": false,
  "serviceAccountClientId": "user-context-service",
  "disableableCredentialTypes": [],
  "requiredActions": [],
  "notBefore": 0
}
     */


}
