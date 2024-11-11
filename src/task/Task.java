package task;

import java.util.Objects;

public class Task {
    String name;
    String description;
    int id;
    Status status;
    static Integer counter = 1;

    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(name, task.name) &&
                Objects.equals(description, task.description) &&
                Objects.equals(status, task.status) &&
                Objects.equals(id, task.id);
    }

    @Override
    public int hashCode() {
        int hash = 17;
        if (name != null) {
            hash = hash + name.hashCode();
        }
        hash = hash * 31;
        if (description != null) {
            hash = hash + description.hashCode();
        }
        hash = hash * id;

        return hash;
    }

    @Override
    public String toString() {
        return "Задача {" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + "}";
    }
}
