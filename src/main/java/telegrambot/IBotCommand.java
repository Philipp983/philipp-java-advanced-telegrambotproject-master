package telegrambot;

import java.io.IOException;

/**
 * Functional interface for the subscriber pattern. Classes that should be
 */
public interface IBotCommand {
	void execute(long chatId, String input, Bot2 bot) throws IOException;
}
