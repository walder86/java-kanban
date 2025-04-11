package manager;

import enumeration.Status;
import enumeration.TaskType;
import exception.ManagerSaveException;
import model.Epic;
import model.SubTask;
import model.Task;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class FileBackedTasksManager extends InMemoryTaskManager {

    private static final String PATH = "tasks_file.csv";
    private File file = new File(PATH);
    public static final String COMMA_SEPARATOR = ",";

    public FileBackedTasksManager() {
        super();
    }

    public FileBackedTasksManager(File file) {
        super();
        this.file = file;
    }

    private void save() {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            bufferedWriter.write("id,type,name,status,description,epic\n"); // Запись шапки с заголовками в файл
            for (Task task : getAllTasks()) {
                bufferedWriter.write(toString(task));
            }
            for (Epic epic : getAllEpics()) {
                bufferedWriter.write(toString(epic));
            }
            for (SubTask subTask : getAllSubTasks()) {
                bufferedWriter.write(toString(subTask));
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка во время записи файла");
        }
    }

    private String toString(Task task) {
        return task.getId() + "," +
                getType(task).toString() + "," +
                task.getName() + "," +
                task.getStatus().toString() + "," +
                task.getDescription() + "," +
                getParentEpicId(task) + "\n";
    }

    private TaskType getType(Task task) {
        if (task instanceof Epic) {
            return TaskType.EPIC;
        } else if (task instanceof SubTask) {
            return TaskType.SUBTASK;
        }
        return TaskType.TASK;
    }

    private String getParentEpicId(Task task) {
        if (task instanceof SubTask) {
            return Integer.toString(((SubTask) task).getEpicId());
        }
        return "";
    }

    public FileBackedTasksManager loadFromFile(File file) {

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {

            String line = bufferedReader.readLine();
            Integer maxId = 0;
            while (bufferedReader.ready()) {
                line = bufferedReader.readLine();
                if (line.isEmpty()) {
                    break;
                }

                Task task = fromString(line);

                if (task instanceof Epic epic) {
                    addEpicClone(epic);
                } else if (task instanceof SubTask subtask) {
                    addSubTaskClone(subtask);
                } else {
                    addTaskClone(task);
                }

                if (task.getId() > maxId) {
                    maxId = task.getId();
                }
            }

            setCountTasks(++maxId);
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка во время чтения файла!");
        }
        return this;
    }

    private static Task fromString(String value) {
        String[] params = value.split(COMMA_SEPARATOR);
        int id = Integer.parseInt(params[0]);
        String type = params[1];
        String name = params[2];
        Status status = Status.valueOf(params[3].toUpperCase());
        String description = params[4];
        Integer epicId = type.equals("SUBTASK") ? Integer.parseInt(params[5]) : null;

        if (type.equals("EPIC")) {
            Epic epic = new Epic(name, description);
            epic.setId(id);
            epic.setStatus(status);
            return epic;
        } else if (type.equals("SUBTASK")) {
            SubTask subtask = new SubTask(name, description, epicId, status);
            subtask.setId(id);
            return subtask;
        } else {
            Task task = new Task(name, description, status);
            task.setId(id);
            return task;
        }
    }

    // Переопределение методов
    @Override
    public Boolean addTask(Task task) {
        if (super.addTask(task)) {
            save();
            return true;
        }
        return false;
    }

    @Override
    public Boolean addEpic(Epic epic) {
        if (super.addEpic(epic)) {
            save();
            return true;
        }
        return false;
    }

    @Override
    public Boolean addSubTask(SubTask subTask) {
        if (super.addSubTask(subTask)) {
            save();
            return true;
        }
        return false;
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllSubTasks() {
        super.removeAllSubTasks();
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public Boolean updateTask(Task task) {
        if (super.updateTask(task)) {
            save();
            return true;
        }
        return false;
    }

    @Override
    public Boolean updateSubTask(SubTask subTask) {
        if (super.updateSubTask(subTask)) {
            save();
            return true;
        }
        return false;
    }

    @Override
    public Boolean updateEpic(Epic epic) {
        if (super.updateEpic(epic)) {
            save();
            return true;
        }
        return false;
    }

    @Override
    public void removeTaskById(Integer taskId) {
        super.removeTaskById(taskId);
        save();
    }

    @Override
    public void removeSubTaskById(Integer subTaskId) {
        super.removeSubTaskById(subTaskId);
        save();
    }

    @Override
    public void removeEpicById(Integer epicId) {
        super.removeEpicById(epicId);
        save();
    }

}
