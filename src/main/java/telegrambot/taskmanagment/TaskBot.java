package telegrambot.taskmanagment;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import telegrambot.Bot2;
import telegrambot.IBotCommand;
import telegrambot.telegram_ui.TelegramMenuUi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TaskBot implements IBotCommand {
    private TaskManager taskManager;
    private boolean isAddingTask = false;
    private boolean isDeletingTask = false;
    private boolean isEditingTask = false;
    private boolean isCompletingTask = false;
    private boolean isEditingTaskDetails = false;
    private String editingTaskTitle;
    private TelegramLongPollingBot bot;

    public TaskBot(TelegramLongPollingBot bot) {
        this.taskManager = new TaskManager();
        this.bot = bot;
    }


    public void useTaskManager(Update update) {
        if (update.hasMessage() && update.getMessage().hasText() || (update.hasCallbackQuery() && update.getCallbackQuery().getData().equals("/task_Manager"))) {
            long chatId = 0;
            String messageText = "";
            if (update.hasMessage()) {
                messageText = update.getMessage().getText();
                chatId = update.getMessage().getChatId();
            }
            String queryText = null;
            if (update.hasCallbackQuery()) {
                queryText = update.getCallbackQuery().getData();
                chatId = update.getCallbackQuery().getMessage().getChatId();

            }
            System.out.println("Query is");

            if (messageText.equals("/task_Manager") || queryText.equals("/task_Manager")) {
                sendMainMenu(chatId);
            } else if (messageText.equals("/add_task")) {
                sendAddTaskMenu(chatId);
            } else if (messageText.equals("/view_tasks")) {
                sendTasksList(chatId);
            } else if (messageText.equals("/delete_task")) {
                sendDeleteTaskPrompt(chatId);
            } else if (messageText.equals("/complete_task")) {
                sendCompleteTaskPrompt(chatId);
            } else if (messageText.equals("/edit_task")) {
                sendTextMessage(chatId, "Please enter the title of the task you want to edit:");
                isEditingTask = true;
            } else if (isEditingTask) {
                handleEditTask(chatId, messageText);
            } else if (isAddingTask) {
                handleAddTask(chatId, messageText);
            } else if (isDeletingTask) {
                handleDeleteTask(chatId, messageText);
            } else if (isCompletingTask) {
                handleCompleteTask(chatId, messageText);
            } else if (isEditingTaskDetails) {
                handleUpdateTask(chatId, messageText);
            } else if (messageText.equals("/view_completed_tasks")) {
                sendCompletedTasksList(chatId);
            }
        } else if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            long chatId = update.getCallbackQuery().getMessage().getChatId();

            switch (callbackData) {
                case "add_task":
                    sendAddTaskMenu(chatId);
                    break;
                case "view_tasks":
                    sendTasksList(chatId);
                    break;
                case "delete_task":
                    sendDeleteTaskPrompt(chatId);
                    break;
                case "complete_task":
                    sendCompleteTaskPrompt(chatId);
                    break;
                case "edit_task":
                    sendTextMessage(chatId, "Please enter the title of the task you want to edit:");
                    isEditingTask = true;
                    break;
                case "view_completed_tasks":
                    sendCompletedTasksList(chatId);
                    break;
                case "add_task_urgent":
                    sendTextMessage(chatId, "Please enter the details for the urgent task in the format: Title, Deadline, Category (e.g., Buy groceries, Tomorrow, Personal)");
                    isAddingTask = true;
                    break;
                case "add_task_normal":
                    sendTextMessage(chatId, "Please enter the details for the normal task in the format: Title, Deadline, Category (e.g., Buy groceries, Tomorrow, Personal)");
                    isAddingTask = true;
                    break;
            }
        }
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
        addTaskButton.setCallbackData("add_task");
        row.add(addTaskButton);
        rows.add(row);

        row = new ArrayList<>();
        InlineKeyboardButton viewTasksButton = new InlineKeyboardButton();
        viewTasksButton.setText("View Tasks");
        viewTasksButton.setCallbackData("view_tasks");
        row.add(viewTasksButton);
        rows.add(row);

        row = new ArrayList<>();
        InlineKeyboardButton deleteTaskButton = new InlineKeyboardButton();
        deleteTaskButton.setText("Delete Task");
        deleteTaskButton.setCallbackData("delete_task");
        row.add(deleteTaskButton);
        rows.add(row);

        row = new ArrayList<>();
        InlineKeyboardButton completeTaskButton = new InlineKeyboardButton();
        completeTaskButton.setText("Complete Task");
        completeTaskButton.setCallbackData("complete_task");
        row.add(completeTaskButton);
        rows.add(row);

        row = new ArrayList<>();
        InlineKeyboardButton editTaskButton = new InlineKeyboardButton();
        editTaskButton.setText("Edit Task");
        editTaskButton.setCallbackData("edit_task");
        row.add(editTaskButton);
        rows.add(row);

        row = new ArrayList<>();
        InlineKeyboardButton viewCompletedTasksButton = new InlineKeyboardButton();
        viewCompletedTasksButton.setText("View Completed Tasks");
        viewCompletedTasksButton.setCallbackData("view_completed_tasks");
        row.add(viewCompletedTasksButton);
        rows.add(row);

        markup.setKeyboard(rows);
        return markup;
    }

    private void sendAddTaskMenu(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Add Task");
        message.setReplyMarkup(getAddTaskKeyboard());
        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private InlineKeyboardMarkup getAddTaskKeyboard() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton urgentButton = new InlineKeyboardButton();
        urgentButton.setText("Urgent Task");
        urgentButton.setCallbackData("add_task_urgent");
        row.add(urgentButton);
        rows.add(row);

        row = new ArrayList<>();
        InlineKeyboardButton normalButton = new InlineKeyboardButton();
        normalButton.setText("Normal Task");
        normalButton.setCallbackData("add_task_normal");
        row.add(normalButton);
        rows.add(row);

        markup.setKeyboard(rows);
        return markup;
    }

    private void sendTasksList(long chatId) {
        List<Task> tasks = taskManager.getTasks();
        StringBuilder response = new StringBuilder("Tasks:\n");
        for (Task task : tasks) {
            String status = task.isCompleted() ? "[✅]" : "[ ]";
            response.append(status).append(" ").append(task.getTitle()).append(" (").append(task.getDeadline()).append(", ").append(task.getCategory()).append(")\n");
        }
        sendTextMessage(chatId, response.toString());
        sendMainMenu(chatId);
    }

    private void sendCompletedTasksList(long chatId) {
        List<Task> tasks = taskManager.getCompletedTasks();
        StringBuilder response = new StringBuilder("Completed Tasks:\n");
        for (Task task : tasks) {
            response.append("[✅] ").append(task.getTitle()).append(" (").append(task.getDeadline()).append(", ").append(task.getCategory()).append(")\n");
        }
        if (tasks.isEmpty()) {
            response.append("No completed tasks found.");
        }
        sendTextMessage(chatId, response.toString());
        sendMainMenu(chatId);
    }

    private void sendDeleteTaskPrompt(long chatId) {
        sendTextMessage(chatId, "Please enter the title of the task you want to delete:");
        isDeletingTask = true;
    }

    private void sendCompleteTaskPrompt(long chatId) {
        sendTextMessage(chatId, "Please enter the title of the task you want to mark as completed:");
        isCompletingTask = true;
    }

    private void handleAddTask(long chatId, String taskDetails) {
        String[] taskInfo = taskDetails.split(",");
        if (taskInfo.length == 3) {
            String title = taskInfo[0].trim();
            String deadline = taskInfo[1].trim();
            String category = taskInfo[2].trim();
            Task task = new Task(title, deadline, category);
            taskManager.addTask(task);
            sendTextMessage(chatId, "Task added successfully!");
        } else {
            sendTextMessage(chatId, "Invalid input format. Please enter the task details in the correct format (e.g., Title, Deadline, Category).");
        }
        isAddingTask = false;
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

    private void handleCompleteTask(long chatId, String title) {
        if (taskManager.completeTask(title)) {
            sendTextMessage(chatId, "Task marked as completed!");
        } else {
            sendTextMessage(chatId, "Task not found or already completed.");
        }
        isCompletingTask = false;
        sendMainMenu(chatId);
    }

    private void handleEditTask(long chatId, String title) {
        editingTaskTitle = title;
        sendTextMessage(chatId, "Please enter the new details for the task in the format: Deadline, Category (e.g., Tomorrow, Personal)");
        isEditingTask = false;
        isEditingTaskDetails = true;
    }

    private void handleUpdateTask(long chatId, String newDetails) {
        String[] details = newDetails.split(",");
        if (details.length == 2) {
            String newDeadline = details[0].trim();
            String newCategory = details[1].trim();
            if (taskManager.editTask(editingTaskTitle, newDeadline, newCategory)) {
                sendTextMessage(chatId, "Task updated successfully!");
            } else {
                sendTextMessage(chatId, "Task not found.");
            }
        } else {
            sendTextMessage(chatId, "Invalid input format. Please enter the new details in the correct format (e.g., Tomorrow, Personal).");
        }
        isEditingTaskDetails = false;
        sendMainMenu(chatId);
    }

    @Override
    public void execute(long chatId, String input, Bot2 bot) throws IOException {
        System.out.println("Here should something happen");
        bot.activateFunctionality("useTaskManager");
    }
}
