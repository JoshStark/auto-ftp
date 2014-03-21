package com.github.autoftp;

import java.util.List;

import com.github.autoftp.client.Client;
import com.github.autoftp.client.ClientFactory;
import com.github.autoftp.config.HostConfig;
import com.github.autoftp.config.SettingsProvider;
import com.github.autoftp.connection.FtpFile;

public class ConnectionScheduler extends ConnectionNotifier implements Runnable {

	private Client client;
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
		
		for(FtpFile file : filtered)
			downloadFile(file);
		
		closeConnectionToHost();
	}

	protected void openConnectionToHost() {

		HostConfig host = settingsProvider.getHost();
		
		client = clientFactory.createClient(host.getClientType());
		
		client.setHost(host.getHostname());
		client.setPort(host.getPort());
		client.setCredentials(host.getUsername(), host.getPassword());
		
		client.connect();
		
		notifyOfConnectionOpening();
	}
	
	protected void closeConnectionToHost () {
		
	}
	
	protected List<FtpFile> retrieveFilesAfterLastScan() {
		return null;
	}
	
	protected List<FtpFile> filterFilesToCreateDownloadQueue(List<FtpFile> filesToFilter) {
		return null;
	}
	
	protected void downloadFile(FtpFile fileToDownload) {
		
	} 
}
