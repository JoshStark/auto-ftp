package com.github.autoftp.exception;

@SuppressWarnings("serial")
public class NoSuchDirectoryException extends RuntimeException {

	public NoSuchDirectoryException(String message, Exception cause) {
		super(message, cause);
	}

	public NoSuchDirectoryException(String message) {
		super(message);
	}
}
