package telegrambot.telegram_ui;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import telegrambot.quiz.MillionaireGame;
import telegrambot.quiz.Question;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TelegramMenuUi {

	private TelegramLongPollingBot telegramLongPollingBot;
	private MillionaireGame millionaireGame;

	public TelegramMenuUi(TelegramLongPollingBot telegramLongPollingBot) {
		this.telegramLongPollingBot = telegramLongPollingBot;
	}

	public void setMillionaireGame(MillionaireGame millionaireGame) {
		this.millionaireGame = millionaireGame;
	}

	public void sendInlineKeyboard(long chatId, String text) {
		InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
		List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

		// Add buttons
		rowsInline.add(createInlineKeyboardButton("Play: Who wants to be a millionaire", "/startgame"));
		rowsInline.add(createInlineKeyboardButton("Use: Weather API", "weather_api"));
		rowsInline.add(createInlineKeyboardButton("Get a Cat Image!", "cat_image"));

		inlineKeyboardMarkup.setKeyboard(rowsInline);

		SendMessage message = SendMessage.builder().chatId(chatId).text(text).replyMarkup(inlineKeyboardMarkup).build();
		try {
			telegramLongPollingBot.execute(message);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}

	private List<InlineKeyboardButton> createInlineKeyboardButton(String text, String callbackData) {
		List<InlineKeyboardButton> rowInline = new ArrayList<>();
		InlineKeyboardButton button = new InlineKeyboardButton();
		button.setText(text);
		button.setCallbackData(callbackData);
		rowInline.add(button);
		return rowInline;
	}

	public void sendInlineKeyboard2(long chatId, String text) {
		// Create inline keyboard markup
		InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

		// Create list of keyboard rows
		List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

		// First row
		List<InlineKeyboardButton> rowInline1 = new ArrayList<>();
		InlineKeyboardButton button1 = new InlineKeyboardButton();
		button1.setText("Play: Who wants to be a millionaire");
		button1.setCallbackData("/startgame");
		rowInline1.add(button1);

		// Second row
		List<InlineKeyboardButton> rowInline2 = new ArrayList<>();
		InlineKeyboardButton button2 = new InlineKeyboardButton();
		button2.setText("Use: Weather API");
		button2.setCallbackData("weather_api");
		rowInline2.add(button2);

		List<InlineKeyboardButton> rowInline3 = new ArrayList<>();
		InlineKeyboardButton button3 = new InlineKeyboardButton();
		button3.setText("Get a Cat Image!");
		button3.setCallbackData("cat_image");
		rowInline3.add(button3);

		// Add rows to the list
		rowsInline.add(rowInline1);
		rowsInline.add(rowInline2);
		rowsInline.add(rowInline3);

		// Set the keyboard to the markup
		inlineKeyboardMarkup.setKeyboard(rowsInline);

		// Create message
		SendMessage message = new SendMessage();
		message.setChatId(chatId);
		message.setText(text);
		message.setReplyMarkup(inlineKeyboardMarkup);

		try {
			telegramLongPollingBot.execute(message);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}

	public void sendPhoto(long userID, String pathname) {
		SendPhoto sendPhoto = new SendPhoto();
		sendPhoto.setChatId(String.valueOf(userID));
		sendPhoto.setPhoto(new InputFile(new File(pathname)));
		try {
			telegramLongPollingBot.execute(sendPhoto);
		} catch (TelegramApiException e) {
			throw new RuntimeException(e);
		}
	}

	public void sendAudio(String file_id, String pathname) {
		SendAudio sendAudio = new SendAudio();
		sendAudio.setChatId(file_id);
		sendAudio.setAudio(new InputFile(new File(pathname)));
		try {
			telegramLongPollingBot.execute(sendAudio);
		} catch (TelegramApiException e) {
			throw new RuntimeException(e);
		}
	}

	public void sendVideo(String file_id, String pathname) {
		SendVideo sendVideo = new SendVideo();
		sendVideo.setChatId(file_id);
		sendVideo.setVideo(new InputFile(new File(pathname)));
		try {
			telegramLongPollingBot.execute(sendVideo);
		} catch (TelegramApiException e) {
			throw new RuntimeException(e);
		}
	}

	public void sendMenu(Long who, String txt, ReplyKeyboardMarkup buttons) {
		SendMessage sm = SendMessage.builder().chatId(who.toString())
				.parseMode("HTML").text(txt)
				.replyMarkup(buttons != null ? buttons : new ReplyKeyboardRemove()).build();

		try {
			telegramLongPollingBot.execute(sm);
		} catch (TelegramApiException e) {
			throw new RuntimeException(e);
		}
	}

	public void sendMenuWithLadder(Long who, String txt, String ladder, ReplyKeyboardMarkup buttons) {
		String fullMessage = ladder + "\n\n" + txt;

		SendMessage sm = SendMessage.builder().chatId(who.toString())
				.parseMode("HTML").text(fullMessage)
				.replyMarkup(buttons != null ? buttons : new ReplyKeyboardRemove()).build();

		try {
			telegramLongPollingBot.execute(sm);
		} catch (TelegramApiException e) {
			throw new RuntimeException(e);
		}
	}

	public ReplyKeyboardMarkup startOver() {
		ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
		keyboardMarkup.setResizeKeyboard(true);
		List<KeyboardRow> keyboard = new ArrayList<>();

		KeyboardRow row = new KeyboardRow();

		row.add(new KeyboardButton("Lets start again!"));
		keyboard.add(row);
		keyboardMarkup.setKeyboard(keyboard);
		return keyboardMarkup;
	}

	public ReplyKeyboardMarkup fiftyFiftyMenu(String[] twoOptions) {

		ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
		keyboardMarkup.setResizeKeyboard(true);
		List<KeyboardRow> keyboard = new ArrayList<>();

		for (String option : twoOptions) {
			KeyboardRow row = new KeyboardRow();
			row.add(new KeyboardButton(option));
			keyboard.add(row);
		}

		keyboardMarkup.setKeyboard(keyboard);
		return keyboardMarkup;
	}

	public ReplyKeyboardMarkup jokerMenu() {
		ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
		keyboardMarkup.setResizeKeyboard(true);
		List<KeyboardRow> keyboard = new ArrayList<>();

		if (millionaireGame.isPhoneJokerAvailable()) {
			KeyboardRow row = new KeyboardRow();
			row.add(new KeyboardButton("PhoneJoker"));
			keyboard.add(row);
		}

		if (millionaireGame.isFiftyFiftyJokerAvailable()) {
			KeyboardRow row = new KeyboardRow();
			row.add(new KeyboardButton("50:50"));
			keyboard.add(row);
		}
		if (millionaireGame.isAudianceJokerAvailable()) {
			KeyboardRow row = new KeyboardRow();
			row.add(new KeyboardButton("Audience"));
			keyboard.add(row);
		}

		keyboardMarkup.setKeyboard(keyboard);
		return keyboardMarkup;
	}

	public ReplyKeyboardMarkup createPlayMenu(Question question) {
		ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
		keyboardMarkup.setResizeKeyboard(true);
		List<KeyboardRow> keyboard = new ArrayList<>();

		for (String option : question.getOptions()) {
			KeyboardRow row = new KeyboardRow();
			row.add(new KeyboardButton(option));
			keyboard.add(row);
		}
		if (millionaireGame.isAnyJokerAvailable()) {
			KeyboardRow row = new KeyboardRow();
			row.add(new KeyboardButton("Joker"));
			keyboard.add(row);
		}

		keyboardMarkup.setKeyboard(keyboard);
		return keyboardMarkup;
	}

	public ReplyKeyboardMarkup createWelcomingMenu() {
		ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
		keyboardMarkup.setResizeKeyboard(true);
		List<KeyboardRow> keyboard = new ArrayList<>();

		KeyboardRow row = new KeyboardRow();

		row.add(new KeyboardButton("Lets start with the first question"));
		keyboard.add(row);
		keyboardMarkup.setKeyboard(keyboard);
		return keyboardMarkup;
	}





}
