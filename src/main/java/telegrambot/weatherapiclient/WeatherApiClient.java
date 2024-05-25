package telegrambot.weatherapiclient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class WeatherApiClient {
	private static final String API_URL = "https://api.openweathermap.org/data/2.5/weather";
	private static final String GEO_API_URL = "http://api.openweathermap.org/geo/1.0/direct?";
	private static final String API_KEY = System.getenv("WEATHER_API");
	private static final String LATITUDE = "52.375893";
	private static final String LONGITUDE = "9.732010";

	private static final OkHttpClient okClient = new OkHttpClient();
	private static final ObjectMapper objectMapper = new ObjectMapper();

	public WeatherApiClient() {
//		this.okClient = new OkHttpClient();
//		this.objectMapper = new ObjectMapper();
	}

	private static String getHannoverWeather() throws IOException {
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

	public String getWeatherFromAnyCity(String... cityAndOptionalCodes) throws IOException {
		String[] coordinates = getCoordinatesFromCity(cityAndOptionalCodes);
		if (coordinates.length == 1) {
			return "Couldn't find the city, please try again";
		}
		String latitude = coordinates[0];
		String longitude = coordinates[1];


		String url = String.format("%s?lat=%s&lon=%s&appid=%s&units=metric", API_URL, latitude, longitude, API_KEY);
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
			String description = responseData.get("weather").get(0).get("description").toString().replaceAll("\"", "").trim();
			String windSpeed = responseData.get("wind").get("speed").toString().replaceAll("\"", "").trim();
			double feelTemp = responseData.get("main").get("feels_like").asDouble();
			double humidity = responseData.get("main").get("humidity").asDouble();
			double temp_min = responseData.get("main").get("temp_min").asDouble();
			double temp_max = responseData.get("main").get("temp_max").asDouble();


			StringBuilder builder = new StringBuilder();

			builder.append("Weather for the City of " + city);
			builder.append(" with country code: " + countryCode);
			builder.append(" in the state of: " + coordinates[2]);
			builder.append("\nDescription: " + description);
			builder.append("\nTemperature: " + temperature + "°C");
			builder.append("\nWindspeed: " + windSpeed + " m/s");
			builder.append("\nWhich feels like: " + feelTemp + "°C");
			builder.append("\nCurrent min/max: " + temp_min + "/" + temp_max + "°C");
			builder.append("\nHumidity: " + humidity + "%");

			return builder.toString();
		}
	}

	private static String[] getCoordinatesFromCity(String... input) throws IOException  {
/*		int n = input.length;
		String url = "";
		if (n == 3) {
			url = String.format("%sq=%s,%s,%s&limit=1&appid=%s", GEO_API_URL, input, input, input, API_KEY);
		} else if (n == 2) {
			url = String.format("%sq=%s,%s&limit=1&appid=%s", GEO_API_URL, input, input, API_KEY);
		} else if (n == 1) {
			url = String.format("%sq=%s&limit=1&appid=%s", GEO_API_URL, input.toString(), API_KEY);
		}*/
		String joinedInput = String.join(",", input);
		String url = String.format("%sq=%s&limit=1&appid=%s&units=metric", GEO_API_URL, joinedInput, API_KEY);

		Request request = new Request.Builder()
				.url(url)
				.build();

		try (Response response = okClient.newCall(request).execute()) {
			if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

			JsonNode responseData = objectMapper.readTree(response.body().string());

			if (responseData.isArray() && responseData.size() > 0) {
				JsonNode location = responseData.get(0);  // Get the first element in the array

				String latitude = location.get("lat").toString();
				String longitude = location.get("lon").toString();

				// Sometimes
				String state = location.get("state") == null ? "Weather API couldn't find the information" : location.get("state").toString();
				return new String[]{latitude, longitude, state};
			} else {
				String[] empty = new String[1];
				return empty;
//				throw new IOException("No location data found for the given city.");
			}
		}

	}

	public static void main(String[] args) throws IOException {
		System.out.println(getHannoverWeather());
//		System.out.println(getWeatherFromAnyCity("Hamburg"));

	}
}
