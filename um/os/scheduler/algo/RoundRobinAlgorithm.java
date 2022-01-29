package um.os.scheduler.algo;

import um.os.scheduler.core.Scheduler;
import um.os.scheduler.task.Task;
import um.os.scheduler.timeunit.TimeUnitObservable;

import java.util.HashMap;
import java.util.Map;

public class RoundRobinAlgorithm extends FirstComeFirstServeAlgorithm implements PreemptiveSchedulingAlgorithm {

    private final Map<Task, Integer> map;
    private final int timeSlice;
    private Scheduler scheduler;

    private final Object mapLock = new Object();

    public RoundRobinAlgorithm(int timeSlice) {
        this.timeSlice = timeSlice;
        map = new HashMap<>();
    }

    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Override
    public boolean onExecuteOneTimeUnit(Task task) {
        synchronized (mapLock) {
            Integer executionTime = map.get(task);

            if(executionTime == null)
                executionTime = 0;

            if(executionTime == timeSlice) {
                map.put(task, 0);
                int currentTime = TimeUnitObservable.getInstance().getCurrentTime();

                task.setEnterTime(currentTime);
                scheduler.pushToReadyQueue(task);
                return true;
            } else {
                map.put(task, executionTime + 1);
            }

            return false;
        }
    }

}
