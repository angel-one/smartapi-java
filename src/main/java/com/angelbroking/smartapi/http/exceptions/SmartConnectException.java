package com.angelbroking.smartapi.http.exceptions;

public class SmartConnectException extends RuntimeException {

    public SmartConnectException(String message) {
        super(message);
    }

    public SmartConnectException(String message, Throwable cause) {
        super(message, cause);
    }
}

