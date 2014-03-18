package com.github.autoftp.exception;

@SuppressWarnings("serial")
public class ConfigCorruptedException extends RuntimeException {

	public ConfigCorruptedException(String message, Exception cause) {
		super(message, cause);
	}
}
