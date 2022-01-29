package um.os.scheduler.algo;

import um.os.scheduler.task.Task;

public class ShortestJobFirstAlgorithm implements NonPreemptiveSchedulingAlgorithm {

    @Override
    public int compareTwoItems(Task o1, Task o2) {
        return Integer.compare(o1.getDuration(), o2.getDuration());
    }

}
