package task;

import java.util.List;
import java.util.ArrayList;

public class Epic extends Task {
    private List<Integer> subTaskIds = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
    }

    public Epic(String name, String description, Integer id, Status status) {
        super(name, description, id, status);
    }

    public void addSubTaskId(Integer id) {
        subTaskIds.add(id);
    }

    public List<Integer> getSubTaskIds() {
        return new ArrayList<>(subTaskIds);
    }

    public void removeSubTaskIds(Integer id) {
        subTaskIds.remove(id);
    }

    public void clearSubTaskIds() {
        subTaskIds.clear();
    }

    @Override
    public TaskTypes getType() {
        return TaskTypes.EPIC;
    }

    public void setAllSubTask(List<Integer> subTasks) {
        this.subTaskIds = new ArrayList<>(subTasks);
    }
}
