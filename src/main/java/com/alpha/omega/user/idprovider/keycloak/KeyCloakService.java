package com.alpha.omega.user.idprovider.keycloak;

import com.alpha.omega.user.model.Context;
import com.alpha.omega.user.service.ContextLoader;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.alpha.omega.user.idprovider.keycloak.KeyCloakUtils.MAP_OBJECT;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KeyCloakService {
    static final Logger logger = LoggerFactory.getLogger(KeyCloakService.class);

    WebClient webClient;
    ObjectMapper objectMapper;
    KeyCloakIdpProperties keyCloakIdpProperties;
    JwtDecoder jwtDecoder;
    @Builder.Default
    Scheduler scheduler = Schedulers.boundedElastic();

    @PostConstruct
    public void init(){
        webClient = WebClient.builder()
                .baseUrl(keyCloakIdpProperties.baseUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .build();
    }

    /*
    The adminCliAccessCreds() method for KeyCloak uses the admin-cli client_id and client_secret to obtain an access token
    against the master realm tokenuri or /realms/master/protocol/openid-connect/token. The admin-cli client has to be
    modified. This url walks through the changes needed for admin-cli.
    https://www.mastertheboss.com/keycloak/how-to-use-keycloak-admin-rest-api/
     */
    public Mono<Map<String, Object>> adminCliAccessCreds() {

        logger.info("keyCloakIdpProperties.baseUrl() + keyCloakIdpProperties.adminTokenUri() => {}",keyCloakIdpProperties.baseUrl() + keyCloakIdpProperties.adminTokenUri());
        logger.info("keyCloakIdpProperties.adminClientId() => {}",keyCloakIdpProperties.adminClientId());
        logger.info("keyCloakIdpProperties.adminClientSecret() => {}",keyCloakIdpProperties.adminClientSecret());

        return webClient.post()
                //  .uri(keyCloakIdpProperties.tokenUri())
                .uri(keyCloakIdpProperties.adminTokenUri())
                //.headers(h -> h.setBasicAuth(basicAuthCreds.get()))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .body(BodyInserters.fromFormData("grant_type", "client_credentials")
                        //.body(BodyInserters.fromFormData("grant_type", "password")
                        .with("client_id", keyCloakIdpProperties.adminClientId())
                        .with("client_secret", keyCloakIdpProperties.adminClientSecret()))
                .retrieve()
                .bodyToMono(MAP_OBJECT);
    }

    public Mono<ResponseEntity<Map<String, Object>>> createRealm(String realmName){

        return Mono.from(adminCliAccessCreds())
                .publishOn(scheduler)
                .flatMap(creds -> webClient.post()
                        .uri(uriBuilder -> uriBuilder.path(keyCloakIdpProperties.adminRealmUri()).build())
                        .headers(h -> h.setContentType(MediaType.APPLICATION_JSON))
                        .headers(h -> h.setBearerAuth((String) creds.get("access_token")))
                        .bodyValue(defaultRealmRepresentation(realmName))
                        .retrieve()
                        .toEntity(MAP_OBJECT));
    }


    public Mono<Map<String, Object>> passwordGrantLoginMap(String username, String password) {

        return webClient.post()
                .uri(keyCloakIdpProperties.tokenUri())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .body(BodyInserters.fromFormData("grant_type", "password")
                        .with("username", username)
                        .with("password", password)
                        .with("client_id", keyCloakIdpProperties.clientId())
                        .with("client_secret", keyCloakIdpProperties.clientSecret()))
                .retrieve()
                .bodyToMono(MAP_OBJECT);
    }

    public Mono<Optional<Jwt>> passwordGrantLoginJwt(String username, String password) {

        return this.passwordGrantLoginMap(username, password)
                .map(KeyCloakUtils.convertResultMapToJwt(jwtDecoder));
    }

    /*
    minimal from https://www.mastertheboss.com/keycloak/how-to-use-keycloak-admin-rest-api/
    {
    "id": "testrealm",
    "realm": "testrealm",
    "accessTokenLifespan": 600,
    "enabled": true,
    "sslRequired": "all",
    "bruteForceProtected": true,
    "loginTheme": "keycloak",
    "eventsEnabled": false,
    "adminEventsEnabled": false
}
     */

    RealmRepresentation defaultRealmRepresentation(String realName){
        return RealmRepresentation.builder()
                .id(realName)
                .realm(realName)
                .enabled(Boolean.TRUE)
                .duplicateEmailsAllowed(Boolean.FALSE)
                .accessTokenLifespan(600)
                .sslRequired("all")
                .bruteForceProtected(Boolean.TRUE)
                .loginTheme("keycloak")
                .eventsEnabled(Boolean.FALSE)
                .adminEventsEnabled(Boolean.FALSE)
                .build();
    }

    @Builder
    public record RealmRepresentation(String id, String realm, boolean enabled, boolean duplicateEmailsAllowed,
                                      int accessTokenLifespan, String sslRequired, boolean bruteForceProtected,
                                      String loginTheme, boolean eventsEnabled, boolean adminEventsEnabled) {
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class KeyCloakRealmLoader  implements ApplicationListener<ContextLoader.ContextLoadedEvent> {
        KeyCloakService keyCloakService;
        @Builder.Default
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        @Builder.Default
        ObjectMapper objectMapper = new ObjectMapper();

        @Override
        public void onApplicationEvent(ContextLoader.ContextLoadedEvent event) {
            Context context = event.getContext();

        }
    }

}
