package com.github.autoftp.connection;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.apache.commons.net.ftp.FTPClient;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.github.autoftp.exception.NoSuchDirectoryException;

public class FtpConnectionTest {

	private static final String DIRECTORY_PATH = "this/is/a/directory";
	private FtpConnection ftpConnection;
	private FTPClient mockFtpClient;

	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	@Before
	public void setUp() throws IOException {
		
		mockFtpClient = mock(FTPClient.class);
		
		when(mockFtpClient.changeWorkingDirectory(DIRECTORY_PATH)).thenReturn(true);
		
		ftpConnection = new FtpConnection(mockFtpClient);
	}
	
	@Test
	public void whenSettingDirectoryThenFtpClientShouldBeCalledToChangeDirectory() throws IOException {
		
		ftpConnection.setDirectory(DIRECTORY_PATH);
		
		verify(mockFtpClient).changeWorkingDirectory(DIRECTORY_PATH);	
	}
	
	@Test
	public void whenRemoteServerThrowsExceptionWhenChangingDirectoryThenConnectionShouldCatchAndRethrow() throws IOException {
		
		expectedException.expect(NoSuchDirectoryException.class);
		expectedException.expectMessage(is(equalTo("Remote server was unable to change directory.")));
		
		when(mockFtpClient.changeWorkingDirectory(DIRECTORY_PATH)).thenThrow(new IOException());

		ftpConnection.setDirectory(DIRECTORY_PATH);
	}
	
	@Test
	public void ifFtpClientReturnsFalseWhenChangingDirectoryThenThrowNoSuchDirectoryException() throws IOException {
		
		expectedException.expect(NoSuchDirectoryException.class);
		expectedException.expectMessage(is(equalTo("The directory this/is/a/directory doesn't exist on the remote server.")));
		
		when(mockFtpClient.changeWorkingDirectory(DIRECTORY_PATH)).thenReturn(false);

		ftpConnection.setDirectory(DIRECTORY_PATH);
	}
	
	@Test
	public void changingDirectoryShouldThenCallOnClientToGetWorkingDirectoryToSetFieldInConnection() throws IOException {
		
		ftpConnection.setDirectory(DIRECTORY_PATH);
		
		verify(mockFtpClient).printWorkingDirectory();
	}
}
