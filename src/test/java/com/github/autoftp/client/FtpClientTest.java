package com.github.autoftp.client;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.apache.commons.net.ftp.FTPClient;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.github.autoftp.connection.Connection;
import com.github.autoftp.connection.ConnectionFactory;
import com.github.autoftp.connection.FtpConnection;
import com.github.autoftp.exception.ClientDisconnectionException;
import com.github.autoftp.exception.ConnectionInitialisationException;

public class FtpClientTest {

	@InjectMocks
	public FtpClient ftpClient = new FtpClient();

	@Mock
	private FTPClient mockFtpClient;

	@Mock
	private ConnectionFactory mockConnectionFactory;

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	private String hostname;
	private int port;
	private String username;
	private String password;

	@Before
	public void setUp() throws IOException {
		initMocks(this);

		hostname = "this is a hostname";
		port = 80;
		username = "thisisausername";
		password = "thisisapassword";

		ftpClient.setHost(hostname);
		ftpClient.setPort(port);
		ftpClient.setCredentials(username, password);

		when(mockFtpClient.getReplyCode()).thenReturn(200);
		when(mockFtpClient.login(username, password)).thenReturn(true);
		when(mockFtpClient.isConnected()).thenReturn(true);
		
		when(mockConnectionFactory.createFtpConnection(mockFtpClient)).thenReturn(new FtpConnection(mockFtpClient));
	}

	@Test
	public void connectMethodShouldCallonUnderlyingFtpClientConnectMethodWithHostname() throws SocketException, IOException {

		ftpClient.connect();

		verify(mockFtpClient).connect(hostname, port);
	}

	@Test
	public void connectMethodShouldEnterPassiveModeThenLoginToUnderlyingFtpClint() throws IOException {

		ftpClient.connect();

		InOrder inOrder = Mockito.inOrder(mockFtpClient);

		inOrder.verify(mockFtpClient).enterLocalPassiveMode();
		inOrder.verify(mockFtpClient).login(username, password);
	}

	@Test
	public void connectMethodShouldReturnNewFtpConnectionTakingInUnderlyingFtpClient() {

		Connection connection = ftpClient.connect();

		verify(mockConnectionFactory).createFtpConnection(mockFtpClient);
		assertThat(connection, is(instanceOf(FtpConnection.class)));
	}

	@Test
	public void disconnectMethodShouldCallOnUnderlyingFtpClientDisconnectMethod() throws IOException {

		ftpClient.disconnect();

		verify(mockFtpClient).disconnect();
	}

	@Test
	public void ifConnectionFailsThenCatchThrownExceptionAndThrowConnectionInitialisationException() throws SocketException,
	        IOException {

		expectedException.expect(ConnectionInitialisationException.class);
		expectedException.expectMessage(is(equalTo("Unable to connect to host " + hostname + " on port " + port)));

		doThrow(new IOException()).when(mockFtpClient).connect(hostname, port);

		ftpClient.connect();
	}

	@Test
	public void ifConnectionFailsDueToUnknownHostThenCatchThrownExceptionAndThrowConnectionInitialisationException()
	        throws SocketException, IOException {

		expectedException.expect(ConnectionInitialisationException.class);
		expectedException.expectMessage(is(equalTo("Unable to connect to host " + hostname + " on port " + port)));

		doThrow(new UnknownHostException()).when(mockFtpClient).connect(hostname, port);

		ftpClient.connect();
	}
	
	@Test
	public void ifUnderlyingClientReturnsBadConnectionCodeThenThrowConnectionException() {
		
		expectedException.expect(ConnectionInitialisationException.class);
		expectedException.expectMessage(is(equalTo("The host " + hostname + " on port " + port + " returned a bad status code.")));
		
		when(mockFtpClient.getReplyCode()).thenReturn(500);
		
		ftpClient.connect();
	}
	
	@Test
	public void ifUnableToLoginToFtpClientThenThrowConnectionInitialisationException() throws IOException {
		
		expectedException.expect(ConnectionInitialisationException.class);
		expectedException.expectMessage(is(equalTo("Unable to login for user " + username)));
		
		when(mockFtpClient.login(username, password)).thenReturn(false);
		
		ftpClient.connect();
	}
	
	@Test
	public void whenDisconnectingThenClientShouldCheckToSeeIfAlreadyDisconnected() {
		
		ftpClient.disconnect();
		
		verify(mockFtpClient).isConnected();
	}
	
	@Test
	public void whenAlreadyDisconnectedThenClientShoudlNotCallOnUnderlyingClientDisconnectMethod() throws IOException {
		
		when(mockFtpClient.isConnected()).thenReturn(false);
		
		ftpClient.disconnect();
		
		verify(mockFtpClient, times(0)).disconnect();
	}
	
	@Test
	public void whenClientIsStillConnectedThenShouldCallOnUnderlyingClientDisconnectMethod() throws IOException {
		
		ftpClient.disconnect();
		
		verify(mockFtpClient).disconnect();
	}
	
	@Test
	public void ifUnderlyingClientThrowsExceptionWhenDisconnectingThenClientShouldCatchAndRethrow() throws IOException {
		
		expectedException.expect(ClientDisconnectionException.class);
		expectedException.expectMessage(is(equalTo("There was an unexpected error while trying to disconnect.")));
		
		doThrow(new IOException()).when(mockFtpClient).disconnect();
		
		ftpClient.disconnect();		
	}
}
