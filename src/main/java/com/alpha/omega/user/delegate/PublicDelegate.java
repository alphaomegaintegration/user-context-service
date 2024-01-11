package com.alpha.omega.user.delegate;

import com.alpha.omega.security.SecurityUtils;
import com.alpha.omega.user.idprovider.keycloak.KeyCloakAuthenticationManager;
import com.alpha.omega.user.server.PublicApi;
import com.alpha.omega.user.server.PublicApiDelegate;
import com.alpha.omega.user.service.ServiceUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

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
        return keyCloakAuthenticationManager.passwordGrantLoginMap(userPass.getT1(), userPass.getT2())
                .map(map -> ServiceUtils.convertToJsonNode().apply(map, objectMapper))
                .map(jsonNode -> ResponseEntity.ok((ObjectNode)jsonNode));

    }
}
