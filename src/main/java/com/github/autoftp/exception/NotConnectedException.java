package com.github.autoftp.exception;

@SuppressWarnings("serial")
public class NotConnectedException extends RuntimeException {

	public NotConnectedException(String message) {
		super(message);
	}
}
