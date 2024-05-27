package telegrambot.taskmanagment;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import telegrambot.configuration.Config;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TaskManager {
    private List<Task> tasks;
    private String csvFilePath = Config.getProperty("added_voice_to_text.path");

    public TaskManager() {
        this.tasks = new ArrayList<>();
        loadTasksFromCSV();
    }

    public void addTask(Task task) {
        tasks.add(task);
        saveTasksToCSV();
    }
    public boolean deleteTask(String title) {
        Task task = findTaskByTitle(title);
        if (task != null) {
            tasks.remove(task);
            saveTasksToCSV();
            return true;
        }
        return false;
    }

    public boolean deleteAllTasks() {
        tasks.clear();
        saveTasksToCSV();  // Save the changes to the CSV file after clearing the list.
        return tasks.isEmpty();  // Check if the list is empty to confirm deletion.
    }

    public boolean completeTask(String title) {
        Task task = findTaskByTitle(title);
        if (task != null && !task.isCompleted()) {
            task.setCompleted(true);
            saveTasksToCSV();
            return true;
        }
        return false;
    }

    public boolean editTask(String oldTitle, String newDeadline, String newCategory) {
        Task task = findTaskByTitle(oldTitle);
        if (task != null) {
            task.setToDo(newDeadline);
            task.setCategory(newCategory);
            saveTasksToCSV();
            return true;
        }
        return false;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public List<Task> getCompletedTasks() {
        List<Task> completedTasks = new ArrayList<>();
        for (Task task : tasks) {
            if (task.isCompleted()) {
                completedTasks.add(task);
            }
        }
        return completedTasks;
    }

    public Task findTaskByTitle(String title) {
        for (Task task : tasks) {
            if (task.getTitle().equalsIgnoreCase(title)) {
                return task;
            }
        }
        return null;
    }

    private void loadTasksFromCSV() {
        try (CSVReader reader = new CSVReader(new FileReader(csvFilePath))) {
            String[] line;
            while ((line = reader.readNext()) != null) {
                if (line.length == 4) {
                    Task task = new Task(line[0], line[1], line[2]);
                    task.setCompleted(Boolean.parseBoolean(line[3]));
                    tasks.add(task);
                } else {
                    System.err.println("Skipping invalid line in CSV: " + String.join(",", line));
                }
            }
        } catch (IOException | CsvValidationException e) {
            System.err.println("Error loading tasks from CSV: " + e.getMessage());
        }
    }

    private void saveTasksToCSV() {
        try (CSVWriter writer = new CSVWriter(new FileWriter(csvFilePath))) {
            for (Task task : tasks) {
                writer.writeNext(new String[]{task.getTitle(), task.getToDo(), task.getCategory(), String.valueOf(task.isCompleted())});
            }
        } catch (IOException e) {
            System.err.println("Error saving tasks to CSV: " + e.getMessage());
        }
    }

}
