package com.github.autoftp;

import org.joda.time.DateTime;

public class ScreenPrinter {

	public void printDownload(String filename) {
		String formattedMessage = String.format("%s [Download] %s", DateTime.now().toString("dd/MM/yyy HH:mm:ss"), filename);
		
		System.out.println(formattedMessage);
	}
	
	public void printError(String message) {
		String formattedMessage = String.format("%s [Error] %s", DateTime.now().toString("dd/MM/yyy HH:mm:ss"), message);
		
		System.out.println(formattedMessage);
	}
	
	public void printInfo(String info) {
		String formattedMessage = String.format("%s [Info] %s", DateTime.now().toString("dd/MM/yyy HH:mm:ss"), info);
		
		System.out.println(formattedMessage);
	}
}
