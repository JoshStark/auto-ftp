package com.github.autoftp.client;

import java.io.IOException;
import java.net.SocketException;

import org.apache.commons.net.ftp.FTPClient;

public class FtpClient extends Client {
	
	private FTPClient ftpClient;
	
	public FtpClient() {
		this.ftpClient = new FTPClient();
	}
	
	public void connect() {
		
		try {
			this.ftpClient.connect(host);
		}
		catch (SocketException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}		
	}

	public void disconnect() {
		
		try {
			this.ftpClient.disconnect();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
}
