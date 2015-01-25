package com.github.autoftp;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class FileUtilities {

	public File getFile(String filePathWithName) {
		return new File(filePathWithName);
	}
	
	public void moveFile(File source, File destination) throws IOException {
		FileUtils.moveFileToDirectory(source, destination, true);
	}
}
