package com.github.autoftp.connection;

import com.github.autoftp.client.FtpClient;
import com.github.autoftp.client.SftpClient;

public class FtpClientFactory {

	public enum ClientType {
		FTP, SFTP
	}

	public FtpClient getClient(ClientType clientType) {

		if (clientType == ClientType.FTP)
			return new FtpClient();
	
		return new SftpClient();
	}
}
