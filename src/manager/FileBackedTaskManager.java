package manager;

import manager.exception.ManagerSaveException;
import task.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

public class FileBackedTaskManager extends InMemoryTaskManager {

    public static final String HEADER = "id,type,name,status,description,epic,startTime,duration";
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
    public Integer addNewSubTask(SubTask subTask) {
        Integer id = super.addNewSubTask(subTask);
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
    public void deleteSubTask(int subTaskId) {
        super.deleteSubTask(subTaskId);
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
    public void updateSubTask(SubTask subTask, int id) {
        super.updateSubTask(subTask, id);
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

        for (SubTask subTask : taskManager.subTasks.values()) {
            Epic epic = taskManager.epics.get(subTask.getEpicId());
            if (epic != null) {
                epic.addSubTaskId(subTask.getId());
            }
        }

        return taskManager;
    }

    public static String toString(Task task) {
        return task.getId() + "," + task.getType() + "," + task.getName() + "," + task.getStatus() + ","
                + task.getDescription() + "," + (task.getType().equals(TaskTypes.SUBTASK)
                ? ((SubTask) task).getEpicId() : "") + "," + task.getStartTime() + "," + (task.getDuration().isZero()
                ? "" : String.valueOf(task.getDuration().toMinutes()));
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
            for (Task subTask : subTasks.values()) {
                writer.write(toString(subTask));
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
                addPriorityTask(task);
                break;
            case EPIC:
                epics.put(id, (Epic) task);
                addPriorityTask(task);
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
                    if (values.length == 5) {
                        return new Task(name, description, id, status);
                    } else {
                        return new Task(name, description, id, status, LocalDateTime.parse(values[5]), Duration.ofMinutes(Long.parseLong(values[6])));
                    }
                case SUBTASK:
                    final int epicId = Integer.parseInt(values[5]);
                    if (values.length == 6) {
                        return new SubTask(name, description, id, status, epicId);
                    } else {
                        return new SubTask(name, description, id, status, epicId, LocalDateTime.parse(values[6]), Duration.ofMinutes(Long.parseLong(values[7])));
                    }
                case EPIC:
                    if (values.length == 5) {
                        return new Epic(name, description, status);
                    } else {
                        return new Epic(name, description, id, status, LocalDateTime.parse(values[5]), Duration.ofMinutes(Long.parseLong(values[6])));
                    }
                default:
                    throw new IllegalArgumentException("Unknown Task type: " + type);
            }
        } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Task line parsing error: " + value + ". Cause: " + e.getMessage(), e);
        }
    }
}