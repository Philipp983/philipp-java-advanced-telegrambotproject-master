package telegrambot.quiz;

import telegrambot.IRun;
import telegrambot.catapiclient.CatApiClient;
import telegrambot.configuration.Config;

public class MillionaireGame implements IRun {

	private static final String CAT_IMAGE_PATH = Config.getProperty("cat_image.path");
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
//	private CatApiClient catApiClient;

	public MillionaireGame(){
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
//		this.catApiClient = new CatApiClient();
	}

	@Override
	public void run() {

	}
}
