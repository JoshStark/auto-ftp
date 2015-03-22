package com.github.autoftp.url;

public class PushbulletException extends RuntimeException {

	private static final long serialVersionUID = -7847731833231577727L;

	public PushbulletException(String message) {
		super(message);
	}
	
	public PushbulletException(String message, Exception cause) {
		super(message, cause);
	}
}
