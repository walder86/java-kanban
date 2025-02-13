import enumeration.Status;
import manager.TaskManager;
import typeTasks.Epic;
import typeTasks.SubTask;
import typeTasks.Task;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        TaskManager taskManager = new TaskManager();
        Task task1 = new Task( "Задача 1", "Задача_1", Status.NEW);
        Task task2 = new Task( "Задача 2", "Задача_2", Status.NEW);

        taskManager.addTask(task1);
        taskManager.addTask(task2);
        System.out.println(taskManager.getAllTasks());

        Epic epic1 = new Epic("Эпик 1", "Эпик_1");
        Epic epic2 = new Epic("Эпик 2", "Эпик_2");

        SubTask subTask1 = new SubTask("Подзадача 1","Подзадача_1", epic1.getId(), Status.NEW);
        SubTask subTask2 = new SubTask("Подзадача 2","Подзадача_2", epic2.getId(), Status.NEW);
        SubTask subTask3 = new SubTask("Подзадача 3","Подзадача_3", epic2.getId(), Status.NEW);

        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);

        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);
        taskManager.addSubTask(subTask3);

        subTask1.setStatus(Status.IN_PROGRESS);
        subTask2.setStatus(Status.DONE);
        subTask3.setStatus(Status.IN_PROGRESS);

        taskManager.updateSubTask(subTask1);
        taskManager.updateSubTask(subTask2);
        taskManager.updateSubTask(subTask3);

        taskManager.removeSubTaskById(subTask3.getId());

        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllSubTasks());
    }
}
