package model;

import enumeration.Status;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private List<SubTask> subTasks;

    private Epic() {
    }

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
        this.subTasks = new ArrayList<>();
    }

    public List<SubTask> getSubTasks() {
        return new ArrayList<>(subTasks);
    }

    private void setSubTasks(List<SubTask> subTasks) {
        this.subTasks = subTasks;
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
        SubTask subTaskForDelete = null;
        for (SubTask subTaskIter : subTasks) {
            if (subTaskIter.getId().equals(subTask.getId())) {
                subTaskForDelete = subTaskIter;
                break;
            }
        }
        if (subTaskForDelete == null) {
            System.out.println("Не была найдена задача по ИД для эпика");
            return false;
        }
        subTasks.remove(subTaskForDelete);
        return true;
    }

    public Epic clone() {
        Epic epic = new Epic();
        epic.setId(this.id);
        epic.setName(this.name);
        epic.setDescription(this.description);
        epic.setStatus(this.status);
        epic.setSubTasks(this.subTasks);
        return epic;
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
