package com.angelbroking.smartapi.http.exceptions;

/**
 * An unclassified, general error. Default code is 500
 */
public class GeneralException extends SmartAPIException {
    // initialize and call the base class
    public GeneralException(String message, String code){
        super(message, code);
    }
}
