package server.httpHandlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import enumeration.StatusCode;
import manager.TaskManager;
import model.Epic;
import server.DurationAdapter;
import server.LocalDateTimeAdapter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();
    private final TaskManager taskManager;

    public EpicHandler(TaskManager taskManager) {
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
                    String jsonString = gson.toJson(taskManager.getAllEpics());
                    response = jsonString;
                } else {
                    try {
                        int id = Integer.parseInt(query.substring(query.indexOf("id=") + 3));
                        Epic epic = taskManager.getEpicById(id);
                        if (epic != null) {
                            response = gson.toJson(epic);
                            statusCode = StatusCode.CODE_200.getCode();
                        } else {
                            response = "Эпик с данным идентификатором не найден";
                            statusCode = StatusCode.CODE_400.getCode();
                        }
                    } catch (StringIndexOutOfBoundsException e) {
                        statusCode = StatusCode.CODE_400.getCode();
                        response = "В запросе отсутствует необходимый параметр Идентификатора эпика (id)";
                    } catch (NumberFormatException e) {
                        statusCode = StatusCode.CODE_400.getCode();
                        response = "Неверный формат Идентификатора эпика (id)";
                    }
                }
                break;
            case "POST":
                String bodyRequest = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                try {
                    Epic epic = gson.fromJson(bodyRequest, Epic.class);
                    if (epic.getId() != null) {
                        taskManager.updateTask(epic);
                        statusCode = StatusCode.CODE_200.getCode();
                        response = "Эпик обновлен";
                    } else {
                        boolean epicCreated = taskManager.addEpic(epic);
                        if (epicCreated) {
                            statusCode = StatusCode.CODE_201.getCode();
                            response = ("Эпик создан");
                        } else {
                            statusCode = StatusCode.CODE_400.getCode();
                            response = ("Неверный формат эпика");
                        }

                    }
                } catch (JsonSyntaxException e) {
                    statusCode = StatusCode.CODE_400.getCode();
                    response = "Неверный формат запроса";
                }
                break;
            case "DELETE":
                response = "";
                query = exchange.getRequestURI().getQuery();
                if (query == null) {
                    taskManager.removeAllEpics();
                    statusCode = StatusCode.CODE_200.getCode();
                } else {
                    try {
                        int id = Integer.parseInt(query.substring(query.indexOf("id=") + 3));
                        taskManager.removeEpicById(id);
                        statusCode = StatusCode.CODE_200.getCode();
                    } catch (StringIndexOutOfBoundsException e) {
                        statusCode = StatusCode.CODE_400.getCode();
                        response = "В запросе отсутствует параметр Идентификатора эпика (id)";
                    } catch (NumberFormatException e) {
                        statusCode = StatusCode.CODE_400.getCode();
                        response = "Неверный формат Идентификатора эпика (id)";
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
