package com.example.okta_rbac_api.exception;


public class ApplicationUserNotFoundException extends RuntimeException {

    public ApplicationUserNotFoundException(String message) {
        super(message);
    }
}

