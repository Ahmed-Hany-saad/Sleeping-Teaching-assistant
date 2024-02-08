package SleepingTA;

import java.util.concurrent.Semaphore;

public class TA implements Runnable {

    private MutexLock signalTrigger;
    private Semaphore chairs;
    private Semaphore TeacherAvailable;
    private Thread t;
    private int numberOfTeacher;
    private int numberofchairs;

    public TA(MutexLock w, Semaphore c, Semaphore a, int numberOfTeacher, int numberofchairs) {
        t = Thread.currentThread();
        signalTrigger = w;
        chairs = c;
        TeacherAvailable = a;
        this.numberOfTeacher = numberOfTeacher;
        this.numberofchairs = numberofchairs;
    }

    @Override
    public synchronized void run() {
        while (!Thread.currentThread().isInterrupted()) {
            System.out.println(
                    "No students are left. The TA " + numberOfTeacher + " is taking a nap.");
            try {
                signalTrigger.waitForSignal();
                System.out.println("The TA " + numberOfTeacher + " was awoke by a student.");
                int permitsAcquired = numberofchairs - chairs.availablePermits();
                while (permitsAcquired > 0) {
                    t.sleep(5000);
                    if (chairs.availablePermits() < numberofchairs) {
                        chairs.release();
                        permitsAcquired--;
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("TeachingAssistant thread interrupted: " + e.getMessage());
                break;
            }
        }
    }
}
