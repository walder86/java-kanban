package model;

import enumeration.Status;

import java.time.Duration;
import java.time.LocalDateTime;

public class SubTask extends Task {

    private Integer epicId;

    private SubTask() {

    }

    public SubTask(String name, String description, Integer epicId, Status status, LocalDateTime startTime, Duration duration) {
        super(name, description, status, startTime, duration);
        this.epicId = epicId;
    }

    public Integer getEpicId() {
        return epicId;
    }

    private void setEpicId(Integer epicId) {
        this.epicId = epicId;
    }

    public SubTask clone() {
        SubTask subTask = new SubTask();
        subTask.setId(this.id);
        subTask.setName(this.name);
        subTask.setDescription(this.description);
        subTask.setStatus(this.status);
        subTask.setEpicId(this.epicId);
        subTask.setStartTime(this.startTime);
        subTask.setDuration(this.duration);
        return subTask;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}
