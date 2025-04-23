package manager;

import com.google.gson.*;
import enumeration.Status;
import enumeration.StatusCode;
import model.Epic;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.*;
import server.DurationAdapter;
import server.HttpTaskServer;
import server.LocalDateTimeAdapter;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static com.google.gson.JsonParser.parseString;


public class HttpTaskServerTest {

    private TaskManager taskManager = Manager.getDefault();
    private static HttpTaskServer taskServer;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();
    private final String BASE_PATH = "http://localhost:8080/";

    @BeforeEach
    void startServer() throws IOException, InterruptedException {
        taskServer = new HttpTaskServer(taskManager);
        taskServer.start();
    }

    @AfterEach
    void stopServer() {
        taskServer.stop();
    }

    @Test
    void getTasksTest() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(BASE_PATH + "task/");
        Task task = new Task("Test task", "Description task", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(15));

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();

        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());

            request = HttpRequest
                    .newBuilder()
                    .uri(url)
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Assertions.assertEquals(StatusCode.CODE_200.getCode(), response.statusCode());

            JsonArray arrayTasks = parseString(response.body()).getAsJsonArray();
            Assertions.assertEquals(1, arrayTasks.size());
            JsonObject jsonObject = arrayTasks.get(0).getAsJsonObject();
            Assertions.assertEquals("Test task", jsonObject.get("name").getAsString());

        } catch (IOException | InterruptedException e) {
            Assertions.fail(e);
        }
    }

    @Test
    void getEpicsTest() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(BASE_PATH + "epic/");
        Epic epic = new Epic("Test epic", "Description epic");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();

        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());

            request = HttpRequest
                    .newBuilder()
                    .uri(url)
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Assertions.assertEquals(StatusCode.CODE_200.getCode(), response.statusCode());

            JsonArray arrayEpics = parseString(response.body()).getAsJsonArray();
            Assertions.assertEquals(1, arrayEpics.size());

            JsonObject jsonObject = arrayEpics.get(0).getAsJsonObject();
            Assertions.assertEquals("Test epic", jsonObject.get("name").getAsString());

        } catch (IOException | InterruptedException e) {
            Assertions.fail(e);
        }
    }

    @Test
    void getSubtasksTest() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(BASE_PATH + "epic/");
        Epic epic = new Epic("Title epic", "Description epic");

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();

        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            Assertions.assertEquals(StatusCode.CODE_201.getCode(), postResponse.statusCode());

            Integer epicId = taskManager.getAllEpics().getFirst().getId();
            SubTask subTask = new SubTask("Title subTask", "Description subTask", epicId,
                    Status.NEW, LocalDateTime.now(), Duration.ofMinutes(15));
            url = URI.create(BASE_PATH + "subtask/");

            request = HttpRequest
                    .newBuilder()
                    .uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subTask)))
                    .build();

            client.send(request, HttpResponse.BodyHandlers.ofString());

            request = HttpRequest
                    .newBuilder()
                    .uri(url)
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Assertions.assertEquals(StatusCode.CODE_200.getCode(), response.statusCode());

            JsonArray arraySubTasks = parseString(response.body()).getAsJsonArray();
            Assertions.assertEquals(1, arraySubTasks.size());

            JsonObject jsonObject = arraySubTasks.get(0).getAsJsonObject();
            Assertions.assertEquals("Title subTask", jsonObject.get("name").getAsString());

        } catch (IOException | InterruptedException e) {
            Assertions.fail(e);
        }
    }

    @Test
    void getTaskByIdTest() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(BASE_PATH + "task/");
        Task task = new Task("Title task", "Description task", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(15));

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();

        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            Assertions.assertEquals(StatusCode.CODE_201.getCode(), postResponse.statusCode());

            Task taskNew = taskManager.getAllTasks().getFirst();
            url = URI.create(BASE_PATH + "task?id=" + taskNew.getId());

            request = HttpRequest
                    .newBuilder()
                    .uri(url)
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Assertions.assertEquals(StatusCode.CODE_200.getCode(), response.statusCode());

            Task responseTask = gson.fromJson(response.body(), Task.class);
            Assertions.assertEquals(taskNew, responseTask);

        } catch (IOException | InterruptedException e) {
            Assertions.fail(e);
        }
    }

    @Test
    void getEpicByIdTest() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(BASE_PATH + "epic/");
        Epic epic = new Epic("Title epic", "Description epic");

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();

        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            Assertions.assertEquals(StatusCode.CODE_201.getCode(), postResponse.statusCode());

            Epic epicNew = taskManager.getAllEpics().getFirst();
            url = URI.create(BASE_PATH + "epic?id=" + epicNew.getId());

            request = HttpRequest
                    .newBuilder()
                    .uri(url)
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Assertions.assertEquals(StatusCode.CODE_200.getCode(), response.statusCode());

            Epic responseTask = gson.fromJson(response.body(), Epic.class);
            Assertions.assertEquals(epicNew, responseTask);

        } catch (IOException | InterruptedException e) {
            Assertions.fail(e);
        }
    }

    @Test
    void getSubTaskByIdTest() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(BASE_PATH + "epic/");
        Epic epic = new Epic("Title epic", "Description epic");

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();

        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            Assertions.assertEquals(StatusCode.CODE_201.getCode(), postResponse.statusCode());

            Epic epicNew = taskManager.getAllEpics().getFirst();
            SubTask subtask = new SubTask("Title subTask", "Description subTask", epicNew.getId(),
                    Status.NEW, LocalDateTime.now(), Duration.ofMinutes(15));

            url = URI.create(BASE_PATH + "subtask/");
            request = HttpRequest
                    .newBuilder()
                    .uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask)))
                    .build();

            postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            Assertions.assertEquals(StatusCode.CODE_201.getCode(), postResponse.statusCode());

            SubTask subTaskNew = taskManager.getAllSubTasks().getFirst();

            url = URI.create(BASE_PATH + "subtask?id=" + subTaskNew.getId());
            request = HttpRequest
                    .newBuilder()
                    .uri(url)
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Assertions.assertEquals(StatusCode.CODE_200.getCode(), response.statusCode());

            SubTask responseTask = gson.fromJson(response.body(), SubTask.class);
            Assertions.assertEquals(subTaskNew, responseTask);

        } catch (IOException | InterruptedException e) {
            Assertions.fail(e);
        }
    }

    @Test
    void deleteTaskTest() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(BASE_PATH + "task/");
        Task task = new Task("Title task", "Description task", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(15));

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();

        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
            request = HttpRequest
                    .newBuilder()
                    .uri(url)
                    .DELETE()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Assertions.assertEquals(StatusCode.CODE_200.getCode(), response.statusCode());

            request = HttpRequest
                    .newBuilder()
                    .uri(url)
                    .GET()
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());

            JsonArray arrayTasks = parseString(response.body()).getAsJsonArray();
            Assertions.assertEquals(0, arrayTasks.size());

        } catch (IOException | InterruptedException e) {
            Assertions.fail(e);
        }
    }

    @Test
    void deleteEpicsTest() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(BASE_PATH + "epic/");
        Epic epic = new Epic("Title epic", "Description epic");

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();

        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
            request = HttpRequest
                    .newBuilder()
                    .uri(url)
                    .DELETE()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Assertions.assertEquals(StatusCode.CODE_200.getCode(), response.statusCode());

            request = HttpRequest.newBuilder().uri(url).GET().build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Assertions.assertEquals(StatusCode.CODE_200.getCode(), response.statusCode());

            JsonArray arrayTasks = parseString(response.body()).getAsJsonArray();
            Assertions.assertEquals(0, arrayTasks.size());

        } catch (IOException | InterruptedException e) {
            Assertions.fail(e);
        }
    }

    @Test
    void deleteSubTasks() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(BASE_PATH + "epic/");
        Epic epic = new Epic("Title epic", "Description epic");

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();

        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            Assertions.assertEquals(StatusCode.CODE_201.getCode(), postResponse.statusCode());

            Epic epicNew = taskManager.getAllEpics().getFirst();
            SubTask subtask = new SubTask("Title subTask", "Description subTask", epicNew.getId(),
                    Status.NEW, LocalDateTime.now(), Duration.ofMinutes(15));

            url = URI.create(BASE_PATH + "subtask/");
            request = HttpRequest
                    .newBuilder()
                    .uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask)))
                    .build();
            client.send(request, HttpResponse.BodyHandlers.ofString());

            request = HttpRequest
                    .newBuilder()
                    .uri(url)
                    .DELETE()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Assertions.assertEquals(StatusCode.CODE_200.getCode(), response.statusCode());

            request = HttpRequest
                    .newBuilder()
                    .uri(url)
                    .GET()
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Assertions.assertEquals(StatusCode.CODE_200.getCode(), response.statusCode());

            JsonArray arrayTasks = parseString(response.body()).getAsJsonArray();
            Assertions.assertEquals(0, arrayTasks.size());

        } catch (IOException | InterruptedException e) {
            Assertions.fail(e);
        }
    }

    @Test
    void deleteTaskByIdTest() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(BASE_PATH + "task/");
        Task task = new Task("Title task", "Description task", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(15));

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();

        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
            Task taskNew = taskManager.getAllTasks().getFirst();
            url = URI.create(BASE_PATH + "task?id=" + taskNew.getId());
            request = HttpRequest
                    .newBuilder()
                    .uri(url)
                    .DELETE()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Assertions.assertEquals(StatusCode.CODE_200.getCode(), response.statusCode());

            request = HttpRequest
                    .newBuilder()
                    .uri(url)
                    .GET()
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Assertions.assertEquals("Задача с данным идентификатором не найдена", response.body());

        } catch (IOException | InterruptedException e) {
            Assertions.fail(e);
        }
    }

    @Test
    void deleteEpicByIdTest() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(BASE_PATH + "epic/");
        Epic epic = new Epic("Title epic", "Description epic");

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();

        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            Assertions.assertEquals(StatusCode.CODE_201.getCode(), postResponse.statusCode());

            Epic epicNew = taskManager.getAllEpics().getFirst();
            url = URI.create(BASE_PATH + "epic?id=" + epicNew.getId());
            request = HttpRequest
                    .newBuilder()
                    .uri(url)
                    .DELETE()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Assertions.assertEquals(StatusCode.CODE_200.getCode(), response.statusCode());

            request = HttpRequest.newBuilder().uri(url).GET().build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Assertions.assertEquals("Эпик с данным идентификатором не найден", response.body());

        } catch (IOException | InterruptedException e) {
            Assertions.fail(e);
        }
    }

    @Test
    void deleteSubtaskByIdTest() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(BASE_PATH + "epic/");
        Epic epic = new Epic("Title epic", "Description epic");

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();

        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            Assertions.assertEquals(StatusCode.CODE_201.getCode(), postResponse.statusCode());

            Epic epicNew = taskManager.getAllEpics().getFirst();
            SubTask subtask = new SubTask("Title SubTask 1", "Description SubTask 1", epicNew.getId(),
                    Status.NEW, LocalDateTime.now(), Duration.ofMinutes(15));

            url = URI.create(BASE_PATH + "subtask/");
            request = HttpRequest
                    .newBuilder()
                    .uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask)))
                    .build();
            postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            Assertions.assertEquals(StatusCode.CODE_201.getCode(), postResponse.statusCode());

            SubTask subTaskNew = taskManager.getAllSubTasks().getFirst();
            url = URI.create(BASE_PATH + "subtask?id=" + subTaskNew.getId());
            request = HttpRequest
                    .newBuilder()
                    .uri(url)
                    .DELETE()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Assertions.assertEquals(StatusCode.CODE_200.getCode(), response.statusCode());

            request = HttpRequest
                    .newBuilder()
                    .uri(url)
                    .GET()
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Assertions.assertEquals("Подзадача с данным идентификатором не найдена", response.body());

        } catch (IOException | InterruptedException e) {
            Assertions.fail(e);
        }
    }
}
