package manager;

import model.Task;

import java.util.List;

public interface HistoryManager {

    void addTask(Task task);

    List<Task> getHistory();

    HistoryManager clone();

    void remove(Integer id);

    void removeAllTasks();

    void removeAllEpics();

    void removeAllSubTasks();
}
