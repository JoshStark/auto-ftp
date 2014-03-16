package com.github.autoftp.client;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.github.autoftp.client.ClientFactory.ClientType;

public class ClientFactoryTest {

	private ClientFactory factory = new ClientFactory();
	
	@Test
	public void factoryShouldReturnNewStfpClientWhenSwitchStringIsSftp() {
		assertThat(factory.createClient(ClientType.SFTP), is(instanceOf(SftpClient.class)));
	}

	@Test
	public void factoryShouldReturnNewFtpClientWhenSwitchedToFtp() {
		assertThat(factory.createClient(ClientType.FTP), is(instanceOf(FtpClient.class)));
	}
}
