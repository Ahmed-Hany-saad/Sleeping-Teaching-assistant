package SleepingTA;

public class MutexLock {

    private boolean signal = false;

    public synchronized void sendSignal() {
        this.signal = true;
        this.notify();
    }

    public synchronized void waitForSignal() throws InterruptedException {
        while (!this.signal) {
            wait();
        }
        this.signal = false;
    }
}
