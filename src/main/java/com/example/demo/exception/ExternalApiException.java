package com.example.demo.exception;

public class ExternalApiException extends RuntimeException {

    private final int externalStatus;

    public ExternalApiException(String message, int externalStatus) {
        super(message);
        this.externalStatus = externalStatus;
    }

    public int getExternalStatus() {
        return externalStatus;
    }
}
