package com.alpha.omega.security;

import com.alpha.omega.user.model.UserContextPermissions;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.nio.charset.Charset;
import java.util.Base64;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.alpha.omega.user.utils.Constants.COLON;

public class SecurityUtils {


    public static String basicAuthCredsFrom(String username, String password){
        return Base64.getEncoder().encodeToString(new StringBuilder(username)
                .append(COLON)
                .append(password)
                .toString().getBytes(Charset.defaultCharset()));
    }

    public static Function<UserContextPermissions, UserDetails> convertUserContextPermissionsToUserDetails() {
        return userContextPermissions -> {
            List<GrantedAuthority> grantedAuthorities = userContextPermissions.getPermissions()
                    .stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            UserDetails user = SecurityUser.builder()
                    .authorities(grantedAuthorities)
                    .username(userContextPermissions.getUserId())
                    .build();
            return user;
        };
    }
}
