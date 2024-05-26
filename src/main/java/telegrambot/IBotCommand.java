package telegrambot;

import java.io.IOException;

public interface IBotCommand {
	void execute(long chatId, String input, Bot2 bot) throws IOException;
}
