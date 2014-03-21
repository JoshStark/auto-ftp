package com.github.autoftp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.joda.time.DateTime;

import com.github.autoftp.client.Client;
import com.github.autoftp.client.ClientFactory;
import com.github.autoftp.config.HostConfig;
import com.github.autoftp.config.SettingsProvider;
import com.github.autoftp.connection.Connection;
import com.github.autoftp.connection.FtpFile;
import com.github.autoftp.exception.ClientDisconnectionException;
import com.github.autoftp.exception.ConnectionInitialisationException;
import com.github.autoftp.exception.DownloadFailedException;
import com.github.autoftp.exception.FileListingException;
import com.github.autoftp.exception.NoSuchDirectoryException;

public class ConnectionScheduler extends ConnectionNotifier implements Runnable {

	private Client client;
	private Connection connection;
	private ClientFactory clientFactory;
	private SettingsProvider settingsProvider;
	private PatternBuilder patternBuilder;

	public ConnectionScheduler() {

		clientFactory = new ClientFactory();
		settingsProvider = new SettingsProvider();
		patternBuilder = new PatternBuilder();
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

			moveToRemoteDownloadFolder(host.getFileDirectory());

		} catch (ConnectionInitialisationException e) {
			notifyOfError(e.getMessage());
		} catch (NoSuchDirectoryException e) {
			notifyOfError(e.getMessage());
		}
	}

	protected void closeConnectionToHost() {

		try {

			client.disconnect();

			notifyOfConnectionClosing();

		} catch (ClientDisconnectionException e) {
			notifyOfError(e.getMessage());
		}
	}

	protected List<FtpFile> retrieveFilesAfterLastScan() {

		List<FtpFile> files = null;

		try {

			DateTime lastRun = settingsProvider.getLastRunDate();

			files = connection.listFiles();

			Iterator<FtpFile> fileIterator = files.iterator();

			while (fileIterator.hasNext()) {

				FtpFile currentFile = fileIterator.next();

				if (currentFile.getLastModified().isBefore(lastRun))
					fileIterator.remove();
			}

		} catch (FileListingException e) {
			notifyOfError(e.getMessage());
		}

		return files;
	}

	protected List<FtpFile> filterFilesToCreateDownloadQueue(List<FtpFile> filesToFilter) {

		List<FtpFile> filteredFiles = new ArrayList<FtpFile>();
		List<String> filterExpressions = settingsProvider.getFilterExpressions();

		for (FtpFile file : filesToFilter) {

			for (String expression : filterExpressions) {

				String expressionRegex = patternBuilder.buildFromFilterString(expression);

				if (file.getName().matches(expressionRegex))
					filteredFiles.add(file);
			}
		}

		notifyOfFilesToDownload(filteredFiles);

		return filteredFiles;
	}

	protected void downloadFile(FtpFile fileToDownload) {

		String downloadDirectory = settingsProvider.getDownloadDirectory();

		notifyOnDownloadStart(fileToDownload.getName());

		try {

			connection.download(fileToDownload, downloadDirectory);

			notifyOnDownloadFinished();

		} catch (DownloadFailedException e) {
			notifyOfError(e.getMessage());
		}
	}

	private void moveToRemoteDownloadFolder(String remoteDirectory) {
		connection.setRemoteDirectory(remoteDirectory);
	}
}
