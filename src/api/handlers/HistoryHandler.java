package api.handlers;

import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;

import java.io.IOException;

public class HistoryHandler extends BaseHandler {
    public HistoryHandler(TaskManager taskManager) {
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
        response = gson.toJson(taskManager.getHistory());
        sendText(httpExchange, response, 200);
    }
}