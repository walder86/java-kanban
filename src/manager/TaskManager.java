package manager;

import typeTasks.Epic;
import typeTasks.SubTask;
import typeTasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskManager {

    public static Integer countTasks = 1;

    private Map<Integer, Epic> epics;
    private Map<Integer, Task> tasks;
    private Map<Integer, SubTask> subTasks;

    public TaskManager() {
        epics = new HashMap<>();
        tasks = new HashMap<>();
        subTasks = new HashMap<>();
    }

    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public List<Task> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    public List<SubTask> getAllSubTasksForEpic(Integer epicId) {
        return epics.get(epicId).getSubTasks();
    }

    public boolean addTask(Task task) {
        tasks.put(task.getId(), task);
        System.out.println("Задача успешно добавлена");
        return true;
    }

    public boolean addEpic(Epic epic) {
        tasks.put(epic.getId(), epic);
        System.out.println("Эпик успешно добавлен");
        return true;
    }

    public boolean addSubTask(SubTask subTask, Integer epicId) {
        Epic epic = this.epics.get(epicId);
        if (epic != null) {
            epic.getSubTasks().add(subTask);
            System.out.println("Подзадача успешно добавлена");
            return true;
        } else {
            System.out.println("Эпик не найден");
            return false;
        }
    }

    public Task getTaskById(Integer taskId) {
        return tasks.get(taskId);
    }

    public Epic getEpicById(Integer epicId) {
        return epics.get(epicId);
    }

    public SubTask getSubTaskById(Integer subTaskId) {
        return subTasks.get(subTaskId);
    }

    public void updateTask(Task task) {
        Task taskForUpdate = tasks.get(task.getId());
        taskForUpdate.setName(task.getName());
        taskForUpdate.setDescription(task.getDescription());
        taskForUpdate.setStatus(task.getStatus());
    }

    public void updateEpic(Epic epic) {
        Epic epicForUpdate = epics.get(epic.getId());
        epicForUpdate.setName(epic.getName());
        epicForUpdate.setDescription(epic.getDescription());
        epicForUpdate.setSubTasks(epic.getSubTasks());
        epicForUpdate.setStatus();
    }

    public void updateSubTask(SubTask subTask) {
        SubTask subTaskForUpdate = subTasks.get(subTask.getId());
        subTaskForUpdate.setName(subTask.getName());
        subTaskForUpdate.setDescription(subTask.getDescription());
        subTaskForUpdate.setStatus(subTask.getStatus());
        epics.get(subTask.getEpicId()).setStatus();
    }

}
