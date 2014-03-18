package com.github.autoftp.config;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

import com.github.autoftp.exception.ConfigCorruptedException;

public class SettingsProvider {

	private static final String APP_DOWNLOAD_DIR = "download-dir";
	
	private XMLConfiguration xmlConfiguration;
	
	public SettingsProvider() {
		
		try {
	        this.xmlConfiguration = new XMLConfiguration("user-config.xml");
	        
        } catch (ConfigurationException e) {
	        e.printStackTrace();
        }
	}
	
	public void setDownloadDirectory(String directory) {
		
		this.xmlConfiguration.addProperty(APP_DOWNLOAD_DIR, directory);
		
		saveConfig();
	}

	public String getDownloadDirectory() {
		return this.xmlConfiguration.getString(APP_DOWNLOAD_DIR);
	}

	private void saveConfig() {
	    try {
			
	        this.xmlConfiguration.save();
	        
        } catch (ConfigurationException e) {
	        
        	throw new ConfigCorruptedException("Unable to save new configuration property.", e);
        }
    }
}
