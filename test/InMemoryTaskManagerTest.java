import manager.InMemoryTaskManager;

import task.Task;
import task.Epic;
import task.SubTask;
import task.Status;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest {
    @Test
    public void AnyTasksShouldBeAddedToListsAndGotBack(){
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        taskManager.addNewTask(new Task("1", "1", Status.NEW));
        taskManager.addNewTask(new Task("1", "2", Status.NEW));
        taskManager.addNewEpic(new Epic("1.0", "1.0"));
        taskManager.addNewEpic(new Epic("2.0", "2.0"));
        taskManager.addNewSubTask(new SubTask("1.1", "1.1",  Status.NEW,3));
        taskManager.addNewSubTask(new SubTask("1.2", "1.2", Status.NEW, 3));
        taskManager.addNewSubTask(new SubTask("2.2", "2.2", Status.NEW, 4));

        assertEquals(taskManager.getTask(2).getDescription(), "2",
                "Ошибка! input и output не совпадают");

        assertEquals(taskManager.getEpic(4).getDescription(), "2.0",
                "Ошибка! input и output не совпадают");

        assertEquals(taskManager.getSubTask(5).getEpicId(), 3,
                "Ошибка! input и output не совпадают");

        assertEquals(taskManager.getSubTask(7).getEpicId(), 4,
                "Ошибка! input и output не совпадают");
    }
}
