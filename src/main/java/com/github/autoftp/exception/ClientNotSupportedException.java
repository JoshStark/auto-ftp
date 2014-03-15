package com.github.autoftp.exception;

@SuppressWarnings("serial")
public class ClientNotSupportedException extends RuntimeException {

	public ClientNotSupportedException(String message) {
		super(message);
	}
	
	public ClientNotSupportedException(String message, Exception e) {
		super(message, e);
	}
}
