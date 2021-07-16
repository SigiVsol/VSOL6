package be.vsol.util;

import be.vsol.tools.Callback;
import be.vsol.tools.Job;

import java.util.HashMap;

public class Task {

    private static final HashMap<String, Job> jobs = new HashMap<>();

    /**
     * Ensures that the task with this exact name only runs if it isn't called again within a certain time.
     * The task will definitely run eventually, but not while it's being repeated.
     * @param name A name to identify the task with, so it doesn't get run multiple times
     * @param delay in ms
     * @param callback The task to be run
     */
    public static void run(String name, long delay, Callback callback) {
        Job job = jobs.get(name);
        if (job != null) job.stop();
        jobs.put(name, new Job(delay, callback));
    }

    public static void run(String name, long delay, long schedule, Callback callback) {
        Job job = jobs.get(name);
        if (job != null) job.stop();
        jobs.put(name, new Job(delay, schedule, callback));
    }
}
