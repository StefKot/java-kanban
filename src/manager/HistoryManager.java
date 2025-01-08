package manager;

import task.Task;

import java.util.List;

public interface HistoryManager {
    List<Task> getHistory();

    void addToHistory(Task task);

    void removeFromHistory(int id);
}