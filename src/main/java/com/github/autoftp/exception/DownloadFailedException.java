package com.github.autoftp.exception;

@SuppressWarnings("serial")
public class DownloadFailedException extends RuntimeException {

	public DownloadFailedException(String message, Exception cause) {
		super(message, cause);
	}
	
	public DownloadFailedException(String message) {
		super(message);
	}
}
