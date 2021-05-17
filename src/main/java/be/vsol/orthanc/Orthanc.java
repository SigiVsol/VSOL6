package be.vsol.orthanc;

import be.vsol.tools.Service;

import java.io.File;
import java.util.Map;

public class Orthanc implements Service {

    private final OrthancConfig orthancConfig;

    public Orthanc(File home, Map<String, String> namedParams) {
        orthancConfig = new OrthancConfig(new File(home, "config/orthanc.json"), namedParams);
    }

    @Override public void start() {

    }

    @Override public void stop() {
        orthancConfig.save();
    }

}
