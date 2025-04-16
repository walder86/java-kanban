package manager;

import enumeration.Status;
import model.Epic;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

class FileBackedTasksManagerTest {

    public static final String PATH = "test_tasks_file.csv";
    File file = new File(PATH);

    private FileBackedTasksManager taskManager;

    @BeforeEach
    void createInMemoryTaskManager() {
        taskManager = new FileBackedTasksManager(file);
    }

    @AfterEach
    public void afterEach() {
        try {
            Files.delete(Path.of(PATH));
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }
    }

    @Test
    public void saveAndLoadTest() {
        Task task = new Task("Test task", "Test task description", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        taskManager.addTask(task);
        Epic epic = new Epic("Test epic", "Test epic description");
        taskManager.addEpic(epic);
        SubTask subTask1 = new SubTask(
                "Test subTask1",
                "Test subTask1 description",
                epic.getId(),
                Status.IN_PROGRESS, LocalDateTime.now().minusHours(1), Duration.ofMinutes(5));
        taskManager.addSubTask(subTask1);

        FileBackedTasksManager fileManager = new FileBackedTasksManager(file);
        fileManager.loadFromFile(file);

        Epic epicById = taskManager.getEpicById(epic.getId());

        Assertions.assertEquals(List.of(task), taskManager.getAllTasks());
        Assertions.assertEquals(List.of(epicById), taskManager.getAllEpics());
    }


}
