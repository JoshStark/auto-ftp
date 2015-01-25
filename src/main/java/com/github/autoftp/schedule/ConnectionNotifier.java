package com.github.autoftp.schedule;

import java.util.ArrayList;
import java.util.List;

import jftp.connection.FtpFile;

import com.github.autoftp.ConnectionListener;

public class ConnectionNotifier {

	private List<ConnectionListener> listeners = new ArrayList<ConnectionListener>();

	public List<ConnectionListener> getListeners() {
		return this.listeners;
	}

	public void registerListener(ConnectionListener listener) {
		this.listeners.add(listener);
	}

	public void notifyOfConnectionOpening() {

		for (ConnectionListener listener : listeners)
			listener.onConnection();
	}

	public void notifyOfConnectionClosing() {

		for (ConnectionListener listener : listeners)
			listener.onDisconnection();
	}

	public void notifyOfFilesToDownload(List<FtpFile> filesToDownload) {

		for (ConnectionListener listener : listeners)
			listener.onFilterListObtained(filesToDownload);
	}

	public void notifyOfError(String errorMessage) {

		for (ConnectionListener listener : listeners)
			listener.onError(errorMessage);
	}

	public void notifyOnDownloadStart(String filename) {

		for (ConnectionListener listener : listeners)
			listener.onDownloadStarted(filename);
	}

	public void notifyOnDownloadFinished(String filename) {

		for (ConnectionListener listener : listeners)
			listener.onDownloadFinished(filename);
	}
}
