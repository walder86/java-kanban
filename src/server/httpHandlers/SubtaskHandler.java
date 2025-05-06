package server.httpHandlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import enumeration.StatusCode;
import manager.TaskManager;
import model.SubTask;
import server.DurationAdapter;
import server.LocalDateTimeAdapter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();
    private final TaskManager taskManager;

    public SubtaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        int statusCode;
        String response;

        String method = exchange.getRequestMethod();
        String path = String.valueOf(exchange.getRequestURI());

        System.out.println("Обрабатывается запрос: " + path + " методом: " + method);

        switch (method) {
            case "GET":
                String query = exchange.getRequestURI().getQuery();
                if (query == null) {
                    statusCode = StatusCode.CODE_200.getCode();
                    response = gson.toJson(taskManager.getAllSubTasks());
                } else {
                    try {
                        int id = Integer.parseInt(query.substring(query.indexOf("id=") + 3));
                        SubTask subtask = taskManager.getSubTaskById(id);
                        if (subtask != null) {
                            response = gson.toJson(subtask);
                            statusCode = StatusCode.CODE_200.getCode();
                        } else {
                            response = "Подзадача с данным идентификатором не найдена";
                            statusCode = StatusCode.CODE_400.getCode();
                        }
                    } catch (StringIndexOutOfBoundsException e) {
                        statusCode = StatusCode.CODE_400.getCode();
                        response = "В запросе отсутствует необходимый параметр Идентификатора задачи (id)";
                    } catch (NumberFormatException e) {
                        statusCode = StatusCode.CODE_400.getCode();
                        response = "Неверный формат Идентификатора подзадачи (id)";
                    }
                }
                break;
            case "POST":
                String bodyRequest = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                try {
                    SubTask subtask = gson.fromJson(bodyRequest, SubTask.class);
                    if (subtask.getId() != null) {
                        taskManager.updateTask(subtask);
                        statusCode = StatusCode.CODE_200.getCode();
                        response = "Подзадача обновлена";
                    } else {
                        SubTask subtaskCreated = taskManager.addSubTask(subtask);
                        if (subtaskCreated != null) {
                            statusCode = StatusCode.CODE_201.getCode();
                            response = ("Подзадача с id = " + subtaskCreated.getId() + " создана");
                        } else {
                            statusCode = StatusCode.CODE_400.getCode();
                            response = ("Неверный формат подзадачи");
                        }
                    }
                } catch (JsonSyntaxException e) {
                    response = "Неверный формат запроса";
                    statusCode = StatusCode.CODE_400.getCode();
                }
                break;
            case "DELETE":
                response = "";
                query = exchange.getRequestURI().getQuery();
                if (query == null) {
                    taskManager.removeAllSubTasks();
                    statusCode = StatusCode.CODE_200.getCode();
                } else {
                    try {
                        int id = Integer.parseInt(query.substring(query.indexOf("id=") + 3));
                        taskManager.removeSubTaskById(id);
                        statusCode = StatusCode.CODE_200.getCode();
                    } catch (StringIndexOutOfBoundsException e) {
                        statusCode = StatusCode.CODE_400.getCode();
                        response = "В запросе отсутствует необходимый параметр id";
                    } catch (NumberFormatException e) {
                        statusCode = StatusCode.CODE_400.getCode();
                        response = "Неверный формат id";
                    }
                }
                break;
            default:
                statusCode = StatusCode.CODE_400.getCode();
                response = "Некорректный запрос";
        }

        sendResponse(exchange, statusCode, response);
    }
}
