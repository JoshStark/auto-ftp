package com.github.autoftp;

import java.util.Iterator;
import java.util.List;

import org.joda.time.DateTime;

import com.github.autoftp.client.Client;
import com.github.autoftp.client.ClientFactory;
import com.github.autoftp.config.HostConfig;
import com.github.autoftp.config.SettingsProvider;
import com.github.autoftp.connection.Connection;
import com.github.autoftp.connection.FtpFile;
import com.github.autoftp.exception.ConnectionInitialisationException;

public class ConnectionScheduler extends ConnectionNotifier implements Runnable {

	private Client client;
	private Connection connection;
	private ClientFactory clientFactory;
	private SettingsProvider settingsProvider;

	public ConnectionScheduler() {

		clientFactory = new ClientFactory();
		settingsProvider = new SettingsProvider();
	}

	@Override
	public void run() {

		openConnectionToHost();

		List<FtpFile> files = retrieveFilesAfterLastScan();
		List<FtpFile> filtered = filterFilesToCreateDownloadQueue(files);

		for (FtpFile file : filtered)
			downloadFile(file);

		closeConnectionToHost();
	}

	protected void openConnectionToHost() {

		HostConfig host = settingsProvider.getHost();

		client = clientFactory.createClient(host.getClientType());

		client.setHost(host.getHostname());
		client.setPort(host.getPort());
		client.setCredentials(host.getUsername(), host.getPassword());

		try {

			connection = client.connect();
			notifyOfConnectionOpening();

		} catch (ConnectionInitialisationException e) {
			notifyOfError(e.getMessage());
		}
	}

	protected void closeConnectionToHost() {

	}

	protected List<FtpFile> retrieveFilesAfterLastScan() {

		DateTime lastRun = settingsProvider.getLastRun();

		List<FtpFile> files = connection.listFiles();

		Iterator<FtpFile> fileIterator = files.iterator();

		while (fileIterator.hasNext()) {
			
			FtpFile currentFile = fileIterator.next();
			
			if(currentFile.getLastModified().isBefore(lastRun))
				fileIterator.remove();
		}

		return files;
	}

	protected List<FtpFile> filterFilesToCreateDownloadQueue(List<FtpFile> filesToFilter) {
		return null;
	}

	protected void downloadFile(FtpFile fileToDownload) {

	}
}
