package um.os.scheduler.core;

import um.os.scheduler.resource.ResourceManager;
import um.os.scheduler.task.Task;
import um.os.scheduler.task.TaskCallback;
import um.os.scheduler.timeunit.TimeUnitObservable;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class Cpu {

    private final Scheduler scheduler;
    private final Processor[] processors;
    private final ResourceManager resourceManager;

    private final Object resourceManagerLock = new Object();
    private final Object processorsLock = new Object();
    private final Object schedulerLock = new Object();

    public Cpu(int processorCount, Scheduler scheduler, ResourceManager resourceManager) {
        this.scheduler = scheduler;
        this.resourceManager = resourceManager;

        processors = new Processor[processorCount];
        setupProcessors();
    }

    private void setupProcessors() {
        for(int i = 0; i < processors.length; i++) {
            processors[i] = new Processor(new TaskCallback() {
                @Override
                public void onTaskFinished(Task task) {
                    freeResources(task);
                }

                @Override
                public Task nextTask() {
                    synchronized (resourceManagerLock) {
                        Task nextTask;
                        synchronized (schedulerLock) {
                            nextTask = getNextReadyTask();
                        }

                        if(nextTask != null) {
                            resourceManager.takeNeededResources(nextTask);
                        }

                        return nextTask;
                    }
                }

                private Task getNextReadyTask() {
                    Task[] poorTasks = scheduler.getQualifiedTasksToPreventStarvation();

                    for(Task task : poorTasks) {
                        if(resourceManager.areAllNeededResourcesAvailable(task)) {
                            return task;
                        }
                    }

                    Task nextTask = scheduler.nextReadyTask();

                    while(nextTask != null && !resourceManager.areAllNeededResourcesAvailable(nextTask)) {
                        scheduler.pushToWaitingQueue(nextTask);
                        nextTask = scheduler.nextReadyTask();
                    }

                    return nextTask;
                }

                @Override
                public void onExecuteOneTimeUnit(Task task) {
                    boolean kickOutOfRunning;
                    synchronized (schedulerLock) {
                        kickOutOfRunning = scheduler.onExecuteOneTimeUnit(task);
                    }
                    if(kickOutOfRunning) {
                        Processor processor = findProcessor(task);
                        if(processor != null) {
                            freeResources(task);
                            processor.moveToNextTask();
                        }
                    }
                }

                private void freeResources(Task task) {
                    synchronized (resourceManagerLock) {
                        resourceManager.freeTakenResources(task);
                        synchronized (schedulerLock) {
                            moveQualifiedTasksToReadyQueue();
                        }
                    }
                }

                private void moveQualifiedTasksToReadyQueue() {
                    for(Task task : scheduler.getWaitingTasks()) {
                        if(resourceManager.areAllNeededResourcesAvailable(task)) {
                            scheduler.pushToReadyQueue(task);
                        }
                    }
                }

                private Processor findProcessor(Task task) {
                    synchronized (processorsLock) {
                        for(Processor processor : processors) {
                            if(processor.getCurrentTask() == task) {
                                return processor;
                            }
                        }
                        return null;
                    }
                }
            });
        }
    }

    public void assignTask(Task task) {
        boolean areAllNeededResourcesAvailable;
        synchronized (resourceManagerLock) {
            areAllNeededResourcesAvailable = resourceManager.areAllNeededResourcesAvailable(task);
        }

        synchronized (schedulerLock) {
            if(areAllNeededResourcesAvailable) {
                scheduler.pushToReadyQueue(task);
            } else {
                scheduler.pushToWaitingQueue(task);
            }
        }
    }

    public boolean areAllTasksFinished() {
        boolean allProcessorsAreIdle;
        synchronized (processorsLock) {
            allProcessorsAreIdle = Arrays
                .stream(processors)
                .allMatch(Processor::isIdle);
        }

        boolean thereIsNoTaskToExecute;
        synchronized (schedulerLock) {
            thereIsNoTaskToExecute = scheduler.thereIsNoTaskToExecute();
        }

        return allProcessorsAreIdle && thereIsNoTaskToExecute;
    }

    public void run() {
        synchronized (processorsLock) {
            for(Processor processor : processors)
                processor.run();
        }
    }

    public String getSystemStatus() {
        StringBuilder builder = new StringBuilder();

        builder.append("Time: ")
                .append(TimeUnitObservable.getInstance().getCurrentTime())
                .append("\n");

        synchronized (resourceManagerLock) {
            for(ResourceManager.ResourceStatus status : resourceManager.getResourceStatuses()) {
                builder
                        .append(status.getResourceType())
                        .append(": ")
                        .append(status.getFreeCount())
                        .append("\t");
            }
        }
        builder.append("\n");

        builder.append("priority queue:\t");
        synchronized (schedulerLock) {
            for(String taskName : scheduler.getReadyTaskNames()) {
                builder.append(taskName)
                        .append("-");
            }
        }
        builder.append("\n");

        builder.append("waiting queue:\t");
        synchronized(schedulerLock) {
            for(String taskName : scheduler.getWaitingTaskNames()) {
                builder.append(taskName)
                        .append("-");
            }
        }
        builder.append("\n");

        synchronized (processorsLock) {
            for (int i = 0; i < processors.length; i++) {
                builder.append("CPU")
                        .append(i + 1)
                        .append(": ");

                if(processors[i].isIdle()) {
                    builder.append("Idle");
                } else {
                    builder.append(processors[i].getCurrentTask().getName());
                }

                builder.append("\n");
            }
        }

        return builder.toString();
    }

}
