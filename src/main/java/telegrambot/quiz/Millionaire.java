package telegrambot.quiz;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import telegrambot.Bot2;
import telegrambot.IBotCommand;
import telegrambot.configuration.Config;
import telegrambot.telegram_ui.TelegramMenuUi;

import java.io.IOException;
import java.util.Random;

public class Millionaire implements IBotCommand {
	private static final String START_GAME_IMAGE_PATH = Config.getProperty("quiz.image.start_game.path");
	private static final String START_GAME_SOUND_PATH = Config.getProperty("quiz.sound.start.path");
	private static final String AUDIENCE_JOKER_PATH = Config.getProperty("quiz.video.audience.path");
	private static final String WIN_GAME_IMAGE_PATH = Config.getProperty("quiz.image.win_game.path");
	private static final String LOSE_GAME_HAROLD_PATH = Config.getProperty("quiz.image.hide_the_pain.path");
	private static final String LOSE_GAME_NEUTRAL_PATH = Config.getProperty("quiz.image.game_over.path");

	private Quiz quiz;
	private boolean gamestate;
	private int gamelevel;
	private Question currentQuestion;
	private int[] pricepool;
	private boolean joker;
	private String[] phonejoker;
	private boolean hasphoneJoker;
	private boolean hasfiftyfifty;
	private boolean hasaudiance;
	private TelegramMenuUi menu;

	public Millionaire(TelegramLongPollingBot bot) {
		this.quiz = new Quiz(); // Initialize the quiz instance
		this.gamestate = false; // Initialize the gamestate to false
		this.gamelevel = 0; // Initialize the gamelevel to 0
		this.pricepool = new int[]{100, 1000, 16000, 64000, 1000000};
		this.joker = true;
		this.phonejoker = new String[]{
				Config.getProperty("quiz.sound.joker.joker_one.path"),
				Config.getProperty("quiz.sound.joker.joker_two.path"),
				Config.getProperty("quiz.sound.joker.joker_three.path"),
				Config.getProperty("quiz.sound.joker.joker_four.path")
		};
		this.hasphoneJoker = true;
		this.hasfiftyfifty = true;
		this.hasaudiance = true;
		this.menu = new TelegramMenuUi(bot, this);

	}

	public void isGameInProgress(long id, String txt) {
		if (txt.equals("/startgame")) {
			menu.sendPhoto(id, START_GAME_IMAGE_PATH);
			menu.sendAudio(String.valueOf(id), START_GAME_SOUND_PATH);
			menu.sendMenu(id, "Hello to the game\n\nWho wants to be a millionaire!\n\n " +
					"We start with the 100€ question", menu.createWelcomingMenu());
			// This Method sets the game into its initial state
			setGameStateToInitialValues();
		} else if (txt.equals("Lets start again!")) {
			menu.sendMenu(id, "Hello to the game \n\nWho wants to be a millionaire!\n\n " +
					"We start with the 100€ question", menu.createWelcomingMenu());
			setGameStateToInitialValues();
		}
		if (gamestate) {
			if (txt.equals("Lets start with the first question") || txt.equals("Lets start again!") || txt.equals("/startgame")) {
				currentQuestion = quiz.getRandomQuestion(gamelevel);
				menu.sendMenuWithLadder(id, currentQuestion.getText(), generateGameLadder(gamelevel, pricepool), menu.createPlayMenu(currentQuestion));
			} else if (txt.equals("Joker")) {

				menu.sendMenu(id, currentQuestion.getText(), menu.jokerMenu());

			} else if (txt.equals("PhoneJoker")) {

				// PhoneJoker has a chance of 75% to be correct, because it uses a random bool to either
				// give the correct phone joker, or chooses one randomly from the four

				hasphoneJoker = false;
				Random random = new Random();
				// Used to test phoneJoker, sent always the second phone joker
//                String test = phonejoker[1];

				int correctIndex = currentQuestion.getOptions().indexOf(currentQuestion.getSolution());
				System.out.println(correctIndex);
				String path = (random.nextBoolean() ? phonejoker[correctIndex] : phonejoker[random.nextInt(4)]);
				menu.sendAudio(String.valueOf(id), path);
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
				menu.sendMenu(id, currentQuestion.getText(), menu.createPlayMenu(currentQuestion));

				checkJokerValues();

			} else if (txt.equals("50:50")) {
				hasfiftyfifty = false;

				applyfiftyFiftyLogicAndSendMenu(id);

				checkJokerValues();

			} else if (txt.equals("Audience")) {
				hasaudiance = false;

				menu.sendVideo(Long.toString(id), AUDIENCE_JOKER_PATH);
				try{
					Thread.sleep(500);
				} catch (Exception e) {
					e.printStackTrace();

				}
				applyfiftyFiftyLogicAndSendMenu(id);
//                sendMenu(id, currentQuestion.getText(), createPlayMenu(currentQuestion));

				checkJokerValues();


			} else if (txt.equals(currentQuestion.getSolution())) {
				gamelevel++;
				if (gamelevel >= quiz.getQuestionCategories().size()) {
					menu.sendPhoto(id, WIN_GAME_IMAGE_PATH);
					menu.sendMenu(id, "Congratulations! You answered all questions correctly!" +
							"\n\nPlay again or go back to menu", menu.startOverAndBackToMenu());
					gamestate = false;
					gamelevel = 0;
				} else {
//                    sendMenu(id, "You are right! \n\tLets go to the next question. \n\t" +
//                            "This question is worth " + pricepool[gamelevel] + "€", createPlayMenu(currentQuestion));
					menu.sendMenu(id, "You are right! \n\tLets go to the next question. \n\t" +
							"This question is worth " + pricepool[gamelevel] + "€", menu.createPlayMenu(currentQuestion));
					currentQuestion = quiz.getRandomQuestion(gamelevel);
					menu.sendMenuWithLadder(id, currentQuestion.getText(), generateGameLadder(gamelevel, pricepool), menu.createPlayMenu(currentQuestion));
				}
			} else {
				// Wrong answer
				// Uses a random generated bool to send one of the both lose game images
				Random random = new Random();

				menu.sendPhoto(id, random.nextBoolean() ? LOSE_GAME_NEUTRAL_PATH : LOSE_GAME_HAROLD_PATH);
				menu.sendMenu(id, "Sorry, wrong answer!\nGame over!" +
						"\n\nPlay again or go back to menu", menu.startOverAndBackToMenu());

				gamestate = false;
				gamelevel = 0;
			}

			}
		}


private void checkJokerValues() {
	if (hasphoneJoker == false && hasfiftyfifty == false && hasaudiance == false) {
		joker = false;
	}
}

	private String generateGameLadder(int currentLevel, int[] pricePool) {
		StringBuilder ladder = new StringBuilder();
		for (int i = pricePool.length - 1; i >= 0; i--) {
			if (i == currentLevel) {
				ladder.append(">> ");
			} else {
				ladder.append("   ");
			}
			ladder.append(String.format("%,d€", pricePool[i])).append("\n");
		}
		return ladder.toString();
	}

	private static int getRandomNumberExcludingPrevious(int previousNumber) {
		Random random = new Random();
		int newRandomNumber;

		do {
			newRandomNumber = random.nextInt(4); // Generates a random number between 0 (inclusive) and 4 (exclusive)
		} while (newRandomNumber == previousNumber);

		return newRandomNumber;
	}

	private void applyfiftyFiftyLogicAndSendMenu(long id) {
		String correctAnswer = currentQuestion.getSolution();
		System.out.println(correctAnswer);

		int correctIndex = currentQuestion.getOptions().indexOf(currentQuestion.getSolution());
		int wrongIndex = getRandomNumberExcludingPrevious(correctIndex);

		String wrongAnswer = currentQuestion.getOptions().get(wrongIndex);
		System.out.println(wrongAnswer);

		boolean putCorrectFirst = new Random().nextBoolean();
		String[] list = new String[2];
		if (putCorrectFirst) {
			list[0] = correctAnswer;
			list[1] = wrongAnswer;
		} else {
			list[0] = wrongAnswer;
			list[1] = correctAnswer;
		}

		menu.sendMenu(id, currentQuestion.getText(), menu.fiftyFiftyMenu(list));
	}

	private void setGameStateToInitialValues() {
		gamelevel = 0;
		gamestate = true;
		joker = true;
		hasphoneJoker = true;
		hasfiftyfifty = true;
		hasaudiance = true;
	}

// Getter

	public boolean isPhoneJokerAvailable() {
		return hasphoneJoker;
	}

	public boolean isFiftyFiftyJokerAvailable() {
		return hasfiftyfifty;
	}

	public boolean isAudianceJokerAvailable() {
		return hasaudiance;
	}

	public boolean isAnyJokerAvailable() {
		return joker;
	}

	@Override
	public void execute(long chatId, String input, Bot2 bot) throws IOException {
		bot.activateFunctionality("playMillionaire");
	}
}
