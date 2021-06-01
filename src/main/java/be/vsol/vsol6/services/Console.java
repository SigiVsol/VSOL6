package be.vsol.vsol6.services;

import be.vsol.tools.Service;
import be.vsol.util.Bytes;
import be.vsol.util.Log;
import javafx.application.Application;

import java.util.Scanner;

public class Console implements Service {

    private final Scanner scanner;
    private final Application application;

    private boolean started = false;

    public Console(Application application) {
        this.scanner = new Scanner(System.in);
        this.application = application;
        new Thread(this).start();
    }

    @Override public void start() {
        started = true;
    }

    @Override public void stop() {
        started = false; // scanner can't be closed, as that would close the underlying System.in as well
    }

    @Override public void run() {
        try {
            while (scanner.hasNext()) {
                String command = scanner.nextLine();
                if (command == null) break;
                if (command.isBlank() || !started) continue;

                String[] subs = command.split(" ", -1);

                switch (subs[0]) {
                    case "exit" -> exit();
                    case "mem" -> mem();
                    case "stop" -> stop();
                }
            }
        } catch (IllegalStateException e) {
            Log.trace(e);
        } finally {
            started = false;
        }
    }

    private void exit() {
        if (application == null) {
            System.exit(0);
        } else {
            try {
                application.stop();
            } catch (Exception e) {
                Log.trace(e);
            }
        }
    }

    private void mem() {
        Runtime runtime = Runtime.getRuntime();
        System.out.println(Bytes.getSizeString(runtime.totalMemory() - runtime.freeMemory()));
    }

}
