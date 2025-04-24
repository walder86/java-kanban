package manager;

import enumeration.Status;
import enumeration.TaskType;
import exception.ManagerSaveException;
import model.Epic;
import model.SubTask;
import model.Task;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

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
            bufferedWriter.write("id,type,name,status,description,epic,startTime,duration\n"); // Запись шапки с заголовками в файл
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
                getParentEpicId(task) + "," +
                task.getStartTime() + "," +
                (task.getDuration() == null ? ",\n" : task.getDuration().getSeconds() / 60 + "\n");
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

        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(file);
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
                    fileBackedTasksManager.addEpicClone(epic);
                } else if (task instanceof SubTask subtask) {
                    fileBackedTasksManager.addSubTaskClone(subtask);
                } else {
                    fileBackedTasksManager.addTaskClone(task);
                }

                if (task.getId() > maxId) {
                    maxId = task.getId();
                }
            }

            fileBackedTasksManager.setCountTasks(++maxId);
        } catch (FileNotFoundException e) {
            System.out.println("Файл не найден. Убедитесь в правильности указанного пути.");
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка во время чтения файла!");
        }
        return fileBackedTasksManager;
    }

    private static Task fromString(String value) {
        String[] params = value.split(COMMA_SEPARATOR);
        int id = Integer.parseInt(params[0]);
        String type = params[1];
        String name = params[2];
        Status status = Status.valueOf(params[3].toUpperCase());
        String description = params[4];
        Integer epicId = type.equals("SUBTASK") ? Integer.parseInt(params[5]) : null;
        LocalDateTime startTime = LocalDateTime.parse(params[6]);
        Duration duration = Duration.ofMinutes(Integer.parseInt(params[7]));

        if (type.equals("EPIC")) {
            Epic epic = new Epic(name, description);
            epic.setId(id);
            epic.setStatus(status);
            return epic;
        } else if (type.equals("SUBTASK")) {
            SubTask subtask = new SubTask(name, description, epicId, status, startTime, duration);
            subtask.setId(id);
            return subtask;
        } else {
            Task task = new Task(name, description, status, startTime, duration);
            task.setId(id);
            return task;
        }
    }

    // Переопределение методов
    @Override
    public Task addTask(Task task) {
        Task taskNew = super.addTask(task);
        if (taskNew != null) {
            save();
            return taskNew;
        }
        return null;
    }

    @Override
    public Epic addEpic(Epic epic) {
        Epic epicNew = super.addEpic(epic);
        save();
        return  epicNew;
    }

    @Override
    public SubTask addSubTask(SubTask subTask) {
        SubTask subTaskNew = super.addSubTask(subTask);
        if (subTaskNew != null) {
            save();
            return subTaskNew;
        }
        return null;
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
