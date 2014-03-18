package com.github.autoftp.client;

import java.io.IOException;

import org.apache.commons.net.ftp.FTPClient;

import com.github.autoftp.connection.Connection;
import com.github.autoftp.connection.ConnectionFactory;
import com.github.autoftp.exception.ConnectionInitialisationException;

public class FtpClient extends Client {
	
	private static final String CONNECTION_ERROR_MESSAGE = "Unable to connect to host %s on port %d";
	
	private FTPClient ftpClient;
	private ConnectionFactory connectionFactory;
	
	public FtpClient() {
		this.ftpClient = new FTPClient();
		this.connectionFactory = new ConnectionFactory();
	}
	
	public Connection connect() {
		
		try {
			
			this.ftpClient.connect(host, port);
			this.ftpClient.enterLocalPassiveMode();
			this.ftpClient.login(username, password);
			
		}
		catch (IOException e) {
			throw new ConnectionInitialisationException(String.format(CONNECTION_ERROR_MESSAGE, host, port), e);
		}	
		
		return this.connectionFactory.createFtpConnection(this.ftpClient);
	}

	public void disconnect() {
		
		try {
			this.ftpClient.disconnect();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
}
