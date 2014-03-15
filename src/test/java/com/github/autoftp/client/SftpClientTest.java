package com.github.autoftp.client;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class SftpClientTest {

	@InjectMocks
	private SftpClient sftpClient = new SftpClient();

	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private JSch mockJsch;

	@Before
	public void setUp() {
		initMocks(this);
	}

	@Test
	public void connectMethodShouldCreateSessionUsingHostPortAndUsername() throws JSchException {
		
		sftpClient.setHost("host");
		sftpClient.setPort(999);
		sftpClient.setCredentials("user", "password");
		
		sftpClient.connect();
		
		verify(mockJsch).getSession("user", "host", 999);
	}
	
	@Test
	public void sessionFromInitialConnectionNeedsConfigAndPasswordSettingBeforeConnecting() throws JSchException {
		
		sftpClient.setHost("host");
		sftpClient.setPort(999);
		sftpClient.setCredentials("user", "password");
		
		Session mockSession = mockJsch.getSession("user", "host", 999);
		
		InOrder inOrder = Mockito.inOrder(mockSession);
		
		sftpClient.connect();
		
		inOrder.verify(mockSession).setConfig("StrictHostKeyChecking", "no");
		inOrder.verify(mockSession).setPassword("password");
		inOrder.verify(mockSession).connect();
	}

}
