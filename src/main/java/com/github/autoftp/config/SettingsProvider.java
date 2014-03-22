package com.github.autoftp.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.joda.time.DateTime;

import com.github.autoftp.client.ClientFactory.ClientType;
import com.github.autoftp.exception.FileConfigurationException;

public class SettingsProvider {

	private static final String HOST_FILE_DIR = "host.file-dir";
	private static final String HOST_PORT = "host.port";
	private static final String HOST_TYPE = "host.type";
	private static final String HOST_PASSWORD = "host.password";
	private static final String HOST_USER = "host.user";
	private static final String HOST_NAME = "host.name";
	private static final String LAST_RUN = "last-run";
	private static final String APP_DOWNLOAD_DIR = "download-dir";
	private static final String FILE_FILTER_LIST = "filters.expression";

	private PropertiesConfiguration propertiesConfiguration;

	public SettingsProvider() {

		loadConfig();
	}

	protected void loadConfig() {

		File xmlConfigFile = new File("user-config.properties");

		try {

			if (xmlConfigFile.exists())
				propertiesConfiguration = new PropertiesConfiguration(xmlConfigFile);

			else {
				xmlConfigFile.createNewFile();
				propertiesConfiguration = new PropertiesConfiguration(xmlConfigFile);
			}

		} catch (ConfigurationException e) {

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setDownloadDirectory(String directory) {

		propertiesConfiguration.setProperty(APP_DOWNLOAD_DIR, directory);

		saveConfig();
	}

	public String getDownloadDirectory() {
		return propertiesConfiguration.getString(APP_DOWNLOAD_DIR);
	}

	public void setFilterExpressions(List<String> filters) {

		propertiesConfiguration.setProperty(FILE_FILTER_LIST, filters);

		saveConfig();
	}

	public List<String> getFilterExpressions() {

		List<Object> configObjects = propertiesConfiguration.getList(FILE_FILTER_LIST);
		List<String> filters = new ArrayList<String>();

		for (Object object : configObjects)
			filters.add(object.toString());

		return filters;
	}

	public void setLastRunDate(DateTime date) {

		propertiesConfiguration.setProperty(LAST_RUN, date.getMillis());

		saveConfig();
	}

	public DateTime getLastRunDate() {

		long timeAsOfNowInMilliseconds = DateTime.now().getMillis();
		long lastRunInMilliseconds = propertiesConfiguration.getLong(LAST_RUN, timeAsOfNowInMilliseconds);

		return new DateTime(lastRunInMilliseconds);
	}

	public void setHost(HostConfig hostConfig) {

		propertiesConfiguration.setProperty(HOST_NAME, hostConfig.getHostname());
		propertiesConfiguration.setProperty(HOST_PASSWORD, hostConfig.getPassword());
		propertiesConfiguration.setProperty(HOST_PORT, hostConfig.getPort());
		propertiesConfiguration.setProperty(HOST_TYPE, hostConfig.getClientType().toString());
		propertiesConfiguration.setProperty(HOST_USER, hostConfig.getUsername());
		propertiesConfiguration.setProperty(HOST_FILE_DIR, hostConfig.getFileDirectory());

		saveConfig();
	}

	public HostConfig getHost() {

		HostConfig hostConfig = new HostConfig();

		hostConfig.setHostname(propertiesConfiguration.getString(HOST_NAME));
		hostConfig.setPort(propertiesConfiguration.getInt(HOST_PORT));
		hostConfig.setUsername(propertiesConfiguration.getString(HOST_USER));
		hostConfig.setPassword(propertiesConfiguration.getString(HOST_PASSWORD));
		hostConfig.setClientType(ClientType.valueOf(propertiesConfiguration.getString(HOST_TYPE)));
		hostConfig.setFileDirectory(propertiesConfiguration.getString(HOST_FILE_DIR));

		return hostConfig;
	}

	private void saveConfig() {

		try {
			propertiesConfiguration.save();

		} catch (ConfigurationException e) {

			throw new FileConfigurationException("Unable to save new configuration property.", e);
		}
	}
}
