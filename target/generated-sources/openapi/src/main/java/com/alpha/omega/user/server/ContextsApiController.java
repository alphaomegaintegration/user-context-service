package com.alpha.omega.user.server;

import com.alpha.omega.user.model.Context;
import com.alpha.omega.user.model.ContextPage;
import com.alpha.omega.user.model.Role;
import com.alpha.omega.user.model.RolePage;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.*;
import javax.validation.Valid;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Generated;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-12-20T06:26:02.527872-05:00[America/New_York]")
@Controller
@RequestMapping("${openapi.userContextService.base-path:}")
public class ContextsApiController implements ContextsApi {

    private final ContextsApiDelegate delegate;

    public ContextsApiController(@Autowired(required = false) ContextsApiDelegate delegate) {
        this.delegate = Optional.ofNullable(delegate).orElse(new ContextsApiDelegate() {});
    }

    @Override
    public ContextsApiDelegate getDelegate() {
        return delegate;
    }

}
