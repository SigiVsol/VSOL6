package be.vsol.vsol6.model.config;

import be.vsol.database.annotations.Db;

public class ServerConfig extends Config {

    @Db private int port;

    // Constructors

    public ServerConfig() {
        super("server");
    }

    // Getters

    public int getPort() { return port; }

    // Setters

    public void setPort(int port) { this.port = port; }

}
