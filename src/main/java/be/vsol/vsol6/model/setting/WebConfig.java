package be.vsol.vsol6.model.setting;

import be.vsol.database.annotations.Db;

public class WebConfig extends Config {

    @Db private int port;

    // Constructors

    public WebConfig() {
        super("webConfigs");
    }

    // Getters

    public int getPort() { return port; }

    // Setters

    public void setPort(int port) { this.port = port; }

}
