package task;

public class EpicTask extends Task {
    private final int id;

    public EpicTask(String name, String description) {
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

    public void changeName(EpicTask epicTask) {
        this.name = epicTask.name;
        this.description = epicTask.description;
    }
}
