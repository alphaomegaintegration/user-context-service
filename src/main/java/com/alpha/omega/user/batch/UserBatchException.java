package com.alpha.omega.user.batch;

public class UserBatchException extends RuntimeException{
    public UserBatchException(String message) {
        super(message);
    }

    public UserBatchException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserBatchException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
