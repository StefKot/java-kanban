import manager.InMemoryTaskManager;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.SubTask;
import task.Task;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InMemoryHistoryManagerTest {
    InMemoryTaskManager taskManager = new InMemoryTaskManager();

    @Test
    public void getHistoryShouldReturnListOf10Tasks() {
        for (int i = 0; i < 10; i++) {
            taskManager.addNewTask(new Task("Name", "Description", Status.NEW, LocalDateTime.now().plusMinutes(i)));
        }

        List<Task> tasks = taskManager.getTasks();
        for (Task task : tasks) {
            taskManager.getTask(task.getId());
        }

        List<Task> list = taskManager.getHistory();
        assertEquals(10, list.size(), "Incorrect number of Tasks.");
    }

    @Test
    public void getHistoryShouldReturnOldTaskAfterUpdate() {
        Task task1 = new Task("Walk the dog", "Walk the dog in the morning", Status.NEW, LocalDateTime.now().plusMinutes(15));
        taskManager.addNewTask(task1);
        taskManager.getTask(task1.getId());
        taskManager.updateTask(new Task("Pack up the cat", "Place the cat in a carrier bag", Status.NEW, LocalDateTime.now().plusMinutes(25)),
                task1.getId());
        Task task2 = new Task("New Task with time", "New Task with time", Status.NEW, LocalDateTime.now().plusMinutes(15));
        task2.setId(task1.getId());
        taskManager.updateTask(task2, task2.getId());
        List<Task> tasks = taskManager.getHistory();
        Task oldTask = tasks.getFirst();
        assertEquals(task1.getName(), oldTask.getName(), "The old version of Task has not been saved.");
        assertEquals(task1.getDescription(), oldTask.getDescription(),
                "The old version of Task has not been saved.");

    }

    @Test
    public void getHistoryShouldReturnOldEpicAfterUpdate() {
        Epic epic1 = new Epic("Move", "Things to do for moving to a new apartment", Status.NEW);
        taskManager.addNewEpic(epic1);
        taskManager.getEpic(epic1.getId());
        taskManager.updateEpic(new Epic("New name", "New Description", Status.IN_PROGRESS), epic1.getId());
        List<Task> epics = taskManager.getHistory();
        Epic oldEpic = (Epic) epics.getFirst();
        assertEquals(epic1.getName(), oldEpic.getName(),
                "The old version of Epic has not been saved.");
        assertEquals(epic1.getDescription(), oldEpic.getDescription(),
                "The old version of Epic has not been saved.");
    }

    @Test
    public void getHistoryShouldReturnOldSubtaskAfterUpdate() {
        Epic epic1 = new Epic("Move", "Things to do for moving to a new apartment", Status.NEW);
        taskManager.addNewEpic(epic1);
        SubTask subTask1 = new SubTask("Assemble the boxes", "All items must be in boxes.", Status.NEW,
                epic1.getId(), LocalDateTime.now().plusMinutes(15));
        taskManager.addNewSubTask(subTask1);
        taskManager.getSubTask(subTask1.getId());
        SubTask subTask2 = new SubTask("New name", "New Description", Status.NEW,
                epic1.getId(), LocalDateTime.now().plusMinutes(25));
        subTask2.setId(subTask1.getId());
        taskManager.updateSubTask(new SubTask("New name", "New Description",
                Status.NEW, epic1.getId()), subTask2.getId());
        List<Task> subTasks = taskManager.getHistory();
        SubTask oldSubtask = (SubTask) subTasks.getFirst();
        assertEquals(subTask1.getName(), oldSubtask.getName(), "The old version of SubTask has not been saved.");
        assertEquals(subTask1.getDescription(), oldSubtask.getDescription(), "The old version of SubTask has not been saved.");
    }
}
