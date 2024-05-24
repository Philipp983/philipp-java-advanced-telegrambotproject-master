package telegrambot.telegram_ui;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
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

		if (hasphoneJoker) {
			KeyboardRow row = new KeyboardRow();
			row.add(new KeyboardButton("PhoneJoker"));
			keyboard.add(row);
		}

		if (hasfiftyfifty) {
			KeyboardRow row = new KeyboardRow();
			row.add(new KeyboardButton("50:50"));
			keyboard.add(row);
		}
		if (hasaudiance) {
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
		if (joker) {
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

	@Override
	public void onUpdateReceived(Update update) {

	}

	@Override
	public String getBotUsername() {
		return "";
	}
}
