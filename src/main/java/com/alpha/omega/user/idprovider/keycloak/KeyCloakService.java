package com.alpha.omega.user.idprovider.keycloak;

import com.alpha.omega.security.ClientRegistrationEntity;
import com.alpha.omega.user.model.Context;
import com.alpha.omega.user.service.ContextCreated;
import com.alpha.omega.user.service.ContextLoader;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.core.Response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
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
import java.util.*;
import java.util.function.Function;

import static com.alpha.omega.user.idprovider.keycloak.KeyCloakUtils.MAP_OBJECT;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KeyCloakService {
    static final Logger logger = LoggerFactory.getLogger(KeyCloakService.class);
    public static final String MASTER = "master";
    public static final String ADMIN_CLI = "admin-cli";

    WebClient webClient;
    ObjectMapper objectMapper;
    KeyCloakIdpProperties keyCloakIdpProperties;
    JwtDecoder jwtDecoder;
    @Builder.Default
    Scheduler scheduler = Schedulers.boundedElastic();
    Keycloak keycloak;

    @PostConstruct
    public void init(){
        webClient = WebClient.builder()
                .baseUrl(keyCloakIdpProperties.baseUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .build();
        /*
        keycloak = Keycloak.getInstance(
                keyCloakIdpProperties.baseUrl(),
                MASTER,
                keyCloakIdpProperties.adminUsername(),
                keyCloakIdpProperties.adminPassword(),
                ADMIN_CLI);

         */
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

        keycloak.realms().create(realmRepresentation(realmName));

        return Mono.from(adminCliAccessCreds())
                .publishOn(scheduler)
                .flatMap(creds -> webClient.post()
                        .uri(uriBuilder -> uriBuilder.path(keyCloakIdpProperties.adminRealmUri()).build())
                        .headers(h -> h.setContentType(MediaType.APPLICATION_JSON))
                        .headers(h -> h.setBearerAuth((String) creds.get("access_token")))
                        .bodyValue(realmRepresentation(realmName))
                        .retrieve()
                        .toEntity(MAP_OBJECT));
    }

    // POST /admin/realms/{realm}/clients


    /*
    public Mono<ResponseEntity<Map<String, Object>>> createClient(ClientRegistrationEntity clientRegistration){

        ClientRepresentation clientRepresentation = clientRepresentation(clientRegistration);
        keycloak.realms().realm(clientRegistration.getClientName()).clients().create(clientRepresentation())

        return Mono.from(adminCliAccessCreds())
                .publishOn(scheduler)
                .flatMap(creds -> webClient.post()
                        .uri(uriBuilder -> uriBuilder.path(keyCloakIdpProperties.adminRealmUri()).build())
                        .headers(h -> h.setContentType(MediaType.APPLICATION_JSON))
                        .headers(h -> h.setBearerAuth((String) creds.get("access_token")))
                        .bodyValue()
                        .retrieve()
                        .toEntity(MAP_OBJECT));
    }

     */

    Function<ClientRegistrationEntity,ClientRepresentation> clientRepresentation(ClientRegistrationEntity clientRegistration) {
        return null;
    }

    /*
    https://gist.github.com/thomasdarimont/c4e739c5a319cf78a4cff3b87173a84b
     Keycloak keycloak = KeycloakBuilder.builder() //
                .serverUrl(serverUrl) //
                .realm(realm) //
                .grantType(OAuth2Constants.PASSWORD) //
                .clientId(clientId) //
                .clientSecret(clientSecret) //
                .username("idm-admin") //
                .password("admin") //
                .build();
    RealmResource realmResource = keycloak.realm(realm);
    ClientRepresentation app1Client = realmResource.clients()
                .findByClientId("app-frontend-springboot").get(0);
                Keycloak keycloak = Keycloak.getInstance(
    "http://localhost:8080",
    "master",
    "admin",
    "password",
    "admin-cli");
RealmRepresentation realm = keycloak.realm("master").toRepresentation();
     */

    public Mono<Map<String, Object>> passwordGrantLoginMap(String username, String password) {

        /*
        Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl("http://localhost:8080/auth")
                .grantType(OAuth2Constants.PASSWORD)
                .realm("master")
                .clientId("admin-cli")
                .username("admin")
                .password("password")
                .resteasyClient(
                        new ResteasyClientBuilder()
                                .connectionPoolSize(10).build()
                ).build();

        keycloak.tokenManager().getAccessToken();
        RealmResource realmResource = keycloak.realm("realm-name");

         */

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

    KCRealmRepresentation defaultRealmRepresentation(String realName){
        return KCRealmRepresentation.builder()
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


    static Function<String, RealmRepresentation> realmRepresentationFunction(KeyCloakIdpProperties keyCloakIdpProperties){
        return realName ->{
            RealmRepresentation realmRepresentation = new RealmRepresentation();
            realmRepresentation.setRealm(realName);
            realmRepresentation.setId(realName);
            realmRepresentation.setEnabled(Boolean.TRUE);
            realmRepresentation.setDuplicateEmailsAllowed(Boolean.FALSE);
            realmRepresentation.setAccessCodeLifespan(keyCloakIdpProperties.accessCodeLifespan());
            realmRepresentation.setSslRequired("all");
            realmRepresentation.setBruteForceProtected(Boolean.TRUE);
            realmRepresentation.setLoginTheme("keycloak");
            realmRepresentation.setEventsEnabled(Boolean.FALSE);
            realmRepresentation.setAdminEventsEnabled(Boolean.FALSE);
            return realmRepresentation;
        };
    }


    RealmRepresentation realmRepresentation(String realName){

        return realmRepresentationFunction(keyCloakIdpProperties).apply(realName);
    }

    @Builder
    public record KCRealmRepresentation(String id, String realm, boolean enabled, boolean duplicateEmailsAllowed,
                                        int accessTokenLifespan, String sslRequired, boolean bruteForceProtected,
                                        String loginTheme, boolean eventsEnabled, boolean adminEventsEnabled) {
    }

    @Builder
    public record ProtocolMapperRepresentation(String id, String name, String protocol, String protocolMapper,Boolean consentRequired,
                                               String consentText, Map<String,String> config){}

    /*
    // Will add this to client representation if needed
    @Builder
    public record ResourceServerRepresentation(){

    }

     */
    /*
    id, clientId, name, description, rootUrl, adminUrl, baseUrl, enabled, alwaysDisplayInConsole, surrogateAuthRequired,
    enabled, clientAuthenticatorType, secret, registrationAccessToken, defaultRoles, redirectUris, webOrigins, notBefore,
    bearerOnly, consentRequired, standardFlowEnabled, implicitFlowEnabled, directAccessGrantsEnabled, serviceAccountsEnabled,
    oauth2DeviceAuthorizationGrantEnabled, authorizationServicesEnabled, directGrantsOnly, publicClient, frontchannelLogout,
    protocol, attributes, authenticationFlowBindingOverrides, fullScopeAllowed, nodeReRegistrationTimeout, registeredNodes,
    protocolMappers, clientTemplate, useTemplateConfig, useTemplateScope, useTemplateMappers, defaultClientScopes,
    optionalClientScopes, authorizationSettings, access, origin
     */

    public static Function<ClientRegistrationEntity, KCClientRepresentation> convertClientRegistrationEntityToClientRepresentation(){

        return entity -> {

            return KCClientRepresentation.builder()
                    .id(entity.getId())
                    .clientId(entity.getClientId())
                    .name(entity.getClientName())
                    .description(entity.getClientName())
                    .secret(entity.getClientSecret())
                    .build();
        };
    }

    @Builder
    public record KCClientRepresentation(String id, String clientId, String name, String description, String rootUrl, String adminUrl, String baseUrl,
                                         Boolean surrogateAuthRequired, Boolean enabled, Boolean alwaysDisplayInConsole, String clientAuthenticatorType,
                                         String secret, String registrationAccessToken, List<String> defaultRoles, List<String> redirectUris,
                                         List<String> webOrigins, Integer notBefore, Boolean bearerOnly, Boolean consentRequired, Boolean standardFlowEnabled,
                                         Boolean implicitFlowEnabled, Boolean directAccessGrantsEnabled, Boolean serviceAccountsEnabled,
                                         Boolean oauth2DeviceAuthorizationGrantEnabled, Boolean authorizationServicesEnabled,
                                         Boolean directGrantsOnly, Boolean publicClient, Boolean frontchannelLogout, String protocol,
                                         Map<String, String> attributes, Map<String, String> authenticationFlowBindingOverrides,
                                         Boolean fullScopeAllowed, Integer nodeReRegistrationTimeout, Map<Integer, Integer> registeredNodes,
                                         List<ProtocolMapperRepresentation> protocolMappers, String clientTemplate, Boolean useTemplateConfig,
                                         Boolean useTemplateScope, Boolean useTemplateMappers, List<String> defaultClientScopes,
                                         List<String> optionalClientScopes,
                                         Boolean access, String origin){}

    static Function<String, ClientRepresentation> clientRepresentationFunction(ClientRepresentation template){
        return clientName -> {
            ClientRepresentation clientRepresentation = new ClientRepresentation();
            BeanUtils.copyProperties(template, clientRepresentation);
            clientRepresentation.setClientId(clientName);
            clientRepresentation.setName(clientName);
            clientRepresentation.setDescription("Client for "+clientName);
            clientRepresentation.setSecret(KeyCloakUtils.createAKey());
            return clientRepresentation;
        };
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class KeyCloakContextListener  implements ApplicationListener<ContextCreated> {
        KeyCloakService keyCloakService;
        @Builder.Default
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        @Builder.Default
        ObjectMapper objectMapper = new ObjectMapper();
        Keycloak keycloak;
        KeyCloakIdpProperties keyCloakIdpProperties;
        ClientRepresentation template;

        @PostConstruct
        public void init(){

            KeyCloakUtils.loadClientRepresentationTemplate(KeyCloakUtils.DEFAULT_CLIENT_TEMPLATE).ifPresent(cr -> template = cr);
            logger.info("Client template {}",template);
            //template = KeyCloakUtils.loadClientRepresentationTemplate(KeyCloakUtils.DEFAULT_CLIENT_TEMPLATE);
        }

        @Override
        public void onApplicationEvent(ContextCreated event) {
            Context context = event.getContext();
            logger.info("Got context {} in KeyCloakContextListener",context.getContextId());
            RealmRepresentation realmRepresentation = null;
            try{
                //UserRepresentation userRepresentation;
                realmRepresentation = realmRepresentationFunction(keyCloakIdpProperties).apply(context.getContextId());
                realmRepresentation.setUsers(createServiceAccountList(context));
                keycloak.realms().create(realmRepresentation);
            } catch (Exception cex){
                logger.warn("Could not create realm {}",context.getContextId(),cex);
            }

            try{
                RealmResource realm =   keycloak.realm(context.getContextId());
                logger.info("Acquired realm {} for creating client {}",realm.toRepresentation().getRealm(), context.getContextId());
                ClientRepresentation clientRepresentation = clientRepresentationFunction(template).apply(context.getContextId());
                /*

  "standardFlowEnabled": true,
  "implicitFlowEnabled": true,
  "directAccessGrantsEnabled": true,
  "serviceAccountsEnabled": true,
  "authorizationServicesEnabled": true,
                 */

                clientRepresentation.setServiceAccountsEnabled(Boolean.FALSE);
                clientRepresentation.setAuthorizationServicesEnabled(Boolean.FALSE);
                clientRepresentation.setImplicitFlowEnabled(Boolean.FALSE);
                clientRepresentation.setDirectAccessGrantsEnabled(Boolean.FALSE);
                clientRepresentation.setImplicitFlowEnabled(Boolean.FALSE);
                Response response = realm.clients().create(clientRepresentation);
                logger.info("From client {} Got client create response status {}",clientRepresentation.getClientId(),response.getStatus());
            } catch (Exception cex){
                logger.warn("Could not create client {}",context.getContextId(),cex);
            }

            //logger.info("################### From client {} ", context.getContextId());
            try{
                RealmResource realm =   keycloak.realm(context.getContextId());
                logger.info("Acquired realm {} for updating client {}",realm.toRepresentation().getRealm(), context.getContextId());

                Response userCreateResponse =  realm.users().create(createServiceAccount(context));
                logger.info("################### From client {} Got user create response status {}",context.getContextId(),userCreateResponse.getStatus());
                List<ClientRepresentation> clientResources = realm.clients().findByClientId(context.getContextId());
                ClientRepresentation clientRepresentation = clientResources.get(0);
                //ClientRepresentation clientRepresentation = clientResource.toRepresentation();
                clientRepresentation.setServiceAccountsEnabled(Boolean.TRUE);
                clientRepresentation.setAuthorizationServicesEnabled(Boolean.TRUE);
                clientRepresentation.setImplicitFlowEnabled(Boolean.TRUE);
                clientRepresentation.setDirectAccessGrantsEnabled(Boolean.TRUE);
                clientRepresentation.setImplicitFlowEnabled(Boolean.TRUE);

                realm.clients().get(clientRepresentation.getId()).update(clientRepresentation);
            } catch (Exception cex){
                logger.warn("Could not update client {}",context.getContextId(),cex);
            }

            keycloak.realm(context.getContextId()).clients().findAll().stream()
                    .forEach(cr -> logger.info("From context {} Got clients {} name {} secret {}",new Object[]{context.getContextId(),cr.getClientId(),cr.getName(), cr.getSecret()} ));
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

        private UserRepresentation createServiceAccount(Context context){
            UserRepresentation userRepresentation = new UserRepresentation();
            userRepresentation.setEnabled(Boolean.TRUE);
            userRepresentation.setCreatedTimestamp(System.currentTimeMillis());
            //userRepresentation.setServiceAccountClientId(context.getContextId());
            userRepresentation.setUsername("service-account-"+context.getContextId());
            userRepresentation.setId(UUID.randomUUID().toString());
            return userRepresentation;
        }

        private List<UserRepresentation> createServiceAccountList(Context context) {
            return Collections.singletonList(createServiceAccount(context));
        }
    }

}
