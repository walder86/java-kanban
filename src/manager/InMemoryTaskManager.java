package manager;

import typeTasks.Epic;
import typeTasks.SubTask;
import typeTasks.Task;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    private Integer countTasks = 1;

    private Map<Integer, Epic> epics;
    private Map<Integer, Task> tasks;
    private Map<Integer, SubTask> subTasks;

    HistoryManager historyManager;

    public InMemoryTaskManager() {
        this.epics = new HashMap<>();
        this.tasks = new HashMap<>();
        this.subTasks = new HashMap<>();
        this.historyManager = Manager.getDefaultHistoryManager();
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Task> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public List<SubTask> getAllSubTasksForEpic(Integer epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            System.out.println("Эпик не найден по ID = " + epicId);
            return new ArrayList<>();
        }
        return epic.getSubTasks();
    }

    @Override
    public boolean addTask(Task task) {
        task.setId(countTasks++);
        tasks.put(task.getId(), task);
        System.out.println("Задача успешно добавлена");
        return true;
    }

    @Override
    public boolean addEpic(Epic epic) {
        epic.setId(countTasks++);
        epics.put(epic.getId(), epic);
        System.out.println("Эпик успешно добавлен");
        return true;
    }

    @Override
    public boolean addSubTask(SubTask subTask) {
        Epic epic = this.epics.get(subTask.getEpicId());
        if (epic != null) {
            subTask.setId(countTasks++);
            epic.addSubTask(subTask);
            subTasks.put(subTask.getId(), subTask);
            epic.changeStatus();
            System.out.println("Подзадача успешно добавлена");
            return true;
        } else {
            System.out.println("Эпик не найден");
            return false;
        }
    }

    @Override
    public Task getTaskById(Integer taskId) {
        Task task = tasks.get(taskId);
        historyManager.addTask(task);
        return task;
    }

    @Override
    public Epic getEpicById(Integer epicId) {
        Epic epic = epics.get(epicId);
        historyManager.addTask(epic);
        return epic;
    }

    @Override
    public SubTask getSubTaskById(Integer subTaskId) {
        SubTask subTask = subTasks.get(subTaskId);
        historyManager.addTask(subTask);
        return subTask;
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.get(task.getId()) == null) {
            System.out.println("Задача не найдена. Для добавления воспользуйтесь другим методом");
            return;
        }
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateEpic(Epic epic) {
        Epic epicForUpdate = epics.get(epic.getId());
        if (epicForUpdate == null) {
            System.out.println("Эпик не найден. Для добавления воспользуйтесь другим методом");
            return;
        }
        epicForUpdate.setName(epic.getName());
        epicForUpdate.setDescription(epic.getDescription());
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        SubTask existingSubTask = subTasks.get(subTask.getId());
        if (existingSubTask == null) {
            System.out.println("Подзадача не найдена. Для добавления воспользуйтесь другим методом");
            return;
        }
        if (!subTask.getEpicId().equals(existingSubTask.getEpicId())) {
            System.out.println("Данная подзадача относится к другому эпику");
            return;
        }

        Epic epic = epics.get(subTask.getEpicId());
        //подзадача не может существовать без эпика, поэтому исключается NPE
        List<SubTask> epicSubTasks = epic.getSubTasks();
        epicSubTasks.remove(existingSubTask);
        epicSubTasks.add(subTask);

        //не обновляем id, так как они равны (первое условие)
        //не обновляем epicId, так как они равны
        existingSubTask.setName(subTask.getName());
        existingSubTask.setDescription(subTask.getDescription());
        existingSubTask.setStatus(subTask.getStatus());

        epic.changeStatus();
    }

    @Override
    public void removeAllTasks() {
        tasks.clear();
        System.out.println("Все задачи удалены");
    }

    @Override
    public void removeAllEpics() {
        epics.clear();
        subTasks.clear();
        System.out.println("Все эпики удалены");
    }

    @Override
    public void removeAllSubTasks() {
        for (Epic epic : epics.values()) {
            epic.clearSubTasks();
            epic.changeStatus();
        }
        subTasks.clear();
        System.out.println("Все подзадачи удалены");
    }

    @Override
    public void removeTaskById(Integer taskId) {
        tasks.remove(taskId);
        System.out.println("Задача с ID = " + taskId + " была удалена");
    }

    @Override
    public void removeEpicById(Integer epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            System.out.println("Эпик не найден");
            return;
        }
        for (SubTask subTask : epic.getSubTasks()) {
            subTasks.remove(subTask.getId());
        }
        epics.remove(epicId);
        System.out.println("Эпик с ID = " + epicId + " был удален");
    }

    @Override
    public void removeSubTaskById(Integer subTaskId) {
        SubTask subTask = subTasks.get(subTaskId);
        if (subTask == null) {
            System.out.println("Подзадача не найдена");
            return;
        }
        Epic epic = epics.get(subTask.getEpicId());
        //подзадача не может существовать без эпика, поэтому исключается NPE
        epic.removeSubTask(subTask);
        epic.changeStatus();
        subTasks.remove(subTaskId);
        System.out.println("Подзадача с ID = " + subTaskId + " была удалена");
    }

}
