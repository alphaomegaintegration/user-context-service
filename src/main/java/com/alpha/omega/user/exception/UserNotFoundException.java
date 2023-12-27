package com.alpha.omega.user.exception;

import com.alpha.omega.user.service.UserContextRequest;

public class UserNotFoundException extends RuntimeException{
    UserContextRequest userContextRequest;

    public UserNotFoundException(UserContextRequest userContextRequest) {
        this.userContextRequest = userContextRequest;
    }

    public UserNotFoundException(String message, UserContextRequest userContextRequest) {
        super(message);
        this.userContextRequest = userContextRequest;
    }


    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(String message, Throwable cause, UserContextRequest userContextRequest) {
        super(message, cause);
        this.userContextRequest = userContextRequest;
    }

    public UserNotFoundException(Throwable cause, UserContextRequest userContextRequest) {
        super(cause);
        this.userContextRequest = userContextRequest;
    }

    public UserNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, UserContextRequest userContextRequest) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.userContextRequest = userContextRequest;
    }

    public UserContextRequest getUserContextRequest() {
        return userContextRequest;
    }
}
