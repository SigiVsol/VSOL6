package be.vsol.vsol6.model.database;

import be.vsol.database.connection.DbDriver;
import be.vsol.database.structures.Database;
import be.vsol.database.structures.DbTable;
import be.vsol.vsol6.model.config.GuiConfig;
import be.vsol.vsol6.model.config.OrthancConfig;
import be.vsol.vsol6.model.config.ServerConfig;
import be.vsol.vsol6.model.config.Vsol4Config;

public abstract class VsolDb extends Database {

    protected DbTable<GuiConfig> guiConfigs;
    protected DbTable<Vsol4Config> vsol4Configs;
    protected DbTable<OrthancConfig> orthancConfigs;
    protected DbTable<ServerConfig> serverConfigs;

    // Constructors

    public VsolDb(DbDriver driver, String name) {
        super(driver, name);

        guiConfigs = new DbTable<>(this, "gui_configs", GuiConfig::new);
        vsol4Configs = new DbTable<>(this, "vsol4_configs", Vsol4Config::new);
        orthancConfigs = new DbTable<>(this, "orthanc_configs", OrthancConfig::new);
        serverConfigs = new DbTable<>(this, "server_configs", ServerConfig::new);
    }

    // Getters

    public DbTable<GuiConfig> getGuiConfigs() { return guiConfigs; }

    public DbTable<Vsol4Config> getVsol4Configs() { return vsol4Configs; }

    public DbTable<OrthancConfig> getOrthancConfigs() { return orthancConfigs; }

    public DbTable<ServerConfig> getServerConfigs() { return serverConfigs; }

}
