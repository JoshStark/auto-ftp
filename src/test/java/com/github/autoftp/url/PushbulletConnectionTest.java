package com.github.autoftp.url;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.util.EntityUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

public class PushbulletConnectionTest {

	@InjectMocks
	private PushbulletConnection pushbulletConnection = new PushbulletConnection("http://some.url", "someApiKey");

	@Mock
	private HttpClient mockHttpClient;

	@Mock
	private HttpResponse mockHttpResponse;
	
	@Mock
	private StatusLine mockStatusLine;
	
	@Captor
	public ArgumentCaptor<HttpPost> captor;

	@Before
	public void setUp() throws ClientProtocolException, IOException {
		
		initMocks(this);
		
		when(mockHttpClient.execute(Mockito.any(HttpUriRequest.class))).thenReturn(mockHttpResponse);
		when(mockHttpResponse.getStatusLine()).thenReturn(mockStatusLine);
		when(mockStatusLine.getStatusCode()).thenReturn(200);
	}

	@Test
	public void clientShouldMakePostCallWithCorrectParams() throws ClientProtocolException, IOException {

		pushbulletConnection.sendNotification("Title", "some message");

		verify(mockHttpClient).execute(captor.capture());

		HttpPost request = captor.getValue();

		assertThat(request.getFirstHeader("Authorization").getValue(), is(equalTo("Bearer someApiKey")));
		assertThat(request.getEntity().getContentType().getValue(), is(equalTo("application/json")));
		assertThat(EntityUtils.toString(request.getEntity()),
		        is(equalTo("{\"type\": \"note\", \"title\": \"Title\", \"body\": \"some message\"}")));
	}
	
	@Test(expected = PushbulletException.class)
	public void ifResponseFromPushbulletIsAnythingOtherThan200ThenExceptionShouldBeThrown() throws ClientProtocolException, IOException {
		
		when(mockStatusLine.getStatusCode()).thenReturn(404);
		
		pushbulletConnection.sendNotification("Title", "some message");
	}

	@Test(expected = PushbulletException.class)
	public void ifClientThrowsClientProtocolExceptionWhenSendingRequestThenExceptionShouldBeCaughtAndRethrown() throws Exception {

		when(mockHttpClient.execute(Mockito.any(HttpUriRequest.class))).thenThrow(new ClientProtocolException());

		pushbulletConnection.sendNotification("Title", "some message");
	}

	@Test(expected = PushbulletException.class)
	public void ifClientThrowsIOExceptionWhenSendingRequestThenExceptionShouldBeCaughtAndRethrown() throws Exception {

		when(mockHttpClient.execute(Mockito.any(HttpUriRequest.class))).thenThrow(new IOException());

		pushbulletConnection.sendNotification("Title", "some message");
	}
}
