package manager;

import model.Task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private CustomLinkedList historyList;
    private Map<Integer, Node<Task>> historyTasks;


    public InMemoryHistoryManager() {
        this.historyList = new CustomLinkedList();
        this.historyTasks = new HashMap<>();

    }

    private InMemoryHistoryManager(CustomLinkedList historyList, Map<Integer, Node<Task>> historyTasks) {
        this.historyList = historyList;
        this.historyTasks = historyTasks;
    }

    @Override
    public void addTask(Task task) {
        Node<Task> node = this.historyList.linkLast(task);

        if (historyTasks.containsKey(task.getId()))
            historyList.removeNode(historyTasks.get(task.getId()));

        historyTasks.put(task.getId(), node);
    }

    @Override
    public List<Task> getHistory() {
        return this.historyList.getTasks();
    }

    @Override
    public HistoryManager clone() {
        return new InMemoryHistoryManager(this.historyList, this.historyTasks);
    }

    @Override
    public void remove(Integer id) {
        historyList.removeNode(historyTasks.get(id));
        historyTasks.remove(id);
    }

}
