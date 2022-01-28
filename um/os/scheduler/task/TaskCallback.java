package um.os.scheduler.task;

import um.os.scheduler.task.Task;

public interface TaskCallback {
    void onTaskFinished(Task task);
    Task nextTask();
}