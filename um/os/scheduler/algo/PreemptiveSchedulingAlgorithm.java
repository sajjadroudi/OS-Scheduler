package um.os.scheduler.algo;

import um.os.scheduler.task.Task;

public interface PreemptiveSchedulingAlgorithm extends SchedulingAlgorithm {

    boolean onExecuteOneTimeUnit(Task task);

}
