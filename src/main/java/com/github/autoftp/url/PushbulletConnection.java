package com.github.autoftp.url;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

public class PushbulletConnection {

	private String apiKey;
	private String apiUrl;

	public PushbulletConnection(String apiUrl, String apiKey) {

		this.apiUrl = apiUrl;
		this.apiKey = apiKey;
	}

	public void sendNotification(String title, String body) {

		HttpClient client = HttpClientBuilder.create().build();

		HttpPost request = new HttpPost(apiUrl);
		request.addHeader("Authorization", "Bearer " + apiKey);

		StringEntity requestJsonEntity = new StringEntity(buildJsonMessage(title, body), "utf-8");
		requestJsonEntity.setContentType("application/json");

		request.setEntity(requestJsonEntity);

		try {

			HttpResponse response = client.execute(request);
			int statusCode = response.getStatusLine().getStatusCode();

			if (statusCode != 200)
				throw new PushbulletException("Response from Pushbullet did not show success. Was " + statusCode);

		} catch (ClientProtocolException e) {
			throw new PushbulletException("Unable to initiate HTTP call to Pushbullet", e);
		} catch (IOException e) {
			throw new PushbulletException("Unable to complete HTTP call to Pushbullet", e);
		}
	}

	private String buildJsonMessage(String title, String body) {

		String jsonFormat = "{\"type\": \"note\", \"title\": \"%s\", \"body\": \"%s\"}";

		return String.format(jsonFormat, title, body);
	}
}
