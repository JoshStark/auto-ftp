package com.github.autoftp.config;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.joda.time.DateTime;

import com.github.autoftp.client.ClientFactory.ClientType;
import com.github.autoftp.exception.ConfigCorruptedException;

public class SettingsProvider {

	private static final String HOST_FILE_DIR = "host.file-dir";
	private static final String HOST = "host";
	private static final String HOST_PORT = "host.port";
	private static final String HOST_TYPE = "host.type";
	private static final String HOST_PASSWORD = "host.password";
	private static final String HOST_USER = "host.user";
	private static final String HOST_NAME = "host.name";
	private static final String LAST_RUN = "last-run";
	private static final String APP_DOWNLOAD_DIR = "download-dir";
	private static final String FILE_FILTER_LIST = "filters.expression";

	private XMLConfiguration xmlConfiguration;

	public SettingsProvider() {

		try {
			xmlConfiguration = new XMLConfiguration("user-config.xml");

		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}

	public void setDownloadDirectory(String directory) {

		xmlConfiguration.clearProperty(APP_DOWNLOAD_DIR);
		xmlConfiguration.addProperty(APP_DOWNLOAD_DIR, directory);

		saveConfig();
	}

	public String getDownloadDirectory() {
		return xmlConfiguration.getString(APP_DOWNLOAD_DIR);
	}

	public void setFilterExpressions(List<String> filters) {

		xmlConfiguration.clearProperty(FILE_FILTER_LIST);
		xmlConfiguration.addProperty(FILE_FILTER_LIST, filters);

		saveConfig();
	}

	public List<String> getFilterExpressions() {

		List<Object> configObjects = xmlConfiguration.getList(FILE_FILTER_LIST);
		List<String> filters = new ArrayList<String>();

		for (Object object : configObjects)
			filters.add(object.toString());

		return filters;
	}
	
	public void setLastRunDate(DateTime date) {
		
		xmlConfiguration.clearProperty(LAST_RUN);
		xmlConfiguration.addProperty(LAST_RUN, date.getMillis());
		
		saveConfig();
	}

	public DateTime getLastRunDate() {
		
		long timeAsOfNowInMilliseconds = DateTime.now().getMillis();
		long lastRunInMilliseconds = xmlConfiguration.getLong(LAST_RUN, timeAsOfNowInMilliseconds);
		
		return new DateTime(lastRunInMilliseconds);
	}
	
	public void setHost(HostConfig hostConfig) {
		
		xmlConfiguration.clearTree(HOST);
		
		xmlConfiguration.addProperty(HOST_NAME, hostConfig.getHostname());
		xmlConfiguration.addProperty(HOST_PASSWORD, hostConfig.getPassword());
		xmlConfiguration.addProperty(HOST_PORT, hostConfig.getPort());
		xmlConfiguration.addProperty(HOST_TYPE, hostConfig.getClientType().toString());
		xmlConfiguration.addProperty(HOST_USER, hostConfig.getUsername());
		xmlConfiguration.addProperty(HOST_FILE_DIR, hostConfig.getFileDirectory());
		
		saveConfig();
	}
	
	public HostConfig getHost() {
		
		HostConfig hostConfig = new HostConfig();
		
		hostConfig.setHostname(xmlConfiguration.getString(HOST_NAME));
		hostConfig.setPort(xmlConfiguration.getInt(HOST_PORT));
		hostConfig.setUsername(xmlConfiguration.getString(HOST_USER));
		hostConfig.setPassword(xmlConfiguration.getString(HOST_PASSWORD));
		hostConfig.setClientType(ClientType.valueOf(xmlConfiguration.getString(HOST_TYPE)));
		hostConfig.setFileDirectory(xmlConfiguration.getString(HOST_FILE_DIR));
		
		return hostConfig;
	}
	
	private void saveConfig() {
		try {

			xmlConfiguration.save();

		} catch (ConfigurationException e) {

			throw new ConfigCorruptedException("Unable to save new configuration property.", e);
		}
	}
}
