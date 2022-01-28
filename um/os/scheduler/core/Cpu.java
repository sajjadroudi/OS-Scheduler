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
                    resourceManager.freeTakenResources(task);
                    moveQualifiedTasksToReadyQueue();
                }

                private void moveQualifiedTasksToReadyQueue() {
                    for(Task task : scheduler.getWaitingTasks()) {
                        if(resourceManager.areAllNeededResourcesAvailable(task)) {
                            scheduler.pushToReadyQueue(task);
                        }
                    }
                }

                @Override
                public Task nextTask() {
                    Task nextTask = getNextReadyTask();

                    if(nextTask != null) {
                        resourceManager.takeNeededResources(nextTask);
                    }

                    return nextTask;
                }

                private Task getNextReadyTask() {
                    Task nextTask = scheduler.nextReadyTask();

                    while(nextTask != null && !resourceManager.areAllNeededResourcesAvailable(nextTask)) {
                        scheduler.pushToWaitingQueue(nextTask);
                        nextTask = scheduler.nextReadyTask();
                    }

                    return nextTask;
                }

            });
        }
    }

    public void assignTask(Task task) {
        if(resourceManager.areAllNeededResourcesAvailable(task)) {
            scheduler.pushToReadyQueue(task);
        } else {
            scheduler.pushToWaitingQueue(task);
        }
    }

    public boolean areAllTasksFinished() {
        boolean allProcessorsAreIdle = Arrays
                .stream(processors)
                .allMatch(Processor::isIdle);

        return allProcessorsAreIdle && scheduler.thereIsNoTaskToExecute();
    }

    public void run() {
        for(Processor processor : processors)
            processor.run();
    }

    public String getSystemStatus() {
        StringBuilder builder = new StringBuilder();

        builder.append("Time: ")
                .append(TimeUnitObservable.getInstance().getCurrentTime())
                .append("\n");

        for(ResourceManager.ResourceStatus status : resourceManager.getResourceStatuses()) {
            builder
                    .append(status.getResourceType())
                    .append(": ")
                    .append(status.getFreeCount())
                    .append("\t");
        }
        builder.append("\n");

        builder.append("priority queue:\t");
        for(String taskName : scheduler.getReadyTaskNames()) {
            builder.append(taskName)
                    .append("-");
        }
        builder.append("\n");

        builder.append("waiting queue:\t");
        for(String taskName : scheduler.getWaitingTaskNames()) {
            builder.append(taskName)
                    .append("-");
        }
        builder.append("\n");

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

        return builder.toString();
    }

}
