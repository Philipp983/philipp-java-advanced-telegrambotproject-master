package telegrambot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.Map;


public class MultiUserBot extends TelegramLongPollingBot {
    private Map<Long, Bot> userBots;
    public MultiUserBot() {
        userBots = new HashMap<>();
    }

    public void onUpdateReceived(Update update) {
        Long chatId = update.getMessage().getChatId();
        Bot bot = userBots.get(chatId);

        if (bot == null) {
            bot = new Bot();
            userBots.put(chatId, bot);
        }

        bot.onUpdateReceived(update);
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
