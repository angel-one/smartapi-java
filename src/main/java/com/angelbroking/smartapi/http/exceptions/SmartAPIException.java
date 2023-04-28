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
		StringBuilder sb = new StringBuilder();
		sb.append("SmartAPIException{")
				.append("message='").append(message).append('\'')
				.append(", code='").append(code).append('\'')
				.append('}');
		return sb.toString();
	}

}
