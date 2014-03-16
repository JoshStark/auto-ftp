package com.github.autoftp.connection;

import java.util.List;

import com.github.autoftp.exception.NoSuchDirectoryException;
import com.jcraft.jsch.ChannelSftp;
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
			this.channel.ls(directory);
		} catch (SftpException e) {
			throw new NoSuchDirectoryException(String.format(DIRECTORY_DOES_NOT_EXIST_MESSAGE, directory), e);
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
