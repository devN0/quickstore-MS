package com.quickstore.orderservice.exceptions;

public class ProductNotInStockException extends IllegalArgumentException {
    public ProductNotInStockException(String message) {
        super(message);
    }
}
