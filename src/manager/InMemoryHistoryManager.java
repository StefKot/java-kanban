package manager;

import task.Task;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager {
    private Node head;
    private Node tail;
    private final Map<Integer, Node> history = new HashMap<>();

    @Override
    public List<Task> getHistory() {
        return fetchHistory();
    }

    @Override
    public void addToHistory(Task task) {
        if (task == null) {
            return;
        }
        final int id = task.getId();
        removeFromHistory(id);
        addTaskToEnd(task);
        history.put(id, tail);
    }

    @Override
    public void removeFromHistory(int id) {
        final Node node = history.remove(id);
        if (node == null) {
            return;
        }
        removeNode(node);
    }

    private ArrayList<Task> fetchHistory() {
        Node element = head;
        ArrayList<Task> taskHistory = new ArrayList<>();
        while (element != null) {
            taskHistory.add(element.data);
            element = element.next;
        }
        return taskHistory;
    }

    private void addTaskToEnd(Task task) {
        final Node node = new Node(task, tail, null);
        if (head == null) {
            head = node;
        } else {
            tail.next = node;
        }
        tail = node;
    }

    private void removeNode(Node node) {
        if (node.prev != null) {
            node.prev.next = node.next;
            if (node.next == null) {
                tail = node.prev;
            } else {
                node.next.prev = node.prev;
            }
        } else {
            head = node.next;
            if (head == null) {
                tail = null;
            } else {
                head.prev = null;
            }
        }
    }

    public boolean isEmpty() {
        return head == null;
    }
}
