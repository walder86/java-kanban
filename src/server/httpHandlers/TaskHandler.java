package server.httpHandlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import enumeration.StatusCode;
import exception.ManagerValidateException;
import manager.TaskManager;
import model.Task;
import server.DurationAdapter;
import server.LocalDateTimeAdapter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    private final TaskManager taskManager;

    public TaskHandler(TaskManager taskManager) {
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
                    response = gson.toJson(taskManager.getAllTasks());
                } else {
                    try {
                        int id = Integer.parseInt(query.substring(query.indexOf("id=") + 3));
                        Task task = taskManager.getTaskById(id);
                        if (task != null) {
                            response = gson.toJson(task);
                            statusCode = StatusCode.CODE_200.getCode();
                        } else {
                            response = "Задача с данным идентификатором не найдена";
                            statusCode = StatusCode.CODE_400.getCode();
                        }
                    } catch (StringIndexOutOfBoundsException e) {
                        statusCode = StatusCode.CODE_400.getCode();
                        response = "В запросе отсутствует необходимый параметр Идентификатор задачи (id)";
                    } catch (NumberFormatException e) {
                        statusCode = StatusCode.CODE_400.getCode();
                        response = "Неверный формат Идентификатора задачи (id)";
                    }
                }
                break;
            case "POST":
                String bodyRequest = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                try {
                    Task task = gson.fromJson(bodyRequest, Task.class);
                    if (task.getId() != null) {
                        taskManager.updateTask(task);
                        statusCode = StatusCode.CODE_201.getCode();
                        response = "Задача обновлена";
                    } else {
                        boolean taskCreated = taskManager.addTask(task);
                        if (taskCreated) {
                            statusCode = StatusCode.CODE_201.getCode();
                            response = ("Задача создана");
                        } else {
                            statusCode = StatusCode.CODE_400.getCode();
                            response = ("Неверный формат задачи");
                        }
                    }
                } catch (ManagerValidateException e) {
                    statusCode = StatusCode.CODE_406.getCode();
                    response = e.getMessage();
                }
                break;
            case "DELETE":
                response = "";
                query = exchange.getRequestURI().getQuery();
                if (query == null) {
                    taskManager.removeAllTasks();
                    statusCode = StatusCode.CODE_200.getCode();
                } else {
                    try {
                        int id = Integer.parseInt(query.substring(query.indexOf("id=") + 3));
                        taskManager.removeTaskById(id);
                        statusCode = StatusCode.CODE_200.getCode();
                    } catch (StringIndexOutOfBoundsException e) {
                        statusCode = StatusCode.CODE_400.getCode();
                        response = "В запросе отсутствует необходимый параметр Идентификатор задачи (id)";
                    } catch (NumberFormatException e) {
                        statusCode = StatusCode.CODE_400.getCode();
                        response = "Неверный формат Идентификатора задачи (id)";
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
