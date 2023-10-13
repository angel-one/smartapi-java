package com.angelbroking.smartapi.http.exceptions;

/**
 * Exception raised when invalid API Key is provided for Smart API trade.
 */

public class ApiKeyException extends SmartAPIException {

    // initialize 2fa exception and call constructor of Base Exception
    public ApiKeyException(String message, String code){
        super(message, code);
    }
}

