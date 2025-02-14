package HttpServer.server;

import HttpServer.handlers.*;
import com.sun.net.httpserver.HttpServer;
import manager.Managers;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final int PORT = 8080;

    public static void main(String[] args) throws IOException {
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);

        httpServer.createContext("/hello", new BaseHandler(Managers.getDefault()));
        httpServer.createContext("/tasks", new TasksHandler(Managers.getDefault()));
        httpServer.createContext("/epics", new EpicsHandler(Managers.getDefault()));
        httpServer.createContext("/subtasks", new SubTasksHandler(Managers.getDefault()));
        httpServer.createContext("/history", new HistoryHandler(Managers.getDefault()));
        httpServer.createContext("/priority", new PriorityTasksHandler(Managers.getDefault()));

        httpServer.start();

        System.out.println("HTTP server is running on " + PORT + " port!");
    }
}