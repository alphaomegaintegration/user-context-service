package com.alpha.omega.user.delegate;

import com.alpha.omega.user.model.Context;
import com.alpha.omega.user.model.ContextPage;
import com.alpha.omega.user.model.Role;
import com.alpha.omega.user.model.RolePage;
import com.alpha.omega.user.server.ContextsApiDelegate;
import com.alpha.omega.user.service.ContextService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

import static com.alpha.omega.user.service.ServiceUtils.CORRELATION_ID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContextsDelegate implements ContextsApiDelegate {

    private static final Logger logger = LoggerFactory.getLogger(ContextsDelegate.class);

    ContextService contextService;


    //@PreAuthorize("hasAuthority('CREATE_CONTEXTS')")
    @Override
    public Mono<ResponseEntity<Context>> createContext(Mono<Context> context, ServerWebExchange exchange) {

        return Mono.from(context)
                .flatMap(ctx -> contextService.createContext(ctx))
                .map(ctx -> ResponseEntity.created(calculateContextLocationUri(exchange.getRequest().getURI()))
                        .body(ctx));
    }


    URI calculateContextLocationUri(URI uri) {
        return null;
    }


    @Override
    public Mono<ResponseEntity<Void>> deleteContextByContextId(String contextId, ServerWebExchange exchange) {
        return contextService.deleteContextByContextId(contextId)
                .map(ctx -> ResponseEntity.ok().build());
    }


    @Override
    public Mono<ResponseEntity<ContextPage>> getAllContexts(Integer page, Integer pageSize, String direction, ServerWebExchange exchange) {
        return contextService.getAllContextEntities(PageRequest.of(page, pageSize))
                .doOnNext(contextPage -> logger.debug("Got context page => {}", contextPage))
//                .map(cPage -> ResponseEntity.status(HttpStatus.OK)
//                        .headers(headers -> headers.add(CORRELATION_ID, exchange.getAttribute(CORRELATION_ID)))
//                        .body(cPage))
                .map(val -> {
                    if (val.getContent() != null && !val.getContent().isEmpty()) {
                        return ResponseEntity.status(HttpStatus.OK)
                                .headers(headers -> headers.add(CORRELATION_ID, exchange.getAttribute(CORRELATION_ID)))
                                .body(val);
                    } else {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .headers(headers -> headers.add(CORRELATION_ID, exchange.getAttribute(CORRELATION_ID)))
                                .build();
                    }
                });
    }

    @Override
    public Mono<ResponseEntity<Context>> getContextByContextId(String contextId, ServerWebExchange exchange) {
        return contextService.getContextByContextId(contextId).map(ctx -> {
            if (ctx != null && !StringUtils.isBlank(ctx.getContextId())) {
                return ResponseEntity.status(HttpStatus.OK)
                        .headers(headers -> headers.add(CORRELATION_ID, exchange.getAttribute(CORRELATION_ID)))
                        .body(ctx);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .headers(headers -> headers.add(CORRELATION_ID, exchange.getAttribute(CORRELATION_ID)))
                        .build();
            }
        });
    }

    @Override
    public Mono<ResponseEntity<Context>> updateContextByContextId(String contextId, Mono<Context> context, ServerWebExchange exchange) {
        return contextService.updateContextByContextId(contextId, context)
                .map(val -> {
                    if (val != null) {
                        return ResponseEntity.status(HttpStatus.OK)
                                .headers(headers -> headers.add(CORRELATION_ID, exchange.getAttribute(CORRELATION_ID)))
                                .body(val);
                    } else {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .headers(headers -> headers.add(CORRELATION_ID, exchange.getAttribute(CORRELATION_ID)))
                                .build();
                    }
                });
    }

    @Override
    public Mono<ResponseEntity<RolePage>> getRolesByContextId(String contextId, ServerWebExchange exchange) {
        return contextService.getRolesByContextId(contextId)
                .map(val -> {
                    if (val != null) {
                        return ResponseEntity.status(HttpStatus.OK)
                                .headers(headers -> headers.add(CORRELATION_ID, exchange.getAttribute(CORRELATION_ID)))
                                .body(val);
                    } else {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .headers(headers -> headers.add(CORRELATION_ID, exchange.getAttribute(CORRELATION_ID)))
                                .build();
                    }
                });
    }

    @Override
    public Mono<ResponseEntity<Context>> addAdditionalRolesByContextId(String contextId, Mono<Role> role, ServerWebExchange exchange) {
        return contextService.addAdditionalRolesByContextId(contextId, role)
                .map(val -> {
                    if (val != null) {
                        return ResponseEntity.status(HttpStatus.OK)
                                .headers(headers -> headers.add(CORRELATION_ID, exchange.getAttribute(CORRELATION_ID)))
                                .body(val);
                    } else {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .headers(headers -> headers.add(CORRELATION_ID, exchange.getAttribute(CORRELATION_ID)))
                                .build();
                    }
                });
    }


    @Override
    public Mono<ResponseEntity<Role>> getRoleByContextIdAndRoleId(String contextId, String roleId, ServerWebExchange exchange) {
        return contextService.getRoleByContextIdAndRoleId(contextId, roleId)
                .map(val -> {
                    if (val != null) {
                        return ResponseEntity.status(HttpStatus.OK)
                                .headers(headers -> headers.add(CORRELATION_ID, exchange.getAttribute(CORRELATION_ID)))
                                .body(val);
                    } else {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .headers(headers -> headers.add(CORRELATION_ID, exchange.getAttribute(CORRELATION_ID)))
                                .build();
                    }
                });
    }


}
