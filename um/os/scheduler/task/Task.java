package um.os.scheduler.task;

import um.os.scheduler.resource.ResourceType;

import java.util.Objects;

public class Task {
    private final String name;
    private final TaskType type;
    private TaskState state;
    private int duration;
    private int enterTime;

    public Task(String name, TaskType type, int duration) {
        this.name = name;
        this.type = type;
        this.state = TaskState.WAITING;
        this.duration = duration;
    }

    public void setEnterTime(int enterTime) {
        this.enterTime = enterTime;
    }

    public String getName() {
        return name;
    }

    public TaskType getType() {
        return type;
    }

    public TaskState getState() {
        return state;
    }

    public int getDuration() {
        return duration;
    }

    public void decrementDuration() {
        duration -= 1;
    }

    public void decrementDuration(int amount) {
        duration = Math.max(duration - amount, 0);
    }

    public int getEnterTime() {
        return enterTime;
    }

    public ResourceType[] getNeededResources() {
        return type.getNeededResources();
    }

    public int getPriority() {
        return type.getPriority();
    }

    public boolean isFinished() {
        return duration == 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return name.equals(task.name) && type == task.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type);
    }
}
