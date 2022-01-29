package um.os.scheduler.timeunit;

import um.os.scheduler.Utils;

import java.util.ArrayList;
import java.util.List;

public class TimeUnitObservable {

    private static TimeUnitObservable instance;
    private int currentTime = 0;

    public static TimeUnitObservable getInstance() {
        if(instance == null) {
            instance = new TimeUnitObservable();
        }
        return instance;
    }

    public int getCurrentTime() {
        return currentTime;
    }

    private final List<TimeUnitObserver> observers;

    private TimeUnitObservable() {
        observers = new ArrayList<>();
    }

    public void addObserver(TimeUnitObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(TimeUnitObserver observer) {
        observers.remove(observer);
    }

    public void notifyOneTimeUnitPassed() {
        Utils.sleepThread();
        currentTime++;
        for(TimeUnitObserver observer : observers)
            observer.oneTimeUnitPassed();
        Utils.sleepThread();
    }

}
