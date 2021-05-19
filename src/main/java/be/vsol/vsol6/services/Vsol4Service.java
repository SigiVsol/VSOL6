package be.vsol.vsol6.services;

import be.vsol.tools.Service;

public class Vsol4Service implements Service {

    private boolean running = false;

    @Override public void start() {
        running = true;
    }

    @Override public void stop() {
        running = false;
    }

    // Getters

    @Override public boolean isRunning() { return running; }

}
