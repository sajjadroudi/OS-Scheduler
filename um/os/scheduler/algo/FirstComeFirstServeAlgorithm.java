package um.os.scheduler.algo;

import um.os.scheduler.task.Task;

public class FirstComeFirstServeAlgorithm implements SchedulingAlgorithm {

    @Override
    public int compareTwoItems(Task o1, Task o2) {
        return Integer.compare(o1.getEnterTime(), o2.getEnterTime());
    }

}
