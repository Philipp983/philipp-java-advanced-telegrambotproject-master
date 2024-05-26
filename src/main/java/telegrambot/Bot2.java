package telegrambot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import telegrambot.apiclients.speechtotext.App;
import telegrambot.apiclients.texttospeech.TextToSpeechAPI;
import telegrambot.catapiclient.CatApiClient;
import telegrambot.configuration.CommandRegistry;
import telegrambot.configuration.Config;
import telegrambot.quiz.Millionaire;
import telegrambot.telegram_ui.TelegramMenuUi;
import telegrambot.apiclients.weatherapiclient.WeatherApiClient;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

public class Bot2 extends TelegramLongPollingBot {

	private static final String CAT_IMAGE_PATH = Config.getProperty("cat_image.path");
	private final TelegramMenuUi menuUI;
	private final Millionaire millionaireGame;
	private final CatApiClient catApiClient;
	private final WeatherApiClient weatherApiClient;
	private final TextToSpeechAPI textToSpeechAPI;
	private boolean isContaced;
	private boolean isWeatherGenerated;
	private final CommandRegistry commandRegistry;
	private final Map<String, Boolean> functionalities;
	private String weather = "Please enter a city first";

	public Bot2() {
		this.millionaireGame = new Millionaire(this);
		this.catApiClient = new CatApiClient();
		this.weatherApiClient = new WeatherApiClient();
		this.textToSpeechAPI = new TextToSpeechAPI();
		this.menuUI = new TelegramMenuUi(this, millionaireGame);

		this.commandRegistry = new CommandRegistry();
		commandRegistry.register("/startgame", new Millionaire(this));
		commandRegistry.register("/weather_api", new WeatherApiClient());
		commandRegistry.register("/cat_image", new CatApiClient());
		commandRegistry.register("/readout", new TextToSpeechAPI());

		// Initialize the functionalities map
		functionalities = new HashMap<>();
		functionalities.put("playMillionaire", false);
		functionalities.put("useWeatherAPI", false);
		functionalities.put("createCatImage", false);
		functionalities.put("useTTS", false);
	}
	@Override
	public void onUpdateReceived(Update update) {
		long id = 0;
		String txt = "";
		// Handle message interactions
		if (update.hasMessage() && update.getMessage().hasText()) {
			id = update.getMessage().getChatId();
			txt = update.getMessage().getText();
			System.out.println("The current text from message is: " + txt);
			System.out.println(txt);
			if (txt.equals("Back to menu")) {
				resetFunctionalities();
				menuUI.clearKeyboard(id);
				isContaced = false;
			}
			// Handle callback interactions
			// For example, process button presses based on callback data
		} else if (update.hasCallbackQuery()) {
			txt = update.getCallbackQuery().getData();
			System.out.println("Current text out of callback: " + txt);
			if (txt.equals("/quit")) {
				resetFunctionalities();
				weather = "Please enter a city first";
				isContaced = false;
				isWeatherGenerated = false;
			}
			id = update.getCallbackQuery().getMessage().getChatId();
		} 			else if (update.hasMessage() && update.getMessage().hasVoice()) {
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
		}

		if ((update.hasMessage() || update.hasCallbackQuery()) && !isContaced) {
			menuUI.sendInlineKeyboard2(id, "Choose an option:");
			isContaced = true;
		}
		try {
			handleUserInput(id, txt);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		try {
			run(id, update, txt);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void run(long id, Update update, String txt) throws IOException {
		boolean isMillionare = functionalities.get("playMillionaire");
		boolean isWeather = functionalities.get("useWeatherAPI");
		boolean isCatImage = functionalities.get("createCatImage");
		boolean isTTS = functionalities.get("useTTS");
		System.out.println("TTS is: " + isTTS);

		if (isTTS) {
			String audio = textToSpeechAPI.translateTextToSpeech(weather);
			try {
				Thread.sleep(2000);
			} catch (Exception e) {
				e.printStackTrace();
			}
			menuUI.sendAudio(Long.toString(id), audio);
			activateFunctionality("useWeatherAPI");
		}

		if (isMillionare) {

			millionaireGame.isGameInProgress(id,txt);

		} else if (isWeather) {
			menuUI.sendInlineKeyboard4(id, "Enter the city name for current weather:\nOr read out the last weather:");
				if (update.getMessage() != null) {
					try {
						System.out.println("is weather updated1 " + isWeatherGenerated);
						weather = weatherApiClient.getWeatherFromAnyCity(txt);
						isWeatherGenerated = true;
						System.out.println("is weather updated2 " + isWeatherGenerated);
						System.out.println(weather.length());
						System.out.println(weather);
						menuUI.sendText(id, weather);
						try {
							Thread.sleep(500);
						} catch (Exception e) {
							e.printStackTrace();
						}
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}

		} else if (isCatImage) {
			while (!catApiClient.createCatImage()) {
				try {
					Thread.sleep(100); // Sleep for 100 milliseconds
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt(); // Restore interrupted status
					throw new RuntimeException("Thread was interrupted", e);
				}
			}
			menuUI.sendPhoto(id, CAT_IMAGE_PATH);
			catApiClient.deleteImage(CAT_IMAGE_PATH);
			menuUI.sendInlineKeyboard2(id, "Choose an option:");
		}
	}

	private void handleUserInput(long id, String input) throws IOException {
		IBotCommand command = commandRegistry.getCommand(input);
		if (command != null) {
			command.execute(id, input, this);
		}
	}

		private void downloadVoiceFile(String fileId, long chatId) throws IOException {
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
	}

	public void activateFunctionality(String functionality) {
		resetFunctionalities();
		functionalities.put(functionality, true);
	}

	private void resetFunctionalities() {
		functionalities.keySet().forEach(key -> functionalities.put(key, false));

	}

	@Override
	public String getBotUsername() {
		return System.getenv("BOT_USERNAME");
	}

	@Override
	public String getBotToken() {
		return System.getenv("BOT_TOKEN");
	}
}
