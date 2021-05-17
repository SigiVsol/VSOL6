package be.vsol.vsol4;

import be.vsol.tools.Service;

import java.io.File;
import java.util.Map;

public class Vsol4 implements Service {

    private final Vsol4Config vsol4Config;

    public Vsol4(File home, Map<String, String> namedParams) {
        vsol4Config = new Vsol4Config(new File(home, "config/vsol4.json"), namedParams);
    }

    @Override public void start() {

    }

    @Override public void stop() {
        vsol4Config.save();
    }
}