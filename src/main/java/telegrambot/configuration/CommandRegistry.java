package telegrambot.configuration;

import telegrambot.IBotCommand;

import java.util.HashMap;
import java.util.Map;

public class CommandRegistry {
	private Map<String, IBotCommand> commands = new HashMap<>();

	public void register(String command, IBotCommand botCommand) {
		commands.put(command, botCommand);
	}

	public IBotCommand getCommand(String command) {
		return commands.get(command);
	}
}
