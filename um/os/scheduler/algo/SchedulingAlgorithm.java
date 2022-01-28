package um.os.scheduler.algo;

import um.os.scheduler.task.Task;

import java.util.Comparator;

public interface SchedulingAlgorithm extends Comparator<Task> {

    @Override
    default int compare(Task o1, Task o2) {
        int result = compareTwoItems(o1, o2);
        if(result == 0) {
            result = Integer.compare(o1.getPriority(), o2.getPriority());
        }
        return  result;
    }

    int compareTwoItems(Task first, Task second);

}
