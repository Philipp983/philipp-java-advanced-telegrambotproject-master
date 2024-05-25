package telegrambot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import telegrambot.catapiclient.CatApiClient;
import telegrambot.configuration.Config;
import telegrambot.quiz.Millionaire;
import telegrambot.telegram_ui.TelegramMenuUi;
import telegrambot.weatherapiclient.WeatherApiClient;
import java.io.IOException;

public class Bot2 extends TelegramLongPollingBot {

	private static final String CAT_IMAGE_PATH = Config.getProperty("cat_image.path");
	private TelegramMenuUi menuUI;
	private Millionaire millionaireGame;
	private CatApiClient catApiClient;
	private WeatherApiClient weatherApiClient;
	private boolean isContaced;
	boolean playMillionare;
	boolean useWeatherAPI;
	boolean createCatImage;

	public Bot2() {
		this.millionaireGame = new Millionaire(this);
		this.catApiClient = new CatApiClient();
		this.weatherApiClient = new WeatherApiClient();
		this.menuUI = new TelegramMenuUi(this, millionaireGame);
	}

	@Override
	public void onUpdateReceived(Update update) {
		long id = 0;
		String txt = "";

		// Handle message interactions
		if (update.hasMessage() && update.getMessage().hasText()) {
			id = update.getMessage().getChatId();
			txt = update.getMessage().getText();
			if (txt.equals("Back to menu")) {
				isContaced = false;
				playMillionare = false;
				useWeatherAPI = false;
				createCatImage = false;
			}
			// Handle callback interactions
			// For example, process button presses based on callback data
		} else if (update.hasCallbackQuery()) {
			txt = update.getCallbackQuery().getData();
			if (txt.equals("/quit")) {
				isContaced = false;
				playMillionare = false;
				useWeatherAPI = false;
				createCatImage = false;
			}
			System.out.println(txt);
			id = update.getCallbackQuery().getMessage().getChatId();
		}

		if ((update.hasMessage() || update.hasCallbackQuery()) && !isContaced) {
			menuUI.sendInlineKeyboard2(id, "Choose an option:");
			System.out.println(txt);
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
		if (playMillionare) {
			millionaireGame.isGameInProgress(id,txt);
		} else if (useWeatherAPI) {
			menuUI.sendInlineKeyboard3(id, "Enter the city name for current weather: ");
			if (update.getMessage() != null) {
				try {
					String weather = weatherApiClient.getWeatherFromAnyCity(txt);
					menuUI.sendText(id, weather);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		} else if (createCatImage) {
			while (!catApiClient.createCatImage());
			menuUI.sendPhoto(id, CAT_IMAGE_PATH);
			catApiClient.deleteImage(CAT_IMAGE_PATH);
			menuUI.sendInlineKeyboard2(id, "Choose an option:");
		}
	}

	private void handleUserInput(long id, String input) throws IOException {
		switch (input) {
			case "/startgame":
				playMillionare = true;
				useWeatherAPI = false;
				createCatImage = false;
				break;
			case "/weather_api":
				playMillionare = false;
				useWeatherAPI = true;
				createCatImage = false;
				break;
			case "/cat_image":
				playMillionare = false;
				useWeatherAPI = false;
				createCatImage = true;
				break;
		}
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
