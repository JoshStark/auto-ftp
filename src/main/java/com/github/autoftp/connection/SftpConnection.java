package com.github.autoftp.connection;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.github.autoftp.exception.NoSuchDirectoryException;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.SftpException;

public class SftpConnection implements Connection {

	private static final String DIRECTORY_DOES_NOT_EXIST_MESSAGE = "Directory %s does not exist.";

	private ChannelSftp channel;

	public SftpConnection(ChannelSftp channel) {
		this.channel = channel;
	}

	@Override
	public void setDirectory(String directory) {

		try {

			this.channel.cd(directory);

		} catch (SftpException e) {

			throw new NoSuchDirectoryException(String.format(DIRECTORY_DOES_NOT_EXIST_MESSAGE, directory), e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FtpFile> listFiles() {

		List<FtpFile> files = new ArrayList<FtpFile>();

		try {

			Vector<LsEntry> lsEntries = this.channel.ls(".");
			String currentDirectory = this.channel.pwd();

			for (LsEntry entry : lsEntries)
				files.add(toFtpFile(entry, currentDirectory));

		} catch (SftpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return files;
	}

	@Override
	public void download(FtpFile file, String localDirectory) {
		// TODO Auto-generated method stub

	}

	private FtpFile toFtpFile(LsEntry lsEntry, String currentDirectory) {

		String name = lsEntry.getFilename();
		long fileSize = lsEntry.getAttrs().getSize();
		String fullPath = String.format("%s/%s", currentDirectory, lsEntry.getFilename());
		int mTime = lsEntry.getAttrs().getMTime();
		
		return new FtpFile(name, fileSize, fullPath, (long) mTime);
	}

}
