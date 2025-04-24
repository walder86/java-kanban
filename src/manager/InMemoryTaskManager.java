package manager;

import exception.ManagerValidateException;
import model.Epic;
import model.SubTask;
import model.Task;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    private Integer countTasks = 1;

    private Map<Integer, Epic> epics;
    private Map<Integer, Task> tasks;
    private Map<Integer, SubTask> subTasks;

    protected Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));

    HistoryManager historyManager;

    public InMemoryTaskManager() {
        this.epics = new HashMap<>();
        this.tasks = new HashMap<>();
        this.subTasks = new HashMap<>();
        this.historyManager = Manager.getDefaultHistoryManager();
    }

    protected void setCountTasks(Integer countTasks) {
        this.countTasks = countTasks;
    }

    @Override
    public List<Task> getHistory() {
        return this.historyManager.getHistory();
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
    public List<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public List<SubTask> getAllSubTasksForEpic(Integer epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            System.out.println("Эпик не найден по ID = " + epicId);
            return new ArrayList<>();
        }
        return new ArrayList<>(epic.getSubTasks());
    }

    @Override
    public Task addTask(Task task) {
        if (task.getClass() == Task.class) {
            validateTaskPriority(task);
            task.setId(countTasks++);
            addNewPrioritizedTask(task.clone());
            tasks.put(task.getId(), task.clone());
            System.out.println("Задача успешно добавлена");
            return task;
        } else {
            return null;
        }
    }

    @Override
    public Epic addEpic(Epic epic) {
        epic.setId(countTasks++);
        epics.put(epic.getId(), epic.clone());
        System.out.println("Эпик успешно добавлен");
        return epic;
    }

    @Override
    public SubTask addSubTask(SubTask subTask) {
        Epic epic = this.epics.get(subTask.getEpicId());
        if (epic != null) {
            validateTaskPriority(subTask);
            subTask.setId(countTasks++);
            epic.addSubTask(subTask.clone());
            addNewPrioritizedTask(subTask.clone());
            subTasks.put(subTask.getId(), subTask);
            epic.changeStatus();
            epic.changeTime();
            System.out.println("Подзадача успешно добавлена");
            return subTask;
        } else {
            System.out.println("Эпик не найден");
            return null;
        }
    }

    @Override
    public Task getTaskById(Integer taskId) {
        Task task = tasks.get(taskId);
        if (task != null) {
            historyManager.addTask(task);
            return task;
        }
        return null;
    }

    @Override
    public Epic getEpicById(Integer epicId) {
        Epic epic = epics.get(epicId);
        if (epic != null) {
            historyManager.addTask(epic);
            return epic;
        }
        return null;
    }

    @Override
    public SubTask getSubTaskById(Integer subTaskId) {
        SubTask subTask = subTasks.get(subTaskId);
        if (subTask != null) {
            historyManager.addTask(subTask);
            return subTask;
        }
        return null;
    }

    @Override
    public Boolean updateTask(Task task) {
        if (task.getClass() != Task.class) {
            System.out.println("Нельзя обновлять эпики и задачи в методе обновления задач");
            return false;
        }
        if (tasks.get(task.getId()) == null) {
            System.out.println("Задача не найдена. Для добавления воспользуйтесь другим методом");
            return false;
        }
        validateTaskPriority(task);
        tasks.put(task.getId(), task);
        removePrioritizedTask(task);
        addNewPrioritizedTask(task.clone());
        return true;
    }

    @Override
    public Boolean updateEpic(Epic epic) {
        Epic epicForUpdate = epics.get(epic.getId());
        if (epicForUpdate == null) {
            System.out.println("Эпик не найден. Для добавления воспользуйтесь другим методом");
            return false;
        }
        epicForUpdate.setName(epic.getName());
        epicForUpdate.setDescription(epic.getDescription());
        return true;
    }

    @Override
    public Boolean updateSubTask(SubTask subTask) {
        SubTask existingSubTask = subTasks.get(subTask.getId());
        if (existingSubTask == null) {
            System.out.println("Подзадача не найдена. Для добавления воспользуйтесь другим методом");
            return false;
        }
        if (!subTask.getEpicId().equals(existingSubTask.getEpicId())) {
            System.out.println("Данная подзадача относится к другому эпику");
            return false;
        }

        validateTaskPriority(subTask);

        Epic epic = epics.get(subTask.getEpicId());
        //подзадача не может существовать без эпика, поэтому исключается NPE
        epic.removeSubTask(subTask);
        epic.addSubTask(subTask.clone());

        //id и epicId равны, поэтому изменяться только имя, описание и статус
        subTasks.put(subTask.getId(), subTask.clone());

        removePrioritizedTask(subTask);
        addNewPrioritizedTask(subTask.clone());

        epic.changeStatus();
        epic.changeTime();
        return true;
    }

    @Override
    public void removeAllTasks() {
        tasks.values()
                .forEach(this::removePrioritizedTask);
        tasks.clear();
        historyManager.removeAllTasks();
        System.out.println("Все задачи удалены");
    }

    @Override
    public void removeAllEpics() {
        epics.clear();
        subTasks.values()
                .forEach(this::removePrioritizedTask);
        subTasks.clear();
        historyManager.removeAllEpics();
        System.out.println("Все эпики удалены");
    }

    @Override
    public void removeAllSubTasks() {
        for (Epic epic : epics.values()) {
            epic.clearSubTasks();
            epic.changeStatus();
            epic.changeTime();
        }
        subTasks.values()
                .forEach(this::removePrioritizedTask);
        subTasks.clear();
        historyManager.removeAllSubTasks();
        System.out.println("Все подзадачи удалены");
    }

    @Override
    public void removeTaskById(Integer taskId) {
        removePrioritizedTask(tasks.get(taskId));
        tasks.remove(taskId);
        historyManager.remove(taskId);
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
            prioritizedTasks.remove(subTask);
            subTasks.remove(subTask.getId());
            historyManager.remove(subTask.getId());
        }
        epics.remove(epicId);
        historyManager.remove(epicId);
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
        epic.changeTime();
        removePrioritizedTask(subTasks.get(subTaskId));
        subTasks.remove(subTaskId);
        historyManager.remove(subTaskId);
        System.out.println("Подзадача с ID = " + subTaskId + " была удалена");
    }

    protected void addTaskClone(Task task) {
        tasks.put(task.getId(), task.clone());
        addNewPrioritizedTask(task.clone());
    }

    protected void addEpicClone(Epic epic) {
        epics.put(epic.getId(), epic.clone());
    }

    protected void addSubTaskClone(SubTask subTask) {
        Epic epic = this.epics.get(subTask.getEpicId());
        if (epic != null) {
            epic.addSubTask(subTask.clone());
        } else {
            System.out.println("Подзадача: \n" + subTask + "\n не привязана ни к одному Эпику. Необходимо добавить подзадачу в один иэ Эпиков");
        }
        //сделано добавление, так как можно обратиться к подзадаче и привязать её к нужному эпику
        addNewPrioritizedTask(subTask.clone());
        subTasks.put(subTask.getId(), subTask.clone());
    }

    private void addNewPrioritizedTask(Task task) {
        prioritizedTasks.add(task);
    }

    private void validateTaskPriority(Task task) {
        List<Task> tasks = getPrioritizedTasks();

        if (!tasks.isEmpty()) {
            for (Task prioritizedTask : tasks) {
                //равенство может быть только в случае обновления,
                //чтобы одна и та же задача не рассматривались на пересечение
                if (Objects.equals(task.getId(), prioritizedTask.getId())) {
                    continue;
                }
                boolean validate = checkTime(prioritizedTask, task);
                if (!validate) {
                    throw new ManagerValidateException(
                            "ВНИМАНИЕ! Новая задача пересекается с задачей " + prioritizedTask);
                }
            }
        }
    }

    public boolean checkTime(Task existTask, Task newTask) {
        if (newTask.getStartTime().isBefore(existTask.getStartTime())
                && newTask.getEndTime().isBefore(existTask.getStartTime())) {
            return true;
        } else if (newTask.getStartTime().isAfter(existTask.getEndTime())
                && newTask.getEndTime().isAfter(existTask.getEndTime())) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return prioritizedTasks.stream().toList();
    }

    public void removePrioritizedTask(Task task) {
        prioritizedTasks.remove(task);
    }

}
