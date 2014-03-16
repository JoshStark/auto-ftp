package com.github.autoftp.client;


public class ClientFactory {

	public enum ClientType {
		FTP, SFTP
	}

	public Client createClient(ClientType clientType) {

		if (clientType == ClientType.FTP)
			return new FtpClient();
	
		return new SftpClient();
	}
}
