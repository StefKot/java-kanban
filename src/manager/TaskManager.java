package manager;

import task.Status;
import task.Epic;
import task.SubTask;
import task.Task;

import java.util.HashMap;
import java.util.ArrayList;

public class TaskManager {
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, SubTask> subtasks = new HashMap<>();

    private int idGenerator = 0;

    public int addNewTask(Task task) {
        int id = ++idGenerator;
        task.setId(id);
        tasks.put(id, task);
        return id;
    }

    public Integer addNewEpic(Epic epic) {
        int id = ++idGenerator;
        epic.setId(id);
        epics.put(id, epic);
        return id;
    }

    public Integer addNewSubTask(SubTask subTask) {
        int epicId = subTask.getEpicId();
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return -1;
        }
        int id = ++idGenerator;
        subTask.setId(id);
        subtasks.put(id, subTask);
        epic.addSubtaskId(subTask.getId());
        updateEpicStatus(epicId);
        return id;
    }

    public void deleteTask(int id) {
        tasks.remove(id);
    }

    public void deleteEpic(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            for (Integer subtaskId : epic.getSubtaskIds()) {
                subtasks.remove(subtaskId);
            }
        }
    }

    public void deleteSubTask(int subtaskId) {
        SubTask subTask = subtasks.get(subtaskId);

        if (subTask != null) {
            int epicId = subTask.getEpicId();
            Epic epic = epics.get(epicId);

            if (epic != null) {
                epic.removeSubtaskIds(subtaskId);
                subtasks.remove(subtaskId);
                updateEpicStatus(epicId);
            }
        }
    }


    public void updateTask(Task task) {
        int id = task.getId();
        Task savedTask = tasks.get(id);
        if (savedTask != null) {
            tasks.put(id, task);
        }
    }

    public void updateEpic(Epic epic) {
        Epic savedEpic = epics.get(epic.getId());
        if (savedEpic != null) {
            savedEpic.setName(epic.getName());
            savedEpic.setDescription(epic.getDescription());
        }
    }

    public void updateSubTask(SubTask subTask) {
        int id = subTask.getId();
        int epicId = subTask.getEpicId();
        SubTask savedSubtask = subtasks.get(id);

        if (savedSubtask != null && savedSubtask.getEpicId() == epicId) {
            Epic epic = epics.get(epicId);
            if (epic != null) {
                subtasks.put(id, subTask);
                updateEpicStatus(epicId);
            }
        }
    }

    public void deleteTasks() {
        tasks.clear();
    }

    public void deleteEpics() {
        epics.clear();
        subtasks.clear();
    }

    public void deleteSubtasks() {
        for (Epic epic : epics.values()) {
            epic.cleanSubtaskIds();
            updateEpicStatus(epic.getId());
        }
        subtasks.clear();
    }

    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    public ArrayList<SubTask> getSubTasks() {
        return new ArrayList<>(subtasks.values());
    }

    public Task getTask(int id) {
        return tasks.get(id);
    }

    public Epic getEpic(int id) {
        return epics.get(id);
    }

    public SubTask getSubTask(int id) {
        return subtasks.get(id);
    }

    public ArrayList<SubTask> getEpicSubTasks(int epicId) {
        Epic epic = epics.get(epicId);
        ArrayList<SubTask> subTasksList = new ArrayList<>();

        if (epic != null) {
            for (int subtaskId : epic.getSubtaskIds()) {
                SubTask subTask = subtasks.get(subtaskId);
                if (subTask != null) {
                    subTasksList.add(subTask);
                }
            }
        }
        return subTasksList;
    }

    public Epic getEpicForSubTask(int id) {
        return subtasks.containsKey(id) ? epics.get(subtasks.get(id).getEpicId()) : null;
    }

    private void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        int newStatusCount = 0;
        int doneStatusCount = 0;

        if (epic.getSubtaskIds().isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        for (Integer id : epic.getSubtaskIds()) {
            SubTask subTask = subtasks.get(id);
            if (subTask != null) {
                if (subTask.getStatus() == Status.NEW) {
                    newStatusCount++;
                } else if (subTask.getStatus() == Status.DONE) {
                    doneStatusCount++;
                }
            }
        }

        if (doneStatusCount == epic.getSubtaskIds().size()) {
            epic.setStatus(Status.DONE);
        } else if (newStatusCount == epic.getSubtaskIds().size()) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }
}
