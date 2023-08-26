package com.qortex.inventory.common.exception;

public class ValidationException extends Exception {
	private static final String VALIDATION_MESSAGE = "";

	public ValidationException() {
		super(VALIDATION_MESSAGE);
	}

	public ValidationException(String message) {
		super(VALIDATION_MESSAGE + message);
	}

	public ValidationException(Throwable cause) {
		super(VALIDATION_MESSAGE + cause.getMessage());
	}

	public ValidationException(String message, Throwable cause) {
		super(VALIDATION_MESSAGE + message + ".Root cause - " + cause.getMessage());
	}
}
