package telegrambot.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class CatService {

	private static final String API_URL = "https://api.thecatapi.com/v1/images/search";
	private static final String API_KEY = System.getenv("CAT_API");

	private final OkHttpClient httpClient;
	private final ObjectMapper objectMapper;

	public CatService() {
		this.httpClient = new OkHttpClient();
		this.objectMapper = new ObjectMapper();
	}

	// Wofür ist die Main Methode, du hast bereits eine Main Methode in Main.java
	public static void main(String[] args) {
		final CatService catApiClient = new CatService();
		try {
			final String imageUrl = catApiClient.getImageUrl();
			System.out.println("Cat Image URL: " + imageUrl);

			// Define the path where the image will be saved
			final String destinationPath = "catimages/cat.jpg";
			File destinationFile = new File(destinationPath);

			// Ensure the directory exists
			destinationFile.getParentFile().mkdirs();

			// Download and save the image
			catApiClient.downloadImage(imageUrl, destinationPath);
			System.out.println("Image saved to: " + destinationPath);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getImage() throws IOException {
		final Request request = new Request.Builder()
				.url(API_URL)
				.header("x-api-key", API_KEY)
				.build();

		try (Response response = httpClient.newCall(request).execute()) {
			if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

			return response.body().string();
		}
	}

	public String getImageUrl() throws IOException {

		// der Request wird bereits zum zweiten Mal verwendet, auslagern in eine Methode establishConnection()
		// final Request request = establishConnection();
		final Request request = new Request.Builder()
				.url(API_URL)
				.header("x-api-key", API_KEY)
				.build();

		try (Response response = httpClient.newCall(request).execute()) {
			if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

			String responseBody = response.body().string();
			JsonNode jsonNode = objectMapper.readTree(responseBody);
			return jsonNode.get(0).get("url").asText();
		}
	}

	public boolean downloadImage(String imageUrl, String destinationFile) throws IOException {
		URL url = new URL(imageUrl);
		try (InputStream in = url.openStream();
			 FileOutputStream out = new FileOutputStream(destinationFile)) {

			byte[] buffer = new byte[1_000_000];
			int bytesRead;
			while ((bytesRead = in.read(buffer)) != -1) {
				out.write(buffer, 0, bytesRead);
			}
		}
		return true;
	}

	public boolean deleteImage(String filePath) {
		File file = new File(filePath);
		return file.delete();
	}


	private Request establishConnection() {
		// Connection code
		return null;
	}
	
}
