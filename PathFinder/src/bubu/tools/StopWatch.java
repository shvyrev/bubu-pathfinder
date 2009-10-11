package bubu.tools;

public class StopWatch {

    private static long startTime;
    private static long endTime;

    public static void startTimer() {
        startTime = System.currentTimeMillis();
    }

    public static void stopTimer() {
        endTime = System.currentTimeMillis();
    }
    
    public static long getDuration() {
        return endTime - startTime;
    }
}
