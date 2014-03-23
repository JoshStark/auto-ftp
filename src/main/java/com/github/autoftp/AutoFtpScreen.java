package com.github.autoftp;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import jline.console.ConsoleReader;

import com.github.autoftp.client.ClientFactory.ClientType;
import com.github.autoftp.config.HostConfig;
import com.github.autoftp.config.SettingsProvider;
import com.github.autoftp.connection.FtpFile;
import com.github.autoftp.schedule.ConnectionScheduleExecutor;

public class AutoFtpScreen implements ConnectionListener {

	private ConsoleReader reader = null;
	private PrintWriter writer = null;
	private SettingsProvider settingsProvider;
	private ConnectionScheduleExecutor executor;

	public AutoFtpScreen() {

		settingsProvider = new SettingsProvider();
		executor = new ConnectionScheduleExecutor();

		try {

			reader = new ConsoleReader();
			writer = new PrintWriter(reader.getOutput());

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Auto FTP\r\n");

		try {

			while (true) {

				writer.println("1. Run");
				writer.println("2. Set up server info");
				writer.println("3. Add file filters");
				writer.println("4. Quit");
				writer.flush();

				String option = reader.readLine("\r\nEnter option: ");

				if (option.equals("1")) {
					run();
					return;
				} else if (option.equals("2")) {
					setUpServer();
				} else if (option.equals("3")) {
					setUpFilters();
				} else {
					return;
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void setUpFilters() throws IOException {

		List<String> currentFilters = settingsProvider.getFilterExpressions();

		if (currentFilters.isEmpty())
			writer.println("No filters currently set up.");

		else {

			writer.println("Current filters: \r\n");

			for (String filter : currentFilters)
				writer.println(filter);

			writer.flush();

			String choice = reader.readLine("\r\nDo you want to remove these? (y/N): ");

			if (choice.toUpperCase().equals("Y"))
				currentFilters.clear();
		}

		List<String> newFilters = new ArrayList<String>(currentFilters);

		writer.println("\r\nEnter your filters. When done, leave the last line blank and hit <Enter>.");
		writer.println("? = Single character. * = Any number of any character.");
		writer.flush();

		boolean stillEntering = true;

		while (stillEntering) {

			try {
				String filter = reader.readLine("> ");

				if (filter.trim().length() > 0)
					newFilters.add(filter);

				else
					stillEntering = false;

			} catch (IOException e) {
				writer.println("Error writing to screen: " + e.getMessage());
				writer.flush();
				stillEntering = false;
			}
		}

		settingsProvider.setFilterExpressions(newFilters);

		writer.println("Done.\r\n");
		writer.flush();
	}

	private void setUpServer() {

		writer.println("Please enter the details of the server you want to connect to:\r\n");
		writer.flush();

		try {
			HostConfig hostConfig = new HostConfig();

			hostConfig.setHostname(reader.readLine("Server address: "));
			hostConfig.setPort(Integer.parseInt(reader.readLine("Server port: ")));
			hostConfig.setClientType(ClientType.valueOf(reader.readLine("Server type (FTP/SFTP): ").toUpperCase()));
			hostConfig.setUsername(reader.readLine("Username: "));
			hostConfig.setPassword(reader.readLine("Password: ", new Character('*')));

			hostConfig.setFileDirectory(reader.readLine("Directory to scan: "));

			settingsProvider.setHost(hostConfig);
			settingsProvider.setConnectionInterval(Integer.parseInt(reader.readLine("Scan interval (in minutes): ")));
			settingsProvider.setDownloadDirectory(reader.readLine("Download destination directory: "));

			writer.println("Done.\r\n");
			writer.flush();

		} catch (IOException e) {
			writer.println("Error printing to screen: " + e.getMessage());
			writer.flush();
		}

	}

	private void run() {

		writer.println("\r\n");
		executor.scheduleAndListen(this);
	}

	@Override
	public void onConnection() {
		printInfo("Connected to server.");
	}

	@Override
	public void onDisconnection() {
		printInfo("Disconnected from server. Going idle.");

	}

	@Override
	public void onFilterListObtained(List<FtpFile> files) {
		printInfo("Found files to download:");

		for (FtpFile file : files)
			writer.println(file.getName());
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
	public void onDownloadFinished() {
		printInfo("Download finished.");

	}

	public void printDownload(String filename) {
		String formattedMessage = String.format("%s [Download] %s", DateTime.now().toString("dd/MM/yyy HH:mm:ss"), filename);

		System.out.println(formattedMessage);
	}

	public void printError(String message) {
		String formattedMessage = String.format("%s [Error] %s", DateTime.now().toString("dd/MM/yyy HH:mm:ss"), message);

		System.out.println(formattedMessage);
	}

	public void printInfo(String info) {
		String formattedMessage = String.format("%s [Info] %s", DateTime.now().toString("dd/MM/yyy HH:mm:ss"), info);

		System.out.println(formattedMessage);
	}
}
