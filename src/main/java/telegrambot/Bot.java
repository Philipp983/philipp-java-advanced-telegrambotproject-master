package telegrambot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import telegrambot.quiz.Question;
import telegrambot.quiz.Quiz;
import telegrambot.services.CatService;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Bot extends TelegramLongPollingBot {

    private Quiz quiz;
    private Question currentQuestion;
    
    private CatService catApiClient;
    
    private final String[] phonejoker;
    
    private int gamelevel;
    private int[] pricepool;
    
    private boolean joker;
    private boolean gamestate;
    private boolean hasphoneJoker;
    private boolean hasfiftyfifty;
    private boolean hasaudiance;

    public Bot() {
        this.quiz = new Quiz(); // Initialize the quiz instance
        this.gamestate = false; // Initialize the gamestate to false
        this.gamelevel = 0; // Initialize the gamelevel to 0
        this.pricepool = new int[]{100, 1000, 16000, 64000, 1000000};
        this.joker = true;
        this.phonejoker = new String[]{"./joker/number1.mp3", "./joker/number2.mp3", "./joker/number3.mp3", "./joker/number4.mp3"};
        this.hasphoneJoker = true;
        this.hasfiftyfifty = true;
        this.hasaudiance = true;
        this.catApiClient = new CatService();

    }

    @Override
    public void onUpdateReceived(Update update) {
        var msg = update.getMessage();
        var user = msg.getFrom();
        long id = user.getId();
        long chat_id2 = update.getMessage().getChatId();
        var txt = msg.getText();

        if (update.hasMessage() && update.getMessage().hasText()) {
            if (txt.equals("/startgame")) {
                try {
                    String imageUrl = catApiClient.getImage();
                    System.out.println("Cat Image URL: " + imageUrl);

                    // Define the path where the image will be saved
                    String destinationPath = "catimages/cat.jpg";
                    File destinationFile = new File(destinationPath);

                    // Ensure the directory exists
                    destinationFile.getParentFile().mkdirs();

                    // Download and save the image
                    while (!catApiClient.downloadImage(imageUrl, destinationPath)) ;

                    System.out.println("Image saved to: " + destinationPath);
                    sendPhoto(id, "catimages/cat.jpg");
                    catApiClient.deleteImage("catimages/cat.jpg");

                } catch (IOException e) {
                    e.printStackTrace();
                }




                sendPhoto(id, "./start.jpg");
                sendAudio(String.valueOf(id), "./start-sound.mp3");
                sendMenu(id, "Hello to the game\n\nWho wants to be a millionaire!\n\n " +
                        "We start with the 100€ question", createWelcomingMenu());
                // This Method sets the game into its initial state
                setGameStateToInitialValues();
            } else if (txt.equals("Lets start again!")) {
                sendMenu(id, "Hello to the game \n\nWho wants to be a millionaire!\n\n " +
                        "We start with the 100€ question", createWelcomingMenu());
                setGameStateToInitialValues();
            }
            //sendMessage(id, "I don't understand you");

        }

        if (gamestate) {
            if (txt.equals("Lets start with the first question") || txt.equals("Lets start again!") || txt.equals("/startgame")) {
                currentQuestion = quiz.getRandomQuestion(gamelevel);
                sendMenuWithLadder(id, currentQuestion.getText(), generateGameLadder(gamelevel, pricepool), createPlayMenu(currentQuestion));
//                sendMenu(id, currentQuestion.getText(), createPlayMenu(currentQuestion));

            } else if (txt.equals("Joker")) {

                sendMenu(id, currentQuestion.getText(), jokerMenu());

            } else if (txt.equals("PhoneJoker")) {

                hasphoneJoker = false;
                Random random = new Random();
                // Used to test phoneJoker, sent always the second phone joker
//                String test = phonejoker[1];
                int correctIndex = currentQuestion.getOptions().indexOf(currentQuestion.getSolution());
                System.out.println(correctIndex);
                String path = (random.nextBoolean() ? phonejoker[correctIndex] : phonejoker[random.nextInt(4)]);
                sendAudio(String.valueOf(id), path);
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                sendMenu(id, currentQuestion.getText(), createPlayMenu(currentQuestion));

                checkJokerValues();

            } else if (txt.equals("50:50")) {
                hasfiftyfifty = false;

                applyfiftyFiftyLogicAndSendMenu(id);

                checkJokerValues();

            } else if (txt.equals("Audience")) {
                hasaudiance = false;

                sendVideo(Long.toString(id), "./audience.mp4");
                applyfiftyFiftyLogicAndSendMenu(id);
//                sendMenu(id, currentQuestion.getText(), createPlayMenu(currentQuestion));

                checkJokerValues();


            } else if (txt.equals(currentQuestion.getSolution())) {
                gamelevel++;
                if (gamelevel >= quiz.getCategories().size()) {
                    sendPhoto(id, "./win.png");
                    sendMenu(id, "Congratulations! You answered all questions correctly!", startOver());
                    gamestate = false;
                    gamelevel = 0;
                } else {
//                    sendMenu(id, "You are right! \n\tLets go to the next question. \n\t" +
//                            "This question is worth " + pricepool[gamelevel] + "€", createPlayMenu(currentQuestion));
                    sendMenu(id, "You are right! \n\tLets go to the next question. \n\t" +
                            "This question is worth " + pricepool[gamelevel] + "€", createPlayMenu(currentQuestion));
                    currentQuestion = quiz.getRandomQuestion(gamelevel);
                    sendMenuWithLadder(id, currentQuestion.getText(), generateGameLadder(gamelevel, pricepool), createPlayMenu(currentQuestion));
                }
            } else {
                // Wrong answer
                sendPhoto(id, "./gameover.png");
                sendMenu(id, "Sorry, wrong answer!\n Game over", startOver());
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

    private void setGameStateToInitialValues() {
        gamelevel = 0;
        gamestate = true;
        joker = true;
        hasphoneJoker = true;
        hasfiftyfifty = true;
        hasaudiance = true;
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

        sendMenu(id, currentQuestion.getText(), fiftyFiftyMenu(list));
    }

    private static int getRandomNumberExcludingPrevious(int previousNumber) {
        Random random = new Random();
        int newRandomNumber;

        do {
            newRandomNumber = random.nextInt(4); // Generates a random number between 0 (inclusive) and 4 (exclusive)
        } while (newRandomNumber == previousNumber);

        return newRandomNumber;
    }

    private void sendPhoto(long userID, String pathname) {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(String.valueOf(userID));
        sendPhoto.setPhoto(new InputFile(new File(pathname)));
        try {
            execute(sendPhoto);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendAudio(String file_id, String pathname) {
        SendAudio sendAudio = new SendAudio();
        sendAudio.setChatId(file_id);
        sendAudio.setAudio(new InputFile(new File(pathname)));
        try {
            execute(sendAudio);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendVideo(String file_id, String pathname) {
        SendVideo sendVideo = new SendVideo();
        sendVideo.setChatId(file_id);
        sendVideo.setVideo(new InputFile(new File(pathname)));
        try {
            execute(sendVideo);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendMenu(Long who, String txt, ReplyKeyboardMarkup buttons) {
        SendMessage sm = SendMessage.builder().chatId(who.toString())
                .parseMode("HTML").text(txt)
                .replyMarkup(buttons != null ? buttons : new ReplyKeyboardRemove()).build();

        try {
            execute(sm);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendMenuWithLadder(Long who, String txt, String ladder, ReplyKeyboardMarkup buttons) {
        String fullMessage = ladder + "\n\n" + txt;

        SendMessage sm = SendMessage.builder().chatId(who.toString())
                .parseMode("HTML").text(fullMessage)
                .replyMarkup(buttons != null ? buttons : new ReplyKeyboardRemove()).build();

        try {
            execute(sm);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private ReplyKeyboardMarkup startOver() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();

        row.add(new KeyboardButton("Lets start again!"));
        keyboard.add(row);
        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }

    private ReplyKeyboardMarkup fiftyFiftyMenu(String[] twoOptions) {

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

    private ReplyKeyboardMarkup jokerMenu() {
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

    private ReplyKeyboardMarkup createPlayMenu(Question question) {
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

    private ReplyKeyboardMarkup createWelcomingMenu() {
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
    public String getBotUsername() {
        return System.getenv("BOT_USERNAME");
    }

    @Override
    public String getBotToken() {
        return System.getenv("BOT_TOKEN");
    }


}

