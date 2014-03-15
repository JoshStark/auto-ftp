package com.github.autoftp.client;

public interface Client {

	void setCredentials(String username, String password);
	void setHost(String host);
	void setPort(int port);
	
	void connect();
	void disconnect();
	
}
