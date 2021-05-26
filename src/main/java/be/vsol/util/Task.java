package be.vsol.util;

import be.vsol.tools.Callback;
import be.vsol.tools.Job;

import java.util.HashMap;

public class Task {

    private static final HashMap<String, Job> jobs = new HashMap<>();

    public static void run(String name, long delay, Callback callback) {
        Job job = jobs.get(name);
        if (job != null) job.stop();
        jobs.put(name, new Job(delay, callback));
    }

}
