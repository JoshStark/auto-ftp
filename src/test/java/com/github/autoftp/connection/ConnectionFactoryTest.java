package com.github.autoftp.connection;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

import com.jcraft.jsch.ChannelSftp;

public class ConnectionFactoryTest {

	private ConnectionFactory connectionFactory = new ConnectionFactory();
	
	@Test
	public void createSftpConnectionShouldReturnNewInstanceOfSftpConnection() {
		
		ChannelSftp channel = new ChannelSftp();
		
		assertThat(connectionFactory.createSftpConnection(channel), is(instanceOf(SftpConnection.class)));
	}
	
}
