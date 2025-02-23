package manager;

import typeTasks.Epic;
import typeTasks.SubTask;
import typeTasks.Task;

import java.util.List;

public interface TaskManager {
    HistoryManager getHistoryManager();

    List<Epic> getAllEpics();

    List<Task> getAllTasks();

    List<SubTask> getAllSubTasks();

    List<SubTask> getAllSubTasksForEpic(Integer epicId);

    Boolean addTask(Task task);

    Boolean addEpic(Epic epic);

    Boolean addSubTask(SubTask subTask);

    Task getTaskById(Integer taskId);

    Epic getEpicById(Integer epicId);

    SubTask getSubTaskById(Integer subTaskId);

    Boolean updateTask(Task task);

    Boolean updateEpic(Epic epic);

    Boolean updateSubTask(SubTask subTask);

    void removeAllTasks();

    void removeAllEpics();

    void removeAllSubTasks();

    void removeTaskById(Integer taskId);

    void removeEpicById(Integer epicId);

    void removeSubTaskById(Integer subTaskId);
}
