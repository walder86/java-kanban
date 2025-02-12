package typeTasks;

import enumeration.Status;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task{

    private List<SubTask> subTasks;

    public Epic(String name, String description) {
        super(name, description);
        this.subTasks = new ArrayList<>();
    }

    public List<SubTask> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(List<SubTask> subTasks) {
        this.subTasks = subTasks;
    }

    public void setStatus() {
        if (subTasks.isEmpty()) {
            this.status = Status.NEW;
            return;
        }
        for (SubTask subTask : subTasks) {
            if (subTask.getStatus() == Status.IN_PROGRESS) {
                this.status = Status.IN_PROGRESS;
                return;
            }
        }
        this.status = Status.DONE;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subTasks=" + subTasks +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}
