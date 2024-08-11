package com.zosh.ecommerce.exception;

public class JwtSignatureException  extends RuntimeException{
    public JwtSignatureException(String message){
        super(message);
    }
}
