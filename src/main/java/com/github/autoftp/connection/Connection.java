package com.github.autoftp.connection;

import java.util.List;

public interface Connection {

	void setRemoteDirectory(String directory);
	List<FtpFile> listFiles();
		
	void download(FtpFile file, String localDirectory);
}
