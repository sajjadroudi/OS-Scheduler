package um.os.scheduler.task;

import um.os.scheduler.resource.ResourceType;

import static um.os.scheduler.resource.ResourceType.*;

public enum TaskType {

    X(3, R1, R2),
    Y(2, R2, R3),
    Z(1, R1, R3);

    private final ResourceType[] neededResources;
    private final int priority;

    TaskType(int priority, ResourceType... resourceTypes) {
        this.priority = priority;
        this.neededResources = resourceTypes;
    }

    public ResourceType[] getNeededResources() {
        return neededResources;
    }

    public int getPriority() {
        return priority;
    }

}
