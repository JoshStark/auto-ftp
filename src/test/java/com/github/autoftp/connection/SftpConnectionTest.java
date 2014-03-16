package com.github.autoftp.connection;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import com.github.autoftp.exception.NoSuchDirectoryException;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;

public class SftpConnectionTest {
	
	private SftpConnection sftpConnection;
	private ChannelSftp mockChannel;
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	@Before
	public void setUp() {
		
		mockChannel = mock(ChannelSftp.class);
		
		sftpConnection = new SftpConnection(mockChannel);		
	}
	
	@Test
	public void setDirectoryShouldCallOnChannelLsCommandWithDirectoryPath() throws SftpException {
		
		String directory = "directory/path";
		
		sftpConnection.setDirectory(directory);
		
		verify(mockChannel).cd(directory);
	}
	
	@Test
	public void whenDirectoryDoesNotExistThenNoSuchDirectoryExceptionShouldBeThrown() throws SftpException {
		
		expectedException.expect(NoSuchDirectoryException.class);
		expectedException.expectMessage(is(equalTo("Directory not/a/directory does not exist.")));

		String directory = "not/a/directory";
		
		doThrow(new SftpException(0, "")).when(mockChannel).cd(directory);
		
		sftpConnection.setDirectory(directory);
	}

}
