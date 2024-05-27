package telegrambot.taskmanagment;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import telegrambot.Bot2;
import telegrambot.IBotCommand;
import telegrambot.apiclients.speechtotext.TranscriptionAPI;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TaskBot2 implements IBotCommand {
	private TaskManager taskManager;
	private boolean isAddingTask = false;
	private boolean isDeletingTask = false;
	private boolean isDeletingAllTasks = false;

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
		if (txt.equals("/task_Manager")) {
			sendMainMenu(id);
		} else if (txt.equals("/add_task")) {
			sendAddTaskMenu(id);
			isAddingTask = true;
		} else if (isAddingTask) {
			handleAddTask(id, txt);
		} else if (txt.equals("/view_tasks")) {
			sendTasksList(id);
		} else if (txt.equals("/delete_task")) {
			sendDeleteTaskPrompt(id);
		}  else if (txt.equals("/delete_all_tasks")) {
			handleDeleteAllTasks(id);
		}   else if (isDeletingTask) {
			handleDeleteTask(id, txt);
		}else if (isDeletingAllTasks) {
			handleDeleteAllTasks(id);
		}

	}

	private void sendTasksList(long chatId) {
		List<Task> tasks = taskManager.getTasks();
		StringBuilder response = new StringBuilder("Tasks:\n");
		int counter = 1;
		for (Task task : tasks) {
			response.append("Task ").append(counter).append(": ")
					.append(task.getTitle()).append(" (").append(task.getToDo()).append(")\n");
			counter++;
		}
		sendTextMessage(chatId, response.toString());
		sendMainMenu(chatId);
	}

	private void handleDeleteTask(long chatId, String title) {

		if(taskManager.deleteTask(title)){
			sendTextMessage(chatId, "Task deleted successfully!");}
		else {
			sendTextMessage(chatId, "Task name not correct!");
		}
		isDeletingTask = false;
		sendMainMenu(chatId);
	}
	private void handleDeleteAllTasks(long chatId) {

		if(taskManager.deleteAllTasks()){
			sendTextMessage(chatId, "All Tasks deleted successfully!");}
		else {
			sendTextMessage(chatId, "Something went wrong, please check!");
		}
		isDeletingAllTasks = false;
		sendMainMenu(chatId);
	}

	private void sendDeleteTaskPrompt(long chatId) {
		sendTextMessage(chatId, "Please enter the title of the task you want to delete:");
		isDeletingTask = true;
	}
	private void sendDeleteAllTasksPrompt(long chatId) {
		sendTextMessage(chatId, "All tasks are going to be deleted, sure?:");
		isDeletingAllTasks = true;
	}

	private void sendTasksList2(long chatId) {
		List<Task> tasks = taskManager.getTasks();
		StringBuilder response = new StringBuilder("Tasks:\n");
		for (Task task : tasks) {
			String status = task.isCompleted() ? "[✅]" : "[ ]";
			response.append(status).append(" ").append(task.getTitle()).append(" (").append(task.getToDo()).append(", ").append(task.getCategory()).append(")\n");
		}
		sendTextMessage(chatId, response.toString());
		sendMainMenu(chatId);
	}

	private void handleAddTask(long chatId, String taskDetails) {
		// here my voice recording api

		String taskInfo = taskDetails;
			String title = "The Task";
			String deadline = taskInfo;
			String category = "Voice recording";
			Task task = new Task(title, deadline, category);
			taskManager.addTask(task);
			sendTextMessage(chatId, "Task added successfully!");

		isAddingTask = false;
		sendMainMenu(chatId);
	}

	private void sendTextMessage(long chatId, String text) {
		SendMessage message = new SendMessage();
		message.setChatId(chatId);
		message.setText(text);
		try {
			bot.execute(message);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}

	private void sendAddTaskMenu(long chatId) {
		SendMessage message = new SendMessage();
		message.setChatId(chatId);
		message.setText("Add Task by typing or using the record function.\n" +
				"Audio recording takes a bit, so please be patient 🤖 ");

		try {
			bot.execute(message);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}

	private void sendMainMenu(long chatId) {
		SendMessage message = new SendMessage();
		message.setChatId(chatId);
		message.setText("Welcome! What would you like to do?");
		message.setReplyMarkup(getMainMenuKeyboard());
		try {
			bot.execute(message);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}

	private InlineKeyboardMarkup getMainMenuKeyboard() {
		InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
		List<List<InlineKeyboardButton>> rows = new ArrayList<>();

		List<InlineKeyboardButton> row = new ArrayList<>();
		InlineKeyboardButton addTaskButton = new InlineKeyboardButton();
		addTaskButton.setText("Add Task");
		addTaskButton.setCallbackData("/add_task");
		row.add(addTaskButton);
		rows.add(row);

		row = new ArrayList<>();
		InlineKeyboardButton viewTasksButton = new InlineKeyboardButton();
		viewTasksButton.setText("View Tasks");
		viewTasksButton.setCallbackData("/view_tasks");
		row.add(viewTasksButton);
		rows.add(row);

		row = new ArrayList<>();
		InlineKeyboardButton deleteTaskButton = new InlineKeyboardButton();
		deleteTaskButton.setText("Delete Task");
		deleteTaskButton.setCallbackData("/delete_task");
		row.add(deleteTaskButton);
		rows.add(row);

		row = new ArrayList<>();
		InlineKeyboardButton deleteAllTasksButton = new InlineKeyboardButton();
		deleteAllTasksButton.setText("Delete All Tasks");
		deleteAllTasksButton.setCallbackData("/delete_all_tasks");
		row.add(deleteAllTasksButton);
		rows.add(row);

		row = new ArrayList<>();
		InlineKeyboardButton backToMainMenuTasksButton = new InlineKeyboardButton();
		backToMainMenuTasksButton.setText("Go Back to main menu");
		backToMainMenuTasksButton.setCallbackData("/quit");
		row.add(backToMainMenuTasksButton);
		rows.add(row);

		markup.setKeyboard(rows);
		return markup;
	}

	@Override
	public void execute(long chatId, String input, Bot2 bot) throws IOException {
		bot.activateFunctionality("useTaskManager");
	}
}
