package com.angelbroking.smartapi.http.exceptions;

/**
 * Exceptions raised when invalid data is returned from Smart API trade.
 */

public class DataSmartAPIException extends SmartAPIException {

    public DataSmartAPIException(String message, String code){
        super(message, code);
    }
}

