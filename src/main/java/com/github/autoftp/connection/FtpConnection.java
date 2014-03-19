package com.github.autoftp.connection;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import com.github.autoftp.exception.DownloadFailedException;
import com.github.autoftp.exception.FileListingException;
import com.github.autoftp.exception.NoSuchDirectoryException;

public class FtpConnection implements Connection {

	private static final String FILE_DOWNLOAD_FAILURE_MESSAGE = "Unable to download file %s";
	private static final String FILE_STREAM_OPEN_FAIL_MESSAGE = "Unable to write to local directory %s";
	private static final String FILE_LISTING_ERROR_MESSAGE = "Unable to list files in directory %s";
	private static final String NO_SUCH_DIRECTORY_MESSAGE = "The directory %s doesn't exist on the remote server.";
	private static final String UNABLE_TO_CD_MESSAGE = "Remote server was unable to change directory.";

	private static final String FILE_SEPARATOR = System.getProperty("file.separator");

	private FTPClient client;
	private String currentDirectory;

	public FtpConnection(FTPClient client) {
		this.client = client;
		this.currentDirectory = ".";
	}

	@Override
	public void setRemoteDirectory(String directory) {

		try {

			boolean success = client.changeWorkingDirectory(directory);

			if (!success)
				throw new NoSuchDirectoryException(String.format(NO_SUCH_DIRECTORY_MESSAGE, directory));

			currentDirectory = client.printWorkingDirectory();

		} catch (IOException e) {

			throw new NoSuchDirectoryException(UNABLE_TO_CD_MESSAGE, e);
		}
	}

	@Override
	public List<FtpFile> listFiles() {

		List<FtpFile> files = new ArrayList<FtpFile>();

		try {

			FTPFile[] ftpFiles = client.listFiles();

			for (FTPFile file : ftpFiles)
				files.add(toFtpFile(file));

		} catch (IOException e) {

			throw new FileListingException(String.format(FILE_LISTING_ERROR_MESSAGE, currentDirectory), e);
		}

		return files;
	}

	@Override
	public void download(FtpFile file, String localDirectory) {

		String localDestination = String.format("%s%s%s", localDirectory, FILE_SEPARATOR, file.getName());

		try {
			OutputStream outputStream = new FileOutputStream(localDestination);

			boolean hasDownloaded = client.retrieveFile(file.getFullPath(), outputStream);
			
			if(!hasDownloaded)
				throw new DownloadFailedException("Server returned failure while downloading.");
			
			outputStream.close();

		} catch (FileNotFoundException e) {
			throw new DownloadFailedException(String.format(FILE_STREAM_OPEN_FAIL_MESSAGE, localDestination), e);

		} catch (IOException e) {
			throw new DownloadFailedException(String.format(FILE_DOWNLOAD_FAILURE_MESSAGE, file.getName()), e);
		}
	}

	private FtpFile toFtpFile(FTPFile ftpFile) {

		String name = ftpFile.getName();
		long fileSize = ftpFile.getSize();
		String fullPath = String.format("%s/%s", currentDirectory, ftpFile.getName());
		long mTime = ftpFile.getTimestamp().getTime().getTime();

		return new FtpFile(name, fileSize, fullPath, mTime);
	}
}
