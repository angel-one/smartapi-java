package com.angelbroking.smartapi.http.exceptions;

public class SmartConnectException extends RuntimeException {
    public SmartConnectException() {
        super();
    }

    public SmartConnectException(String message) {
        super(message);
    }

    public SmartConnectException(String message, Throwable cause) {
        super(message, cause);
    }

    public SmartConnectException(Throwable cause) {
        super(cause);
    }
}

