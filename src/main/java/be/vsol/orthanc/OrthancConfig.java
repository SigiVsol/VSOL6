package be.vsol.orthanc;

import be.vsol.annotations.JsonField;
import be.vsol.annotations.StrMapField;
import be.vsol.tools.Config;

import java.io.File;
import java.util.Map;

public class OrthancConfig extends Config {

    @JsonField @StrMapField private String host = "localhost";
    @JsonField @StrMapField private int port;

    public OrthancConfig(File file, Map<String, String> namedParams) {
        super(file);
        super.load(namedParams, "orthanc");
    }




}
