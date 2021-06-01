package be.vsol.test.sigi;

import be.vsol.tools.Job;
import be.vsol.vsol6.services.Console;

public class TestConsole {

    public static void main(String[] args) {
        Console console = new Console(null);

        console.start();

        new Job(2000, () -> {
            System.out.println("stop");
            console.stop();
        });

        new Job(4000, () -> {
            System.out.println("start");
            console.start();
        });

    }

}
