package com.zosh.ecommerce.exception;

public class InvalidImageException extends RuntimeException{

    public InvalidImageException(String message) {
        super(message);
    }
}