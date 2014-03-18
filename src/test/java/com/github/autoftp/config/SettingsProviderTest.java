package com.github.autoftp.config;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.apache.commons.configuration.XMLConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

public class SettingsProviderTest {

	private static final String APP_DOWNLOAD_DIR = "app.download-dir";

	private static final String THIS_IS_A_DOWNLOAD_DIR = "this/is/a/download/dir";

	@InjectMocks
	private SettingsProvider settingsProvider = new SettingsProvider();
	
	@Mock
	private XMLConfiguration xmlConfiguration;
	
	@Before
	public void setUp() {
		initMocks(this);
	}
	
	@Test
	public void setDownloadDirectoryShouldCallXmlConfigurationToSetDownloadDirectory() {
		
		settingsProvider.setDownloadDirectory(THIS_IS_A_DOWNLOAD_DIR);
		
		verify(xmlConfiguration).addProperty(APP_DOWNLOAD_DIR, THIS_IS_A_DOWNLOAD_DIR);
	}
	
	@Test
	public void getDownloadDirectoryShouldCalLXmlConfigurationToGetDownloadDirectory() {
		
		when(xmlConfiguration.getString(APP_DOWNLOAD_DIR)).thenReturn(THIS_IS_A_DOWNLOAD_DIR);
		
		assertThat(settingsProvider.getDownloadDirectory(), is(equalTo(THIS_IS_A_DOWNLOAD_DIR)));
	}
	
}
