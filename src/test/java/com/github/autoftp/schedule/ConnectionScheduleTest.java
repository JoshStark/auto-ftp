package com.github.autoftp.schedule;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
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
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.github.autoftp.ConnectionListener;
import com.github.autoftp.client.Client;
import com.github.autoftp.client.ClientFactory;
import com.github.autoftp.client.ClientFactory.ClientType;
import com.github.autoftp.config.HostConfig;
import com.github.autoftp.config.SettingsProvider;
import com.github.autoftp.connection.Connection;
import com.github.autoftp.connection.FtpFile;
import com.github.autoftp.exception.ClientDisconnectionException;
import com.github.autoftp.exception.ConnectionInitialisationException;
import com.github.autoftp.exception.DownloadFailedException;
import com.github.autoftp.exception.FileListingException;
import com.github.autoftp.exception.NoSuchDirectoryException;
import com.github.autoftp.schedule.ConnectionSchedule;

public class ConnectionScheduleTest {

	@InjectMocks
	private ConnectionSchedule connectionScheduler = new ConnectionSchedule();

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
		when(mockSettingsProvider.getLastRunDate()).thenReturn(new DateTime(2014, 1, 1, 07, 0, 0));

		when(mockClientFactory.createClient(hostConfig.getClientType())).thenReturn(mockClient);
		when(mockClient.connect()).thenReturn(mockConnection);

		List<String> filterExpressions = createExpressions();
		when(mockSettingsProvider.getFilterExpressions()).thenReturn(filterExpressions);

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
	public void onceConnectedTheConnectionShouldSetTheWorkingDirectory() {

		connectionScheduler.openConnectionToHost();

		verify(mockConnection).setRemoteDirectory(hostConfig.getFileDirectory());
	}

	@Test
	public void ifServerCantChangeDirectoryThenListenersShouldBeNotified() {

		doThrow(new NoSuchDirectoryException("Could not change directory")).when(mockConnection).setRemoteDirectory(
		        hostConfig.getFileDirectory());

		connectionScheduler.openConnectionToHost();

		verify(mockListener).onError("Could not change directory");
	}

	@Test
	public void whenRetrievingFilesFromServerThenLastRunDateShouldBeObtainedFromSettings() {

		connectionScheduler.retrieveFilesAfterLastScan();

		verify(mockSettingsProvider).getLastRunDate();
	}

	@Test
	public void afterGettingFilesFromServerThenClientShouldFilterOutFilesModifiedBeforeLastScan() {

		List<FtpFile> allFiles = createFiles();

		when(mockConnection.listFiles()).thenReturn(allFiles);

		List<FtpFile> filteredFiles = connectionScheduler.retrieveFilesAfterLastScan();

		assertThat(filteredFiles.size(), is(equalTo(2)));
		assertThat(filteredFiles.get(0).getName(), is(equalTo("File 1")));
		assertThat(filteredFiles.get(1).getName(), is(equalTo("File 3 with some text")));
	}

	@Test
	public void inOrderToFilterFilesTheSchedulerNeedsToGetListOfFilterStringsFromSettings() {

		connectionScheduler.filterFilesToCreateDownloadQueue(new ArrayList<FtpFile>());

		verify(mockSettingsProvider).getFilterExpressions();
	}

	@Test
	public void filteringFilesByNameShouldRemoveAnyFilesThatDontMatchGivenFilterList() {

		List<FtpFile> filesToFilter = createFiles();

		List<FtpFile> filteredFiles = connectionScheduler.filterFilesToCreateDownloadQueue(filesToFilter);

		assertThat(filteredFiles.size(), is(equalTo(2)));
		assertThat(filteredFiles.get(0).getName(), is(equalTo("File 3 with some text")));
		assertThat(filteredFiles.get(1).getName(), is(equalTo("Unusual File 4")));
	}

	@Test
	public void whenFilesHaveBeenFilteredTheSchedulerShouldNotifyListenersWithListOfFilesItWillBeDownloading() {

		List<FtpFile> filesToFilter = createFiles();
		List<FtpFile> filteredFiles = connectionScheduler.filterFilesToCreateDownloadQueue(filesToFilter);

		InOrder inOrder = Mockito.inOrder(mockListener, mockSettingsProvider);
		
		inOrder.verify(mockListener).onFilterListObtained(filteredFiles);
		inOrder.verify(mockSettingsProvider).setLastRunDate(any(DateTime.class)); // I really hate having to use any().
	}
	
	@Test
	public void ifThereAreNoFilesToDownloadThenListenersShouldNotBeNotified() {
		
		List<FtpFile> filesToFilter = new ArrayList<FtpFile>();
		filesToFilter.add(new FtpFile("File 1", 8000l, "/full/path/to/File 1", new DateTime(2014, 1, 5, 07, 0, 0).getMillis()));
		
		List<FtpFile> filteredFiles = connectionScheduler.filterFilesToCreateDownloadQueue(filesToFilter);
		
		verify(mockListener, times(0)).onFilterListObtained(filteredFiles);
	}

	@Test
	public void ifUnableToGetFileListFromServerThenSchedulerShouldNotifyListeners() {

		when(mockConnection.listFiles()).thenThrow(new FileListingException("Unable to set directory", new RuntimeException()));

		connectionScheduler.retrieveFilesAfterLastScan();

		verify(mockListener).onError("Unable to set directory");
	}

	@Test
	public void whenDownloadingAFileTheSchedulerShouldCallOnSettingsToObtainTheLocalDirectory() {

		FtpFile fileToDownload = new FtpFile("File 1", 8000l, "/full/path/to/File 1",
		        new DateTime(2014, 1, 5, 07, 0, 0).getMillis());

		connectionScheduler.downloadFile(fileToDownload);

		verify(mockSettingsProvider).getDownloadDirectory();
	}

	@Test
	public void toDownloadAFileTheSchedulerShouldCallOnConnectionDownloadMethodWithFileAndDirectory() {

		when(mockSettingsProvider.getDownloadDirectory()).thenReturn("local/directory");

		FtpFile fileToDownload = new FtpFile("File 1", 8000l, "/full/path/to/File 1",
		        new DateTime(2014, 1, 5, 07, 0, 0).getMillis());

		connectionScheduler.downloadFile(fileToDownload);

		verify(mockConnection).download(fileToDownload, "local/directory");
	}

	@Test
	public void beforeDownloadingAFileTheSchedulerShouldNotifyListenersItIsAboutToStartDownloadingThenNotifyWhenFinished() {

		when(mockSettingsProvider.getDownloadDirectory()).thenReturn("local/directory");

		FtpFile fileToDownload = new FtpFile("File 1", 8000l, "/full/path/to/File 1",
		        new DateTime(2014, 1, 5, 07, 0, 0).getMillis());

		InOrder inOrder = Mockito.inOrder(mockListener, mockConnection);

		connectionScheduler.downloadFile(fileToDownload);

		inOrder.verify(mockListener).onDownloadStarted("File 1");
		inOrder.verify(mockConnection).download(fileToDownload, "local/directory");
		inOrder.verify(mockListener).onDownloadFinished();
	}

	@Test
	public void ifCurrentDownloadFailsThenListenersShouldBeNotified() {

		when(mockSettingsProvider.getDownloadDirectory()).thenReturn("local/directory");

		FtpFile fileToDownload = new FtpFile("File 1", 8000l, "/full/path/to/File 1",
		        new DateTime(2014, 1, 5, 07, 0, 0).getMillis());

		doThrow(new DownloadFailedException("Unable to download file")).when(mockConnection).download(fileToDownload,
		        "local/directory");

		connectionScheduler.downloadFile(fileToDownload);

		verify(mockListener).onError("Unable to download file");
		verify(mockListener, times(0)).onDownloadFinished();
	}

	@Test
	public void toCloseTheConnectionTheUnderlyingClientShouldHaveDisconnectMethodCalledAndShouldNotifyListeners() {

		connectionScheduler.closeConnectionToHost();

		verify(mockClient).disconnect();
		verify(mockListener).onDisconnection();
	}

	@Test
	public void ifConnectionIsUnableToCloseThenSchedulerShouldInformListeners() {

		doThrow(new ClientDisconnectionException("Unable to disconnect")).when(mockClient).disconnect();

		connectionScheduler.closeConnectionToHost();

		verify(mockListener).onError("Unable to disconnect");
		verify(mockListener, times(0)).onDisconnection();
	}

	private List<FtpFile> createFiles() {

		List<FtpFile> files = new ArrayList<FtpFile>();

		files.add(new FtpFile("File 1", 8000l, "/full/path/to/File 1", new DateTime(2014, 1, 5, 07, 0, 0).getMillis()));
		files.add(new FtpFile("File 2", 54000l, "/full/path/to/File 2", new DateTime(2014, 1, 1, 06, 40, 0).getMillis()));
		files.add(new FtpFile("File 3 with some text", 240l, "/full/path/to/File 3", new DateTime(2014, 1, 1, 07, 0, 1)
		        .getMillis()));
		files.add(new FtpFile("Unusual File 4", 873l, "/full/path/to/File 4", new DateTime(2013, 12, 15, 07, 0, 0).getMillis()));

		return files;
	}

	private List<String> createExpressions() {

		List<String> expressions = new ArrayList<String>();

		expressions.add("*File?4");
		expressions.add("File*some*");

		return expressions;
	}
}
