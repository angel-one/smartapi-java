package com.angelbroking.smartapi.http.exceptions;

/**
 * This is the base exception class which has a publicly accessible message and
 * code that is received from Angel Connect api.
 */

public class SmartAPIException extends Throwable {

	private static final long serialVersionUID = 1L;
	// variables
	private String message;
	private String code;

	// constructor that sets the message and code
	public SmartAPIException(String message, String code) {
		this.message = message;
		this.code = code;
	}

	// constructor that sets the message
	public SmartAPIException(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return String.format("SmartAPIException [message=%s, code=%s]", message, code);
	}

}
