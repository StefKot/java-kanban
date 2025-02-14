package HttpServer.handlers;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import manager.exception.IntersectionException;
import task.Task;

import java.io.IOException;

public class TasksHandler extends BaseHandler {
    public TasksHandler(TaskManager taskManager) {
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
            response = gson.toJson(taskManager.getTasks());
            sendText(exchange, response, 200);
        } else {
            try {
                int id = Integer.parseInt(path[2]);
                Task task = taskManager.getTask(id);
                if (task != null) {
                    response = gson.toJson(task);
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
            Task task = gson.fromJson(bodyRequest, Task.class);
            if (taskManager.getTask(task.getId()) != null) {
                taskManager.updateTask(task, task.getId());
                sendText(exchange, "Task id: " + task.getId() + " updated", 200);
            } else {
                try {
                    int tId = taskManager.addNewTask(task);
                    sendText(exchange, Integer.toString(tId), 201);
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
            taskManager.deleteTask(id);
            sendText(exchange, "Success", 200);
        } catch (NumberFormatException e) {
            sendNotFound(exchange);
        }
    }
}