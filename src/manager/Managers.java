package manager;

import java.io.File;

public class Managers {
    public static TaskManager getDefault() {
        return new FileBackedTaskManager(new File("data/items.csv"));
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
