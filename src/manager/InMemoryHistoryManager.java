package manager;

import typeTasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{

    private List<Task> historyTasks;

    public InMemoryHistoryManager() {
        this.historyTasks = new ArrayList<>();
    }

    @Override
    public void addTask(Task task) {
        if (this.historyTasks.size() == 10) {
            this.historyTasks.removeFirst();
        } else {
            this.historyTasks.add(task);
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        return new ArrayList<>(this.historyTasks);
    }

}
