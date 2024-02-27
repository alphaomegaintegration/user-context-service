package com.alpha.omega.user.delegate;

import com.alpha.omega.security.SecurityUser;
import com.alpha.omega.security.SecurityUtils;
import com.alpha.omega.security.idprovider.keycloak.KeyCloakAuthenticationManager;
import com.alpha.omega.user.server.PublicApi;
import com.alpha.omega.user.server.PublicApiDelegate;
import com.alpha.omega.user.service.ServiceUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.Optional;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublicDelegate implements PublicApiDelegate {
    private static final Logger logger = LoggerFactory.getLogger(PublicDelegate.class);

    KeyCloakAuthenticationManager keyCloakAuthenticationManager;
    @Builder.Default
    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Mono<ResponseEntity<ObjectNode>> getPublicLogin(String authorization, String contextId, ServerWebExchange exchange) {

        Tuple2<String,String> userPass = SecurityUtils.fromBasicAuthToTuple(authorization);
//        return keyCloakAuthenticationManager.passwordGrantLoginMap(userPass.getT1(), userPass.getT2())
//                .map(map -> ServiceUtils.convertToJsonNode().apply(map, objectMapper))
//                .cast(ObjectNode.class)
//                .map(objectNode -> ResponseEntity.ok(objectNode));

        String ctxId = StringUtils.isBlank(contextId) ? keyCloakAuthenticationManager.getDefaultContext() : contextId;
        logger.info("Context id {}",ctxId);
        return keyCloakAuthenticationManager.passwordGrantLoginMap(userPass.getT1(), userPass.getT2(), ctxId)
                .map(map -> ServiceUtils.convertToJsonNode().apply(map, objectMapper))
                .cast(ObjectNode.class)
                .map(objectNode -> ResponseEntity.ok(objectNode));

    }

    @Override
    public Mono<ResponseEntity<ObjectNode>> validateToken(String authorization, ServerWebExchange exchange) {
        String extractedToken = SecurityUtils.fromBearerHeaderToToken(authorization);
        return keyCloakAuthenticationManager.validLoginJwt(extractedToken)
                .filter(Optional::isPresent)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new BadCredentialsException("Invalid Credentials"))))
                .map(map -> ServiceUtils.convertToJsonNode().apply(map.get().getClaims(), objectMapper))
                .cast(ObjectNode.class)
                .map(objectNode -> ResponseEntity.ok(objectNode));
    }
}
