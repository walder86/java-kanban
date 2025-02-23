package manager;

import enumeration.Status;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import typeTasks.Epic;
import typeTasks.SubTask;
import typeTasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    private TaskManager taskManager;
    private HistoryManager historyManager;

    @BeforeEach
    void createInMemoryTaskManager() {
        taskManager = Manager.getDefault();
        historyManager = Manager.getDefaultHistoryManager();
    }

    @Test
    void getAllSubTasksForEpic() {
    }

    @Test
    void addTask() {
        Task task = new Task("Test task", "Test task description", Status.NEW);
        boolean checkAddTask = taskManager.addTask(task);
        Assertions.assertTrue(checkAddTask, "Задача не была добавлена");

        Task taskFromManager = taskManager.getTaskById(task.getId());
        Assertions.assertNotNull(taskFromManager, "Полученная задача равна null");
        Assertions.assertEquals(task, taskFromManager, "Добавленная задача не равна созданной");

        List<Task> tasks = taskManager.getAllTasks();
        Assertions.assertNotNull(tasks, "Список задач равен null");
        Assertions.assertEquals(1, tasks.size(), "Количество в списке задач не совпадает с ожидаемым");
        Assertions.assertEquals(task, tasks.get(0), "Добавленная задача не совпадает с задачей из списка");
    }

    @Test
    void addEpic() {
        Epic epic = new Epic("Test epic", "Test epic description");

        boolean checkAddTask = taskManager.addTask(epic);
        Assertions.assertFalse(checkAddTask, "Недопустима вставка эпика в список задач");

        boolean checkAddEpic = taskManager.addEpic(epic);
        Assertions.assertTrue(checkAddEpic, "Эпик не был добавлен");

        Epic epicFromManager = taskManager.getEpicById(epic.getId());
        Assertions.assertNotNull(epicFromManager, "Полученный эпик равен null");
        Assertions.assertEquals(epic, epicFromManager, "Добавленный эпик не равна созданному");

        List<Epic> epics = taskManager.getAllEpics();
        Assertions.assertNotNull(epics, "Список эпиков равен null");
        Assertions.assertEquals(1, epics.size(), "Количество в списке эпиков не совпадает с ожидаемым");
        Assertions.assertEquals(epic, epics.get(0), "Добавленный эпик не совпадает с эпиком из списка");

    }

    @Test
    void addSubTask() {
        Epic epic = new Epic("Test epic", "Test epic description");
        taskManager.addEpic(epic);

        SubTask subTask1 = new SubTask(
                "Test subTask1",
                "Test subTask1 description",
                10,
                Status.IN_PROGRESS);
        Boolean checkAddSubTask = taskManager.addSubTask(subTask1);
        Assertions.assertFalse(checkAddSubTask, "Подзадача не может быть добавлена к несуществующему эпику");

        subTask1 = new SubTask(
                "Test subTask1",
                "Test subTask1 description",
                epic.getId(),
                Status.IN_PROGRESS);
        Boolean checkAddTask = taskManager.addTask(subTask1);
        Assertions.assertFalse(checkAddTask, "Недопустима вставка подзадачи в список задач");

        checkAddSubTask = taskManager.addSubTask(subTask1);
        Epic epicById = taskManager.getEpicById(epic.getId());
        Assertions.assertTrue(checkAddSubTask, "Не найден эпик для подзадачи");
        Assertions.assertEquals(Status.IN_PROGRESS, epicById.getStatus());

        SubTask subTaskFromManager = taskManager.getSubTaskById(subTask1.getId());
        Assertions.assertNotNull(subTaskFromManager, "Полученная подзадача равна null");
        Assertions.assertEquals(subTask1, subTaskFromManager, "Добавленная подзадача не равна созданной");

        List<SubTask> subTasks = taskManager.getAllSubTasks();
        Assertions.assertNotNull(subTasks, "Список подзадач равен null");
        Assertions.assertEquals(1, subTasks.size(), "Количество в списке подзадач не совпадает с ожидаемым");
        Assertions.assertEquals(subTask1, subTasks.get(0), "Добавленная подзадача не совпадает с подзадачей из списка");

        List<SubTask> subTasksFromEpic = taskManager.getAllSubTasksForEpic(epic.getId());
        Assertions.assertNotNull(subTasksFromEpic, "Список подзадач равен null");
        Assertions.assertEquals(1, subTasksFromEpic.size(), "Количество в списке подзадач не совпадает с ожидаемым");
        Assertions.assertArrayEquals(subTasks.toArray(), subTasksFromEpic.toArray(), "Список подзадач и список подзадач из эпика не совпадают");

    }

    @Test
    void updateTask() {
        Task task = new Task("Test task", "Test task description", Status.NEW);
        Boolean checkUpdateTask = taskManager.updateTask(task);
        Assertions.assertFalse(checkUpdateTask, "Нельзя изменять не добавленную задача");

        taskManager.addTask(task);
        task.setName("Test task change");
        task.setDescription("Test task description change");
        task.setStatus(Status.IN_PROGRESS);
        taskManager.updateTask(task);
        Assertions.assertEquals(task, taskManager.getTaskById(task.getId()), "Задача не была изменена в списке задач");

        task.setId(5);
        checkUpdateTask = taskManager.updateTask(task);
        Assertions.assertFalse(checkUpdateTask, "Нельзя изменять ИД задачи");

    }

    @Test
    void updateEpic() {
        Epic epic = new Epic("Epic task", "Epic task description");
        Boolean checkUpdateEpic = taskManager.updateEpic(epic);
        Assertions.assertFalse(checkUpdateEpic, "Нельзя изменять не добавленный эпик");

        taskManager.addEpic(epic);
        checkUpdateEpic = taskManager.updateTask(epic);
        Assertions.assertFalse(checkUpdateEpic, "Нельзя обновить эпик через метод обновления задач");

        epic.setName("Test task change");
        epic.setDescription("Test task description change");
        taskManager.updateEpic(epic);
        Epic epicById = taskManager.getEpicById(epic.getId());
        Assertions.assertEquals(epic, epicById, "Эпик не был изменен в списке эпиков");

        epic.setStatus(Status.IN_PROGRESS);
        taskManager.updateEpic(epic);
        epicById = taskManager.getEpicById(epic.getId());
        Assertions.assertNotEquals(Status.IN_PROGRESS, epicById.getStatus(), "Статус эпика должен зависеть только от статуса подзадач");

        epic.setId(5);
        checkUpdateEpic = taskManager.updateEpic(epic);
        Assertions.assertFalse(checkUpdateEpic, "Нельзя изменять ИД эпика");
    }

    @Test
    void updateSubTask() {

        Epic epic = new Epic("Test epic", "Test epic description");
        taskManager.addEpic(epic);

        SubTask subTask1 = new SubTask(
                "Test subTask1",
                "Test subTask1 description",
                epic.getId(),
                Status.IN_PROGRESS);
        Boolean checkUpdateSubTask = taskManager.updateSubTask(subTask1);
        Assertions.assertFalse(checkUpdateSubTask, "Нельзя обновить несуществующему подзадачу");

        taskManager.addSubTask(subTask1);
        subTask1.setStatus(Status.DONE);
        taskManager.updateSubTask(subTask1);
        Epic epicById = taskManager.getEpicById(epic.getId());
        Assertions.assertEquals(Status.DONE, epicById.getStatus(), "Статус эпика не обновился после обновления статуса подзадачи");

        SubTask subTask2 = new SubTask(
                "Test subTask2",
                "Test subTask2 description",
                epic.getId(),
                Status.IN_PROGRESS);
        taskManager.addSubTask(subTask2);
        epicById = taskManager.getEpicById(epic.getId());
        assertEquals(Status.IN_PROGRESS, epicById.getStatus(), "Статус эпика не обновился после добавления подзадачи");

    }

    @Test
    void removeAllTasks() {
        Task task = new Task("Test task", "Test task description", Status.NEW);
        taskManager.addTask(task);
        taskManager.addTask(task);
        taskManager.removeAllTasks();
        List<Task> allTasks = taskManager.getAllTasks();
        Assertions.assertEquals(0, allTasks.size(), "Список задач не был очищен");
    }

    @Test
    void removeAllEpics() {
        Epic epic = new Epic("Test epic", "Test epic description");
        taskManager.addEpic(epic);
        taskManager.addEpic(epic);
        taskManager.removeAllEpics();
        List<Epic> allEpics = taskManager.getAllEpics();
        Assertions.assertEquals(0, allEpics.size(), "Список эпиков не был очищен");
    }

    @Test
    void removeAllSubTasks() {
        Epic epic = new Epic("Test epic", "Test epic description");
        taskManager.addEpic(epic);
        SubTask subTask = new SubTask(
                "Test subTask1",
                "Test subTask1 description",
                epic.getId(),
                Status.IN_PROGRESS);
        taskManager.addSubTask(subTask);

        taskManager.removeAllSubTasks();
        List<SubTask> allSubTasks = taskManager.getAllSubTasks();
        List<SubTask> allSubTasksForEpic = taskManager.getAllSubTasksForEpic(epic.getId());
        Assertions.assertEquals(0, allSubTasks.size(), "Список подзадач не был очищен");
        Assertions.assertEquals(0, allSubTasksForEpic.size(), "Список для эпика не был очищен");

        Epic epicById = taskManager.getEpicById(epic.getId());
        Assertions.assertEquals(Status.NEW, epicById.getStatus(), "Статус после очищения всех подзадач у эпика не изменился");
    }

    @Test
    void removeTaskById() {
        Task task1 = new Task("Test task1", "Test task1 description", Status.NEW);
        Task task2 = new Task("Test task2", "Test task2 description", Status.NEW);
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.removeTaskById(task2.getId());
        List<Task> allTasks = taskManager.getAllTasks();
        Assertions.assertEquals(1, allTasks.size(), "Была удалена не одна задача");
        Assertions.assertEquals(task1, allTasks.get(0), "Была удалена другая задача");
    }

    @Test
    void removeEpicById() {
        Epic epic1 = new Epic("Test epic1", "Test epic1 description");
        Epic epic2 = new Epic("Test epic2", "Test epic2 description");
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        taskManager.removeEpicById(epic2.getId());
        List<Epic> allEpics = taskManager.getAllEpics();
        Assertions.assertEquals(1, allEpics.size(), "Был удален не один эпик");
        Assertions.assertEquals(epic1, allEpics.get(0), "Был удален другой эпик");
    }

    @Test
    void removeSubTaskById() {
        Epic epic1 = new Epic("Test epic1", "Test epic1 description");
        Epic epic2 = new Epic("Test epic2", "Test epic2 description");
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        SubTask subTask1 = new SubTask("Test subTask1", "Test subTask1 descroption", epic1.getId(), Status.IN_PROGRESS);
        SubTask subTask2 = new SubTask("Test subTask2", "Test subTask2 descroption", epic2.getId(), Status.IN_PROGRESS);
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);
        taskManager.removeSubTaskById(subTask1.getId());
        List<SubTask> allSubTasks = taskManager.getAllSubTasks();
        List<SubTask> allSubTasksForEpic1 = taskManager.getAllSubTasksForEpic(epic1.getId());
        List<SubTask> allSubTasksForEpic2 = taskManager.getAllSubTasksForEpic(epic2.getId());
        Assertions.assertEquals(1, allSubTasks.size(), "Количество всех подзадач не изменилось");
        Assertions.assertEquals(0, allSubTasksForEpic1.size(), "Количество подзадач у эпика не изменилось");
        Assertions.assertEquals(1, allSubTasksForEpic2.size(), "Количество подзадач у другого эпика изменилось");

        Epic epicById1 = taskManager.getEpicById(epic1.getId());
        Epic epicById2 = taskManager.getEpicById(epic2.getId());
        Assertions.assertEquals(Status.NEW, epicById1.getStatus(), "Статус после удаления подзадачи у эпика не изменился");
        Assertions.assertEquals(Status.IN_PROGRESS, epicById2.getStatus(), "Статус после удаления подзадачи у другого эпика изменился");

    }
}