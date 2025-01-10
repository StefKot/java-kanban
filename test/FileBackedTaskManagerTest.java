import manager.FileBackedTaskManager;
import manager.exception.ManagerSaveException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.SubTask;
import task.Task;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileBackedTaskManagerTest {
    private File tempFile;
    private FileBackedTaskManager manager;

    @BeforeEach
    void setUp() throws IOException {
        tempFile = File.createTempFile("file-backed-task-manager-test", ".csv");
        manager = new FileBackedTaskManager(tempFile);
    }

    @AfterEach
    void tearDown() {
        tempFile.delete();
    }

    @Test
    void shouldCreateEmptyFileOnInitialization() throws IOException {
        assertTrue(tempFile.exists());
        for (String line : readFileLines()) {
            if (line.equals("id,type,name,status,description,epic")) {
                assertEquals("id,type,name,status,description,epic", line);
            }
        }
    }

    @Test
    void shouldAddAndSaveTaskToFile() throws IOException {
        Task task = new Task("Task 1", "Description Task 1", Status.NEW);
        manager.addNewTask(task);

        String expectedContent = "1,TASK,Task 1,NEW,Description Task 1";

        for (String line : readFileLines()) {
            if (line.equals(expectedContent)) {
                assertEquals(expectedContent, line);
            }
        }
    }

    @Test
    void shouldAddAndSaveEpicToFile() throws IOException {
        Epic epic = new Epic("Epic  1", "Description Epic 1");
        manager.addNewEpic(epic);

        String expectedContent = "1,EPIC,Epic  1,NEW,Description Epic 1";

        for (String line : readFileLines()) {
            if (line.equals(expectedContent)) {
                assertEquals(expectedContent, line);
            }
        }
    }

    @Test
    void shouldAddAndSaveSubtaskToFile() throws IOException {
        Epic epic = new Epic("Epic  1", "Description Epic 1");
        manager.addNewEpic(epic);

        SubTask subtask = new SubTask("SubTask 1", "Description SubTask 1", Status.NEW, 1);
        subtask.setStatus(Status.IN_PROGRESS);
        manager.addNewSubTask(subtask);

        String expectedContent = "2,SUBTASK,SubTask 1,IN_PROGRESS,Description SubTask 1,1";
        for (String line : readFileLines()) {
            if (line.equals(expectedContent)) {
                assertEquals(expectedContent, line);
            }
        }
    }

    @Test
    void shouldLoadTasksFromFile() throws IOException {
        tempFile = File.createTempFile("tmp", ".csv");
        writeToFile(FileBackedTaskManager.HEADER);
        writeToFile("1,TASK,Task 1,NEW,Description Task 1");
        writeToFile("2,EPIC,Epic  1,NEW,Description Epic 1");
        writeToFile("3,SUBTASK,SubTask 1,IN_PROGRESS,Description SubTask 1,2");

        manager = FileBackedTaskManager.loadFromFile(tempFile);

        List<Task> tasks = manager.getTasks();
        List<Epic> epics = manager.getEpics();
        List<SubTask> subtasks = manager.getSubTasks();

        assertEquals(1, tasks.size());
        assertEquals(1, epics.size());
        assertEquals(1, subtasks.size());

        Task loadedTask = tasks.getFirst();
        assertEquals(1, loadedTask.getId());
        assertEquals("Task 1", loadedTask.getName());
        assertEquals(Status.NEW, loadedTask.getStatus());
        assertEquals("Description Task 1", loadedTask.getDescription());

        Epic loadedEpic = epics.getFirst();
        assertEquals(2, loadedEpic.getId());
        assertEquals("Epic  1", loadedEpic.getName());
        assertEquals(Status.NEW, loadedEpic.getStatus());
        assertEquals("Description Epic 1", loadedEpic.getDescription());

        SubTask loadedSubTask = subtasks.getFirst();
        assertEquals(3, loadedSubTask.getId());
        assertEquals("SubTask 1", loadedSubTask.getName());
        assertEquals(Status.IN_PROGRESS, loadedSubTask.getStatus());
        assertEquals("Description SubTask 1", loadedSubTask.getDescription());
        assertEquals(2, loadedSubTask.getEpicId());
    }

    private List<String> readFileLines() throws IOException {
        List<String> lines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(tempFile, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }

        return lines;
    }

    private void writeToFile(String lineToWrite) {
        try (FileWriter writer = new FileWriter(tempFile, StandardCharsets.UTF_8, true)) {
            writer.write(lineToWrite + "\n");
        } catch (IOException e) {
            throw new ManagerSaveException("Can't save to file", e);
        }
    }
}