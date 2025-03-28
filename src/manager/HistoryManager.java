package manager;

import typeTasks.Task;

import java.util.List;

interface HistoryManager {

    void addTask(Task task);

    List<Task> getHistory();

    HistoryManager clone();

    void remove(Integer id);
}
