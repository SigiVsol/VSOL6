package be.vsol.vsol6.model.setting;

import be.vsol.database.annotations.Db;

public class OrthancConfig extends Config {

    @Db private String host;
    @Db private int port;

    // Constructors

    public OrthancConfig() {
        super("orthancConfigs");
    }

    // Getters

    public String getHost() { return host; }

    public int getPort() { return port; }

    // Setters

    public void setHost(String host) { this.host = host; }

    public void setPort(int port) { this.port = port; }

}