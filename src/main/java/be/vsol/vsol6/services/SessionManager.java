package be.vsol.vsol6.services;

import be.vsol.tools.Service;

public class SessionManager implements Service {

    private boolean running = false;

    public SessionManager() {
    }

    @Override public void start() {
        running = true;
    }

    @Override public void stop() {
        running = false;
    }

    // Getters

    @Override public boolean isRunning() { return running; }

}
