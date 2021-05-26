package be.vsol.util;

import java.util.Stack;

public class Debug {

    private static final Stack<Debug> stack = new Stack<>();

    private final long start;
    private final String name;

    public Debug(String name) {
        start = System.currentTimeMillis();
        this.name = name;
    }

    public static void start(String name) {
        Debug debug = new Debug(name);

        String prefix = getPrefix();
        stack.push(debug);

        Log.out(prefix + "Start [" + name + "].");
    }

    public static void stop() {
        Debug debug = stack.pop();
        String prefix = getPrefix();

        long start = debug.start;
        long stop = System.currentTimeMillis();
        String name = debug.name;

        long diff = stop - start;
        Log.out(prefix + "Finished [" + name + "]: " + (diff / 1000.0) + " sec");
    }

    private synchronized static String getPrefix() {
        return "\t".repeat(stack.size());
    }



}
