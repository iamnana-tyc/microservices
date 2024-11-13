package com.iamnana.microservice.exception;

public class ProductPurchaseException extends RuntimeException {
    public ProductPurchaseException(String message){
        super(message);
    }
}
