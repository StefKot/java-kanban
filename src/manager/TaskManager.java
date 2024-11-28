package manager;

import task.Epic;
import task.SubTask;
import task.Task;

import java.util.List;

public interface TaskManager {
    int addNewTask(Task task);
    Integer addNewEpic(Epic epic);
    Integer addNewSubTask(SubTask subTask);

    void deleteTask(int id);
    void deleteEpic(int id);
    void deleteSubTask(int subtaskId);

    void updateTask(Task task, int id);
    void updateEpic(Epic epic, int id);
    void updateSubTask(SubTask subTask, int id);

    void deleteTasks();
    void deleteEpics();
    void deleteSubTasks();

    List<Task> getTasks();
    List<Epic> getEpics();
    List<SubTask> getSubTasks();

    Task getTask(int id);
    Epic getEpic(int id);
    SubTask getSubTask(int id);
    List<SubTask> getEpicSubTasks(int epicId);

    List<Task> getHistory();
}
