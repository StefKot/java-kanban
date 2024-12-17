package manager;

import task.Task;

public class Node {
    public Task data;
    public Node prev;
    public Node next;

    public Node(Task data, Node prev, Node next) {
        this.data = data;
        this.prev = prev;
        this.next = next;
    }
}
