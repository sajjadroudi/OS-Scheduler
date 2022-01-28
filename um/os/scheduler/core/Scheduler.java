package um.os.scheduler.core;

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

    public Scheduler(SchedulingAlgorithm algorithm) {
        ready = new PriorityQueue<>(algorithm);
        waiting = new LinkedList<>();
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
                    .collect(Collectors.toList())
                    .toArray(new String[0]);
        }
    }

    public String[] getWaitingTaskNames() {
        synchronized (waitingQueueLock) {
            return waiting.stream()
                    .map(Task::getName)
                    .collect(Collectors.toList())
                    .toArray(new String[0]);
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
