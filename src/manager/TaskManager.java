package manager;

import task.Epic;
import task.SubTask;
import task.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    int addNewTask(Task task);
    Integer addNewEpic(Epic epic);
    Integer addNewSubTask(SubTask subTask);

    void deleteTask(int id);
    void deleteEpic(int id);
    void deleteSubTask(int subtaskId);

    void updateTask(Task task);
    void updateEpic(Epic epic);
    void updateSubTask(SubTask subTask);

    void deleteTasks();
    void deleteEpics();
    void deleteSubTasks();

    List<Task> getTasks();
    List<Epic> getEpics();
    ArrayList<SubTask> getSubTasks();

    Task getTask(int id);
    Epic getEpic(int id);
    SubTask getSubTask(int id);
    ArrayList<SubTask> getEpicSubTasks(int epicId);

    List<Task> getHistory();
}
