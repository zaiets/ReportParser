import java.util.*;

/**
 * Created by Levsha on 25.04.2016.
 */
public class Report {
    private TaskType stage;
    private String department;
    private List<Task> tasks;

    /*Для обобщенного отчета*/
    public Report(TaskType stage, List<Task> tasks) {
        this.stage = stage;
        this.department = "Отдел ЭТО";
        this.tasks = tasks;
    }

    public TaskType getStage() {
        return stage;
    }

    public void setStage(TaskType stage) {
        this.stage = stage;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public Map<String, List<Task>> getTaskMapByObjTag() {
        Map<String, List<Task>> map = new HashMap<>();
        Set<String> keyset = new HashSet<>();
        tasks.forEach(task -> keyset.add(task.getContractTag()));
        for (String key : keyset) {
            List<Task> currentTaskList = new ArrayList<>();
            for (Task task : tasks) {
                if (task.getContractTag().equals(key)) currentTaskList.add(task);
            }
            map.put(key, currentTaskList);
        }
        return map;
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Report{");
        sb.append("stage=").append(stage);
        sb.append(", department='").append(department).append('\'');
        sb.append(", tasks=").append(tasks);
        sb.append('}');
        return sb.toString();
    }
}
