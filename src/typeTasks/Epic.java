package typeTasks;

import enumeration.Status;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task{

    private List<SubTask> subTasks;

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
        this.subTasks = new ArrayList<>();
    }

    public List<SubTask> getSubTasks() {
        return new ArrayList<>(subTasks);
    }

    public void changeStatus() {
        if (subTasks.isEmpty()) {
            this.status = Status.NEW;
            return;
        }
        int countDone = 0;
        int countNew = 0;
        for (SubTask subTask : subTasks) {
            if (subTask.getStatus() == Status.DONE) {
                countDone++;
            }
            if (subTask.getStatus() == Status.NEW) {
                countNew++;
            }
        }
        if (countDone == subTasks.size()) {
            this.status = Status.DONE;
            return;
        }
        if (countNew == subTasks.size()) {
            this.status = Status.NEW;
            return;
        }
        this.status = Status.IN_PROGRESS;
    }

    public boolean addSubTask(SubTask subTask) {
        if (!subTask.getEpicId().equals(this.id)) {
            System.out.println("Подзадача относится к другому эпику");
            return false;
        }
        subTasks.add(subTask);
        return true;
    }

    public void clearSubTasks() {
        subTasks.clear();
    }

    public boolean removeSubTask(SubTask subTask) {
        if (!subTask.getEpicId().equals(this.id)) {
            System.out.println("Подзадача относится к другому эпику");
            return false;
        }
        subTasks.remove(subTask);
        return true;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", subTasks=" + subTasks +
                '}';
    }
}
