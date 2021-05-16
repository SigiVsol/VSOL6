package be.vsol.vsol4;

import be.vsol.annotations.JsonField;
import be.vsol.annotations.StrMapField;
import be.vsol.tools.Config;

import java.io.File;
import java.util.Map;

public class Vsol4Config extends Config {

    @JsonField @StrMapField private String host = "localhost";
    @JsonField @StrMapField private int port = 8080, timeout = 1000;

    public Vsol4Config(File file, Map<String, String> namedParams) {
        super(file);
        super.load(namedParams, "vsol4");
    }

    public Vsol4Token getToken() {
        return new Vsol4Token(host, port);
    }

    public Vsol4Token getSuperToken() {
        return new Vsol4Token(host, port);
    }

    public int getTimeout() { return timeout; }

    public void setHost(String host) { this.host = host; }
    public void setPort(int port) { this.port = port; }
    public void setTimeout(int timeout) { this.timeout = timeout; }

}
