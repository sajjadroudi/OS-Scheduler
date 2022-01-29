package um.os.scheduler;

public class Utils {

    public static void sleepThread() {
        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
