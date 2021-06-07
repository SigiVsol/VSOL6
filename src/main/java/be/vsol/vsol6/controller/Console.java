package be.vsol.vsol6.controller;

import be.vsol.util.Bytes;
import be.vsol.util.Log;
import be.vsol.vsol6.controller.Ctrl;

import java.util.Scanner;

public class Console implements Runnable {

    private final Ctrl ctrl;

    public Console(Ctrl ctrl) {
        this.ctrl = ctrl;
    }

    public void start() {
        new Thread(this).start();
    }

    @Override public void run() {
        try {
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNext()) {
                String command = scanner.nextLine();
                if (command == null) break;
                if (command.isBlank()) continue;

                String[] subs = command.split(" ", -1);
                if (subs.length > 0) {
                    handle(subs);
                }
            }
        } catch (IllegalStateException e) {
            Log.trace(e);
        }
    }

    private void handle(String[] subs) {
        switch (subs[0]) {
            case "exit" -> ctrl.exit();
            case "mem" -> mem();
        }
    }

    // Handling Methods

    private void mem() {
        Runtime runtime = Runtime.getRuntime();
        System.out.println(Bytes.getSizeString(runtime.totalMemory() - runtime.freeMemory()));
    }

}
