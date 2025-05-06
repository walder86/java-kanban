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

public class SubtaskByEpicHandler extends BaseHttpHandler implements HttpHandler {
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();
    private final TaskManager taskManager;

    public SubtaskByEpicHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        int statusCode;
        String response;
        String method = exchange.getRequestMethod();
        String path = String.valueOf(exchange.getRequestURI());

        System.out.println("Обрабатывается запрос " + path + " с методом " + method);

        switch (method) {
            case "GET":
                String query = exchange.getRequestURI().getQuery();
                try {
                    int id = Integer.parseInt(query.substring(query.indexOf("id=") + 3));
                    response = gson.toJson(taskManager.getAllSubTasksForEpic(id));
                    statusCode = StatusCode.CODE_200.getCode();
                } catch (StringIndexOutOfBoundsException | NullPointerException e) {
                    response = "В запросе отсутствует необходимый параметр Идентификатор эпика (id)";
                    statusCode = StatusCode.CODE_400.getCode();
                } catch (NumberFormatException e) {
                    response = "Неверный формат Идентификатор эпика (id)";
                    statusCode = StatusCode.CODE_400.getCode();
                }
                break;
            default:
                response = "Некорректный запрос";
                statusCode = StatusCode.CODE_404.getCode();
        }

        sendResponse(exchange, statusCode, response);
    }
}
