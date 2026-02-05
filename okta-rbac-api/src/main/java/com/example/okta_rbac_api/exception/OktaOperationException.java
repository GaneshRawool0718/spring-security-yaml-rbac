package com.example.okta_rbac_api.exception;

public class OktaOperationException extends RuntimeException {

    public OktaOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}

