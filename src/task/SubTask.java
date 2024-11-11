package task;

public class SubTask extends Task {
    private final int id;

    public SubTask(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
        this.id = Task.counter;
        Task.counter++;
    }

    public int getId() {
        return id;
    }

    public void changeStatus(Status status) {
        this.status = status;
    }

    public void changeName(SubTask subTask) {
        this.name = subTask.name;
        this.description = subTask.description;
    }
}
