package task;

public class UsualTask extends Task {
    private final int id;

    public UsualTask(String name, String description) {
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

    public void changeName(UsualTask usualTask) {
        this.name = usualTask.name;
        this.description = usualTask.description;
    }
}
