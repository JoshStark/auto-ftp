package com.github.autoftp.schedule;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.github.autoftp.ConnectionListener;
import com.github.autoftp.config.SettingsProvider;

public class ConnectionScheduleExecutor {

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
		
		int connectionInterval = settingsProvider.getConnectionInterval();
		
		scheduledExecutorService.scheduleAtFixedRate(connectionSchedule, 0, connectionInterval, TimeUnit.MINUTES);
	}
}
