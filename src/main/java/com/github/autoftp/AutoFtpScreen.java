package com.github.autoftp;

import java.text.DecimalFormat;
import java.util.List;

import jftp.connection.FtpFile;

import org.joda.time.DateTime;

import com.github.autoftp.schedule.ConnectionScheduleExecutor;

public class AutoFtpScreen implements ConnectionListener {

	private static final double MB = 1024.0 * 1024.0;

	private ConnectionScheduleExecutor executor;

	public AutoFtpScreen() {

		executor = new ConnectionScheduleExecutor();
		executor.scheduleAndListen(this);
	}

	@Override
	public void onConnection() {
		printInfo("Connected to server. Checking for any new files...");
	}

	@Override
	public void onDisconnection() {
		printInfo("Disconnected from server. Going idle.");
	}

	@Override
	public void onFilterListObtained(List<FtpFile> files) {

		int fileCount = files.size();

		printInfo(String.format("%d %s found:", fileCount, (fileCount == 1 ? "file" : "files")));

		for (FtpFile file : files)
			printFile(file);
	}

	@Override
	public void onError(String errorMessage) {
		printError(errorMessage);
	}

	@Override
	public void onDownloadStarted(String filename) {
		printDownload(filename);
	}

	@Override
	public void onDownloadFinished(String filename) {
		printInfo("Download finished.");
	}

	public void printDownload(String filename) {

		String formattedMessage = String.format("%s [Download]\t %s", formattedTime(), filename);

		System.out.println(formattedMessage);
	}

	public void printError(String message) {

		String formattedMessage = String.format("%s [Error]\t %s", formattedTime(), message);

		System.out.println(formattedMessage);
	}

	public void printInfo(String info) {

		String formattedMessage = String.format("%s [Info]\t %s", formattedTime(), info);

		System.out.println(formattedMessage);
	}

	public void printFile(FtpFile file) {

		DecimalFormat format = new DecimalFormat("#.##");

		String extension = "MB";

		long fileSize = file.getSize();
		double sizeInMb = fileSize / MB;

		double printableSize = sizeInMb;

		if (sizeInMb > 1024.0) {
			printableSize = (sizeInMb / 1024);
			extension = "GB";
		}

		String formattedMessage = String.format("%s [File]\t %s (%s%s)", formattedTime(), file.getName(),
		        format.format(printableSize), extension);

		System.out.println(formattedMessage);
	}

	private String formattedTime() {
		return DateTime.now().toString("dd/MM/yyy HH:mm:ss");
	}
}
