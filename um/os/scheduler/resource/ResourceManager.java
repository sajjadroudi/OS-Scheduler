package um.os.scheduler.resource;

import um.os.scheduler.task.Task;

public class ResourceManager {

    public static class ResourceStatus {
        private final ResourceType resourceType;
        private final int freeCount;

        public ResourceStatus(ResourceType resourceType, int freeCount) {
            this.resourceType = resourceType;
            this.freeCount = freeCount;
        }

        public ResourceType getResourceType() {
            return resourceType;
        }

        public int getFreeCount() {
            return freeCount;
        }
    }

    private final Resource[] resources;

    private final Object resourcesLock = new Object();

    public ResourceManager(Resource... resources) {
        this.resources = resources;
    }

    public boolean areAllNeededResourcesAvailable(Task task) {
        synchronized (resourcesLock) {
            for(ResourceType resourceType : task.getNeededResources()) {
                if(!isResourceAvailable(resourceType))
                    return false;
            }
        }

        return true;
    }

    private boolean isResourceAvailable(ResourceType resourceType) {
        for(Resource resource : resources) {
            if(resource.getType() == resourceType && resource.isAvailable())
                return true;
        }

        return false;
    }

    public void freeTakenResources(Task task) {
        synchronized (resourcesLock) {
            for(ResourceType resourceType : task.getNeededResources()) {
                    Resource resource = findResource(resourceType);
                    resource.freeOne();
            }
        }
    }

    public void takeNeededResources(Task task) {
        synchronized (resourcesLock) {
            for(ResourceType resourceType : task.getNeededResources()) {
                Resource resource = findResource(resourceType);
                resource.takeOne();
            }
        }
    }

    private Resource findResource(ResourceType resourceType) {
        for(Resource resource : resources) {
            if(resource.getType() == resourceType)
                return resource;
        }

        throw new IllegalStateException();
    }

    public ResourceStatus[] getResourceStatuses() {
        synchronized (resourcesLock) {
            ResourceStatus[] statuses = new ResourceStatus[resources.length];
            for(int i = 0; i < resources.length; i++) {
                statuses[i] = new ResourceStatus(resources[i].getType(), resources[i].getFreeCount());
            }
            return statuses;
        }
    }

}
