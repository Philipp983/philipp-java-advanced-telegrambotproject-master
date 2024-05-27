package telegrambot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.Map;

/**
 * The MultiUserBot is used to allow telegram to have different users interacting with the bot, without them having
 * the same session. this is achieved by checking each id of the user contacting the bot whether that id already exists
 * as a key in the hashmap, and if not, a new bot instance is created with the id as its key. therefore, each
 * input
 */
public class MultiUserBot extends TelegramLongPollingBot {
    private Map<Long, Bot2> userBots;
    public MultiUserBot() {
        userBots = new HashMap<>();
    }

    public void onUpdateReceived(Update update) {
        long id = 0;
        if (update.hasMessage() && update.getMessage().hasText()) {
            id = update.getMessage().getChatId();
            String text = update.getMessage().getText();
            // Handle message interactions
            // For example, process text messages
        } else if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            id = update.getCallbackQuery().getMessage().getChatId();
            // Handle callback interactions
            // For example, process button presses based on callback data
        } else if (update.getMessage().hasVoice()) {
            id = update.getMessage().getChatId();
        }
//            Long chatId = update.getMessage().getChatId();
//            Bot bot2 = userBots.get(chatId);
            Bot2 bot = userBots.get(id);

            if (bot == null) {
                bot = new Bot2();
                userBots.put(id, bot);
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
