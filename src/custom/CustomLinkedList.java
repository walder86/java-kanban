package custom;

import model.Task;

import java.util.ArrayList;
import java.util.List;


public class CustomLinkedList {

    public CustomLinkedList() {

    }

    private Node<Task> first;
    private Node<Task> last;

    public Node<Task> linkLast(Task task) {

        Node<Task> newNode = new Node<>(last, task, null);
        if (last == null) {
            first = newNode;
        } else {
            last.next = newNode;
        }
        last = newNode;
        return last;
    }

    public void clear() {
        first = null;
        last = null;
    }

    public List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node<Task> element = first;

        while (element != null) {
            tasks.add(element.item);
            element = element.next;
        }

        return tasks;
    }

    public void removeNode(Node<Task> node) {
        if (node == null) {
            return;
        }
        if (node.equals(first)) {
            first = node.next;
            if (node.next != null) {
                node.next.prev = null;
            }
        } else {
            node.prev.next = node.next;
            if (node.next != null) {
                node.next.prev = node.prev;
            }
        }
    }

}