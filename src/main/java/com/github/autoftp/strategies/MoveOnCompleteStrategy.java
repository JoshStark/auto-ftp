package com.github.autoftp.strategies;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.joda.time.DateTime;

import jftp.connection.FtpFile;

import com.github.autoftp.ConnectionListener;
import com.github.autoftp.FileUtilities;
import com.github.autoftp.config.SettingsProvider;

public class MoveOnCompleteStrategy implements ConnectionListener {

	private SettingsProvider settingsProvider;
	private FileUtilities fileFactory;

	public MoveOnCompleteStrategy() {

		settingsProvider = new SettingsProvider();
		fileFactory = new FileUtilities();
	}

	@Override
	public void onConnection() {
	}

	@Override
	public void onDisconnection() {
	}

	@Override
	public void onFilterListObtained(List<FtpFile> files) {
	}

	@Override
	public void onError(String errorMessage) {
	}

	@Override
	public void onDownloadStarted(String filename) {
	}

	@Override
	public void onDownloadFinished(String filename) {

		String downloadDirectory = settingsProvider.getDownloadDirectory();

		File fileToMove = fileFactory.getFile(downloadDirectory + "/" + filename);
		File destination = fileFactory.getFile(settingsProvider.getMoveDirectory());

		try {

			fileFactory.moveFile(fileToMove, destination);

		} catch (IOException e) {

			String formattedMessage = String.format("%s [Error]\t Unable to move file. Reason: %s", formattedTime(),
			        e.getMessage());

			System.out.println(formattedMessage);
		}
	}

	private String formattedTime() {
		return DateTime.now().toString("dd/MM/yyy HH:mm:ss");
	}
}
