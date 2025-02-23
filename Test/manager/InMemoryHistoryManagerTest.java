package manager;

import enumeration.Status;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import typeTasks.Epic;
import typeTasks.SubTask;
import typeTasks.Task;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private static HistoryManager historyManager;
    private static TaskManager taskManager;

    @BeforeAll
    static void createInMemoryHistoryManager() {
        taskManager = Manager.getDefault();
        historyManager = taskManager.getHistoryManager();
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
        taskManager.addTask(subTask);
        ArrayList<Task> history = historyManager.getHistory();
        Assertions.assertEquals(0, history.size(), "Количество задач в истории не равно нулю");

        taskManager.getTaskById(task.getId());
        taskManager.getEpicById(epic.getId());
        taskManager.getSubTaskById(subTask.getId());
        history = historyManager.getHistory();
        Assertions.assertEquals(3, history.size(), "Количество задач в истории не равно ожидаемому");

        for (int i = 0; i < 10; i++) {
            taskManager.addTask(task);
            taskManager.getTaskById(task.getId());
        }
        history = historyManager.getHistory();
        Assertions.assertEquals(10, history.size(), "Количество задач в истории не равно 10");
    }

}