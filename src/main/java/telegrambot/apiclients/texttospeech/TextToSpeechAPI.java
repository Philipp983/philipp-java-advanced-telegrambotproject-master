package telegrambot.apiclients.texttospeech;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import telegrambot.configuration.Config;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;

public class TextToSpeechAPI {

	public static final String API_KEY = System.getenv("API_KEY");
	public static final String API_URL = "http://api.voicerss.org/";
	private static final OkHttpClient okClient = new OkHttpClient();
	private static final String AUDIO_OUTPUT_DIR = Config.getProperty("text_to_speech.path");
	private static final ConcurrentHashMap<String, String> cache = new ConcurrentHashMap<>();

	public TextToSpeechAPI(){}


public static String translateTextToSpeech(String textToSpeech) throws IOException {
	String encodedText = URLEncoder.encode(textToSpeech, StandardCharsets.UTF_8.toString());
	String cachedFilePath = cache.get(encodedText);
	if (cachedFilePath != null) {
		return cachedFilePath; // Return cached file path if available
	}

	// Construct the full URL with query parameters
	String url = API_URL + "?key=" + API_KEY + "&hl=en-us&c=MP3&f=16khz_16bit_stereo&src=" + java.net.URLEncoder.encode(textToSpeech, "UTF-8");

	// Build the GET request
	Request request = new Request.Builder()
			.url(url)
			.get()
			.build();

	try (Response response = okClient.newCall(request).execute()) {
		if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

		String outputFilePath = AUDIO_OUTPUT_DIR + System.currentTimeMillis() + "_output.mp3";
		// Read the response body as an InputStream
		try (InputStream inputStream = response.body().byteStream();
			 FileOutputStream fileOutputStream = new FileOutputStream(outputFilePath)) {
			byte[] buffer = new byte[2048];
			int bytesRead;
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				fileOutputStream.write(buffer, 0, bytesRead);
			}
		}
		cache.put(encodedText, outputFilePath); // Cache the result
		return outputFilePath; // Return the file path
	}
	}

	public static void main(String[] args) {
		try {
			translateTextToSpeech("Here can stand your own text of your choosing, for the cheap price " +
					" of only 3 fiddy");

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
