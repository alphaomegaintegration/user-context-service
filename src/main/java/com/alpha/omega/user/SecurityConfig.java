package com.alpha.omega.user;

import com.alpha.omega.security.ClientRegistrationConfig;
import com.alpha.omega.user.idprovider.keycloak.*;
import com.alpha.omega.user.service.RedisUserContextService;
import com.alpha.omega.user.service.UserContextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.server.DefaultServerOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.server.DelegatingServerAuthenticationEntryPoint;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.*;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.security.web.server.util.matcher.*;


import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@Import(value={ClientRegistrationConfig.class})
public class SecurityConfig {

    /*
    https://docs.spring.io/spring-security/reference/reactive/authorization/method.html
     */
    /*
    @Bean
    SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

        DelegatingJwtGrantedAuthoritiesConverter authoritiesConverter =
                // Using the delegating converter multiple converters can be combined
                new DelegatingJwtGrantedAuthoritiesConverter(
                        // First add the default converter
                        new JwtGrantedAuthoritiesConverter(),
                        // Second add our custom Keycloak specific converter
                        new KeycloakJwtRolesConverter());

        // Set up http security to use the JWT converter defined above
        httpSecurity.oauth2ResourceServer().jwt().jwtAuthenticationConverter(
                jwt -> new JwtAuthenticationToken(jwt, authoritiesConverter.convert(jwt)));

        httpSecurity.authorizeHttpRequests(authorize -> authorize
                // Only users with the role "user" can access the endpoint /user.
                .requestMatchers("/user").hasAuthority(KeycloakJwtRolesConverter.PREFIX_RESOURCE_ROLE + "rest-api_user")
                // Only users with the role "admin" can access the endpoint /admin.
                .requestMatchers("/admin").hasAuthority(KeycloakJwtRolesConverter.PREFIX_RESOURCE_ROLE + "rest-api_admin")
                // All users, even once without an access token, can access the endpoint /public.
                .requestMatchers("/public").permitAll()
        );

        return httpSecurity.build();
    }

    @Order(Ordered.HIGHEST_PRECEDENCE)
    @Bean
    SecurityWebFilterChain apiHttpSecurity(ServerHttpSecurity http) {
        http
                .securityMatcher(new PathPatternParserServerWebExchangeMatcher("/api/**"))
                .authorizeExchange((exchanges) -> exchanges
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(ServerHttpSecurity.OAuth2ResourceServerSpec::new);
        return http.build();
    }

     */



    /*
    @Bean
    public MapReactiveUserDetailsService userDetailsService() {
        // @formatter:off
        UserDetails user = User.withDefaultPasswordEncoder()
                .username("user")
                .password("password")
                .roles("USER")
                .build();
        // @formatter:on
        return new MapReactiveUserDetailsService(user);
    }

     */

    @Bean
    ReactiveUserDetailsService userDetailsService(UserContextService userContextService){
        return RedisUserContextService.RedisReactiveUserDetailsService.builder()
                .userContextService(userContextService)
                .contextId(KeyCloakUtils.KEY_CLOAK_DEFAULT_CONTEXT)
                .build();
    }

    @Bean("idProviderAuthenticationManager")
    @ConditionalOnMissingBean
    ReactiveAuthenticationManager keyCloakAuthenticationManager(UserContextService userContextService,
                                                                KeyCloakUserService keyCloakUserService,
                                                                Environment env){
        return KeyCloakAuthenticationManager.builder()
                .defaultContext(KeyCloakUtils.KEY_CLOAK_DEFAULT_CONTEXT)
                .userContextService(userContextService)
                .keyCloakUserService(keyCloakUserService)
                .realmClientId(env.getProperty("idp.provider.keycloak.client-id"))
                .realmClientSecret(env.getProperty("idp.provider.keycloak.client-secret"))
                .realmTokenUri(env.getProperty("idp.provider.keycloak.token-uri"))
                .realmBaseUrl(env.getProperty("idp.provider.keycloak.base-url"))
                .realmJwkSetUri(env.getProperty("idp.provider.keycloak.jwkset-uri"))
                .issuerURL(env.getProperty("idp.provider.keycloak.issuer-url"))
                .build();
    }

    @Order(Ordered.HIGHEST_PRECEDENCE)
    @Bean
    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http,
                                                     @Qualifier("idProviderAuthenticationManager")ReactiveAuthenticationManager authenticationManager,
                                                     KeyCloakIdpProperties keyCloakIdpProperties) {
        // @formatter:off
        http.authorizeExchange((authorize) -> authorize ///usercontexts/user/{userId}/context/{contextId}
                        //.pathMatchers("/**").permitAll()
                        //.pathMatchers("").access()


                        .pathMatchers(HttpMethod.POST, "/contexts").hasAuthority("CREATE_CONTEXTS")
                        .pathMatchers(HttpMethod.GET, "/contexts").hasAuthority("LIST_CONTEXTS")
                        .pathMatchers(HttpMethod.POST, "/usercontexts").hasAuthority("CREATE_USER_CONTEXTS")
                        .pathMatchers(HttpMethod.GET, "/usercontexts").hasAuthority("LIST_USER_CONTEXTS")
                        .pathMatchers(HttpMethod.POST, "/usercontexts/user/*/context/*/role/*").hasAuthority("CREATE_USER_CONTEXTS")
                        .pathMatchers(HttpMethod.PATCH, "/usercontexts/user/*/context/*/role/*").hasAuthority("CREATE_USER_CONTEXTS")
                        .pathMatchers(HttpMethod.GET, "/usercontexts/user/*/context/*").hasAuthority("LIST_USER_CONTEXTS")
                        .pathMatchers(HttpMethod.GET,"/actuator/**").hasAuthority("VIEW_CONFIGURATIONS")
                        .anyExchange().authenticated()

                )
                .httpBasic(httpBasicSpec -> httpBasicSpec.authenticationManager(authenticationManager));
              //  .formLogin((form) -> form
              //          .loginPage("/login")
              //  );
        //WebFilter webFilter = createHttpBasicFilter(authenticationManager);
        //http.addFilterAt(webFilter, SecurityWebFiltersOrder.HTTP_BASIC);
        //http.oauth2Client(oAuth2ClientSpec -> oAuth2ClientSpec.authorizationRequestResolver())
        http.oauth2ResourceServer(oAuth2ResourceServerSpec -> oAuth2ResourceServerSpec.authenticationManagerResolver(new KeyCloakReactiveAuthenticationManagerResolver(authenticationManager)));
        //http.oauth2ResourceServer(oAuth2ResourceServerSpec -> oAuth2ResourceServerSpec.bearerTokenConverter());
        //http.authenticationManager(authenticationManager);
        http.csrf(csrfSpec -> csrfSpec.disable());
        return http.build();
    }

    @Autowired
    private ReactiveClientRegistrationRepository clientRegistrationRepository;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http.authorizeExchange(authorize -> authorize.anyExchange().authenticated())
                .oauth2Login(oauth2 -> oauth2
                        .authorizationRequestResolver(
                                authorizationRequestResolver(this.clientRegistrationRepository)
                        )
                );
        return http.build();
    }

    private ServerOAuth2AuthorizationRequestResolver authorizationRequestResolver(
            ReactiveClientRegistrationRepository clientRegistrationRepository) {

        DefaultServerOAuth2AuthorizationRequestResolver authorizationRequestResolver =
                new DefaultServerOAuth2AuthorizationRequestResolver(
                        clientRegistrationRepository);
        authorizationRequestResolver.setAuthorizationRequestCustomizer(
                authorizationRequestCustomizer());

        return  authorizationRequestResolver;
    }

    private Consumer<OAuth2AuthorizationRequest.Builder> authorizationRequestCustomizer() {
        return customizer -> customizer
                .additionalParameters(params -> params.put("prompt", "consent"));
    }

}
