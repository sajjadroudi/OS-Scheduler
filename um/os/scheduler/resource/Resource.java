package um.os.scheduler.resource;

public class Resource {

    private final ResourceType type;
    private final int totalCount;
    private int freeCount;

    public Resource(ResourceType type, int totalCount) {
        this.type = type;
        this.totalCount = totalCount;
        this.freeCount = totalCount;
    }

    public void freeOne() {
        if(freeCount == totalCount)
            throw new AllResourcesAreFreeException();

        freeCount++;
    }

    public void takeOne() {
        if(freeCount == 0)
            throw new NoFreeResourceException();

        freeCount--;
    }

    public int getFreeCount() {
        return freeCount;
    }

    public ResourceType getType() {
        return type;
    }

    public boolean isAvailable() {
        return freeCount > 0;
    }

}
