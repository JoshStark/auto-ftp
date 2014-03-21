package com.github.autoftp;

import com.github.autoftp.client.ClientFactory.ClientType;
import com.github.autoftp.config.HostConfig;
import com.github.autoftp.config.SettingsProvider;

public class Main {

	public static void main(String[] args) {

		SettingsProvider settingsProvider = new SettingsProvider();
		
		HostConfig config = new HostConfig();

		config.setClientType(ClientType.SFTP);
		config.setHostname("hostname");
		config.setPassword("password");
		config.setPort(80);
		config.setUsername("username");

		settingsProvider.setHost(config);
	}
}
