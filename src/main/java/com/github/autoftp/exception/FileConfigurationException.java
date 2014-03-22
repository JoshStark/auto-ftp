package com.github.autoftp.exception;

@SuppressWarnings("serial")
public class FileConfigurationException extends RuntimeException {

	public FileConfigurationException(String message, Exception cause) {
		super(message, cause);
	}
}
