package com.alpha.omega.user.server;

import com.alpha.omega.user.model.UserContext;
import com.alpha.omega.user.model.UserContextBatchRequest;
import com.alpha.omega.user.model.UserContextPage;
import com.alpha.omega.user.model.UserContextPermissions;


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
public class UsercontextsApiController implements UsercontextsApi {

    private final UsercontextsApiDelegate delegate;

    public UsercontextsApiController(@Autowired(required = false) UsercontextsApiDelegate delegate) {
        this.delegate = Optional.ofNullable(delegate).orElse(new UsercontextsApiDelegate() {});
    }

    @Override
    public UsercontextsApiDelegate getDelegate() {
        return delegate;
    }

}
