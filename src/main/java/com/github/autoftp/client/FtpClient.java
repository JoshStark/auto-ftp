package com.github.autoftp.client;

import java.io.IOException;
import java.net.SocketException;

import org.apache.commons.net.ftp.FTPClient;

public class FtpClient implements Client {

	private String username;
	private String password;
	private String host;
	private int port;
	
	private FTPClient ftpClient;
	
	public FtpClient() {
		this.ftpClient = new FTPClient();
	}
	
	@Override
	public void setCredentials(String username, String password) {
		
		this.username = username;
		this.password = password;		
	}

	@Override
	public void setHost(String host) {
		this.host = host;
	}

	@Override
	public void setPort(int port) {
		this.port = port;
		
	}

	@Override
	public void connect() {
		
		try {
			this.ftpClient.connect(this.host);
		}
		catch (SocketException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}		
	}

	@Override
	public void disconnect() {
		
		try {
			this.ftpClient.disconnect();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
}
