import manager.FileBackedTaskManager;
import manager.TaskManager;
import manager.exception.IntersectionException;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.SubTask;
import task.Task;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TaskManagerTest {
    protected TaskManager taskManager = new FileBackedTaskManager(File.createTempFile("task-manager-test", ".csv"));

    TaskManagerTest() throws IOException {
    }

    @Test
    void TaskShouldBeCreated() {

        Task task = new Task("Name", "Description", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(10));
        int taskId = taskManager.addNewTask(task);

        Task savedTask = taskManager.getTask(taskId);

        assertNotNull(savedTask, "Task was not found.");
        assertEquals(task, savedTask, "Tasks don't match.");

        List<Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Tasks are not returned.");
        assertEquals(1, tasks.size(), "Incorrect number of Tasks.");
        assertEquals(task, tasks.getFirst(), "Tasks don't match.");
    }

    @Test
    void EpicShouldBeCreated() {

        Epic epic = new Epic("Name", "Description", Status.NEW);
        int epicId = taskManager.addNewEpic(epic);

        Task savedEpic = taskManager.getEpic(epicId);

        assertNotNull(savedEpic, "Epic was not found.");
        assertEquals(epic, savedEpic, "Epics don't match.");

        List<Epic> epics = taskManager.getEpics();

        assertNotNull(epics, "Epics are not returned.");
        assertEquals(1, epics.size(), "Incorrect number of Epics.");
        assertEquals(epic, epics.getFirst(), "Epics don't match.");
    }

    @Test
    void SubTaskShouldBeCreated() {

        Epic epic = new Epic("Name", "Description", Status.NEW);
        int epicId = taskManager.addNewEpic(epic);

        SubTask subTask = new SubTask("Name", "Description", epicId, Status.NEW, 1, LocalDateTime.now(), Duration.ofMinutes(15));
        int subTaskId = taskManager.addNewSubTask(subTask);

        Task savedSubTask = taskManager.getSubTask(subTaskId);

        assertNotNull(savedSubTask, "SubTask was not found.");
        assertEquals(subTask, savedSubTask, "SubTasks don't match.");

        List<SubTask> subTasks = taskManager.getSubTasks();

        assertNotNull(subTasks, "SubTasks are not returned.");
        assertEquals(1, subTasks.size(), "Incorrect number of SubTasks.");
        assertEquals(subTask, subTasks.getFirst(), "SubTasks don't match.");
    }

    @Test
    public void shouldAddAndGetById() {
        Task task = new Task("Name", "Description", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(15));
        int taskId = taskManager.addNewTask(task);
        Epic epic = new Epic("Name", "Description", Status.NEW);
        int epicId = taskManager.addNewEpic(epic);
        SubTask subTask = new SubTask("Name", "Description", 1, Status.NEW, epicId, LocalDateTime.now().plusMinutes(20), Duration.ofMinutes(15));
        int subTaskId = taskManager.addNewSubTask(subTask);

        assertEquals(task, taskManager.getTask(taskId), "Saved Tasks don't match.");
        assertEquals(epic, taskManager.getEpic(epicId), "Saved Epics don't match.");
        assertEquals(subTask, taskManager.getSubTask(subTaskId), "Saved SubTasks don't match.");
    }

    @Test
    public void shouldSaveAndRetrieveTaskCorrectly() {
        Task task = new Task("Name", "Description", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(15));
        int taskId = taskManager.addNewTask(task);

        Task savedTask = taskManager.getTask(taskId);

        assertEquals(task.getDescription(), savedTask.getDescription(), "By Description.");
        assertEquals(task.getId(), savedTask.getId(), "By Id.");
        assertEquals(task.getName(), savedTask.getName(), "By Name.");
        assertEquals(task.getStatus(), savedTask.getStatus(), "By Status.");
    }

    @Test
    public void shouldSaveAndRetrieveEpicCorrectly() {
        Epic epic = new Epic("Name", "Description", Status.NEW);
        int taskId = taskManager.addNewEpic(epic);
        SubTask subTask = new SubTask("Name", "Description", taskId, Status.NEW, 1, LocalDateTime.now(), Duration.ofMinutes(15));
        int subTaskId = taskManager.addNewSubTask(subTask);

        Epic savedEpic = taskManager.getEpic(taskId);

        assertEquals(epic.getDescription(), savedEpic.getDescription(), "By Description.");
        assertEquals(epic.getId(), savedEpic.getId(), "By Id.");
        assertEquals(epic.getName(), savedEpic.getName(), "By Name.");
        assertEquals(epic.getStatus(), savedEpic.getStatus(), "By Status.");
        assertEquals(epic.getSubTaskIds(), savedEpic.getSubTaskIds(), "By SubTasks.");
    }

    @Test
    void EpicShouldChangeStatus() {
        int epic = taskManager.addNewEpic(new Epic("Name", "Description", Status.NEW));
        assertEquals(taskManager.getEpic(epic).getStatus(), Status.NEW, "Status of empty epic is not NEW");
        taskManager.addNewSubTask(new SubTask("Name", "Description", epic, Status.NEW, 1, LocalDateTime.now(), Duration.ofMinutes(0)));
        taskManager.addNewSubTask(new SubTask("Name", "Description", epic, Status.NEW, 1, LocalDateTime.now().plusMinutes(10), Duration.ofMinutes(0)));
        assertEquals(taskManager.getEpic(epic).getStatus(), Status.NEW, "Epic's status is not NEW, with NEW SubTasks");
        taskManager.deleteSubTasks();
        taskManager.addNewSubTask(new SubTask("Name", "Description", epic, Status.DONE, 1, LocalDateTime.now().plusMinutes(15), Duration.ofMinutes(0)));
        taskManager.addNewSubTask(new SubTask("Name", "Description", epic, Status.DONE, 1, LocalDateTime.now().plusMinutes(20), Duration.ofMinutes(0)));
        assertEquals(taskManager.getEpic(epic).getStatus(), Status.DONE, "Epic's status is not DONE, with DONE SubTasks");
        taskManager.deleteSubTasks();
        taskManager.addNewSubTask(new SubTask("Name", "Description", epic, Status.NEW, 1, LocalDateTime.now().plusMinutes(25), Duration.ofMinutes(0)));
        taskManager.addNewSubTask(new SubTask("Name", "Description", epic, Status.DONE, 1, LocalDateTime.now().plusMinutes(30), Duration.ofMinutes(0)));
        assertEquals(taskManager.getEpic(epic).getStatus(), Status.IN_PROGRESS, "Epic's status is not IN_PROGRESS, with NEW and DONE SubTasks");
        taskManager.deleteSubTasks();
        taskManager.addNewSubTask(new SubTask("Name", "Description", epic, Status.IN_PROGRESS, 1, LocalDateTime.now().plusMinutes(35), Duration.ofMinutes(0)));
        taskManager.addNewSubTask(new SubTask("Name", "Description", epic, Status.IN_PROGRESS, 1, LocalDateTime.now().plusMinutes(40), Duration.ofMinutes(0)));
        assertEquals(taskManager.getEpic(epic).getStatus(), Status.IN_PROGRESS, "Epic's status is not IN_PROGRESS, with IN_PROGRESS SubTasks");
    }

    @Test
    void shouldCheckPriority() {
        Task task1 = new Task("Name1", "Description1", Status.NEW, LocalDateTime.of(2025, 2, 1, 15, 0), Duration.ofMinutes(15));
        taskManager.addNewTask(task1);
        Task task2 = new Task("Name2", "Description2", Status.NEW, LocalDateTime.of(2025, 2, 1, 15, 5), Duration.ofMinutes(20));
        assertThrows(IntersectionException.class, () -> taskManager.addNewTask(task2));
    }
}
