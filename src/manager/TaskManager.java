package manager;

import typeTasks.Epic;
import typeTasks.SubTask;
import typeTasks.Task;

import java.util.List;

public interface TaskManager {
    List<Epic> getAllEpics();

    List<Task> getAllTasks();

    List<Task> getAllSubTasks();

    List<SubTask> getAllSubTasksForEpic(Integer epicId);

    boolean addTask(Task task);

    boolean addEpic(Epic epic);

    boolean addSubTask(SubTask subTask);

    Task getTaskById(Integer taskId);

    Epic getEpicById(Integer epicId);

    SubTask getSubTaskById(Integer subTaskId);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubTask(SubTask subTask);

    void removeAllTasks();

    void removeAllEpics();

    void removeAllSubTasks();

    void removeTaskById(Integer taskId);

    void removeEpicById(Integer epicId);

    void removeSubTaskById(Integer subTaskId);
}
