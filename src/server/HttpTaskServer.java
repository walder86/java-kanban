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
        this.httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/task", new TaskHandler(manager));
        httpServer.createContext("/epic", new EpicHandler(manager));
        httpServer.createContext("/epic/subtasks", new SubtaskByEpicHandler(manager));
        httpServer.createContext("/subtask", new SubtaskHandler(manager));
        httpServer.createContext("/history", new HistoryHandler(manager));
        httpServer.createContext("/prioritized", new PrioritizedTasksHandler(manager));
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
