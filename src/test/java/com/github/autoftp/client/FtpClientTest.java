package com.github.autoftp.client;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.IOException;
import java.net.SocketException;

import org.apache.commons.net.ftp.FTPClient;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

public class FtpClientTest {

	@InjectMocks
	public FtpClient ftpClient = new FtpClient();
	
	@Mock
	FTPClient mockFtpClient;
	
	@Before
	public void setUp() {
		initMocks(this);
	}
	
	@Test
	public void connectMethodShouldCallonUnderlyingFtpClientConnectMethodWithHostname() throws SocketException, IOException {
		
		String hostname = "this is a hostname";
		
		ftpClient.setHost(hostname);
		ftpClient.connect();
		
		verify(mockFtpClient).connect(hostname);
	}
	
	@Test
	public void disconnectMethodShouldCallOnUnderlyingFtpClientDisconnectMethod() throws IOException {
		
		ftpClient.disconnect();
		
		verify(mockFtpClient).disconnect();
	}
}
