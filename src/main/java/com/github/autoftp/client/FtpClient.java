package com.github.autoftp.client;

import java.io.IOException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import com.github.autoftp.connection.Connection;
import com.github.autoftp.connection.ConnectionFactory;
import com.github.autoftp.exception.ClientDisconnectionException;
import com.github.autoftp.exception.ConnectionInitialisationException;

public class FtpClient extends Client {

	private static final int FIVE_MINUTES = 300;
	private static final String UNABLE_TO_LOGIN_MESSAGE = "Unable to login for user %s";
	private static final String CONNECTION_ERROR_MESSAGE = "Unable to connect to host %s on port %d";
	private static final String STATUS_ERROR_MESSAGE = "The host %s on port %d returned a bad status code.";

	private FTPClient ftpClient;
	private ConnectionFactory connectionFactory;

	public FtpClient() {
		this.ftpClient = new FTPClient();
		this.connectionFactory = new ConnectionFactory();
	}

	public Connection connect() {

		try {

			this.ftpClient.connect(host, port);

			if (!FTPReply.isPositiveCompletion(this.ftpClient.getReplyCode()))
				throw new ConnectionInitialisationException(String.format(STATUS_ERROR_MESSAGE, host, port));

			this.ftpClient.enterLocalPassiveMode();
			this.ftpClient.setControlKeepAliveTimeout(FIVE_MINUTES);

			boolean hasLoggedIn = this.ftpClient.login(username, password);

			if (!hasLoggedIn)
				throw new ConnectionInitialisationException(String.format(UNABLE_TO_LOGIN_MESSAGE, username));

		} catch (IOException e) {
			throw new ConnectionInitialisationException(String.format(CONNECTION_ERROR_MESSAGE, host, port), e);
		}

		return this.connectionFactory.createFtpConnection(this.ftpClient);
	}

	public void disconnect() {

		try {

			if (null == this.ftpClient)
				throw new ClientDisconnectionException("The underlying client was null.");

			if (this.ftpClient.isConnected())
				this.ftpClient.disconnect();

		} catch (IOException e) {
			throw new ClientDisconnectionException("There was an unexpected error while trying to disconnect.", e);
		}
	}
}
