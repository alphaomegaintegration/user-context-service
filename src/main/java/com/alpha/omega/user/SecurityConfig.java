package com.alpha.omega.user;

import com.alpha.omega.user.client.auth.Authentication;
import com.alpha.omega.user.idprovider.keycloak.KeyCloakAuthenticationManager;
import com.alpha.omega.user.idprovider.keycloak.KeyCloakUserService;
import com.alpha.omega.user.idprovider.keycloak.KeycloakJwtRolesConverter;
import com.alpha.omega.user.idprovider.keycloak.KeycloakJwtRolesReactiveConverter;
import com.alpha.omega.user.service.UserContextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.oauth2.server.resource.authentication.DelegatingJwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.server.DelegatingServerAuthenticationEntryPoint;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.*;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.security.web.server.util.matcher.*;

import static com.alpha.omega.user.idprovider.keycloak.KeyCloakAuthenticationManager.KEY_CLOAK_DEFAULT_CONTEXT;
import static org.springframework.web.reactive.function.client.ExchangeStrategies.withDefaults;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.server.WebFilter;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

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

    @Bean("idProviderAuthenticationManager")
    KeyCloakAuthenticationManager keyCloakAuthenticationManager(UserContextService userContextService, KeyCloakUserService keyCloakUserService){
        return KeyCloakAuthenticationManager.builder()
                .userContextService(userContextService)
                .keyCloakUserService(keyCloakUserService)
                .defaultContext(KEY_CLOAK_DEFAULT_CONTEXT)
                .build();
    }

    @Order(Ordered.HIGHEST_PRECEDENCE)
    @Bean
    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http,
                                                     @Qualifier("idProviderAuthenticationManager")ReactiveAuthenticationManager authenticationManager,
                                                     KeyCloakUserService.KeyCloakIdpProperties keyCloakIdpProperties) {
        // @formatter:off
        http.authorizeExchange((authorize) -> authorize
                        .pathMatchers("/**").permitAll()
                        //.anyExchange().authenticated()
                )
                .httpBasic(httpBasicSpec -> httpBasicSpec.authenticationManager(authenticationManager));
              //  .formLogin((form) -> form
              //          .loginPage("/login")
              //  );
        //WebFilter webFilter = createHttpBasicFilter(authenticationManager);
        //http.addFilterAt(webFilter, SecurityWebFiltersOrder.HTTP_BASIC);
        //http.oauth2Client(oAuth2ClientSpec -> oAuth2ClientSpec.authorizationRequestResolver())
        http.oauth2ResourceServer(oAuth2ResourceServerSpec ->
            oAuth2ResourceServerSpec.jwt(jwtSpec -> jwtSpec.jwtAuthenticationConverter(new KeycloakJwtRolesReactiveConverter())
                    .jwkSetUri(keyCloakIdpProperties.jwksetUri()))

        );
        http.csrf(csrfSpec -> csrfSpec.disable());
        return http.build();
    }

    AuthenticationWebFilter createHttpBasicFilter(ReactiveAuthenticationManager authenticationManager){
        final ServerWebExchangeMatcher xhrMatcher = (exchange) -> Mono.just(exchange.getRequest().getHeaders())
                .filter((h) -> h.getOrEmpty("X-Requested-With").contains("XMLHttpRequest"))
                .flatMap((h) -> ServerWebExchangeMatcher.MatchResult.match())
                .switchIfEmpty(ServerWebExchangeMatcher.MatchResult.notMatch());
        MediaTypeServerWebExchangeMatcher restMatcher = new MediaTypeServerWebExchangeMatcher(
                MediaType.APPLICATION_ATOM_XML, MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_OCTET_STREAM, MediaType.APPLICATION_XML, MediaType.MULTIPART_FORM_DATA,
                MediaType.TEXT_XML);
        restMatcher.setIgnoredMediaTypes(Collections.singleton(MediaType.ALL));
        ServerWebExchangeMatcher notHtmlMatcher = new NegatedServerWebExchangeMatcher(new MediaTypeServerWebExchangeMatcher(MediaType.TEXT_HTML));
        ServerWebExchangeMatcher restNotHtmlMatcher = new AndServerWebExchangeMatcher(Arrays.asList(notHtmlMatcher, restMatcher));
        ServerWebExchangeMatcher preferredMatcher = new OrServerWebExchangeMatcher(Arrays.asList(xhrMatcher, restNotHtmlMatcher));
        List<DelegatingServerAuthenticationEntryPoint.DelegateEntry> entryPoints = new ArrayList<>();
        entryPoints.add(new DelegatingServerAuthenticationEntryPoint.DelegateEntry(xhrMatcher, new HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED)));
        DelegatingServerAuthenticationEntryPoint defaultEntryPoint = new DelegatingServerAuthenticationEntryPoint(entryPoints);
        defaultEntryPoint.setDefaultEntryPoint(new HttpBasicServerAuthenticationEntryPoint());
        //ServerHttpSecurity.this.defaultEntryPoints.add(new DelegatingServerAuthenticationEntryPoint.DelegateEntry(preferredMatcher, defaultEntryPoint));
        AuthenticationWebFilter authenticationFilter = new AuthenticationWebFilter(authenticationManager);
        authenticationFilter.setRequiresAuthenticationMatcher(preferredMatcher);
        authenticationFilter.setAuthenticationFailureHandler(new ServerAuthenticationEntryPointFailureHandler(defaultEntryPoint));
        authenticationFilter.setAuthenticationConverter(new ServerHttpBasicAuthenticationConverter());
        authenticationFilter.setSecurityContextRepository(NoOpServerSecurityContextRepository.getInstance());
        return authenticationFilter;
        //http.addFilterAt(authenticationFilter, SecurityWebFiltersOrder.HTTP_BASIC);
    }

}
