package um.os.scheduler.core;

import um.os.scheduler.task.Task;
import um.os.scheduler.task.TaskCallback;
import um.os.scheduler.timeunit.TimeUnitObservable;

public class Processor {

    private final Thread thread;
    private final TaskCallback taskCallback;

    private volatile Task currentTask;
    private volatile boolean shouldRunThread = false;

    public Processor(TaskCallback callback) {
        this.taskCallback = callback;

        TimeUnitObservable.getInstance().addObserver(() -> {
            shouldRunThread = true;
        });

        thread = new Thread(() -> {
            while(true) {
                if(shouldRunThread) {
                    process();
                    shouldRunThread = false;
                }
            }
        });
    }

    private void process() {
        if(currentTask == null) {
            currentTask = taskCallback.nextTask();
        } else if(currentTask.isFinished()) {
            taskCallback.onTaskFinished(currentTask);
            currentTask = taskCallback.nextTask();
        } else {
            currentTask.decrementDuration();
            taskCallback.onExecuteOneTimeUnit(currentTask);
        }
    }

    public void moveToNextTask() {
        currentTask = null;
        currentTask = taskCallback.nextTask();
    }

    public boolean isIdle() {
        return currentTask == null;
    }

    public Task getCurrentTask() {
        return currentTask;
    }

    public void run() {
        thread.start();
    }

}
