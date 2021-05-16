package be.vsol.tools;

import be.vsol.util.Json;
import be.vsol.util.StrMap;

import java.io.File;
import java.util.Map;

public abstract class Config {

    private final File file;

    public Config(File file) {
        this.file = file;
    }

    public void load(Map<String, String> namedParams, String prefix) {
        Json.load(this, Json.load(file));
        StrMap.load(this, namedParams, prefix);
    }

    public void save() {
        if (file != null) { Json.save(Json.get(this), file); }
    }

}
