package be.vsol.tools;

import be.vsol.util.Thr;

public class Job {

    private final long initialDelay, schedule;
    private final boolean repeats;
    private final Callback callback;

    private int counter = 0;
    private boolean active = false;

    public Job(long initialDelay, boolean repeats, long schedule, Callback callback, boolean autostart) {
        this.initialDelay = initialDelay;
        this.repeats = repeats;
        this.schedule = schedule;
        this.callback = callback;

        if (autostart) start();
    }

    public Job(long initialDelay, long schedule, Callback callback) {
        this(initialDelay, true, schedule, callback, true);
    }

    public Job(long initialDelay, Callback callback) {
        this(initialDelay, false, 0L, callback, true);
    }

    public Job(Callback callback) {
        this(0L, false, 0L, callback, true);
    }

    public void start() {
        active = true;

        new Thread(() -> {
            Thr.sleep(initialDelay);
            action();

            if (repeats) {
                while (active) {
                    Thr.sleep(schedule);
                    action();
                }
            }

            active = false;
        }).start();
    }

    private void action() {
        if (active) {
            callback.invoke();
            counter++;
        }
    }

    public void stop() {
        active = false;
    }

    public boolean isActive() { return active; }
    public int getCounter() { return counter; }

}
