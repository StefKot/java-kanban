package manager;

import task.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    public static final int MAX_SIZE = 10;
    private final List<Task> history = new ArrayList<>();

    @Override
    public List<Task> getHistory() {
        return history;
    }

    @Override
    public void addHistory(Task task) {
        if (task == null) {
            return;
        }
        if (history.size() == MAX_SIZE) {
            history.removeFirst();
        }
        history.add(task);
    }
}
