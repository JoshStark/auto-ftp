package com.github.autoftp.connection;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Vector;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.github.autoftp.exception.DownloadFailedException;
import com.github.autoftp.exception.NoSuchDirectoryException;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;

public class SftpConnectionTest {

	private SftpConnection sftpConnection;
	private ChannelSftp mockChannel;

	private static final String DIRECTORY = "this/is/the/pwd";

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Before
	public void setUp() throws SftpException {

		mockChannel = mock(ChannelSftp.class);

		Vector<LsEntry> lsEntries = createEntries();
		when(mockChannel.ls(".")).thenReturn(lsEntries);
		when(mockChannel.pwd()).thenReturn(DIRECTORY);

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

	@Test
	public void listFilesMethodShouldCallOnChannelLsMethodForPresentDirectory() throws SftpException {

		sftpConnection.listFiles();

		verify(mockChannel).ls(".");
	}

	@Test
	public void listFilesMethodShouldCallOnChannelPwdMethodToGetCurrentDirectory() throws SftpException {

		sftpConnection.listFiles();

		verify(mockChannel, times(1)).pwd();
	}

	@Test
	public void lsEntriesReturnedFromChannelShouldBeParsedIntoFtpFileAndReturnedInList() {

		List<FtpFile> files = sftpConnection.listFiles();

		assertThat(files.get(0).getName(), is(equalTo("File 1")));
		assertThat(files.get(0).getSize(), is(equalTo(123l)));
		assertThat(files.get(0).getFullPath(), is(equalTo(DIRECTORY + "/File 1")));

		assertThat(files.get(1).getName(), is(equalTo("File 2")));
		assertThat(files.get(1).getSize(), is(equalTo(456l)));
		assertThat(files.get(1).getFullPath(), is(equalTo(DIRECTORY + "/File 2")));

		assertThat(files.get(2).getName(), is(equalTo("File 3")));
		assertThat(files.get(2).getSize(), is(equalTo(789l)));
		assertThat(files.get(2).getFullPath(), is(equalTo(DIRECTORY + "/File 3")));
	}

	@Test
	public void returnedFtpFilesShouldHaveCorrectModifiedDateTimesAgainstThem() {

		List<FtpFile> files = sftpConnection.listFiles();

		assertThat(files.get(0).getLastModified().toString("dd/MM/yyyy HH:mm:ss"), is(equalTo("11/03/2014 08:07:45")));
		assertThat(files.get(1).getLastModified().toString("dd/MM/yyyy HH:mm:ss"), is(equalTo("12/03/2014 19:22:41")));
		assertThat(files.get(2).getLastModified().toString("dd/MM/yyyy HH:mm:ss"), is(equalTo("08/02/2014 17:09:24")));
	}

	@Test
	public void downloadMethodShouldCallChannelGetMethodWithFtpFileNameAndDirectory() throws SftpException {

		FtpFile file = new FtpFile("File Name.txt", 1000, "remote/server/dir/File Name.txt", 123456789);

		sftpConnection.download(file, "some/directory");

		verify(mockChannel).get("File Name.txt", "some/directory");
	}

	@Test
	public void downloadMethodShouldThrowDownloadFailedExceptionWhenChannelThrowsSftpConnection() throws SftpException {

		expectedException.expect(DownloadFailedException.class);
		expectedException.expectMessage(is(equalTo("Unable to download file.")));

		doThrow(new SftpException(999, "")).when(mockChannel).get(anyString(), anyString());

		FtpFile file = new FtpFile("File Name.txt", 1000, "remote/server/dir/File Name.txt", 123456789);

		sftpConnection.download(file, "some/directory");
	}

	private Vector<LsEntry> createEntries() {

		Vector<LsEntry> vector = new Vector<LsEntry>();

		vector.add(createSingleEntry("File 1", 123l, 1394525265)); // March 11
																   // 2014
																   // 08:07:45
		vector.add(createSingleEntry("File 2", 456l, 1394652161)); // March 12
																   // 3014
																   // 19:22:41
		vector.add(createSingleEntry("File 3", 789l, 1391879364)); // Feb 08
																   // 2014
																   // 17:09:24

		return vector;
	}

	private LsEntry createSingleEntry(String fileName, long size, int mTime) {

		SftpATTRS attributes = mock(SftpATTRS.class);
		when(attributes.getSize()).thenReturn(size);
		when(attributes.getMTime()).thenReturn(mTime);

		LsEntry entry = mock(LsEntry.class);
		when(entry.getAttrs()).thenReturn(attributes);
		when(entry.getFilename()).thenReturn(fileName);

		return entry;
	}

}
