package com.github.autoftp.schedule;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.github.autoftp.ConnectionListener;
import com.github.autoftp.config.SettingsProvider;

public class ConnectionScheduleExecutorTest {

	@InjectMocks
	private ConnectionScheduleExecutor executor = new ConnectionScheduleExecutor();
	
	@Mock
	private ScheduledExecutorService mockScheduledExecutorService;
	
	@Mock
	private ConnectionSchedule mockConnectionSchedule;
	
	@Mock
	private SettingsProvider mockSettingsProvider;
	
	@Mock
	private ConnectionListener listener;
	
	@Before
	public void setUp() {
		initMocks(this);
	}
	
	@Test
	public void executeMethodShouldCallOnSettingsToGetIntervalTime() {
		
		executor.scheduleAndListen(listener);
		
		verify(mockSettingsProvider).getConnectionInterval();
	}
	
	@Test
	public void executeMethodShouldScheduleConnection() {
		
		when(mockSettingsProvider.getConnectionInterval()).thenReturn(5);
		
		executor.scheduleAndListen(listener);
		
		verify(mockScheduledExecutorService).scheduleAtFixedRate(mockConnectionSchedule, 0, 5, TimeUnit.MINUTES);
	}
	
	@Test
	public void underlyingScheduleShouldHaveListenerAdded() {
		
		executor.scheduleAndListen(listener);
		
		verify(mockConnectionSchedule).registerListener(listener);
	}
}
