package telegrambot.taskmanagment;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import telegrambot.Bot2;
import telegrambot.IBotCommand;
import telegrambot.apiclients.speechtotext.TranscriptionAPI;

import java.io.IOException;

public class TaskBot2 implements IBotCommand {
	private TaskManager taskManager;
	private boolean isAddingTask = false;
	private boolean isDeletingTask = false;
	private boolean isEditingTask = false;
	private boolean isCompletingTask = false;
	private boolean isEditingTaskDetails = false;
	private String editingTaskTitle;
	private TelegramLongPollingBot bot;
	private TranscriptionAPI transcribeServiceAudioToText;

	public TaskBot2(TelegramLongPollingBot bot) {
		this.taskManager = new TaskManager();
		this.bot = bot;
		this.transcribeServiceAudioToText = new TranscriptionAPI();
	}

	public void useTaskManager(long id, String txt) {
		System.out.println("hello");
	}
	@Override
	public void execute(long chatId, String input, Bot2 bot) throws IOException {
		bot.activateFunctionality("useTaskManager");
	}
}
