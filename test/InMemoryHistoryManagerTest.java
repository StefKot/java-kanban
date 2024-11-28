import manager.InMemoryTaskManager;


import task.Task;
import task.Epic;
import task.SubTask;
import task.Status;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class InMemoryHistoryManagerTest {
    InMemoryTaskManager taskManager = new InMemoryTaskManager();

    @Test
    public void getHistoryShouldReturnListOf10Tasks() {
        for (int i = 0; i < 20; i++) {
            taskManager.addNewTask(new Task("Название", "Описание", Status.NEW));
        }

        List<Task> tasks = taskManager.getTasks();
        for (Task task : tasks) {
            taskManager.getTask(task.getId());
        }

        List<Task> list = taskManager.getHistory();
        assertEquals(10, list.size(), "Неверное количество задач.");
    }

    @Test
    public void getHistoryShouldReturnOldTaskAfterUpdate() {
        Task task1 = new Task("Погулять с собакой", "Погулять с собакой утром", Status.NEW);
        taskManager.addNewTask(task1);
        taskManager.getTask(task1.getId());
        taskManager.updateTask(new Task("Упаковать кошку", "Поместить кошку в сумку-переноску", Status.NEW),
                task1.getId());
        List<Task> tasks = taskManager.getHistory();
        Task oldTask = tasks.getFirst();
        assertEquals(task1.getName(), oldTask.getName(), "Старая версия Task не сохранилась.");
        assertEquals(task1.getDescription(), oldTask.getDescription(),
                "Старая версия Task не сохранилась.");

    }

    @Test
    public void getHistoryShouldReturnOldEpicAfterUpdate() {
        Epic epic1 = new Epic("Переезд", "Дела для переезда в новую квартиру");
        taskManager.addNewEpic(epic1);
        taskManager.getEpic(epic1.getId());
        taskManager.updateEpic(new Epic("Новое имя", "Новое описание"), epic1.getId());
        List<Task> epics = taskManager.getHistory();
        Epic oldEpic = (Epic) epics.getFirst();
        assertEquals(epic1.getName(), oldEpic.getName(),
                "Старая версия Epic не сохранилась.");
        assertEquals(epic1.getDescription(), oldEpic.getDescription(),
                "Старая версия Epic не сохранилась.");
    }

    @Test
    public void getHistoryShouldReturnOldSubtaskAfterUpdate() {
        Epic epic1 = new Epic("Переезд", "Дела для переезда в новую квартиру");
        taskManager.addNewEpic(epic1);
        SubTask subtask1 = new SubTask("Собрать коробки", "Все вещи должны быть в коробках", Status.NEW,
                epic1.getId());
        taskManager.addNewSubTask(subtask1);
        taskManager.getSubTask(subtask1.getId());
        taskManager.updateSubTask(new SubTask("Новое имя", "Новое описание",
                Status.NEW, epic1.getId()), subtask1.getId());
        List<Task> subtasks = taskManager.getHistory();
        SubTask oldSubtask = (SubTask) subtasks.getFirst();
        assertEquals(subtask1.getName(), oldSubtask.getName(), "Старая версия Epic не сохранилась.");
        assertEquals(subtask1.getDescription(), oldSubtask.getDescription(), "Старая версия Epic не сохранилась.");
    }
}
