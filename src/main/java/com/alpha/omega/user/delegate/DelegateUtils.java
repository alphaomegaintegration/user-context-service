package com.alpha.omega.user.delegate;

import io.micrometer.common.util.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ServerWebExchange;

import java.util.function.Function;
import java.util.function.Supplier;

import static com.alpha.omega.user.service.ServiceUtils.CORRELATION_ID;

public class DelegateUtils {

    public static Function<Supplier<Boolean>, ResponseEntity<?>> determineResponseEntity(Object val, ServerWebExchange exchange){
        return sup ->{
            if (sup.get()) {
                return ResponseEntity.status(HttpStatus.OK)
                        .headers(headers -> headers.add(CORRELATION_ID, exchange.getAttribute(CORRELATION_ID)))
                        .body(val);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .headers(headers -> headers.add(CORRELATION_ID, exchange.getAttribute(CORRELATION_ID)))
                        .build();
            }
        };
    }
}
