package main;


import manager.InMemoryTaskManager;
import task.Status;
import task.Epic;
import task.Task;
import task.SubTask;

public class Main {

    public static void main(String[] args) {
/*        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        Epic epic1 = new Epic("EpicName1", "EpicDescription1");
        taskManager.addNewEpic(epic1);

        Epic epic2 = new Epic("EpicName2", "EpicDescription2");
        taskManager.addNewEpic(epic2);

        Task task1 = new Task("TaskName1", "TaskDescription1", Status.NEW);
        taskManager.addNewTask(task1);

        Task task2 = new Task("TaskName2", "TaskDescription2", Status.IN_PROGRESS);
        taskManager.addNewTask(task2);

        SubTask subtask1 = new SubTask("SubTaskName1", "SubTaskDescription1", Status.NEW, epic1.getId());
        taskManager.addNewSubTask(subtask1);

        SubTask subtask2 = new SubTask("SubTaskName2", "SubTaskDescription2", Status.IN_PROGRESS, epic1.getId());
        taskManager.addNewSubTask(subtask2);

        SubTask subtask3 = new SubTask("SubTaskName3", "SubTaskDescription3", Status.DONE, epic1.getId());
        taskManager.addNewSubTask(subtask3);

        // Получение задач, подзадач и эпиков
        taskManager.getTasks();
        taskManager.getSubTasks();
        taskManager.getEpics();
        // Получение подзадач для эпика
        taskManager.getEpicSubTasks(epic1.getId());

        // Получение конкретных задач и подзадач
        taskManager.getEpic(epic1.getId());
        taskManager.getTask(task1.getId());
        taskManager.getSubTask(subtask1.getId());

        // Удаление задач и эпиков
        taskManager.deleteEpic(epic2.getId());
        taskManager.deleteSubTask(subtask2.getId());
        taskManager.deleteTask(task2.getId());

        // Обновление задач и эпиков
        taskManager.updateTask(task1);
        taskManager.updateEpic(epic1);
        taskManager.updateSubTask(subtask1);

        // Удаление всех задач, эпиков и подзадач
        taskManager.deleteTasks();
        taskManager.deleteEpics();
        taskManager.deleteSubTasks();*/
    }
}
