package com.github.autoftp;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.github.autoftp.client.Client;
import com.github.autoftp.client.ClientFactory;
import com.github.autoftp.client.ClientFactory.ClientType;
import com.github.autoftp.config.HostConfig;
import com.github.autoftp.config.SettingsProvider;

public class ConnectionSchedulerTest {

	@InjectMocks
	private ConnectionScheduler connectionScheduler = new ConnectionScheduler();
	
	@Mock
	private SettingsProvider mockSettingsProvider;
	
	@Mock
	private ClientFactory mockClientFactory;
	
	@Mock
	private Client mockClient;
	
	@Mock
	private ConnectionListener mockListener;
	
	private HostConfig hostConfig;
	
	@Before
	public void setUp() {
		initMocks(this);
		
		hostConfig = new HostConfig();

		hostConfig.setClientType(ClientType.SFTP);
		hostConfig.setHostname("hostname");
		hostConfig.setPassword("password");
		hostConfig.setPort(80);
		hostConfig.setUsername("username");
		
		when(mockSettingsProvider.getHost()).thenReturn(hostConfig);
		
		when(mockClientFactory.createClient(hostConfig.getClientType())).thenReturn(mockClient);
		
		connectionScheduler.registerListener(mockListener);
	}
	
	@Test
	public void openingAConnectionShouldGetHostConfigFromSettings() {
		
		connectionScheduler.openConnectionToHost();
		
		verify(mockSettingsProvider).getHost();
	}
	
	@Test
	public void openingAConnectionToHostShouldCallOnClientFactoryToReturnClient() {
		
		connectionScheduler.openConnectionToHost();
		
		verify(mockClientFactory).createClient(ClientType.SFTP);
	}

	@Test
	public void openingAConnectionShouldInsertHostDetailsToClient() {
		
		connectionScheduler.openConnectionToHost();
		
		verify(mockClient).setHost(hostConfig.getHostname());
		verify(mockClient).setPort(hostConfig.getPort());
		verify(mockClient).setCredentials(hostConfig.getUsername(), hostConfig.getPassword());
	}
	
	@Test
	public void clientConnectMethodShouldBeCalledWhenOpeningAConnection() {

		connectionScheduler.openConnectionToHost();
		
		verify(mockClient).connect();
	}
	
	@Test
	public void ifConnectionWasSuccessfulThenListenersShouldBeNotified() {
		
		connectionScheduler.openConnectionToHost();
		
		verify(mockListener).onConnection();
	}
}
