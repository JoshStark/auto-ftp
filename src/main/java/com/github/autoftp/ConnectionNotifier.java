package com.github.autoftp;

import java.util.ArrayList;
import java.util.List;

import com.github.autoftp.connection.FtpFile;

public class ConnectionNotifier {

	private List<ConnectionListener> listeners = new ArrayList<ConnectionListener>();

	protected List<ConnectionListener> getListeners() {
		return this.listeners;
	}

	protected void registerListener(ConnectionListener listener) {
		this.listeners.add(listener);
	}

	protected void notifyOfConnectionOpening() {

		for (ConnectionListener listener : listeners)
			listener.onConnection();
	}

	protected void notifyOfConnectionClosing() {

		for (ConnectionListener listener : listeners)
			listener.onDisconnection();
	}

	protected void notifyOfFilesToDownload(List<FtpFile> filesToDownload) {

		for (ConnectionListener listener : listeners)
			listener.onFilterListObtained(filesToDownload);
	}

	protected void notifyOfError(String errorMessage) {

		for (ConnectionListener listener : listeners)
			listener.onError(errorMessage);
	}

	protected void notifyOnDownloadStart(String filename) {

		for (ConnectionListener listener : listeners)
			listener.onDownloadStarted(filename);
	}

	protected void notifyOnDownloadFinished() {

		for (ConnectionListener listener : listeners)
			listener.onDownloadFinished();
	}
}
