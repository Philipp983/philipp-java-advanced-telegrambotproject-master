package telegrambot.weatherapiclient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class WeatherApiClient {
	private static final String API_URL = "https://api.openweathermap.org/data/2.5/weather";
	private static final String API_KEY = System.getenv("WEATHER_API");
	private static final String LATITUDE = "52.375893";
	private static final String LONGITUDE = "9.732010";

	private static final OkHttpClient okClient = new OkHttpClient();
	private static final ObjectMapper objectMapper = new ObjectMapper();

	public WeatherApiClient() {
//		this.okClient = new OkHttpClient();
//		this.objectMapper = new ObjectMapper();
	}

	public static String getHannoverWeather() throws IOException {
		String url = String.format("%s?lat=%s&lon=%s&appid=%s", API_URL, LATITUDE, LONGITUDE, API_KEY);
		Request request = new Request.Builder()
				.url(url)
				.build();
		try (Response response = okClient.newCall(request).execute()) {
			if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

			JsonNode responseData = objectMapper.readTree(response.body().string());
			// Now you can parse the JSON response as needed
			// For example, you can extract the temperature:
			double temperature = responseData.get("main").get("temp").asDouble();
			String city = responseData.get("name").toString().replaceAll("\"", "").trim();
			String countryCode = responseData.get("sys").get("country").toString().replaceAll("\"", "").trim();
			double feelTemp = responseData.get("main").get("feels_like").asDouble();
			double humidity = responseData.get("main").get("humidity").asDouble();

			StringBuilder builder = new StringBuilder();

			builder.append("In the City of " + city);
			builder.append(" with country code: " + countryCode);
			builder.append(", the temperature is: " + temperature);
			builder.append(" which feels like: " + feelTemp);
			builder.append(". The humidity is: " + humidity);

			return builder.toString();
		}

	}



	public static void main(String[] args) throws IOException {
		System.out.println(getHannoverWeather());
	}
}
