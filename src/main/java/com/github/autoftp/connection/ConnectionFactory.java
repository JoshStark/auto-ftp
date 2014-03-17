package com.github.autoftp.connection;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;

public class ConnectionFactory {

	public SftpConnection createSftpConnection(Channel channel) {
		return new SftpConnection((ChannelSftp) channel);
	}
}
