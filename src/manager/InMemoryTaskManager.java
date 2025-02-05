package manager;

import manager.exception.IntersectionException;
import task.Epic;
import task.Status;
import task.SubTask;
import task.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, SubTask> subTasks = new HashMap<>();

    protected final HistoryManager historyManager = Managers.getDefaultHistory();

    protected int idGenerator = 0;

    private static final Comparator<Task> taskComparator = Comparator.comparing(Task::getStartTime);
    protected final Set<Task> priorityTasks = new TreeSet<>(taskComparator);

    @Override
    public Integer addNewTask(Task task) {
        int id = ++idGenerator;
        task.setId(id);

        if (checkIntersections(task)) {
            throw new IntersectionException("An intersection in time has been found: " + task.getStartTime());
        }

        tasks.put(id, task);
        addPriorityTask(task);
        return id;
    }

    @Override
    public Integer addNewEpic(Epic epic) {
        int id = ++idGenerator;
        epic.setId(id);
        epics.put(id, epic);
        updateEpicStatus(epic.getId());
        return id;
    }

    @Override
    public Integer addNewSubTask(SubTask subTask) {
        int epicId = subTask.getEpicId();
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return null;
        }

        if (checkIntersections(subTask)) {
            throw new IntersectionException("An intersection in time has been found: " + subTask.getStartTime());
        }

        int id = ++idGenerator;
        subTask.setId(id);

        subTasks.put(id, subTask);
        epic.addSubTaskId(subTask.getId());
        updateEpicStatus(epicId);
        changeEpicTime(epic);
        addPriorityTask(subTask);
        return id;
    }

    @Override
    public void deleteTask(int id) {
        Task task = tasks.remove(id);
        if (task != null) {
            historyManager.removeFromHistory(id);
            priorityTasks.remove(tasks.get(id));
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
                priorityTasks.remove(subTasks.get(subTaskId));
            }
        }
    }

    @Override
    public void deleteSubTask(int subTaskId) {
        SubTask subTask = subTasks.remove(subTaskId);
        if (subTask != null) {
            historyManager.removeFromHistory(subTaskId);
            priorityTasks.remove(subTask);

            int epicId = subTask.getEpicId();
            Epic epic = epics.get(epicId);
            if (epic != null) {
                epic.removeSubTaskIds(subTaskId);
                updateEpicStatus(epicId);
                changeEpicTime(epic);
            }
        }
    }

    @Override
    public void updateTask(Task task, int id) {
        final Task savedTask = tasks.get(id);
        if (savedTask != null) {
            task.setId(id);

            if (checkIntersections(task)) {
                throw new IntersectionException("An intersection in time has been found: " + task.getStartTime());
            }

            priorityTasks.remove(savedTask);
            addPriorityTask(task);

            tasks.put(id, task);
        }
    }

    @Override
    public void updateEpic(Epic epic, int id) {
        final Epic savedEpic = epics.get(id);
        if (savedEpic != null) {
            if (checkIntersections(epic)) {
                throw new IntersectionException("An intersection in time has been found: " + epic.getStartTime());
            }

            savedEpic.setName(epic.getName());
            savedEpic.setDescription(epic.getDescription());
        }
    }

    @Override
    public void updateSubTask(SubTask subTask, int id) {
        final SubTask savedSubTask = subTasks.get(id);

        if (savedSubTask != null) {
            int epicId = savedSubTask.getEpicId();
            Epic epic = epics.get(epicId);

            if (epic != null && subTask.getEpicId() == epicId) {
                if (checkIntersections(subTask)) {
                    throw new IntersectionException("An intersection in time has been found for subtask: " + subTask.getStartTime());
                }

                subTask.setId(id);

                priorityTasks.remove(savedSubTask);
                addPriorityTask(subTask);

                subTasks.put(id, subTask);
                updateEpicStatus(epicId);
                changeEpicTime(epic);
            }
        }
    }

    @Override
    public void deleteTasks() {
        for (Integer task : tasks.keySet()) {
            historyManager.removeFromHistory(task);
            priorityTasks.remove(tasks.get(task));
        }
        tasks.clear();
    }

    @Override
    public void deleteEpics() {
        for (Epic epic : epics.values()) {
            historyManager.removeFromHistory(epic.getId());
            for (Integer subTaskId : epic.getSubTaskIds()) {
                historyManager.removeFromHistory(subTaskId);
                priorityTasks.remove(subTasks.get(subTaskId));
            }
        }
        epics.clear();
        subTasks.clear();
    }

    @Override
    public void deleteSubTasks() {
        for (Integer subTaskId : subTasks.keySet()) {
            historyManager.removeFromHistory(subTaskId);
            priorityTasks.remove(subTasks.get(subTaskId));
        }

        for (Epic epic : epics.values()) {
            epic.clearSubTaskIds();
            updateEpicStatus(epic.getId());
            changeEpicTime(epic);
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
        int inProgressStatusCount = 0;

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
                } else if (subTask.getStatus() == Status.IN_PROGRESS) {
                    inProgressStatusCount++;
                }
            }
        }

        if (doneStatusCount == epic.getSubTaskIds().size()) {
            epic.setStatus(Status.DONE);
        } else if (inProgressStatusCount > 0) {
            epic.setStatus(Status.IN_PROGRESS);
        } else if (newStatusCount == epic.getSubTaskIds().size()) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    protected void changeEpicTime(Epic epic) {
        if (epic.getSubTaskIds().isEmpty()) {
            epic.setStartTime(null);
            epic.setEndTime(null);
            epic.setDuration(Duration.ZERO);
            return;
        }

        LocalDateTime startTime = null;
        LocalDateTime endTime = null;
        Duration epicDuration = Duration.ZERO;

        for (Integer id : epic.getSubTaskIds()) {
            SubTask subTask = subTasks.get(id);
            if (subTask == null) {
                continue;
            }

            LocalDateTime subTaskStartTime = subTask.getStartTime();
            LocalDateTime subTaskEndTime = subTask.getEndTime();

            if (subTaskStartTime != null) {
                if (startTime == null || subTaskStartTime.isBefore(startTime)) {
                    startTime = subTaskStartTime;
                }
            }

            if (subTaskEndTime != null) {
                if (endTime == null || subTaskEndTime.isAfter(endTime)) {
                    endTime = subTaskEndTime;
                }
            }

            epicDuration = epicDuration.plus(subTask.getDuration());
        }

        epic.setStartTime(startTime);
        epic.setEndTime(endTime);
        epic.setDuration(epicDuration);
    }

    protected void addPriorityTask(Task task) {
        priorityTasks.add(task);
    }

    private boolean checkIntersections(Task task) {
        LocalDateTime startTime = task.getStartTime();
        LocalDateTime endTime = task.getEndTime();

        return priorityTasks.stream()
                .filter(otherTask -> otherTask.getStartTime() != null && otherTask.getId() != task.getId())
                .anyMatch(otherTask -> startTime.isBefore(otherTask.getEndTime()) && endTime.isAfter(otherTask.getStartTime()));
    }

    @Override
    public List<Task> getPriorityTasks() {
        return priorityTasks.stream().toList();
    }
}
