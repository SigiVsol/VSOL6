package be.vsol.vsol6.controller;

import be.vsol.http.HttpServer;
import be.vsol.orthanc.Orthanc;
import be.vsol.tools.Job;
import be.vsol.tools.Sig;
import be.vsol.util.Log;
import be.vsol.util.Resource;
import be.vsol.vsol4.Vsol4;
import be.vsol.vsol6.controller.api.API;
import be.vsol.vsol6.controller.api.Vsol4API;
import be.vsol.vsol6.controller.http.ServerHandler;
import be.vsol.vsol6.model.LocalSystem;
import be.vsol.vsol6.model.Organization;
import be.vsol.vsol6.model.User;
import be.vsol.vsol6.model.config.Config;
import be.vsol.vsol6.model.database.Db;
import be.vsol.vsol6.model.Session;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.Month;
import java.util.Map;

public class Ctrl {

    public static final Sig sig = new Sig("VSOL6", 0, 1, 2, Sig.Publisher.SIGI_DEV, LocalDate.of(2021, Month.MAY, 15));

    private final JSONObject jsonDefaults;
    private final Map<String, String> params;

    private final LocalSystem system;
    private final Console console;
    private final Gui gui;
    private final Db db;
    private final Vsol4 vsol4;
    private final Orthanc orthanc;
    private final HttpServer server;
    private final API api;

    private Session systemSession, localSession;

    /**
     * Default constructor
     * @param params The named command line parameters, as passed from launch. Format: --key=value
     * @param primaryStage The main JavaFX-stage, as passed from the JavaFX-launcher, or null if launched as CL
     */
    public Ctrl(Map<String, String> params, Stage primaryStage) {
        this.params = params;
        this.jsonDefaults = new JSONObject(Resource.getString("config/defaults.json"));
        this.system = new LocalSystem();
        Config appConfig = new Config(jsonDefaults, params);

        Log.init(appConfig.app.home, sig.getAppTitle(), appConfig.app.debug);
        Log.out("Starting " + sig + ".");

        this.console = new Console(this);
        this.gui = primaryStage == null ? null : new Gui(this, primaryStage);
        this.db = new Db(appConfig);
        this.server = new HttpServer(sig.getAppTitle());
        this.vsol4 = new Vsol4();
        this.orthanc = new Orthanc();

        this.api = appConfig.app.legacy ? new Vsol4API(this) : null;

        if (gui != null && appConfig.gui.active) gui.showSplashScreen();
        new Job(() -> boot(appConfig));
    }

    private void boot(Config appConfig) {
        if (appConfig.console.active) startConsole();
        if (appConfig.db.active) startDb();

        systemSession = new Session(this, system);

        if (appConfig.server.active) startServer(systemSession.getConfig());
        if (appConfig.vsol4.active && !appConfig.vsol4.forward) startVsol4(systemSession.getConfig());
        if (appConfig.orthanc.active) startOrthanc(systemSession.getConfig());
        if (gui != null && appConfig.gui.active) startGui(systemSession.getConfig());
    }

    private void startConsole() {
        console.start();
    }

    private void startDb() {
        db.start();
    }

    private void startServer(Config config) {
        server.start(config.server.port, new ServerHandler(config, sig.getVariables(), api));
    }

    private void startVsol4(Config config) {
        vsol4.start(config.vsol4.host, config.vsol4.port, config.vsol4.username, config.vsol4.password, config.vsol4.timeout);
    }

    private void startOrthanc(Config config) {
        orthanc.start(config.orthanc.host, config.orthanc.port, config.orthanc.timeout);
    }

    private void startGui(Config config) {
        if (gui == null) {
            Log.err("Can't show GUI. Not launched as a JavaFX application.");
        } else {
            User user = api.getUser(config.gui.userId);
            Organization organization = api.getOrganization(config.gui.organizationId);

            localSession = new Session(this, system, user, organization);
            gui.start(config);
        }
    }

    /**
     * Performs the necessary closing tasks and exits the program. Will also be called by JavaFX upon closing the last window.
     */
    public void exit() {
        System.exit(0);
    }

    // Getters

    public Sig getSig() { return sig; }

    public JSONObject getJsonDefaults() { return jsonDefaults; }

    public Console getConsole() { return console; }

    public Gui getGui() { return gui; }

    public Db getDb() { return db; }

    public Map<String, String> getParams() { return params; }

    public Vsol4 getVsol4() { return vsol4; }

    public Orthanc getOrthanc() { return orthanc; }

    public Session getLocalSession() { return localSession; }

    public Session getSystemSession() { return systemSession; }

    public LocalSystem getSystem() { return system; }

    // Setters

    public void setLocalSession(Session localSession) { this.localSession = localSession; }

}
