package com.zosh.ecommerce.exception;

public class ResourceNotFoundException extends RuntimeException{
    String resourceName;
    String fieldName;
    Long fieldValue;
    String details;

    public ResourceNotFoundException(String resourceName, String fieldName, Long fieldValue) {
        super(String.format("%s not found with %s: %s", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s : '%s'", resourceName, fieldName, fieldValue));
        this.details = String.format("Resource not found with %s : '%s'", fieldName, fieldValue);
    }

    public ResourceNotFoundException(String cartNotFoundWitId, Long userId) {
    }
}
