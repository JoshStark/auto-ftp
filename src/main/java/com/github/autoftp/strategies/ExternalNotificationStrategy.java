package com.github.autoftp.strategies;

import java.util.List;

import org.joda.time.DateTime;

import jftp.connection.FtpFile;

import com.github.autoftp.ConnectionListener;
import com.github.autoftp.config.SettingsProvider;
import com.github.autoftp.url.PushbulletConnection;
import com.github.autoftp.url.PushbulletException;

public class ExternalNotificationStrategy implements ConnectionListener {

	private PushbulletConnection pushbulletConnection;
	private SettingsProvider settingsProvider;

	public ExternalNotificationStrategy() {

		this.settingsProvider = new SettingsProvider("/etc/autoftp/autoftp.conf");
		this.pushbulletConnection = new PushbulletConnection("https://api.pushbullet.com/v2/pushes",
		        settingsProvider.getPushbulletApiKey());
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

		try {

			pushbulletConnection.sendNotification("A new file has been downloaded", filename);

		} catch (PushbulletException e) {

			String formattedMessage = String.format("%s [Error]\t Unable to notify. Reason: %s", formattedTime(), e.getMessage());

			System.out.println(formattedMessage);

		} catch (Exception e) {

			String formattedMessage = String.format("%s [Error]\t Unknown exception when notifying. Reason: %s", formattedTime(),
			        e.getMessage());

			System.out.println(formattedMessage);
		}
	}

	private String formattedTime() {
		return DateTime.now().toString("dd/MM/yyy HH:mm:ss");
	}
}
