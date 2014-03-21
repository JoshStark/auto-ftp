package com.github.autoftp;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.github.autoftp.client.Client;
import com.github.autoftp.client.ClientFactory;
import com.github.autoftp.client.ClientFactory.ClientType;
import com.github.autoftp.config.HostConfig;
import com.github.autoftp.config.SettingsProvider;
import com.github.autoftp.connection.Connection;
import com.github.autoftp.connection.FtpFile;
import com.github.autoftp.exception.ConnectionInitialisationException;

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
	private Connection mockConnection;
	
	@Mock
	private ConnectionListener mockListener;
	
	private HostConfig hostConfig;
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
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
		when(mockSettingsProvider.getLastRun()).thenReturn(new DateTime(2014, 1, 1, 07, 0, 0));
		
		when(mockClientFactory.createClient(hostConfig.getClientType())).thenReturn(mockClient);
		when(mockClient.connect()).thenReturn(mockConnection);
		
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
	
	@Test
	public void ifConnectionWasUnableToOpenThenCatchExceptionAndNotifyListenersWithMessage() {

		when(mockClient.connect()).thenThrow(new ConnectionInitialisationException("Unable to connect"));
		
		connectionScheduler.openConnectionToHost();
		
		verify(mockListener, times(0)).onConnection();
		verify(mockListener).onError("Unable to connect");
	}
	
	@Test
	public void whenRetrievingFilesFromServerThenLastRunDateShouldBeObtainedFromSettings() {
		
		connectionScheduler.retrieveFilesAfterLastScan();
		
		verify(mockSettingsProvider).getLastRun();
	}
	
	@Test
	public void afterGettingFilesFromServerThenClientShouldFilterOutFilesModifiedBeforeLastScan() {
		
		List<FtpFile> allFiles = createFiles();
		
		when(mockConnection.listFiles()).thenReturn(allFiles);
		
		List<FtpFile> filteredFiles = connectionScheduler.retrieveFilesAfterLastScan();
		
		assertThat(filteredFiles.size(), is(equalTo(2)));
		assertThat(filteredFiles.get(0).getName(), is(equalTo("File 1")));
		assertThat(filteredFiles.get(1).getName(), is(equalTo("File 3")));
	}
	
	private List<FtpFile> createFiles() {
		
		List<FtpFile> files = new ArrayList<FtpFile>();
		
		files.add(new FtpFile("File 1", 8000l, "/full/path/to/File 1", new DateTime(2014, 1, 5, 07, 0, 0).getMillis()));
		files.add(new FtpFile("File 2", 54000l, "/full/path/to/File 2", new DateTime(2014, 1, 1, 06, 40, 0).getMillis()));
		files.add(new FtpFile("File 3", 240l, "/full/path/to/File 3", new DateTime(2014, 1, 1, 07, 0, 1).getMillis()));
		files.add(new FtpFile("File 4", 873l, "/full/path/to/File 4", new DateTime(2013, 12, 15, 07, 0, 0).getMillis()));
		
		return files;
	}
}
