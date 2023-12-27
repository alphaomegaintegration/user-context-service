package com.alpha.omega.user.idprovider.keycloak;

import com.alpha.omega.user.batch.UserLoad;
import com.alpha.omega.user.repository.UserEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.alpha.omega.user.batch.BatchConstants.PROMOTE_USER_ENTITY_CHUNK_KEY;
import static com.alpha.omega.user.batch.BatchConstants.PROMOTE_USER_LOAD_CHUNK_KEY;
import static com.alpha.omega.user.batch.BatchUtil.basicAuthCredsFrom;


@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KeyCloakUserService implements ItemWriter<UserEntity>, StepExecutionListener, Tasklet,
        Consumer<UserLoad> {

    private static final Logger logger = LoggerFactory.getLogger(KeyCloakUserService.class);


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
    JwtDecoder jwtDecoder;

    final static Mono<Map<String, Object>> EMPTY_ACCESS_CREDS = Mono.empty();
    final static ParameterizedTypeReference<Map<String, Object>> MAP_OBJECT = new ParameterizedTypeReference<Map<String, Object>>() {
    };

    private List<UserEntity> userEntities;

    @Override
    public void beforeStep(StepExecution stepExecution) {
        logger.debug("Using keyCloakIdpProperties => {}", keyCloakIdpProperties);
        jwtDecoder = NimbusJwtDecoder.withJwkSetUri(keyCloakIdpProperties.jwksetUri()).build();
        webClient = WebClient.builder()
                .filters(exchangeFilterFunctions -> {
                    //exchangeFilterFunctions.add(logRequest());
                    // exchangeFilterFunctions.add(logResponse());
                })
                .baseUrl(keyCloakIdpProperties.baseUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .build();
        basicAuthCreds = () -> basicAuthCredsFrom(keyCloakIdpProperties.adminClientId, keyCloakIdpProperties.adminClientSecret);

        logger.info("Using basic auth creds {} for username => {} password => {}",
                new Object[]{basicAuthCreds.get(), keyCloakIdpProperties.adminClientId, keyCloakIdpProperties.adminClientSecret});

        accessCreds = accessCreds();
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


    private Mono<Map<String, Object>> accessCreds() {


        return webClient.post()
                //  .uri(keyCloakIdpProperties.tokenUri())
                .uri(keyCloakIdpProperties.adminTokenUri())
                //.headers(h -> h.setBasicAuth(basicAuthCreds.get()))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .body(BodyInserters.fromFormData("grant_type", "client_credentials")
                        .with("client_id", keyCloakIdpProperties.adminClientId())
                        .with("client_secret", keyCloakIdpProperties.adminClientSecret()))
                .retrieve()
                .bodyToMono(MAP_OBJECT);
    }

    private Function<Map<String, Object>, Mono<Map<String, Object>>> validateAccess() {

        return access -> {
            return isExpired(access) ? accessCreds() : EMPTY_ACCESS_CREDS;
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


    Function<Tuple2<? extends UserEntity, Map<String, Object>>, Mono<ResponseEntity<Map<String, Object>>>> postUserRepresentation() {


        return tuple -> {
            return Mono.just(tuple.getT2())
                    .publishOn(Schedulers.boundedElastic())
                    .flatMap(creds -> webClient.post()
                            .uri(uriBuilder -> uriBuilder.path(keyCloakIdpProperties.userUri())
                                    .build(Map.of("realm", "master")))
                            .headers(h -> h.setContentType(MediaType.APPLICATION_JSON))
                            .headers(h -> h.setBearerAuth((String) creds.get("access_token")))
                            .bodyValue(convertToUserRepresentation().apply(tuple.getT1()))
                            .retrieve()
                            .toEntity(MAP_OBJECT));

        };

    }


    /*
    Function<Tuple2<? extends UserLoad, Map<String, Object>>, Mono<ResponseEntity<Map<String, Object>>>> postUserRepresentation3() {


        return tuple -> {
            return Mono.just(tuple.getT2())
                    .publishOn(Schedulers.boundedElastic())
                    .flatMap(creds -> webClient.post()
                            .uri(uriBuilder -> uriBuilder.path(keyCloakIdpProperties.userUri())
                                    .build(Map.of("realm", "master")))
                            .headers(h -> h.setContentType(MediaType.APPLICATION_JSON))
                            .headers(h -> h.setBearerAuth((String) creds.get("access_token")))
                            .bodyValue(convertToUserRepresentation().apply(tuple.getT1()))
                            .retrieve()
                            .toEntity(MAP_OBJECT));

        };

    }

     */


    Predicate<WebClientResponseException> httpExceptionPredicate() {
        return exception -> {
            boolean filter = Boolean.FALSE;

            return filter;
        };
    }

    Function<UserEntity, UserRepresentation> convertToUserRepresentation() {
        final Long createdTime = System.currentTimeMillis();
        return userEntity -> {
            return UserRepresentation.builder()
                    .email(userEntity.getEmail())
                    .username(userEntity.getEmail())
                    .enabled(Boolean.TRUE)
                    .lastName(userEntity.getLastName())
                    .firstName(userEntity.getFirstName())
                    .createdTimestamp(createdTime)
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
                    .lastName(userLoad.getLast())
                    .firstName(userLoad.getFirst())
                    .createdTimestamp(createdTime)
                    .build();
        };
    }

    @Override
    public void write(Chunk<? extends UserEntity> chunk) throws Exception {

        Flux.fromIterable(chunk.getItems())
                .publishOn(Schedulers.boundedElastic())
                .flatMap(userEntity -> Mono.zip(Mono.just(userEntity), accessCreds(),
                        (ue, creds) -> Tuples.of(ue, creds)))
                .flatMap(tpl -> postUserRepresentation().apply(tpl))
                .subscribe();


    }

    public void writeUserLoad() throws Exception {

    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        StepExecution stepExecution = chunkContext.getStepContext().getStepExecution();
        JobExecution jobExecution = stepExecution.getJobExecution();
        ExecutionContext jobContext = jobExecution.getExecutionContext();
        List<UserLoad> userLoadList = (List<UserLoad>) jobContext.get(PROMOTE_USER_LOAD_CHUNK_KEY);
        List<UserEntity> entities = userLoadList.stream()
                .map(userLoadUserEntityFunction)
                .collect(Collectors.toList());
        Chunk<UserEntity> userEntityChunk = new Chunk<>(entities);
        write(userEntityChunk);
        return RepeatStatus.FINISHED;
    }

    @Override
    public void accept(UserLoad userLoad) {

    }

    public Mono<Map<String, Object>> passwordGrantLogin(String username, String password) {


        return webClient.post()
                //  .uri(keyCloakIdpProperties.tokenUri())
                .uri(keyCloakIdpProperties.adminTokenUri())
                //.headers(h -> h.setBasicAuth(basicAuthCreds.get()))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .body(BodyInserters.fromFormData("grant_type", "password")
                        .with("username", username)
                        .with("password", password)
                        .with("client_id", keyCloakIdpProperties.adminClientId())
                        .with("client_secret", keyCloakIdpProperties.adminClientSecret()))
                .retrieve()
                .bodyToMono(MAP_OBJECT);
    }

    public Mono<Optional<Jwt>> passwordGrantLoginJwt(String username, String password) {


        return webClient.post()
                .uri(keyCloakIdpProperties.tokenUri())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .body(BodyInserters.fromFormData("grant_type", "password")
                        .with("username", username)
                        .with("password", password)
                        .with("client_id", keyCloakIdpProperties.clientId())
                        .with("client_secret", keyCloakIdpProperties.clientSecret()))
                .retrieve()
                .bodyToMono(MAP_OBJECT)
                .map(convertResultMapToJwt(jwtDecoder));
    }

    final static Function<Map<String, Object>,Optional<Jwt>> convertResultMapToJwt(JwtDecoder jwtDecoder) {
        return map -> {
            String accessToken = (String)map.get("access_token");
            Jwt jwt = null;
            try{
                jwt = jwtDecoder.decode(accessToken);
            } catch (Exception e){
                logger.warn("Could not decode jwt!",e);
            }
            return Optional.ofNullable(jwt);
        };
    }


    @ConfigurationProperties(prefix = "idp.provider.keycloak")
    public record KeyCloakIdpProperties(String clientId, String clientSecret, String baseUrl, String tokenUri,
                                        String userUri, String realm, String adminTokenUri, String adminUsername,
                                        String adminPassword, String adminClientId, String adminClientSecret,
                                        String jwksetUri) {
    }

    /*
    UserRepresentation
     */

    /*
    public JwtDecoder getObject() {
        return NimbusJwtDecoder.withJwkSetUri(this.jwkSetUri).build();
    }

     */

    public record CredentialRepresentation(String id, String type, String value) {
    }

    @Builder
    public record UserRepresentation(String id, String email, String firstName, String lastName, Long createdTimestamp,
                                     String username, Boolean enabled) {
    }


}
