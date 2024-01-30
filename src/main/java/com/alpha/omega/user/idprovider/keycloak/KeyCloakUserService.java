package com.alpha.omega.user.idprovider.keycloak;

import com.alpha.omega.user.batch.UserLoad;
import com.alpha.omega.user.repository.UserEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.*;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.alpha.omega.user.batch.BatchConstants.PROMOTE_USER_ENTITY_CHUNK_KEY;
import static com.alpha.omega.user.batch.BatchConstants.PROMOTE_USER_LOAD_CHUNK_KEY;
import static com.alpha.omega.user.batch.BatchUtil.basicAuthCredsFrom;
import static com.alpha.omega.user.idprovider.keycloak.KeyCloakUtils.EMPTY_ACCESS_CREDS;
import static com.alpha.omega.user.idprovider.keycloak.KeyCloakUtils.MAP_OBJECT;


@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KeyCloakUserService implements ItemWriter<UserEntity>, StepExecutionListener, Tasklet{

    // TODO Refactor


    static final Logger logger = LoggerFactory.getLogger(KeyCloakUserService.class);

/*
    https://www.keycloak.org/docs-api/22.0.1/rest-api/index.html#_users
    https://www.keycloak.org/docs-api/22.0.1/rest-api/index.html#UserRepresentation
    POST /admin/realms/{realm}/users
     */

     /*
    https://www.baeldung.com/spring-batch-tasklet-chunk
     */

    WebClient webClient;
    ObjectMapper objectMapper;
    KeyCloakIdpProperties keyCloakIdpProperties;
    Mono<Map<String, Object>> accessCreds;
    Function<UserLoad, UserEntity> userLoadUserEntityFunction;

    Supplier<String> basicAuthCreds;
    Supplier<String> passwordSupplier;
    JwtDecoder jwtDecoder;

    private List<UserEntity> userEntities;
    private StepExecution stepExecution;
    @Builder.Default
    Scheduler scheduler = Schedulers.boundedElastic();

    @Override
    public void beforeStep(StepExecution stepExecution) {
        logger.debug("Using keyCloakIdpProperties => {}", keyCloakIdpProperties);
        NimbusJwtDecoder nimbusJwtDecoder = NimbusJwtDecoder.withJwkSetUri(keyCloakIdpProperties.jwksetUri())
                .build();
        nimbusJwtDecoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(keyCloakIdpProperties.issuerUrl()));
        jwtDecoder = nimbusJwtDecoder;
        webClient = WebClient.builder()
                .filters(exchangeFilterFunctions -> {
                    //exchangeFilterFunctions.add(logRequest());
                    // exchangeFilterFunctions.add(logResponse());
                })
                .baseUrl(keyCloakIdpProperties.baseUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .build();
        basicAuthCreds = () -> basicAuthCredsFrom(keyCloakIdpProperties.adminClientId(), keyCloakIdpProperties.adminClientSecret());

        logger.info("Using basic auth creds {} for username => {} password => {}",
                new Object[]{basicAuthCreds.get(), keyCloakIdpProperties.adminClientId(), keyCloakIdpProperties.adminClientSecret()});

        accessCreds = adminCliAccessCreds();
        JobExecution jobExecution = stepExecution.getJobExecution();
        ExecutionContext jobContext = jobExecution.getExecutionContext();
        this.userEntities = (List<UserEntity>) jobContext.get(PROMOTE_USER_ENTITY_CHUNK_KEY);
    }

    ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            if (logger.isDebugEnabled()) {
                StringBuilder sb = new StringBuilder("Request: \n");
                //append clientRequest method and url
                clientRequest
                        .headers()
                        .forEach((name, values) -> values.forEach(value -> logger.debug("header => {}, value => {}", name, value)));
                logger.debug("url => {}", clientRequest.url());
                logger.debug("body => {}", clientRequest.body());
                //logger.debug("url => {}",);
                logger.debug(sb.toString());
            }
            return Mono.just(clientRequest);
        });
    }

    /*
    The adminCliAccessCreds() method for KeyCloak uses the admin-cli client_id and client_secret to obtain an access token
    against the master realm tokenuri or /realms/master/protocol/openid-connect/token. The admin-cli client has to be
    modified. This url walks through the changes needed for admin-cli.
    https://www.mastertheboss.com/keycloak/how-to-use-keycloak-admin-rest-api/
     */
    private Mono<Map<String, Object>> adminCliAccessCreds() {

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

    private Function<Map<String, Object>, Mono<Map<String, Object>>> validateAccess() {

        return access -> {
            return isExpired(access) ? adminCliAccessCreds() : EMPTY_ACCESS_CREDS;
        };
    }


    private Function<Mono<Map<String, Object>>, Mono<Map<String, Object>>> validateAccessMono() {

        return access -> {
            return Mono.from(access)
                    .flatMap(creds -> validateAccess().apply(creds));
        };
    }

    private boolean isExpired(Map<String, Object> access) {
        return Boolean.TRUE.booleanValue();
    }


    /*
    Once you have received an access_token from the admin-cli client via the adminCliAccessCreds() method,
    you can call into the specific keycloak realm to add users to that realm.
     */
    Function<Tuple2<? extends UserEntity, Map<String, Object>>, Mono<ResponseEntity<Map<String, Object>>>> postUserRepresentation() {
        return tuple -> {
            logger.info("Got contextId => {} for userUri => {}",tuple.getT1().getContextId(), keyCloakIdpProperties.userUri());
            final UserRepresentation userRepresentation = convertToUserRepresentation().apply(tuple.getT1());
            logger.info("Got userRepresentation => {} ",userRepresentation);
            return Mono.just(tuple.getT2())
                    .publishOn(Schedulers.boundedElastic())
                    .flatMap(creds -> webClient.post()
                            .uri(uriBuilder -> uriBuilder.path(keyCloakIdpProperties.userUri())
                                    .build(Map.of("realm", tuple.getT1().getContextId())))
                            .headers(h -> h.setContentType(MediaType.APPLICATION_JSON))
                            .headers(h -> h.setBearerAuth((String) creds.get("access_token")))
                            .bodyValue(userRepresentation)
                            .retrieve()
                            .toEntity(MAP_OBJECT));

        };

    }

    /*
   Once you have received an access_token from the admin-cli client via the adminCliAccessCreds() method,
   you can call into the specific keycloak realm to add users to that realm.
    */
    Function<Tuple2<? extends UserLoad, Map<String, Object>>, Mono<ResponseEntity<Map<String, Object>>>> postUserRepresentationFromUserLoad() {
        return tuple -> {
            logger.debug("Got contextId => {} for userUri => {}",tuple.getT1().getContextId(), keyCloakIdpProperties.userUri());
            final UserRepresentation userRepresentation = convertToUserRepresentationFromUserLoad().apply(tuple.getT1());
            logger.debug("Got userRepresentation => {} ",userRepresentation);
            return Mono.just(tuple.getT2())
                    .publishOn(Schedulers.boundedElastic())
                    .flatMap(creds -> webClient.post()
                            .uri(uriBuilder -> uriBuilder.path(keyCloakIdpProperties.userUri())
                                    .build(Map.of("realm", tuple.getT1().getContextId())))
                            .headers(h -> h.setContentType(MediaType.APPLICATION_JSON))
                            .headers(h -> h.setBearerAuth((String) creds.get("access_token")))
                            .bodyValue(userRepresentation)
                            .retrieve()
                            .toEntity(MAP_OBJECT));

        };

    }


    Function<String, List<CredentialRepresentation>> listCredentialRepresentationFrom(){
        return creds -> {
            return StringUtils.isNotBlank(creds) ? Collections.singletonList(CredentialRepresentation.builder()
                    .value(creds)
                    .build())
                    : Collections.emptyList();
        };
    }

    Function<UserEntity, UserRepresentation> convertToUserRepresentation() {
        final Long createdTime = System.currentTimeMillis();
        return userEntity -> {
            return UserRepresentation.builder()
                    .email(userEntity.getEmail())
                    .username(userEntity.getEmail())
                    .enabled(Boolean.TRUE)
                    .emailVerified(Boolean.TRUE)
                    .lastName(userEntity.getLastName())
                    .firstName(userEntity.getFirstName())
                    .createdTimestamp(createdTime)
                    .credentials(listCredentialRepresentationFrom().apply(userEntity.getPassword()))
                    .build();
        };
    }


    Function<UserLoad, UserRepresentation> convertToUserRepresentationFromUserLoad() {
        final Long createdTime = System.currentTimeMillis();
        return userLoad -> {
            return UserRepresentation.builder()
                    .email(userLoad.getEmail())
                    .username(userLoad.getEmail())
                    .enabled(Boolean.TRUE)
                    .emailVerified(Boolean.TRUE)
                    .lastName(userLoad.getLast())
                    .firstName(userLoad.getFirst())
                    .createdTimestamp(createdTime)
                    .credentials(listCredentialRepresentationFrom().apply(userLoad.getPassword()))
                    .build();
        };
    }


    Function<UserLoad, UserRepresentation> convertToUserRepresentation2() {
        final Long createdTime = System.currentTimeMillis();
        return userLoad -> {
            return UserRepresentation.builder()
                    .email(userLoad.getEmail())
                    .username(userLoad.getEmail())
                    .enabled(Boolean.TRUE)
                    .emailVerified(Boolean.TRUE)
                    .lastName(userLoad.getLast())
                    .firstName(userLoad.getFirst())
                    .createdTimestamp(createdTime)
                    .credentials(listCredentialRepresentationFrom().apply(userLoad.getPassword()))
                    .build();
        };
    }

    @Override
    public void write(Chunk<? extends UserEntity> chunk) throws Exception {

        Flux.fromIterable(chunk.getItems())
                .publishOn(scheduler)
                .flatMap(userEntity -> Mono.zip(Mono.just(userEntity), adminCliAccessCreds(),
                        (ue, creds) -> Tuples.of(ue, creds)))
                .flatMap(tpl -> postUserRepresentation().apply(tpl))
                .subscribe();
    }

    final static BiConsumer<Throwable, Object> withErrorConsumer(){
        return (Throwable throwable, Object obj) -> {
            if (logger.isDebugEnabled()){
                //logger.error("Got some error ",throwable);
            }

            if (throwable instanceof WebClientResponseException){
                WebClientResponseException ex = (WebClientResponseException)throwable;
                //if (ex.getStatusCode().value() == 409)
                logger.warn("Got a response status {} with text {}...Ignoring for now.  Exception is => {}",
                        new Object[]{ex.getStatusCode().value(),ex.getStatusText(),ex.getMessage()});
            }
        };
    }

    private void writeUserLoadsToIdentityProvider(List<UserLoad> userLoadList) {

        Flux.fromIterable(userLoadList)
                .publishOn(scheduler)
                .flatMap(userLoad -> Mono.zip(Mono.just(userLoad), adminCliAccessCreds(),
                        (ue, creds) -> Tuples.of(ue, creds)))
                .flatMap(tpl -> postUserRepresentationFromUserLoad().apply(tpl))
                .onErrorContinue(withErrorConsumer())
                .subscribe();
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        StepExecution stepExecution = chunkContext.getStepContext().getStepExecution();
        JobExecution jobExecution = stepExecution.getJobExecution();
        ExecutionContext jobContext = jobExecution.getExecutionContext();
        List<UserLoad> userLoadList = (List<UserLoad>) jobContext.get(PROMOTE_USER_LOAD_CHUNK_KEY);
        if (userLoadList != null && !userLoadList.isEmpty()){
            writeUserLoadsToIdentityProvider(userLoadList);
        }


        /*

        List<UserEntity> entities = userLoadList.stream()
                .map(userLoadUserEntityFunction)
                .collect(Collectors.toList());
        Chunk<UserEntity> userEntityChunk = new Chunk<>(entities);
        write(userEntityChunk);

         */
        return RepeatStatus.FINISHED;
    }



    /*
    @ConfigurationProperties(prefix = "idp.provider.keycloak")
    public record KeyCloakIdpProperties(String clientId, String clientSecret, String baseUrl, String tokenUri,
                                        String userUri, String realm, String adminTokenUri, String adminUsername,
                                        String adminPassword, String adminClientId, String adminClientSecret,
                                        String jwksetUri, String issuerUrl) {
    }

     */

    @Builder
    public record CredentialRepresentation(String id, String type, String value) {
    }

    /*
    https://www.keycloak.org/docs-api/22.0.1/rest-api/index.html#UserRepresentation
     */
    @Builder
    public record UserRepresentation(String id, String email, String firstName, String lastName, Long createdTimestamp,
                                     String username, Boolean enabled, Boolean emailVerified,
                                     List<CredentialRepresentation> credentials) {
    }


}
