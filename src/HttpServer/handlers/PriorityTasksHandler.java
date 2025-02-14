package HttpServer.handlers;

import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;

import java.io.IOException;

public class PriorityTasksHandler extends BaseHandler {
    public PriorityTasksHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        if ("GET".equals(method)) {
            handleGet(exchange);
        } else {
            sendInternalServerError(exchange);
        }
    }

    private void handleGet(HttpExchange httpExchange) throws IOException {
        response = gson.toJson(taskManager.getPriorityTasks());
        sendText(httpExchange, response, 200);
    }
}