package telegrambot.apiclients.speechtotext;

import com.assemblyai.api.AssemblyAI;
import com.assemblyai.api.resources.transcripts.types.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import okhttp3.*;

public final class App {
	private static final String API_KEY = System.getenv("ASSEMBLY_API_KEY");
	private static final String AUDIO_URL = "https://storage.googleapis.com/aai-web-samples/5_common_sports_injuries.mp3";
	public static final String UPLOAD_URL = "https://api.assemblyai.com/v2/upload";
	private static final OkHttpClient okClient = new OkHttpClient.Builder()
			.readTimeout(60, TimeUnit.SECONDS)
			.build();
	private static final ObjectMapper objectMapper = new ObjectMapper();

	public static String uploadFile(String path) throws IOException {
		java.io.File file = new File(path);
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

	public static Optional<String> transcribeAudioToText(String pathToFile) {
		AssemblyAI client = AssemblyAI.builder()
				.apiKey(API_KEY)
				.build();


		var params = TranscriptOptionalParams.builder()
				.speakerLabels(true)
				.build();

		//Old version, took no param and had the AUDIO_URL for transcribing
//		Transcript transcript = client.transcripts().transcribe(AUDIO_URL, params);
		Transcript transcript = client.transcripts().transcribe(pathToFile, params);

		System.out.println(transcript.getText());

		transcript.getUtterances().ifPresent(utterances ->
				utterances.forEach(utterance ->
						System.out.println("Speaker " + utterance.getSpeaker() + ": " + utterance.getText())
				)
		);
		return transcript.getText();
	}
//	public static void main(String[] args) {
////		transcribeAudioToText();
//	}

	/*		else if (update.hasMessage() && update.getMessage().hasVoice()) {
			id = update.getMessage().getChatId();
			System.out.println("Has voice");
			System.out.println("is this true: " + update.getMessage().hasText());
			System.out.println(update.getMessage().getVoice());
			String fileId = update.getMessage().getVoice().getFileId();
			try {
				downloadVoiceFile(fileId, id);
			} catch (IOException e) {
				e.printStackTrace();
			}
			try{
				Thread.sleep(5000);
			} catch (Exception e) {
				e.printStackTrace();
			}
			txt = String.valueOf(App.transcribeAudioToText(Config.getProperty("voice_recording.path") + "voiceRecording.ogg"));

			try {
				Thread.sleep(5000);
			} catch (Exception e) {
				e.printStackTrace();
			}

			System.out.println(txt);
		}*/


/*	private void downloadVoiceFile(String fileId, long chatId) throws IOException {
		// Get the file path from Telegram API
		GetFile getFileRequest = new GetFile();
		getFileRequest.setFileId(fileId);

		File file = null;
		try {
			file = execute(getFileRequest);
		} catch (TelegramApiException e) {
			throw new RuntimeException(e);
		}

		// Retrieve the base file path from the properties file
		String baseFilePath = Config.getProperty("voice_recording.path");
		System.out.println("Base file path: " + baseFilePath);

		// Ensure the directory exists
		java.io.File directory = new java.io.File(baseFilePath).getParentFile();
		if (!directory.exists()) {
			directory.mkdirs();
		}

		// Construct the full file path with the file ID
		String localFilePath = baseFilePath + "voiceRecording.ogg";
		java.io.File localFile = new java.io.File(localFilePath);

		// Construct the URL to download the file
		String fileUrl = "https://api.telegram.org/file/bot" + getBotToken() + "/" + file.getFilePath();
		System.out.println("File URL: " + fileUrl);

		// Download the file from the URL
		try (InputStream in = new URL(fileUrl).openStream()) {
			Files.copy(in, localFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		}

		System.out.println("Voice file downloaded to: " + localFile.getAbsolutePath());

		// Now you can process the voice file as needed
	}*/
}
