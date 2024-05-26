package telegrambot.apiclients.speechtotext;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class TranscriptionAPI {

	public static final String API_KEY = System.getenv("ASSEMBLYAI_API_KEY");
	public static final String UPLOAD_URL = "https://api.assemblyai.com/v2/upload";
	public static final String TRANSCRIPT_URL = "https://api.assemblyai.com/v2/transcript";
	private static final OkHttpClient okClient = new OkHttpClient.Builder()
			.readTimeout(60, TimeUnit.SECONDS)
			.build();
	private static final ObjectMapper objectMapper = new ObjectMapper();

	public TranscriptionAPI() {}

	public static void main(String[] args) {
		try {
			String path = "C:\\Users\\niels\\Downloads\\espn-bears.m4a"; // Update the file path here
			String uploadedFileUrl = uploadFile(path);
			String transcript = getTranscript(uploadedFileUrl);
			System.out.println("Transcript:\n" + transcript);
		} catch (IOException | InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public static String uploadFile(String path) throws IOException {
		File file = new File(path);
		RequestBody fileBody = RequestBody.create(file, MediaType.parse("application/octet-stream"));
		Request request = new Request.Builder()
				.url(UPLOAD_URL)
				.addHeader("authorization", API_KEY)
				.post(fileBody)
				.build();

		try (Response response = okClient.newCall(request).execute()) {
			if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
			JsonNode jsonResponse = objectMapper.readTree(response.body().string());
			return jsonResponse.get("upload_url").asText();
		}
	}

	public static String getTranscript(String audioUrl) throws IOException, InterruptedException {
		// Prepare the JSON payload
		String jsonPayload = objectMapper.createObjectNode()
				.put("audio_url", audioUrl)
				.toString();

		RequestBody body = RequestBody.create(jsonPayload, MediaType.parse("application/json"));
		Request request = new Request.Builder()
				.url(TRANSCRIPT_URL)
				.addHeader("authorization", API_KEY)
				.post(body)
				.build();

		// Get the transcript ID
		try (Response response = okClient.newCall(request).execute()) {
			if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
			JsonNode jsonResponse = objectMapper.readTree(response.body().string());
			String transcriptId = jsonResponse.get("id").asText();

			// Polling for the transcript result
			String pollingUrl = TRANSCRIPT_URL + "/" + transcriptId;
			while (true) {
				Request pollingRequest = new Request.Builder()
						.url(pollingUrl)
						.addHeader("authorization", API_KEY)
						.get()
						.build();

				try (Response pollingResponse = okClient.newCall(pollingRequest).execute()) {
					if (!pollingResponse.isSuccessful()) throw new IOException("Unexpected code " + pollingResponse);
					JsonNode pollingJsonResponse = objectMapper.readTree(pollingResponse.body().string());
					String status = pollingJsonResponse.get("status").asText();

					switch (status) {
						case "processing":
						case "queued":
							TimeUnit.SECONDS.sleep(3);
							break;
						case "completed":
							return pollingJsonResponse.get("text").asText();
						case "error":
							String error = pollingJsonResponse.get("error").asText();
							throw new IOException("Transcription failed: " + error);
						default:
							throw new IllegalStateException("Unexpected value: " + status);
					}
				}
			}
		}
	}
}
