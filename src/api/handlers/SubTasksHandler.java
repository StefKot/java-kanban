package api.handlers;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import manager.exception.IntersectionException;
import task.SubTask;

import java.io.IOException;

public class SubTasksHandler extends BaseHandler {

    public SubTasksHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String[] path = exchange.getRequestURI().getPath().split("/");

        switch (method) {
            case "GET":
                handleGet(exchange, path);
                break;
            case "POST":
                handlePost(exchange);
                break;
            case "DELETE":
                handleDelete(exchange, path);
                break;
            default:
                sendInternalServerError(exchange);
        }
    }

    private void handleGet(HttpExchange exchange, String[] path) throws IOException {
        if (path.length == 2) {
            response = gson.toJson(taskManager.getSubTasks());
            sendText(exchange, response, 200);
        } else {
            try {
                int id = Integer.parseInt(path[2]);
                SubTask subTask = taskManager.getSubTask(id);
                if (subTask != null) {
                    response = gson.toJson(subTask);
                    sendText(exchange, response, 200);
                } else {
                    sendNotFound(exchange);
                }
            } catch (NumberFormatException e) {
                sendNotFound(exchange);
            }
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        String bodyRequest = readText(exchange);
        if (bodyRequest.isEmpty()) {
            sendNotFound(exchange);
            return;
        }
        try {
            SubTask subTask = gson.fromJson(bodyRequest, SubTask.class);
            if (taskManager.getSubTask(subTask.getId()) != null) {
                taskManager.updateSubTask(subTask, subTask.getId());
                sendText(exchange, "SubTask id: " + subTask.getId() + " updated", 200);
            } else {
                try {
                    int subTaskId = taskManager.addNewSubTask(subTask);
                    sendText(exchange, Integer.toString(subTaskId), 201);
                } catch (IntersectionException e) {
                    sendHasInteractions(exchange);
                }
            }
        } catch (JsonSyntaxException e) {
            sendNotFound(exchange);
        }
    }

    private void handleDelete(HttpExchange exchange, String[] path) throws IOException {
        try {
            int id = Integer.parseInt(path[2]);
            taskManager.deleteSubTask(id);
            sendText(exchange, "Success", 200);
        } catch (NumberFormatException e) {
            sendNotFound(exchange);
        }
    }
}