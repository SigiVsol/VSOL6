package be.vsol.database.connection;

import java.sql.Connection;

public abstract class ServiceBasedDbDriver extends DbDriver {

    protected final String host, user, password;
    protected final int port;

    public ServiceBasedDbDriver(String protocol, String host, int port, String user, String password) {
        super(protocol);

        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
    }

}
