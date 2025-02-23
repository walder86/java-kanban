package manager;

import typeTasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{

    private List<Task> historyTasks;

    public InMemoryHistoryManager() {
        this.historyTasks = new ArrayList<>();
    }

    private InMemoryHistoryManager(List<Task> tasks) {
        this.historyTasks = tasks;
    }

    @Override
    public void addTask(Task task) {
        if (this.historyTasks.size() == 10) {
            this.historyTasks.removeFirst();
        }
        this.historyTasks.add(task);

    }

    @Override
    public ArrayList<Task> getHistory() {
        return new ArrayList<>(this.historyTasks);
    }

    @Override
    public HistoryManager clone() {
        return new InMemoryHistoryManager(this.historyTasks);
    }

}
