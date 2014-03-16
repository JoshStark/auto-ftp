package com.github.autoftp.client;

import com.github.autoftp.connection.Connection;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class SftpClient extends Client {

	private JSch jsch;
	
	public SftpClient() {
		this.jsch = new JSch();
	}
	
	public Connection connect() {
		
		try {
			
			Session session = jsch.getSession(username, host, port);
			session.setConfig("StrictHostKeyChecking", "no");
			session.setPassword(password);
			
			session.connect();
			
		} catch (JSchException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	public void disconnect() {
		// TODO Auto-generated method stub
		
	}

}
