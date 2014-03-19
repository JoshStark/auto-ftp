package com.github.autoftp.config;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

import com.github.autoftp.exception.ConfigCorruptedException;

public class SettingsProvider {

	private static final String APP_DOWNLOAD_DIR = "download-dir";
	private static final String FILE_FILTER_LIST = "filters.expression";
	
	private XMLConfiguration xmlConfiguration;
	
	public SettingsProvider() {
		
		try {
	        this.xmlConfiguration = new XMLConfiguration("user-config.xml");
	        
        } catch (ConfigurationException e) {
	        e.printStackTrace();
        }
	}
	
	public void setDownloadDirectory(String directory) {
		
		this.xmlConfiguration.clearProperty(APP_DOWNLOAD_DIR);
		this.xmlConfiguration.addProperty(APP_DOWNLOAD_DIR, directory);
		
		saveConfig();
	}

	public String getDownloadDirectory() {
		return this.xmlConfiguration.getString(APP_DOWNLOAD_DIR);
	}
	
	public void setFilterExpressions(List<String> filters) {
		
		this.xmlConfiguration.clearProperty(FILE_FILTER_LIST);
		this.xmlConfiguration.addProperty(FILE_FILTER_LIST, filters);
		
		saveConfig();
	}
	
	public List<String> getFilterExpressions() {
		
		List<Object> configObjects = this.xmlConfiguration.getList(FILE_FILTER_LIST);
		List<String> filters = new ArrayList<String>();
		
		for(Object object : configObjects) 
			filters.add(object.toString());
		
		return filters;
	}

	private void saveConfig() {
	    try {
			
	        this.xmlConfiguration.save();
	        
        } catch (ConfigurationException e) {
	        
        	throw new ConfigCorruptedException("Unable to save new configuration property.", e);
        }
    }
}
