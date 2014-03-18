package com.github.autoftp.connection;

import java.util.List;

import org.apache.commons.net.ftp.FTPClient;

public class FtpConnection implements Connection {

	public FtpConnection(FTPClient client) {
		
	}
	
	@Override
	public void setDirectory(String directory) {
		// TODO Auto-generated method stub

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
