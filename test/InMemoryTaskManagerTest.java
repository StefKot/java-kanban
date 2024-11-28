import manager.InMemoryTaskManager;

import task.Task;
import task.Epic;
import task.SubTask;
import task.Status;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest {
    @Test
    public void AnyTasksShouldBeAddedToListsAndGotBack() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        taskManager.addNewTask(new Task("Task1", "Description1", Status.NEW));
        taskManager.addNewTask(new Task("Task1", "Description2", Status.NEW));
        taskManager.addNewEpic(new Epic("Epic1", "Description1"));
        taskManager.addNewEpic(new Epic("Epic2", "Description2"));
        taskManager.addNewSubTask(new SubTask("SubTask1", "Description1", Status.NEW, 3));
        taskManager.addNewSubTask(new SubTask("SubTask2", "Description2", Status.NEW, 3));
        taskManager.addNewSubTask(new SubTask("SubTask3", "Description3", Status.NEW, 4));

        assertEquals(taskManager.getTask(2).getDescription(), "Description2",
                "Ошибка! Входные и выходные данные не совпадают.");

        assertEquals(taskManager.getEpic(4).getDescription(), "Description2",
                "Ошибка! Входные и выходные данные не совпадают.");

        assertEquals(taskManager.getSubTask(5).getEpicId(), 3,
                "Ошибка! Входные и выходные данные не совпадают.");

        assertEquals(taskManager.getSubTask(7).getEpicId(), 4,
                "Ошибка! Входные и выходные данные не совпадают.");
    }
}
