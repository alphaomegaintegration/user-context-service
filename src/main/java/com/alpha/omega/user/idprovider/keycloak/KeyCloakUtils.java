package com.alpha.omega.user.idprovider.keycloak;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class KeyCloakUtils {

    final static Mono<Map<String, Object>> EMPTY_ACCESS_CREDS = Mono.empty();
    final static ParameterizedTypeReference<Map<String, Object>> MAP_OBJECT = new ParameterizedTypeReference<Map<String, Object>>() {
    };

    public final static Function<Map<String, Object>, Optional<Jwt>> convertResultMapToJwt(JwtDecoder jwtDecoder) {
        return map -> {
            String accessToken = (String)map.get("access_token");
            Jwt jwt = null;
            try{
                jwt = jwtDecoder.decode(accessToken);
            } catch (Exception e){
                KeyCloakUserService.logger.warn("Could not decode jwt!",e);
            }
            return Optional.ofNullable(jwt);
        };
    }
}
