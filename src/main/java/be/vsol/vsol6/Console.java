package be.vsol.vsol6;

import be.vsol.util.Bytes;
import be.vsol.util.Log;
import be.vsol.vsol6.view.Gui;

import java.util.Scanner;

public class Console implements Runnable {

    private final Scanner scanner;
    private final Vsol6 vsol6;

    public Console(Vsol6 vsol6) {
        this.scanner = new Scanner(System.in);
        this.vsol6 = vsol6;

    }

    public void start() {
        new Thread(this).start();
    }

    @Override public void run() {
        try {
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
            case "start" -> start(subs);
            case "exit" -> exit();
            case "mem" -> mem();
        }
    }

    // Handling Methods

    private void start(String[] subs) {
        if (subs.length > 1) {
            switch (subs[1]) {
                case "db" -> startDb();
                case "gui" -> startGui();
            }
        }
    }

    private void startDb() {

    }

    private void startGui() {

    }

    private void exit() {
        if (vsol6 == null) {
            System.exit(0);
        } else {
            try {
                vsol6.stop();
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
