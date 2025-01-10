package manager;

import manager.exception.ManagerSaveException;
import task.*;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class FileBackedTaskManager extends InMemoryTaskManager {

    public static final String HEADER = "id,type,name,status,description,epic";
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    @Override
    public Integer addNewTask(Task task) {
        Integer id = super.addNewTask(task);
        saveToFile();
        return id;
    }

    @Override
    public Integer addNewEpic(Epic epic) {
        Integer id = super.addNewEpic(epic);
        saveToFile();
        return id;
    }

    @Override
    public Integer addNewSubTask(SubTask subtask) {
        Integer id = super.addNewSubTask(subtask);
        saveToFile();
        return id;
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        saveToFile();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        saveToFile();
    }

    @Override
    public void deleteSubTask(int subtaskId) {
        super.deleteSubTask(subtaskId);
        saveToFile();
    }

    @Override
    public void updateTask(Task task, int id) {
        super.updateTask(task, id);
        saveToFile();
    }

    @Override
    public void updateEpic(Epic epic, int id) {
        super.updateEpic(epic, id);
        saveToFile();
    }

    @Override
    public void updateSubTask(SubTask subtask, int id) {
        super.updateSubTask(subtask, id);
        saveToFile();
    }

    @Override
    public void deleteTasks() {
        super.deleteTasks();
        saveToFile();
    }

    @Override
    public void deleteEpics() {
        super.deleteEpics();
        saveToFile();
    }

    @Override
    public void deleteSubTasks() {
        super.deleteSubTasks();
        saveToFile();
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        final FileBackedTaskManager taskManager = new FileBackedTaskManager(file);
        int maxId = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isBlank()) {
                    Task task = parseTask(line);
                    maxId = Math.max(maxId, task.getId());
                    taskManager.addTask(task);
                }
            }

            taskManager.idGenerator = maxId + 1;

        } catch (IOException e) {
            throw new ManagerSaveException("Can't read from file: " + file.getName(), e);
        }

        for (SubTask subtask : taskManager.subTasks.values()) {
            Epic epic = taskManager.epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.addSubTaskId(subtask.getId());
            }
        }

        return taskManager;
    }

    private static String toString(Task task) {
        return task.getId() + "," + task.getType() + "," + task.getName() + "," + task.getStatus() + "," + task.getDescription()
                + "," + (task.getType().equals(TaskTypes.SUBTASK) ? ((SubTask) task).getEpicId() : "");
    }

    protected void saveToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(HEADER);
            writer.newLine();

            for (Task task : tasks.values()) {
                writer.write(toString(task));
                writer.newLine();
            }
            for (Task epic : epics.values()) {
                writer.write(toString(epic));
                writer.newLine();
            }
            for (Task subtask : subTasks.values()) {
                writer.write(toString(subtask));
                writer.newLine();
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Can't save to file: " + file.getName(), e);
        }
    }

    private void addTask(Task task) {
        final int id = task.getId();
        idGenerator = Math.max(idGenerator, id + 1);

        switch (task.getType()) {
            case TASK:
                tasks.put(id, task);
                break;
            case EPIC:
                epics.put(id, (Epic) task);
                break;
            case SUBTASK:
                subTasks.put(id, (SubTask) task);
                break;
        }
    }

    private static Task parseTask(String value) {
        final String[] values = value.split(",");
        try {
            final int id = Integer.parseInt(values[0]);
            final TaskTypes type = TaskTypes.valueOf(values[1]);
            final String name = values[2];
            final Status status = Status.valueOf(values[3]);
            final String description = values[4];

            switch (type) {
                case TASK:
                    return new Task(name, description, id, status);
                case SUBTASK:
                    final int epicId = Integer.parseInt(values[5]);
                    return new SubTask(name, description, id, status, epicId);
                case EPIC:
                    return new Epic(name, description, id, status);
                default:
                    throw new IllegalArgumentException("Unknown task type: " + type);
            }
        } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Task line parsing error: " + value + ". Cause: " + e.getMessage(), e);
        }
    }
}