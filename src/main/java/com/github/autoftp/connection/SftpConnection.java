package com.github.autoftp.connection;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.github.autoftp.exception.DownloadFailedException;
import com.github.autoftp.exception.FileListingException;
import com.github.autoftp.exception.NoSuchDirectoryException;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.SftpException;

public class SftpConnection implements Connection {

	private static final String DIRECTORY_DOES_NOT_EXIST_MESSAGE = "Directory %s does not exist.";
	private static final String FILE_SEPARATOR = System.getProperty("file.separator");
	
	private static final int MILLIS = 1000;

	private ChannelSftp channel;
	private String currentDirectory;

	public SftpConnection(ChannelSftp channel) {
		this.channel = channel;
		this.currentDirectory = ".";
	}

	@Override
	public void setRemoteDirectory(String directory) {

		try {

			channel.cd(directory);
			currentDirectory = channel.pwd();

		} catch (SftpException e) {

			throw new NoSuchDirectoryException(String.format(DIRECTORY_DOES_NOT_EXIST_MESSAGE, directory), e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FtpFile> listFiles() {

		List<FtpFile> files = new ArrayList<FtpFile>();

		try {

			Vector<LsEntry> lsEntries = channel.ls(".");

			for (LsEntry entry : lsEntries)
				files.add(toFtpFile(entry));

		} catch (SftpException e) {

			throw new FileListingException("Unable to list files in directory " + currentDirectory, e);
		}

		return files;
	}

	@Override
	public void download(FtpFile file, String localDirectory) {

		try {

			channel.get(file.getName(), localDirectory);

		} catch (SftpException e) {

			throw new DownloadFailedException("Unable to download file " + file.getName(), e);
		}

	}

	private FtpFile toFtpFile(LsEntry lsEntry) {

		String name = lsEntry.getFilename();
		long fileSize = lsEntry.getAttrs().getSize();
		String fullPath = String.format("%s%s%s", currentDirectory, FILE_SEPARATOR, lsEntry.getFilename());
		int mTime = lsEntry.getAttrs().getMTime();

		return new FtpFile(name, fileSize, fullPath, (long) mTime * MILLIS);
	}

}
