package task;

public class SubTask extends Task {
    private final Integer epicId;

    public SubTask(String name, String description, Status status,Integer epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }
}
