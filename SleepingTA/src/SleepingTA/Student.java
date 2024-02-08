package SleepingTA;

import java.util.concurrent.Semaphore;

public class Student implements Runnable {

    private int waitToAsk;
    private int studentNum;
    private MutexLock wakeup;
    private Semaphore chairs;
    private Semaphore TeacherAvailable;
    private Thread t;

    public Student(int waitToAsk, MutexLock w, Semaphore c, Semaphore a, int studentNum) {
        this.waitToAsk = waitToAsk;
        wakeup = w;
        chairs = c;
        TeacherAvailable = a;
        this.studentNum = studentNum;
        t = Thread.currentThread();
    }

    @Override
    public synchronized void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                System.out.println("Student " + studentNum + " has started asking for " + waitToAsk + " seconds.");
                t.sleep(waitToAsk * 1000);
                System.out.println("Student " + studentNum + " is checking to see if TA is available.");
                if (TeacherAvailable.tryAcquire())
                {
                    try {
                        wakeup.sendSignal();
                        System.out.println("Student " + studentNum + " has woke up the TA.");
                        System.out.println("Student " + studentNum + " has started working with the TA.");
                        t.sleep(5000);
                        System.out.println("Student " + studentNum + " has stopped working with the TA.");
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        System.err.println("Student thread interrupted: " + e.getMessage());
                        continue;
                    } finally {
                        TeacherAvailable.release();
                        break;
                    }
                } else {
                    System.out.println("Student " + studentNum + " can't see the TA. Checking for chairs.");
                    if (chairs.tryAcquire()) {
                        try {
                            System.out.println("Student " + studentNum + " is sitting outside the office. "
                                    + "He is #" + ((3 - chairs.availablePermits())) + " in line.");
                            TeacherAvailable.acquire();
                            System.out.println("Student " + studentNum + " has started working with the TA. ");
                            t.sleep(5000);
                            System.out.println("Student " + studentNum + " has stopped working with the TA. ");
                            TeacherAvailable.release();
                            break;
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            System.err.println("Student thread interrupted: " + e.getMessage());
                            continue;
                        }
                    } else {
                        System.out.println("Student " + studentNum + " could not see the TA and all chairs were taken, so student will come later!");
                    }
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Student thread interrupted: " + e.getMessage());

                break;
            } finally {
                if (Thread.currentThread().isInterrupted()) {
                    System.out.println("Student thread interrupted. Exiting loop.");
                    break;
                }
            }
        }
    }
}
