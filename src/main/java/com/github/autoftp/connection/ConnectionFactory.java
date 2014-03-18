package com.github.autoftp.connection;

import org.apache.commons.net.ftp.FTPClient;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;

public class ConnectionFactory {

	public SftpConnection createSftpConnection(Channel channel) {
		return new SftpConnection((ChannelSftp) channel);
	}
	
	public FtpConnection createFtpConnection(FTPClient client) {
		return new FtpConnection(client);
	}
}
