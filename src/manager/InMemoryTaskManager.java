package manager;

import task.Epic;
import task.Status;
import task.SubTask;
import task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, SubTask> subTasks = new HashMap<>();

    protected final HistoryManager historyManager = Managers.getDefaultHistory();

    protected int idGenerator = 0;

    @Override
    public Integer addNewTask(Task task) {
        int id = ++idGenerator;
        task.setId(id);
        tasks.put(id, task);
        return id;
    }

    @Override
    public Integer addNewEpic(Epic epic) {
        int id = ++idGenerator;
        epic.setId(id);
        epics.put(id, epic);
        return id;
    }

    @Override
    public Integer addNewSubTask(SubTask subTask) {
        int epicId = subTask.getEpicId();
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return -1;
        }
        int id = ++idGenerator;
        subTask.setId(id);
        subTasks.put(id, subTask);
        epic.addSubTaskId(subTask.getId());
        updateEpicStatus(epicId);
        return id;
    }

    @Override
    public void deleteTask(int id) {
        Task task = tasks.remove(id);
        if (task != null) {
            historyManager.removeFromHistory(id);
        }
    }

    @Override
    public void deleteEpic(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            historyManager.removeFromHistory(id);
            for (Integer subTaskId : epic.getSubTaskIds()) {
                subTasks.remove(subTaskId);
                historyManager.removeFromHistory(subTaskId);
            }
        }
    }

    @Override
    public void deleteSubTask(int subtaskId) {
        SubTask subTask = subTasks.remove(subtaskId);
        if (subTask != null) {
            historyManager.removeFromHistory(subtaskId);
            int epicId = subTask.getEpicId();
            Epic epic = epics.get(epicId);
            if (epic != null) {
                epic.removeSubTaskIds(subtaskId);
                updateEpicStatus(epicId);
            }
        }

    }

    @Override
    public void updateTask(Task task, int id) {
        final Task savedTask = tasks.get(id);
        if (savedTask != null) {
            tasks.put(id, task);
        }
    }

    @Override
    public void updateEpic(Epic epic, int id) {
        final Epic savedEpic = epics.get(id);
        if (savedEpic != null) {
            savedEpic.setName(epic.getName());
            savedEpic.setDescription(epic.getDescription());
        }
    }

    @Override
    public void updateSubTask(SubTask subTask, int id) {
        int epicId = subTask.getEpicId();
        final SubTask savedSubTask = subTasks.get(id);

        if (savedSubTask != null && savedSubTask.getEpicId() == epicId) {
            Epic epic = epics.get(epicId);
            if (epic != null) {
                subTasks.put(id, subTask);
                updateEpicStatus(epicId);
            }
        }
    }

    @Override
    public void deleteTasks() {
        for (Task task : tasks.values()) {
            historyManager.removeFromHistory(task.getId());
        }
        tasks.clear();
    }

    @Override
    public void deleteEpics() {
        for (Epic epic : epics.values()) {
            historyManager.removeFromHistory(epic.getId());
            for (Integer subTaskId : epic.getSubTaskIds()) {
                historyManager.removeFromHistory(subTaskId);
            }
        }
        epics.clear();
        subTasks.clear();
    }


    @Override
    public void deleteSubTasks() {
        for (SubTask subtask : subTasks.values()) {
            historyManager.removeFromHistory(subtask.getId());
        }
        for (Epic epic : epics.values()) {
            epic.clearSubTaskIds();
            updateEpicStatus(epic.getId());

        }
        subTasks.clear();
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<SubTask> getSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public Task getTask(int id) {
        final Task task = tasks.get(id);
        historyManager.addToHistory(task);
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        final Epic epic = epics.get(id);
        historyManager.addToHistory(epic);
        return epic;
    }

    @Override
    public SubTask getSubTask(int id) {
        final SubTask subTask = subTasks.get(id);
        historyManager.addToHistory(subTask);
        return subTask;
    }

    @Override
    public List<SubTask> getEpicSubTasks(int epicId) {
        Epic epic = epics.get(epicId);
        List<SubTask> subTasksList = new ArrayList<>();

        if (epic != null) {
            for (int subTaskId : epic.getSubTaskIds()) {
                SubTask subTask = subTasks.get(subTaskId);
                if (subTask != null) {
                    subTasksList.add(subTask);
                }
            }
        }
        return subTasksList;
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(historyManager.getHistory());
    }

    private void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        int newStatusCount = 0;
        int doneStatusCount = 0;

        if (epic.getSubTaskIds().isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        for (Integer id : epic.getSubTaskIds()) {
            SubTask subTask = subTasks.get(id);
            if (subTask != null) {
                if (subTask.getStatus() == Status.NEW) {
                    newStatusCount++;
                } else if (subTask.getStatus() == Status.DONE) {
                    doneStatusCount++;
                }
            }
        }

        if (doneStatusCount == epic.getSubTaskIds().size()) {
            epic.setStatus(Status.DONE);
        } else if (newStatusCount == epic.getSubTaskIds().size()) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }
}
