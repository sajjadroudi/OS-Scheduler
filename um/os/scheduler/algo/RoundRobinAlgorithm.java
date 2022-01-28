package um.os.scheduler.algo;

import um.os.scheduler.task.Task;

import java.util.HashMap;
import java.util.Map;

// TODO
public class RoundRobinAlgorithm implements SchedulingAlgorithm {

    private final Map<Task, Integer> map;
    private final int timeSlice;

    public RoundRobinAlgorithm(int timeSlice) {
        map = new HashMap<>();
        this.timeSlice = timeSlice;
    }

    @Override
    public int compare(Task o1, Task o2) {
        Integer executedTime1 = map.get(o1);
        Integer executedTime2 = map.get(o1);

        if(executedTime1 == null && executedTime2 == null) {
            return Integer.compare(o1.getEnterTime(), o2.getEnterTime());
        } else if(executedTime1 == null) {

        } else if(executedTime2 == null) {

        } else {
            return Integer.compare(executedTime1, executedTime2);
        }

        return 0;
    }

    private void reset(Task task) {
        map.put(task, 0);
    }

}
