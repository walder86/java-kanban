package model;

import enumeration.Status;

public class SubTask extends Task{

    private Integer epicId;

    private SubTask() {

    }

    public SubTask(String name, String description, Integer epicId, Status status) {
        super(name, description, status);
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
