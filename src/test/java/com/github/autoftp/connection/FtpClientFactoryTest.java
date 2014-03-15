package com.github.autoftp.connection;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.github.autoftp.client.FtpClient;
import com.github.autoftp.client.SftpClient;
import com.github.autoftp.connection.FtpClientFactory.ClientType;

public class FtpClientFactoryTest {

	private FtpClientFactory factory = new FtpClientFactory();
	
	@Test
	public void factoryShouldReturnNewStfpClientWhenSwitchStringIsSftp() {
		assertThat(factory.getClient(ClientType.SFTP), is(instanceOf(SftpClient.class)));
	}

	@Test
	public void factoryShouldReturnNewFtpClientWhenSwitchedToFtp() {
		assertThat(factory.getClient(ClientType.FTP), is(instanceOf(FtpClient.class)));
	}
}
