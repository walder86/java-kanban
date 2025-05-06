package server.httpHandlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import enumeration.StatusCode;
import manager.TaskManager;
import server.DurationAdapter;
import server.LocalDateTimeAdapter;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public class PrioritizedTasksHandler extends BaseHttpHandler implements HttpHandler {

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();
    private final TaskManager taskManager;

    public PrioritizedTasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        int statusCode = StatusCode.CODE_400.getCode();
        String response;
        String method = exchange.getRequestMethod();
        String path = String.valueOf(exchange.getRequestURI());

        System.out.println("Обрабатывается запрос " + path + " методом " + method);

        switch (method) {
            case "GET":
                statusCode = StatusCode.CODE_200.getCode();
                response = gson.toJson(taskManager.getPrioritizedTasks());
                break;
            default:
                response = "Неверный запрос";
        }

        sendResponse(exchange, statusCode, response);
    }
}
