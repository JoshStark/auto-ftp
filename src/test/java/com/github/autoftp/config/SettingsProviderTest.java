package com.github.autoftp.config;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.github.autoftp.client.ClientFactory.ClientType;
import com.github.autoftp.exception.ConfigCorruptedException;

public class SettingsProviderTest {

	private static final String HOST = "host";
	private static final String HOST_PORT = "host.port";
	private static final String HOST_TYPE = "host.type";
	private static final String HOST_PASSWORD = "host.password";
	private static final String HOST_USER = "host.user";
	private static final String HOST_NAME = "host.name";
	private static final String LAST_RUN = "last-run";
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

		verify(xmlConfiguration).clearProperty(APP_DOWNLOAD_DIR);
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
		
		verify(xmlConfiguration).clearProperty(FILE_FILTER_LIST);
		verify(xmlConfiguration).addProperty(FILE_FILTER_LIST, filters);
	}
	
	@Test
	public void setFilterExpressionsShouldSaveConfig() throws ConfigurationException {
		
		List<String> filters = new ArrayList<String>();
		filters.add("New String");
		
		settingsProvider.setFilterExpressions(filters);
		
		verify(xmlConfiguration).save();
	}
	
	@Test
	public void setLastRunDateShouldAddNewPropertyToXmlConfiguration() throws ConfigurationException {
		
		DateTime dateTime = new DateTime(123456789);
		
		settingsProvider.setLastRunDate(dateTime);
		
		verify(xmlConfiguration).clearProperty(LAST_RUN);
		verify(xmlConfiguration).addProperty(LAST_RUN, 123456789l);
		verify(xmlConfiguration).save();
	}
	
	@Test
	public void getLastRunDateShouldGetLastRunMillisAndConvertToDateTimeThenReturn() {
		
		DateTime dateTime = new DateTime(123456789);
		
		when(xmlConfiguration.getLong(eq(LAST_RUN), anyLong())).thenReturn(123456789l);
		
		assertThat(settingsProvider.getLastRun(), is(equalTo(dateTime)));
	}
	
	@Test
	public void ifLastRunHasNotBeenPreviousSetThenItShouldReturnTimeNow() {
		
		DateTime now = DateTime.now();
		long timeNow = now.getMillis();

		when(xmlConfiguration.getLong(LAST_RUN, timeNow)).thenReturn(timeNow);
		
		assertThat(settingsProvider.getLastRun(), is(equalTo(now)));
	}
	
	@Test
	public void getHostConfigShouldCompileMultipleObjectValuesFromConfigIntoHostConfigObject() {
		
		when(xmlConfiguration.getString(HOST_NAME)).thenReturn("hostname");
		when(xmlConfiguration.getString(HOST_USER)).thenReturn("a user");
		when(xmlConfiguration.getString(HOST_PASSWORD)).thenReturn("a password");
		when(xmlConfiguration.getInt(HOST_PORT)).thenReturn(80);
		when(xmlConfiguration.getString(HOST_TYPE)).thenReturn("SFTP");
		
		HostConfig hostConfig = settingsProvider.getHost();
		
		verify(xmlConfiguration).getString(HOST_NAME);
		verify(xmlConfiguration).getString(HOST_USER);
		verify(xmlConfiguration).getString(HOST_PASSWORD);
		verify(xmlConfiguration).getString(HOST_TYPE);
		verify(xmlConfiguration).getInt(HOST_PORT);
		
		assertThat(hostConfig.getHostname(), is(equalTo("hostname")));
		assertThat(hostConfig.getPassword(), is(equalTo("a password")));
		assertThat(hostConfig.getUsername(), is(equalTo("a user")));
		assertThat(hostConfig.getPort(), is(equalTo(80)));
		assertThat(hostConfig.getClientType(), is(equalTo(ClientType.SFTP)));
	}
	
	@Test
	public void setHostConfigShouldTakeHostConfigObjectAndWriteToXmlForEveryField() throws ConfigurationException {
		
		HostConfig config = new HostConfig();
		
		config.setClientType(ClientType.SFTP);
		config.setHostname("hostname");
		config.setPassword("password");
		config.setPort(80);
		config.setUsername("username");
		
		settingsProvider.setHost(config);
		
		verify(xmlConfiguration).clearProperty(HOST);
		verify(xmlConfiguration).addProperty(HOST_NAME, "hostname");
		verify(xmlConfiguration).addProperty(HOST_PASSWORD, "password");
		verify(xmlConfiguration).addProperty(HOST_PORT, 80);
		verify(xmlConfiguration).addProperty(HOST_TYPE, "SFTP");
		verify(xmlConfiguration).addProperty(HOST_USER, "username");
		
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
