package com.github.autoftp.connection;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.github.autoftp.exception.DownloadFailedException;
import com.github.autoftp.exception.FileListingException;
import com.github.autoftp.exception.NoSuchDirectoryException;

public class FtpConnectionTest {

	private static final String TEST_DOWNLOAD_FILE = "jUnit_Mock_File.txt";
	private static final String DIRECTORY_PATH = "this/is/a/directory";
	private static final String FILE_SEPARATOR = System.getProperty("file.separator");

	private FtpConnection ftpConnection;
	private FTPClient mockFtpClient;

	private File jUnitTestFile;

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Before
	public void setUp() throws IOException {

		mockFtpClient = mock(FTPClient.class);

		when(mockFtpClient.changeWorkingDirectory(DIRECTORY_PATH)).thenReturn(true);
		when(mockFtpClient.printWorkingDirectory()).thenReturn(DIRECTORY_PATH);
		when(mockFtpClient.retrieveFile(anyString(), any(OutputStream.class))).thenReturn(true);

		FTPFile[] files = createRemoteFTPFiles();

		when(mockFtpClient.listFiles()).thenReturn(files);

		ftpConnection = new FtpConnection(mockFtpClient);

		jUnitTestFile = new File("." + FILE_SEPARATOR + TEST_DOWNLOAD_FILE);
		jUnitTestFile.createNewFile();
	}

	@After
	public void tearDown() throws Exception {

		if (!jUnitTestFile.delete())
			throw new Exception("Couldn't delete test file");
	}

	@Test
	public void whenSettingDirectoryThenFtpClientShouldBeCalledToChangeDirectory() throws IOException {

		ftpConnection.setRemoteDirectory(DIRECTORY_PATH);

		verify(mockFtpClient).changeWorkingDirectory(DIRECTORY_PATH);
	}

	@Test
	public void whenRemoteServerThrowsExceptionWhenChangingDirectoryThenConnectionShouldCatchAndRethrow() throws IOException {

		expectedException.expect(NoSuchDirectoryException.class);
		expectedException.expectMessage(is(equalTo("Remote server was unable to change directory.")));

		when(mockFtpClient.changeWorkingDirectory(DIRECTORY_PATH)).thenThrow(new IOException());

		ftpConnection.setRemoteDirectory(DIRECTORY_PATH);
	}

	@Test
	public void ifFtpClientReturnsFalseWhenChangingDirectoryThenThrowNoSuchDirectoryException() throws IOException {

		expectedException.expect(NoSuchDirectoryException.class);
		expectedException.expectMessage(is(equalTo("The directory this/is/a/directory doesn't exist on the remote server.")));

		when(mockFtpClient.changeWorkingDirectory(DIRECTORY_PATH)).thenReturn(false);

		ftpConnection.setRemoteDirectory(DIRECTORY_PATH);
	}

	@Test
	public void changingDirectoryShouldThenCallOnClientToGetWorkingDirectoryToSetFieldInConnection() throws IOException {

		ftpConnection.setRemoteDirectory(DIRECTORY_PATH);

		verify(mockFtpClient).printWorkingDirectory();
	}

	@Test
	public void whenListingFilesThenFtpClientListFilesMethodShouldBeCalled() throws IOException {

		ftpConnection.listFiles();

		verify(mockFtpClient).listFiles();
	}

	@Test
	public void ifWhenListingFilesFtpClientThrowsExceptionThenCatchAndRethrowFileListingExcepton() throws IOException {

		expectedException.expect(FileListingException.class);
		expectedException.expectMessage(is(equalTo("Unable to list files in directory .")));

		when(mockFtpClient.listFiles()).thenThrow(new IOException());

		ftpConnection.listFiles();
	}

	@Test
	public void whenListingFilesThenFileArrayThatListFilesReturnsShouldBeConvertedToListOfFtpFilesAndReturned()
	        throws IOException {

		ftpConnection.setRemoteDirectory(DIRECTORY_PATH);

		List<FtpFile> returnedFiles = ftpConnection.listFiles();

		assertThat(returnedFiles.get(0).getName(), is(equalTo("File 1")));
		assertThat(returnedFiles.get(0).getSize(), is(equalTo(1000l)));
		assertThat(returnedFiles.get(0).getFullPath(), is(equalTo(DIRECTORY_PATH + "/File 1")));

		assertThat(returnedFiles.get(1).getName(), is(equalTo("File 2")));
		assertThat(returnedFiles.get(1).getSize(), is(equalTo(2000l)));
		assertThat(returnedFiles.get(1).getFullPath(), is(equalTo(DIRECTORY_PATH + "/File 2")));

		assertThat(returnedFiles.get(2).getName(), is(equalTo("File 3")));
		assertThat(returnedFiles.get(2).getSize(), is(equalTo(3000l)));
		assertThat(returnedFiles.get(2).getFullPath(), is(equalTo(DIRECTORY_PATH + "/File 3")));
	}

	@Test
	public void returnedFtpFilesShouldHaveCorrectModifiedDateTimesAgainstThem() {

		List<FtpFile> files = ftpConnection.listFiles();

		assertThat(files.get(0).getLastModified().toString("dd/MM/yyyy HH:mm:ss"), is(equalTo("19/03/2014 21:40:00")));
		assertThat(files.get(1).getLastModified().toString("dd/MM/yyyy HH:mm:ss"), is(equalTo("19/03/2014 21:40:00")));
		assertThat(files.get(2).getLastModified().toString("dd/MM/yyyy HH:mm:ss"), is(equalTo("19/03/2014 21:40:00")));
	}

	@Test
	public void downloadMethodShouldCallOnFtpClientRetrieveFilesMethodWithRemoteFilename() throws IOException {

		FtpFile file = new FtpFile(TEST_DOWNLOAD_FILE, 1000, "/full/path/to/FileToDownload.txt", new DateTime().getMillis());

		ftpConnection.download(file, ".");

		verify(mockFtpClient).retrieveFile(eq(file.getFullPath()), any(OutputStream.class));
	}

	@Test
	public void downloadMethodShouldThrowExceptionIfUnableToOpenStreamToLocalFile() throws IOException {

		expectedException.expect(DownloadFailedException.class);
		expectedException
		        .expectMessage(is(equalTo("Unable to write to local directory ." + FILE_SEPARATOR + TEST_DOWNLOAD_FILE)));

		FtpFile file = new FtpFile(TEST_DOWNLOAD_FILE, 1000, "/full/path/to/FileToDownload.txt", new DateTime().getMillis());

		when(mockFtpClient.retrieveFile(eq(file.getFullPath()), any(OutputStream.class))).thenThrow(new FileNotFoundException());

		ftpConnection.download(file, ".");
	}

	@Test
	public void shouldDownloadFailForAnyReasonWhileInProgressThenCatchIOExceptionAndThrowNewDownloadFailedException()
	        throws IOException {

		expectedException.expect(DownloadFailedException.class);
		expectedException.expectMessage(is(equalTo("Unable to download file " + TEST_DOWNLOAD_FILE)));

		FtpFile file = new FtpFile(TEST_DOWNLOAD_FILE, 1000, "/full/path/to/FileToDownload.txt", new DateTime().getMillis());

		when(mockFtpClient.retrieveFile(eq(file.getFullPath()), any(OutputStream.class))).thenThrow(new IOException());

		ftpConnection.download(file, ".");
	}
	
	@Test
	public void ifRetrieveFileMethodInClientReturnsFalseThenThrowDownloadFailedException() throws IOException {
		
		expectedException.expect(DownloadFailedException.class);
		expectedException.expectMessage(is(equalTo("Server returned failure while downloading.")));

		FtpFile file = new FtpFile(TEST_DOWNLOAD_FILE, 1000, "/full/path/to/FileToDownload.txt", new DateTime().getMillis());

		when(mockFtpClient.retrieveFile(eq(file.getFullPath()), any(OutputStream.class))).thenReturn(false);

		ftpConnection.download(file, ".");
	}

	private FTPFile[] createRemoteFTPFiles() {

		Calendar calendar = Calendar.getInstance();
		calendar.set(2014, 2, 19, 21, 40, 00);

		FTPFile[] files = new FTPFile[3];

		for (int i = 0; i < 3; i++) {

			FTPFile file = new FTPFile();
			file.setName("File " + (i + 1));
			file.setSize((i + 1) * 1000);
			file.setTimestamp(calendar);

			files[i] = file;
		}

		return files;
	}
}
