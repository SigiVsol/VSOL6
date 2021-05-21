package be.vsol.vsol6.model.setting;

import be.vsol.database.annotations.Db;

public class ApiConfig extends Config {

    @Db private int port;

    // Constructors

    public ApiConfig() {
        super("apiConfigs");
    }

    // Getters

    public int getPort() { return port; }

    // Setters

    public void setPort(int port) { this.port = port; }

}
