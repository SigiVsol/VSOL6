package be.vsol.tools;

import java.util.concurrent.Semaphore;

public class Sema {
    public static void acquire(Semaphore semaphore) {
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            Log.trace(e);
        }
    }

    public static void release(Semaphore semaphore) {
        if (semaphore != null)
            semaphore.release();
    }
}
