package com.github.autoftp.connection;

import java.util.List;

public interface Connection {

	void setDirectory(String directory);
	List<FtpFile> listFiles();
		
	void download(FtpFile file, String localDirectory);
}
