package com.alpha.omega.user.idprovider.keycloak;

import com.alpha.omega.user.model.UserContextPermissions;
import com.alpha.omega.user.service.UserContextRequest;
import com.alpha.omega.user.service.UserContextService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KeyCloakAuthenticationManager extends AbstractUserDetailsReactiveAuthenticationManager {

    public static final String KEY_CLOAK_DEFAULT_CONTEXT = "user-context-service";
    //private UserDetailsService userDetailsService;
    private String defaultContext;
    private UserContextService userContextService;
    private KeyCloakUserService keyCloakUserService;
    @Builder.Default
    private Scheduler scheduler = Schedulers.boundedElastic();


    Function<UserContextPermissions, UserDetails> convertToUserDetails(){
        return userContextPermissions -> {
            List<GrantedAuthority> grantedAuthorities = userContextPermissions.getPermissions()
                    .stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            UserDetails user = new User(userContextPermissions.getUserId(), null, grantedAuthorities);
            return user;
        };
    }

    @Override
    protected Mono<UserDetails> retrieveUser(String username) {
        final UserContextRequest userContextRequest = UserContextRequest.builder()
                .contextId(defaultContext)
                .userId(username)
                .build();

        return Mono.just(userContextRequest)
                .publishOn(this.scheduler)
                .flatMap(request -> userContextService.getUserContextByUserIdAndContextId(request))
                .map(convertToUserDetails());
    }

    // JwtAuthenticationToken
    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String username = authentication.getName();
        String presentedPassword = (String) authentication.getCredentials();
        // @formatter:off
        return retrieveUser(username)
                .doOnNext(userDetails -> defaultPreAuthenticationChecks(userDetails))
                .publishOn(this.scheduler)
                .flatMap(userDetails -> keyCloakUserService.passwordGrantLoginJwt(username, presentedPassword).map(jwt -> Tuples.of(userDetails, jwt)))
                .filter((tuple) -> tuple.getT2().isPresent())
                .switchIfEmpty(Mono.defer(() -> Mono.error(new BadCredentialsException("Invalid Credentials"))))
                .doOnNext(tuple -> defaultPostAuthenticationChecks(tuple.getT1()))
                .map(tuple -> this.createJwtAuthenticationToken(tuple));
        }

    private JwtAuthenticationToken createJwtAuthenticationToken(Tuple2<UserDetails, Optional<Jwt>> tuple) {
         /*
            public User(String username, String password, boolean enabled, boolean accountNonExpired,
			boolean credentialsNonExpired, boolean accountNonLocked,
			Collection<? extends GrantedAuthority> authorities)
             */
        UserDetails user = tuple.getT1();
        Jwt jwt = tuple.getT2().get();
        User newUser = new User(user.getUsername(), jwt.getTokenValue(), user.isEnabled(), user.isAccountNonExpired(),
                jwt.getExpiresAt().isBefore(Instant.now()),user.isAccountNonLocked(), user.getAuthorities());
        JwtAuthenticationToken token = new JwtAuthenticationToken(tuple.getT2().get(), tuple.getT1().getAuthorities());
        return token;
    }

    private void defaultPreAuthenticationChecks(UserDetails user) {
        if (!user.isAccountNonLocked()) {
            this.logger.debug("User account is locked");
            throw new LockedException(this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.locked",
                    "User account is locked"));
        }
        if (!user.isEnabled()) {
            this.logger.debug("User account is disabled");
            throw new DisabledException(
                    this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.disabled", "User is disabled"));
        }
        if (!user.isAccountNonExpired()) {
            this.logger.debug("User account is expired");
            throw new AccountExpiredException(this.messages
                    .getMessage("AbstractUserDetailsAuthenticationProvider.expired", "User account has expired"));
        }
    }

    private void defaultPostAuthenticationChecks(UserDetails user) {
        if (!user.isCredentialsNonExpired()) {
            this.logger.debug("User account credentials have expired");
            throw new CredentialsExpiredException(this.messages.getMessage(
                    "AbstractUserDetailsAuthenticationProvider.credentialsExpired", "User credentials have expired"));
        }
    }
}
