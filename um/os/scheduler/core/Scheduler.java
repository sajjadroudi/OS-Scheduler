package um.os.scheduler.core;

import um.os.scheduler.algo.PreemptiveSchedulingAlgorithm;
import um.os.scheduler.algo.SchedulingAlgorithm;
import um.os.scheduler.resource.ResourceManager;
import um.os.scheduler.task.Task;

import java.util.*;
import java.util.stream.Collectors;

public class Scheduler {

    private final Queue<Task> ready;
    private final Queue<Task> waiting;

    private final Object readyQueueLock = new Object();
    private final Object waitingQueueLock = new Object();

    private final SchedulingAlgorithm schedulingAlgorithm;

    public Scheduler(SchedulingAlgorithm algorithm) {
        schedulingAlgorithm = algorithm;
        ready = new PriorityQueue<>(algorithm);
        waiting = new LinkedList<>();
    }

    public boolean onExecuteOneTimeUnit(Task task) {
        boolean kickOutOfRunning = false;
        if(schedulingAlgorithm instanceof PreemptiveSchedulingAlgorithm) {
            PreemptiveSchedulingAlgorithm algorithm = (PreemptiveSchedulingAlgorithm) schedulingAlgorithm;
            kickOutOfRunning = algorithm.onExecuteOneTimeUnit(task);
        }
        return kickOutOfRunning;
    }

    public Task nextReadyTask() {
        synchronized (readyQueueLock) {
            return ready.poll();
        }
    }

    public void pushToReadyQueue(Task... tasks) {
        synchronized (waitingQueueLock) {
            for(Task task : tasks) {
                waiting.remove(task);
            }
        }

        synchronized (readyQueueLock) {
            ready.addAll(Arrays.asList(tasks));
        }
    }

    public void pushToWaitingQueue(Task task) {
        synchronized (readyQueueLock) {
            ready.remove(task);
        }

        synchronized (waitingQueueLock) {
            waiting.add(task);
        }
    }

    public Task[] getWaitingTasks() {
        synchronized (waitingQueueLock) {
            return new ArrayList<>(waiting)
                    .toArray(new Task[0]);
        }
    }

    public String[] getReadyTaskNames() {
        synchronized (readyQueueLock) {
            return ready.stream()
                    .map(Task::getName)
                    .toArray(String[]::new);
        }
    }

    public String[] getWaitingTaskNames() {
        synchronized (waitingQueueLock) {
            return waiting.stream()
                    .map(Task::getName)
                    .toArray(String[]::new);
        }
    }

    public boolean thereIsNoTaskToExecute() {
        synchronized (readyQueueLock) {
            synchronized (waitingQueueLock) {
                return ready.isEmpty() && waiting.isEmpty();
            }
        }
    }

}
