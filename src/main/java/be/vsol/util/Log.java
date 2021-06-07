package be.vsol.util;

import be.vsol.tools.Job;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public class Log {

    private static File dir;
    private static String filename;
    private static boolean debug = false;

    private static File logFile, errFile;

    public static void init(File dir, String filename, boolean debug) {
        Log.dir = dir;
        Log.filename = filename == null ? null : filename.replace(" ", "_");
        Log.debug = debug;
        logFile = null;
        FileSys.create(dir);
    }

    public static void out(String message) {
        log(message, true, false);
    }

    public static void err(String message) {
        log(message, true, true);
    }

    public static void debug(String message) {
        if (debug) {
            out("[DEBUG] " + message);
        }
    }

    public static void trace(Exception e) {
        log(e.toString(), true, true);
        log(getString(e.getStackTrace()), false, true);
    }

    private static void log(String message, boolean out, boolean error) {
        if (error) {
            System.err.println(message);
        } else {
            System.out.println(message);
        }

        if (logFile == null) {
            LocalDate date = LocalDate.now();
            logFile = new File(dir, date + (filename == null || filename.isBlank() ? "" : "_" + filename) + ".log");
            errFile = new File(dir, date + (filename == null || filename.isBlank() ? "" : "_" + filename) + ".err");

            LocalDateTime now = LocalDateTime.now();
            LocalDateTime midnight = LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(0, 0, 0));
            long delayTillMidnight = Date.getMillis(midnight) - Date.getMillis(now);
            new Job(delayTillMidnight, () -> logFile = null);
        }

        if (out) {
            String output = LocalTime.now().truncatedTo(ChronoUnit.SECONDS) + (error ? " [ERR] " : " ") + message;
            FileSys.appendString(logFile, output, true);
        }
        if (error) {
            String output = (out ? LocalTime.now().truncatedTo(ChronoUnit.SECONDS) + " " : "") + message;
            FileSys.appendString(errFile, output, true);
        }
    }

    private static String getString(StackTraceElement[] stackTraceElements) {
        String result = "";
        for (StackTraceElement stackTraceElement : stackTraceElements) {
            result += (result.isEmpty() ? "" : "\r\n") + "   " + stackTraceElement.toString();
        }
        return result;
    }

}
