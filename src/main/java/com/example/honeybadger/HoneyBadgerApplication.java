package com.example.honeybadger;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@CrossOrigin
@RestController
@SpringBootApplication
public class HoneyBadgerApplication {

	public static void main(String[] args) {
		SpringApplication.run(HoneyBadgerApplication.class, args);
	}

	@PostMapping(value = "/slack-alerts/spam-emails", consumes = "application/json", produces = "application/json")
	public PostResponse checkSpam(@RequestBody Payload payload) {
		String type = payload.getType();
		String slackChannelWebhook = System.getenv("SLACK_CHANNEL_WEBHOOK");

		PostResponse postResponse = new PostResponse();

		if(type == null)
			return postResponse.setResponse(HttpStatus.BAD_REQUEST, "No type specified in payload");
		if(!type.equals("SpamNotification"))
			return postResponse.setResponse(HttpStatus.OK, "Not a spam message");

		if(slackChannelWebhook == null)
			return postResponse.setResponse(HttpStatus.INTERNAL_SERVER_ERROR, "No slack channel webhook specified in build");


		return sendSlackMessage(payload, slackChannelWebhook);
	}

	private PostResponse sendSlackMessage(Payload payload, String slackChannelWebhook){
		String email = payload.getEmail();
		String description = payload.getDescription();

		PostResponse postResponse = new PostResponse();

		if(email == null || description == null)
			return postResponse.setResponse(HttpStatus.BAD_REQUEST, "Email or description field missing in payload");

		SlackMessage message = new SlackMessage(String.format("Spam has been sent to %s\nDescription: %s", email, description));

		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<SlackMessage> request = new HttpEntity<>(message, headers);
		try{
			ResponseEntity<String> response = restTemplate.postForEntity(slackChannelWebhook, request, String.class);
			return postResponse.setResponse(HttpStatus.OK, response.getBody());
		}
		catch(HttpClientErrorException e){
			return postResponse.setResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	private static class Payload {
		private String RecordType;
		@JsonProperty(required = true)
		private String Type;
		private String TypeCode;
		private String Name;
		private String Tag;
		private String MessageStream;
		@JsonProperty(required = true)
		private String Description;
		@JsonProperty(required = true)
		private String Email;
		private String From;
		private String BouncedAt;

		public String getType() {
			return Type;
		}

		public String getDescription() {
			return Description;
		}

		public String getEmail() {
			return Email;
		}

		public String getRecordType() {
			return RecordType;
		}

		public String getTypeCode() {
			return TypeCode;
		}

		public String getName() {
			return Name;
		}

		public String getTag() {
			return Tag;
		}

		public String getMessageStream() {
			return MessageStream;
		}

		public String getFrom() {
			return From;
		}

		public String getBouncedAt() {
			return BouncedAt;
		}
	}

	private class PostResponse {
		public HttpStatus status;
		public String message;

		public PostResponse(){}

		public PostResponse setResponse(HttpStatus status, String message){
			this.status = status;
			this.message = message;
			return this;
		}
	}

	private static class SlackMessage {
		@JsonProperty(required = true)
		String text;

		public SlackMessage(String text){
			this.text = text;
		}

		public String getText(){
			return text;
		}
	}

}
