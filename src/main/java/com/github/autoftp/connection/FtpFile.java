package com.github.autoftp.connection;

public class FtpFile {

	private String name;
	private long size;
	private String fullPath;

	public String getName() {
		return name;
	}

	protected void setName(String name) {
		this.name = name;
	}

	public long getSize() {
		return size;
	}

	protected void setSize(long size) {
		this.size = size;
	}

	public String getFullPath() {
		return fullPath;
	}

	protected void setFullPath(String fullPath) {
		this.fullPath = fullPath;
	}
}
