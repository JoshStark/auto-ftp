package com.github.autoftp.schedule;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.github.autoftp.ConnectionListener;
import com.github.autoftp.config.SettingsProvider;
import com.github.autoftp.strategies.ExternalNotificationStrategy;
import com.github.autoftp.strategies.MoveOnCompleteStrategy;

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
	
	@Captor
	public ArgumentCaptor<ConnectionListener> captor;

	@Before
	public void setUp() {
		initMocks(this);

		when(mockSettingsProvider.isMoveEnabled()).thenReturn(false);
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

	@Test
	public void ifMoveIsEnabledThenMoveStrategyShouldBeEmployed() {

		when(mockSettingsProvider.isMoveEnabled()).thenReturn(true);

		executor.scheduleAndListen(listener);

		verify(mockConnectionSchedule, times(2)).registerListener(captor.capture());
		
		assertThat(captor.getAllValues().get(1), is(instanceOf(MoveOnCompleteStrategy.class)));
	}
	
	@Test
	public void ifPushbulletIsEnabledThenNotificationStrategyShouldBeEmployed() {

		when(mockSettingsProvider.isPushbulletNotificationEnabled()).thenReturn(true);

		executor.scheduleAndListen(listener);

		verify(mockConnectionSchedule, times(2)).registerListener(captor.capture());
		
		assertThat(captor.getAllValues().get(1), is(instanceOf(ExternalNotificationStrategy.class)));
	}
}
