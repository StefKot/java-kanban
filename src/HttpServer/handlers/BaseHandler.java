package HttpServer.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.Managers;
import manager.TaskManager;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class BaseHandler implements HttpHandler {

    protected static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    protected final TaskManager taskManager;
    protected final Gson gson = Managers.getGson();
    protected String response;

    public BaseHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    protected void sendText(HttpExchange exchange, String response, int statusCode) throws IOException {
        exchange.sendResponseHeaders(statusCode, 0);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.getResponseBody().write(response.getBytes(DEFAULT_CHARSET));
        exchange.close();
    }

    protected void sendNotFound(HttpExchange httpExchange) throws IOException {
        sendText(httpExchange, "Not Found", 404);
    }

    protected void sendHasInteractions(HttpExchange httpExchange) throws IOException {
        sendText(httpExchange, "Not Acceptable", 406);
    }

    protected void sendInternalServerError(HttpExchange httpExchange) throws IOException {
        sendText(httpExchange, "Internal Server Error", 500);
    }

    protected String readText(HttpExchange httpExchange) throws IOException {
        return new String(httpExchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
    }
}