package task;

import java.time.Duration;
import java.time.LocalDateTime;

public class SubTask extends Task {
    private Integer epicId;

    public SubTask(String name, String description, Status status, Integer epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public SubTask(String name, String description, Status status, Integer epicId, LocalDateTime startTime) {
        super(name, description, status, startTime);
        this.epicId = epicId;
    }

    public SubTask(String name, String description, Integer id, Status status, Integer epicId) {
        super(name, description, id, status);
        this.epicId = epicId;
    }

    public SubTask(String name, String description, Integer id, Status status, Integer epicId, LocalDateTime startTime) {
        super(name, description, id, status, startTime);
        this.epicId = epicId;
    }

    public SubTask(String name, String description, Integer id, Status status, Integer epicId, LocalDateTime startTime, Duration duration) {
        super(name, description, id, status, startTime, duration);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(Integer epicId) {
        this.epicId = epicId;
    }

    @Override
    public TaskTypes getType() {
        return TaskTypes.SUBTASK;
    }
}
