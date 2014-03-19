package com.github.autoftp.exception;

@SuppressWarnings("serial")
public class ConnectionInitialisationException extends RuntimeException {

	public ConnectionInitialisationException(String message, Exception cause) {
		super(message, cause);
	}

	public ConnectionInitialisationException(String message) {
		super(message);
	}
}
