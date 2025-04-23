package server;

import com.sun.net.httpserver.HttpServer;
import manager.Manager;
import manager.TaskManager;
import server.httpHandlers.*;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private final HttpServer httpServer;

    public HttpTaskServer(TaskManager manager) throws IOException, InterruptedException {
        TaskManager taskManager = manager;
        this.httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/task", new TaskHandler(taskManager));
        httpServer.createContext("/epic", new EpicHandler(taskManager));
        httpServer.createContext("/epic/subtasks", new SubtaskByEpicHandler(taskManager));
        httpServer.createContext("/subtask", new SubtaskHandler(taskManager));
        httpServer.createContext("/history", new HistoryHandler(taskManager));
        httpServer.createContext("/prioritized", new PrioritizedTasksHandler(taskManager));
    }

    public void start() {
        System.out.println("HTTP-cервер запущен на порту " + PORT + "!");
        System.out.println("http://localhost:" + PORT + "/tasks/");
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(1);
        System.out.println("HTTP-cервер остановлен на " + PORT + " порту!");
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        new HttpTaskServer(Manager.getDefault()).start();
    }
}
