package com.github.autoftp.client;

import com.github.autoftp.connection.Connection;
import com.github.autoftp.connection.ConnectionFactory;
import com.github.autoftp.exception.NotConnectedException;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class SftpClient extends Client {

	private static final String SFTP = "sftp";

	private JSch jsch;
	private ConnectionFactory connectionFactory;

	private Session session;
	private Channel channel;
	
	public SftpClient() {
		this.jsch = new JSch();
		this.connectionFactory = new ConnectionFactory();
	}

	public Connection connect() {

		session = null;
		channel = null;

		try {

			session = jsch.getSession(username, host, port);
			session.setConfig("StrictHostKeyChecking", "no");
			session.setPassword(password);

			session.connect();

			channel = session.openChannel(SFTP);
			channel.connect();

		} catch (JSchException e) {
			e.printStackTrace();
		}

		return connectionFactory.createSftpConnection(channel);
	}

	public void disconnect() {
		
		if(null == channel || null == session)
			throw new NotConnectedException("The underlying connection was never initially made.");
		
		channel.disconnect();
		session.disconnect();
	}

}
