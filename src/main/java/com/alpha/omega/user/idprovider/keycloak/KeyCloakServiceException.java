package com.alpha.omega.user.idprovider.keycloak;

public class KeyCloakServiceException extends RuntimeException{
    public KeyCloakServiceException() {
    }

    public KeyCloakServiceException(String message) {
        super(message);
    }

    public KeyCloakServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public KeyCloakServiceException(Throwable cause) {
        super(cause);
    }

    public KeyCloakServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
