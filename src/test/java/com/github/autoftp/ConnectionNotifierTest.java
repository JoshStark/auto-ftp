package com.github.autoftp;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.github.autoftp.connection.FtpFile;

public class ConnectionNotifierTest {

	private ConnectionNotifier notifier = new ConnectionNotifier();

	@Mock
	ConnectionListener listener;

	@Before
	public void setUp() {
		initMocks(this);
	}

	@Test
	public void whenNotifierCreatedThenListenerListShouldBeEmpty() {

		assertThat(notifier.getListeners().size(), is(equalTo(0)));
	}

	@Test
	public void registerListenerMethodShouldAddListenerToList() {

		notifier.registerListener(listener);

		assertThat(notifier.getListeners().size(), is(equalTo(1)));
	}

	@Test
	public void whenConnectionHasBeenMadeToServerThenListenersShouldBeNotified() {

		notifier.registerListener(listener);
		notifier.notifyOfConnectionOpening();

		verify(listener).onConnection();
	}

	@Test
	public void whenConnectionHasBeenClosedThenListenersShouldBeNotified() {

		notifier.registerListener(listener);
		notifier.notifyOfConnectionClosing();

		verify(listener).onDisconnection();
	}

	@Test
	public void whenListOfFilesToBeDownloadedHasBeenCollectedThenListenerShouldReceiveFiles() {

		List<FtpFile> filesToDownload = new ArrayList<FtpFile>();

		notifier.registerListener(listener);
		notifier.notifyOfFilesToDownload(filesToDownload);

		verify(listener).onFilterListObtained(filesToDownload);
	}

	@Test
	public void whenAFileDownloadStartsThenListenersShouldBeNotifiedOfWhichFileHasStartedDownloading() {

		notifier.registerListener(listener);
		notifier.notifyOnDownloadStart("File 1");

		verify(listener).onDownloadStarted("File 1");
	}

	@Test
	public void whenAFileHasFinishedDownloadingThenListenersShouldBeNotified() {

		notifier.registerListener(listener);
		notifier.notifyOnDownloadFinished();

		verify(listener).onDownloadFinished();
	}

	@Test
	public void ifAnErrorOccursThenNotifyListenersBySendingMessageOfError() {

		notifier.registerListener(listener);
		notifier.notifyOfError("Something bad happened");

		verify(listener).onError("Something bad happened");
	}

	@Test
	public void ifMultipleListenersHaveBeenRegisteredThenAllShouldBeNotified() {

		List<FtpFile> filesToDownload = new ArrayList<FtpFile>();

		notifier.registerListener(listener);
		notifier.registerListener(listener);
		notifier.registerListener(listener);

		notifier.notifyOfConnectionOpening();
		notifier.notifyOfConnectionClosing();
		notifier.notifyOfFilesToDownload(filesToDownload);
		notifier.notifyOnDownloadStart("File 1");
		notifier.notifyOnDownloadFinished();
		notifier.notifyOfError("Something bad happened");

		verify(listener, times(3)).onError("Something bad happened");
		verify(listener, times(3)).onDownloadFinished();
		verify(listener, times(3)).onDownloadStarted("File 1");
		verify(listener, times(3)).onConnection();
		verify(listener, times(3)).onDisconnection();
		verify(listener, times(3)).onFilterListObtained(filesToDownload);
	}

}
