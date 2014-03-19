package com.github.autoftp.connection;

import java.io.IOException;
import java.util.List;

import org.apache.commons.net.ftp.FTPClient;

import com.github.autoftp.exception.NoSuchDirectoryException;

public class FtpConnection implements Connection {

	private static final String NO_SUCH_DIRECTORY_MESSAGE = "The directory %s doesn't exist on the remote server.";
	private static final String UNABLE_TO_CD_MESSAGE = "Remote server was unable to change directory.";

	private FTPClient client;
	private String currentDirectory;
	
	public FtpConnection(FTPClient client) {
		this.client = client;
		this.currentDirectory = ".";
	}

	@Override
	public void setDirectory(String directory) {

		try {
			boolean success = this.client.changeWorkingDirectory(directory);
			
			if (!success)
				throw new NoSuchDirectoryException(String.format(NO_SUCH_DIRECTORY_MESSAGE, directory));

			this.currentDirectory = this.client.printWorkingDirectory();

		} catch (IOException e) {

			throw new NoSuchDirectoryException(UNABLE_TO_CD_MESSAGE, e);
		}
	}

	@Override
	public List<FtpFile> listFiles() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void download(FtpFile file, String localDirectory) {
		// TODO Auto-generated method stub

	}
}
