package com.github.autoftp.strategies;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.github.autoftp.url.PushbulletConnection;

public class ExternalNotificationStrategyTest {

	@InjectMocks
	private ExternalNotificationStrategy externalNotificationStrategy = new ExternalNotificationStrategy();
	
	@Mock
	private PushbulletConnection mockPushbulletConnection;
	
	@Before
	public void setUp() {
		initMocks(this);
	}
	
	@Test
	public void whenDownloadFinishesThenPushbulletNotificationShouldBeSent() {
		
		externalNotificationStrategy.onDownloadFinished("fileName");
		
		verify(mockPushbulletConnection).sendNotification("A new file has been downloaded", "fileName");
	}
}
