package be.vsol.vsol6.services;

import be.vsol.tools.Job;
import be.vsol.tools.Service;
import be.vsol.util.Bytes;

import java.util.Scanner;

public class ConsoleService implements Service {

    public ConsoleService() {

    }

    @Override public void start() {
        new Job(this::listen);
    }

    @Override public void stop() { }

    private void listen() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("> ");
            String command = scanner.nextLine();
            if (command == null) break;
            if (command.isBlank()) continue;

            String[] subs = command.split(" ", -1);

            switch (subs[0]) {
                case "exit" -> exit();
                case "mem" -> mem();
            }
        }
    }

    private void exit() {
        System.exit(0);
    }

    private void mem() {
        Runtime runtime = Runtime.getRuntime();
        System.out.println(Bytes.getSizeString(runtime.totalMemory() - runtime.freeMemory()));
    }

}
