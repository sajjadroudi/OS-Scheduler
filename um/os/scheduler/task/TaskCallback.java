package um.os.scheduler.task;

public interface TaskCallback {
    void onTaskFinished(Task task);
    Task nextTask();
    void onExecuteOneTimeUnit(Task task);
}