package com.github.autoftp.schedule;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.github.autoftp.ConnectionListener;
import com.github.autoftp.config.SettingsProvider;
import com.github.autoftp.strategies.MoveOnCompleteStrategy;

public class ConnectionScheduleExecutor {

	private static final int START_IMMEDIATELY = 0;

	private ScheduledExecutorService scheduledExecutorService;
	private SettingsProvider settingsProvider;
	private ConnectionSchedule connectionSchedule;

	public ConnectionScheduleExecutor() {

		scheduledExecutorService = Executors.newScheduledThreadPool(1);
		settingsProvider = new SettingsProvider();
		connectionSchedule = new ConnectionSchedule();
	}

	public void scheduleAndListen(ConnectionListener listener) {

		connectionSchedule.registerListener(listener);

		if (settingsProvider.isMoveEnabled())
			connectionSchedule.registerListener(new MoveOnCompleteStrategy());

		int connectionInterval = settingsProvider.getConnectionInterval();

		scheduledExecutorService.scheduleAtFixedRate(connectionSchedule, START_IMMEDIATELY, connectionInterval, TimeUnit.MINUTES);
	}
}
