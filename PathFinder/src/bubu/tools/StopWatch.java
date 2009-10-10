/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bubu.tools;

/**
 *
 * @author Reuben
 */
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
