package com.github.autoftp.exception;

@SuppressWarnings("serial")
public class ClientDisconnectionException extends RuntimeException {

	public ClientDisconnectionException(String message) {
		super(message);
	}
	
	public ClientDisconnectionException(String message, Exception cause) {
		super(message, cause);
	}
}
