package telegrambot.catapiclient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class CatApiClient {
	private static final String API_URL = "https://api.thecatapi.com/v1/images/search";
	private static final String API_KEY = System.getenv("CAT_API");

	private final OkHttpClient client;
	private final ObjectMapper objectMapper;

	public CatApiClient() {
		this.client = new OkHttpClient();
		this.objectMapper = new ObjectMapper();
	}

	public String getCatImage() throws IOException {
		Request request = new Request.Builder()
				.url(API_URL)
				.header("x-api-key", API_KEY)
				.build();

		try (Response response = client.newCall(request).execute()) {
			if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

			return response.body().string();
		}
	}

	public String getCatImageUrl() throws IOException {
		Request request = new Request.Builder()
				.url(API_URL)
				.header("x-api-key", API_KEY)
				.build();

		try (Response response = client.newCall(request).execute()) {
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

	//For testing the api
/*	public static void main(String[] args) {
		CatApiClient catApiClient = new CatApiClient();
		try {
			String imageUrl = catApiClient.getCatImageUrl();
			System.out.println("Cat Image URL: " + imageUrl);

			// Define the path where the image will be saved
			String destinationPath = "catimages/cat.jpg";
			File destinationFile = new File(destinationPath);

			// Ensure the directory exists
			destinationFile.getParentFile().mkdirs();

			// Download and save the image
			catApiClient.downloadImage(imageUrl, destinationPath);
			System.out.println("Image saved to: " + destinationPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}*/
}
