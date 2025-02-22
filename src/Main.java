import enumeration.Status;
import manager.InMemoryTaskManager;
import typeTasks.Epic;
import typeTasks.SubTask;
import typeTasks.Task;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
        Task task1 = new Task( "Задача 1", "Задача_1", Status.NEW);
        Task task2 = new Task( "Задача 2", "Задача_2", Status.NEW);

        inMemoryTaskManager.addTask(task1);
        inMemoryTaskManager.addTask(task2);
        System.out.println(inMemoryTaskManager.getAllTasks());

        Epic epic1 = new Epic("Эпик 1", "Эпик_1");
        Epic epic2 = new Epic("Эпик 2", "Эпик_2");

        inMemoryTaskManager.addEpic(epic1);
        inMemoryTaskManager.addEpic(epic2);

        SubTask subTask1 = new SubTask("Подзадача 1","Подзадача_1", epic1.getId(), Status.NEW);
        SubTask subTask2 = new SubTask("Подзадача 2","Подзадача_2", epic2.getId(), Status.NEW);
        SubTask subTask3 = new SubTask("Подзадача 3","Подзадача_3", epic2.getId(), Status.NEW);

        inMemoryTaskManager.addSubTask(subTask1);
        inMemoryTaskManager.addSubTask(subTask2);
        inMemoryTaskManager.addSubTask(subTask3);

        subTask1.setStatus(Status.IN_PROGRESS);
        subTask2.setStatus(Status.DONE);
        subTask3.setStatus(Status.IN_PROGRESS);

        inMemoryTaskManager.updateSubTask(subTask1);
        inMemoryTaskManager.updateSubTask(subTask2);
        inMemoryTaskManager.updateSubTask(subTask3);

        inMemoryTaskManager.removeSubTaskById(subTask3.getId());

        System.out.println(inMemoryTaskManager.getAllEpics());
        System.out.println(inMemoryTaskManager.getAllSubTasks());
    }
}
