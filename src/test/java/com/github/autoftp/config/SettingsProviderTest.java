package com.github.autoftp.config;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.github.autoftp.exception.ConfigCorruptedException;

public class SettingsProviderTest {

	private static final String APP_DOWNLOAD_DIR = "download-dir";
	private static final String FILE_FILTER_LIST = "filters.expression";

	private static final String THIS_IS_A_DOWNLOAD_DIR = "this/is/a/download/dir";

	@InjectMocks
	private SettingsProvider settingsProvider = new SettingsProvider();

	@Mock
	private XMLConfiguration xmlConfiguration;

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Before
	public void setUp() {
		initMocks(this);

		when(xmlConfiguration.getList(FILE_FILTER_LIST)).thenReturn(initFilterList());
	}

	@Test
	public void setDownloadDirectoryShouldCallXmlConfigurationToSetDownloadDirectory() throws ConfigurationException {

		settingsProvider.setDownloadDirectory(THIS_IS_A_DOWNLOAD_DIR);

		verify(xmlConfiguration).addProperty(APP_DOWNLOAD_DIR, THIS_IS_A_DOWNLOAD_DIR);
		verify(xmlConfiguration).save();
	}

	@Test
	public void ifConfigUnableToSaveThenShouldThrowConfigCorruptedException() throws ConfigurationException {

		doThrow(new ConfigurationException()).when(xmlConfiguration).save();

		expectedException.expect(ConfigCorruptedException.class);
		expectedException.expectMessage(is(equalTo("Unable to save new configuration property.")));

		settingsProvider.setDownloadDirectory(THIS_IS_A_DOWNLOAD_DIR);
	}

	@Test
	public void getDownloadDirectoryShouldCalLXmlConfigurationToGetDownloadDirectory() {

		settingsProvider.getDownloadDirectory();

		verify(xmlConfiguration).getString(APP_DOWNLOAD_DIR);
	}

	@Test
	public void getDownloadDirectoryShouldReturnValueFromXmlConfiguration() {

		when(xmlConfiguration.getString(APP_DOWNLOAD_DIR)).thenReturn(THIS_IS_A_DOWNLOAD_DIR);

		assertThat(settingsProvider.getDownloadDirectory(), is(equalTo(THIS_IS_A_DOWNLOAD_DIR)));
	}

	@Test
	public void getFilterExpressionsShouldCallXmlConfigurationToReturnListOfStrings() {

		settingsProvider.getFilterExpressions();

		verify(xmlConfiguration).getList(FILE_FILTER_LIST);
	}

	@Test
	public void objectListReturnedFromGetListMethodInXmlConfigurationShouldBeConvertedToStringList() {

		List<String> filters = settingsProvider.getFilterExpressions();

		assertThat(filters, hasItems("Filter 1", "Filter 2", "Filter 3"));
		
	}

	@Test
	public void listSizeReturnedFromGetFilterExpressionsShouldMatchListSizeReturnedFromXmlConfiguration() {

		assertThat(settingsProvider.getFilterExpressions().size(), is(equalTo(3)));
	}
	
	@Test
	public void setFilterExpressionsShouldTakeInListOfStringsAndCallXmlConfigurationAddProperty() {
		
		List<String> filters = new ArrayList<String>();
		filters.add("New String");
		
		settingsProvider.setFilterExpressions(filters);
		
		verify(xmlConfiguration).addProperty(FILE_FILTER_LIST, filters);
	}
	
	@Test
	public void setFilterExpressionsShouldSaveConfig() throws ConfigurationException {
		
		List<String> filters = new ArrayList<String>();
		filters.add("New String");
		
		settingsProvider.setFilterExpressions(filters);
		
		verify(xmlConfiguration).save();
	}
	
	private List<Object> initFilterList() {

		List<Object> list = new ArrayList<Object>();

		list.add("Filter 1");
		list.add("Filter 2");
		list.add("Filter 3");

		return list;
	}

}
