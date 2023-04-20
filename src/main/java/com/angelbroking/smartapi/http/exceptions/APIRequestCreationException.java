package com.angelbroking.smartapi.http.exceptions;

public class APIRequestCreationException extends RuntimeException {
    public APIRequestCreationException(String message) {
        super(message);
    }

    public APIRequestCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
