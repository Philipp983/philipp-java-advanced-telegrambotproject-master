package telegrambot.taskmanagment;

public class Task {
    private String title;
    private String toDo;
    private String category;
    private boolean completed;
    private String icon;

    public Task(String title, String deadline, String category) {
        this.title = title;
        this.toDo = deadline;
        this.category = category;
        this.completed = false;
        this.icon = "\uD83D\uDD15";
    }

    // Getters and setters
    public String getToDo() {
        return toDo;
    }

    public void setToDo(String toDo) {
        this.toDo = toDo;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
