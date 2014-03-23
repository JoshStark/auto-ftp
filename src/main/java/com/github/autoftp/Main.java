package com.github.autoftp;

public class Main {

	static {
		System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
	}

	public static void main(String[] args) {
		new AutoFtpScreen();
	}
}
