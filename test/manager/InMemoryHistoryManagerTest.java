package manager;

import enumeration.Status;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import model.Epic;
import model.SubTask;
import model.Task;

import java.util.List;

class InMemoryHistoryManagerTest {

    private static TaskManager taskManager;

    @BeforeAll
    static void createInMemoryHistoryManager() {
        taskManager = Manager.getDefault();
    }

    @Test
    void addTask() {
        Task task = new Task("Test task", "Test task description", Status.NEW);
        taskManager.addTask(task);
        Epic epic = new Epic("Test epic", "Test epic description");
        taskManager.addEpic(epic);
        SubTask subTask = new SubTask(
                "Test subTask1",
                "Test subTask1 description",
                epic.getId(),
                Status.IN_PROGRESS);
        taskManager.addSubTask(subTask);
        List<Task> history = taskManager.getHistory();
        Assertions.assertEquals(0, history.size(), "Количество задач в истории не равно нулю");

        taskManager.getTaskById(task.getId());
        taskManager.getEpicById(epic.getId());
        taskManager.getSubTaskById(subTask.getId());
        history = taskManager.getHistory();
        Assertions.assertEquals(3, history.size(), "Количество задач в истории не равно ожидаемому");


    }

}