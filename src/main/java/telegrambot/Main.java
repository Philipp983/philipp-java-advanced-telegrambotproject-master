package telegrambot;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {
    public static void main(String[] args) {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            MultiUserBot multiUserBot = new MultiUserBot();
            botsApi.registerBot(multiUserBot);

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}