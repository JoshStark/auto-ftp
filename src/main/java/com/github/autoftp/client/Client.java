package com.github.autoftp.client;

public abstract class Client {

	protected String username;
	protected String password;
	protected String host;
	protected int port;
	
	public void setCredentials(String username, String password) {
		
		this.username = username;
		this.password = password;		
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
	public abstract void connect();
	public abstract void disconnect();
}
