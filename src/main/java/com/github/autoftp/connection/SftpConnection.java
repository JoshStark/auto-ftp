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

	private ChannelSftp channel;
	private String currentDirectory;

	public SftpConnection(ChannelSftp channel) {
		this.channel = channel;
		this.currentDirectory = ".";
	}

	@Override
	public void setDirectory(String directory) {

		try {

			this.channel.cd(directory);
			this.currentDirectory = this.channel.pwd();

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

			for (LsEntry entry : lsEntries)
				files.add(toFtpFile(entry));

		} catch (SftpException e) {
			
			throw new FileListingException("Unable to list files in directory " + this.currentDirectory, e);
		}

		return files;
	}

	@Override
	public void download(FtpFile file, String localDirectory) {
		
		try {
			
	        this.channel.get(file.getName(), localDirectory);
	        
        } catch (SftpException e) {
	        
        	throw new DownloadFailedException("Unable to download file.", e);
        }

	}

	private FtpFile toFtpFile(LsEntry lsEntry) {

		String name = lsEntry.getFilename();
		long fileSize = lsEntry.getAttrs().getSize();
		String fullPath = String.format("%s/%s", this.currentDirectory, lsEntry.getFilename());
		int mTime = lsEntry.getAttrs().getMTime();
		
		return new FtpFile(name, fileSize, fullPath, (long) mTime);
	}

}
