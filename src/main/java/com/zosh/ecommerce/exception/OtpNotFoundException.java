package com.zosh.ecommerce.exception;

public class OtpNotFoundException extends RuntimeException{
    public OtpNotFoundException(String message) {
        super(message);
    }
}
