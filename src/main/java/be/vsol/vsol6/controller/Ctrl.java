package be.vsol.vsol6.controller;

import be.vsol.http.HttpServer;
import be.vsol.orthanc.Orthanc;
import be.vsol.tools.Job;
import be.vsol.tools.Sig;
import be.vsol.util.Log;
import be.vsol.util.Resource;
import be.vsol.vsol4.Vsol4;
import be.vsol.vsol6.controller.backend.DataStorage;
import be.vsol.vsol6.controller.backend.DicomStorage;
import be.vsol.vsol6.controller.backend.OrthancDicomStorage;
import be.vsol.vsol6.controller.backend.Vsol4DataStorage;
import be.vsol.vsol6.controller.http.CloudHandler;
import be.vsol.vsol6.controller.http.ServerHandler;
import be.vsol.vsol6.model.Computer;
import be.vsol.vsol6.model.Organization;
import be.vsol.vsol6.model.Session;
import be.vsol.vsol6.model.User;
import be.vsol.vsol6.model.config.Config;
import be.vsol.vsol6.model.database.Db;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.io.File;
import java.time.LocalDate;
import java.time.Month;
import java.util.Map;

public class Ctrl {

    public static final Sig sig = new Sig("VSOL6", 0, 1, 3, Sig.Publisher.SIGI_DEV, LocalDate.of(2021, Month.MAY, 15));

    private final JSONObject jsonDefaults;
    private final Map<String, String> params;

    private final Computer computer;
    private final Console console;
    private final Gui gui;
    private final Db db;
    private final Vsol4 vsol4;
    private final Orthanc orthanc;
    private final HttpServer cloud;
    private final HttpServer server;
    private final DataStorage dataStorage;
    private final DicomStorage dicomStorage;

    private Session systemSession, localSession;

    /**
     * Default constructor
     * @param params The named command line parameters, as passed from launch. Format: --key=value
     * @param primaryStage The main JavaFX-stage, as passed from the JavaFX-launcher, or null if launched as CL
     */
    public Ctrl(Map<String, String> params, Stage primaryStage) {
        this.params = params;
        this.jsonDefaults = new JSONObject(Resource.getString("config/defaults.json"));
        this.computer = new Computer();
        Config appConfig = new Config(jsonDefaults, params);

        Log.init(new File(appConfig.app.home, "logs"), sig.getAppTitle(), appConfig.app.debug);
        Log.out("Starting " + sig + ".");

        this.console = new Console(this);
        this.gui = primaryStage == null ? null : new Gui(this, primaryStage);
        this.db = new Db(appConfig);
        this.server = new HttpServer(sig.getAppTitle());
        this.vsol4 = new Vsol4();
        this.orthanc = new Orthanc();
        this.cloud = new HttpServer("VSOL Cloud Server");

        this.dataStorage = switch (appConfig.app.dataStorage) {
            case vsol4 -> new Vsol4DataStorage(this);
        };

        this.dicomStorage = switch (appConfig.app.dicomStorage) {
            case orthanc -> new OrthancDicomStorage(this);
        };

        if (gui != null && appConfig.gui.active) gui.showSplashScreen();
        new Job(() -> boot(appConfig));
    }

    private void boot(Config appConfig) {
        if (appConfig.console.active) startConsole();
        if (appConfig.db.active) startDb();

        systemSession = new Session(this, computer);

        if (appConfig.server.active) startServer(systemSession.getConfig());
        if (appConfig.vsol4.active) startVsol4(systemSession.getConfig());
        if (appConfig.orthanc.active) startOrthanc(systemSession.getConfig());
        if (gui != null && appConfig.gui.active) startGui(systemSession.getConfig());
        if (cloud != null && appConfig.cloud.active) startCloud(systemSession.getConfig());
    }

    private void startConsole() {
        console.start();
    }

    private void startDb() {
        db.start();
    }

    private void startServer(Config config) {
        server.start(config.server.port, new ServerHandler(config, sig.getVariables(), dataStorage, dicomStorage));
    }

    private void startVsol4(Config config) {
        vsol4.start(config.vsol4.host, config.vsol4.port, config.vsol4.username, config.vsol4.password, config.vsol4.timeout);
    }

    private void startOrthanc(Config config) {
        orthanc.start(config.orthanc.host, config.orthanc.port, config.orthanc.timeout);
    }

    private void startCloud(Config config) {
        cloud.start(config.cloud.port, new CloudHandler(db));
    }

    private void startFujiDR() {

    }

    private void startGenerator() {

    }

    private void startDicomServer() {

    }

    private void startGui(Config config) {
        if (gui == null) {
            Log.err("Can't show GUI. Not launched as a JavaFX application.");
        } else {
            User user = dataStorage.getUser(config.gui.userId);
            Organization organization = dataStorage.getOrganization(config.gui.organizationId);
            localSession = new Session(this, computer, user, organization);
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

    public Computer getComputer() { return computer; }

    public DataStorage getDataStorage() { return dataStorage; }

    public DicomStorage getDicomStorage() { return dicomStorage; }

    // Setters

    public void setLocalSession(Session localSession) { this.localSession = localSession; }

}
