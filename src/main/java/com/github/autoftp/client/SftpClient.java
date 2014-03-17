package com.github.autoftp.client;

import com.github.autoftp.connection.Connection;
import com.github.autoftp.connection.ConnectionFactory;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class SftpClient extends Client {

	private static final String SFTP = "sftp";

	private JSch jsch;
	private ConnectionFactory connectionFactory;

	public SftpClient() {
		this.jsch = new JSch();
		this.connectionFactory = new ConnectionFactory();
	}

	public Connection connect() {

		Session session = null;
		Channel channel = null;

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
		// TODO Auto-generated method stub

	}

}
