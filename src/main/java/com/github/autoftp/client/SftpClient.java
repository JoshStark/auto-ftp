package com.github.autoftp.client;

import com.github.autoftp.connection.Connection;
import com.github.autoftp.connection.SftpConnection;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class SftpClient extends Client {

	private static final String SFTP = "sftp";

	private JSch jsch;

	public SftpClient() {
		this.jsch = new JSch();
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

		return new SftpConnection((ChannelSftp) channel);
	}

	public void disconnect() {
		// TODO Auto-generated method stub

	}

}
