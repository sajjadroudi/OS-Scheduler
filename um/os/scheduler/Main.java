package um.os.scheduler;

import um.os.scheduler.algo.FirstComeFirstServeAlgorithm;
import um.os.scheduler.algo.ShortestJobFirstAlgorithm;
import um.os.scheduler.core.Cpu;
import um.os.scheduler.core.Scheduler;
import um.os.scheduler.resource.Resource;
import um.os.scheduler.resource.ResourceManager;
import um.os.scheduler.resource.ResourceType;
import um.os.scheduler.task.Task;
import um.os.scheduler.task.TaskType;
import um.os.scheduler.timeunit.TimeUnitObservable;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        ResourceManager resourceManager = buildResourceManager(scanner);

        Task[] tasks = readTasks(scanner);

        scanner.close();

        Scheduler scheduler = new Scheduler(new ShortestJobFirstAlgorithm());
        Cpu cpu = new Cpu(4, scheduler, resourceManager);

        cpu.run();

        for(Task task : tasks) {
            task.setEnterTime(TimeUnitObservable.getInstance().getCurrentTime());
            cpu.assignTask(task);
            System.out.println(cpu.getSystemStatus());
            TimeUnitObservable.getInstance().notifyOneTimeUnitPassed();
        }

        while(!cpu.areAllTasksFinished()) {
            System.out.println(cpu.getSystemStatus());
            TimeUnitObservable.getInstance().notifyOneTimeUnitPassed();
        }

        System.out.println(cpu.getSystemStatus());

        System.exit(0);
    }

    private static ResourceManager buildResourceManager(Scanner scanner) {
        Resource[] resources = {
                new Resource(ResourceType.R1, scanner.nextInt()),
                new Resource(ResourceType.R2, scanner.nextInt()),
                new Resource(ResourceType.R3, scanner.nextInt())
        };

        return new ResourceManager(resources);
    }

    private static Task[] readTasks(Scanner scanner) {
        int taskCount = scanner.nextInt();
        Task[] tasks = new Task[taskCount];

        for(int i = 0; i < taskCount; i++) {
            String taskName = scanner.next();
            String taskType = scanner.next();
            int taskDuration = scanner.nextInt();

            Task task = new Task(
                    taskName,
                    TaskType.valueOf(taskType),
                    taskDuration
                );

            tasks[i] = task;
        }

        return tasks;
    }

    /*
2   1   3
7
t1  X   9
t2  Z   8
t3  Y   4
t4  Z   4
t5  X   2
t6  Y   6
t7  X   4
     */

}
