package be.vsol.vsol6;

import be.vsol.tools.Job;
import be.vsol.tools.Service;
import be.vsol.tools.Sig;
import be.vsol.util.Lang;
import be.vsol.util.Log;
import be.vsol.vsol6.model.LocalSystem;
import be.vsol.vsol6.model.setting.*;
import be.vsol.vsol6.services.*;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.File;
import java.time.LocalDate;
import java.time.Month;
import java.util.Vector;

public class Vsol6 extends Application {

    private static final Sig sig = new Sig("VSOL6", 0, 0, 5, Sig.Publisher.SIGI_DEV, LocalDate.of(2021, Month.MAY, 15));

    private static LocalSystem system;
    private static File home;

    private static SettingsManager settingsManager;
    private static GuiManager guiManager;

    private static final Vector<Service> services = new Vector<>(); // will contain the following:
    private static DatabaseManager databaseManager;
    private static WebServer webServer;
    private static ApiServer apiServer;
    private static Vsol4Service vsol4Service;
    private static OrthancService orthancService;

    // Static Method

    public static void main(String[] args) {
        launch(args);
    }

    // Methods

    @Override public void start(Stage primaryStage) { try {
        settingsManager = new SettingsManager(getParameters(), gui.class, db.class, web.class, api.class, vsol4.class, orthanc.class, vsol6.class);
        settingsManager.start(); // start this one first, so it can be used by the other services

        home = new File(vsol6.home);
        Log.init(new File(home, "logs"), sig.getAppTitle());
        Log.out("Starting " + sig + ".");

        system = new LocalSystem();

        if (!vsol6.cloud) {
            services.add(guiManager = new GuiManager(primaryStage));
            guiManager.showSplash(Lang.get("Loading."));
        }

        new Job(() -> {
            services.add(databaseManager = new DatabaseManager());
            services.add(webServer = new WebServer());
            services.add(apiServer = new ApiServer());
            services.add(vsol4Service = new Vsol4Service());
            services.add(orthancService = new OrthancService());

            for (Service service : services) { service.start(); }

            if (guiManager != null) { guiManager.showApp(); }
        });
    } catch (Exception e) { Log.trace(e); System.exit(1); } }

    @Override public void stop() { try {
        for (Service service : services) { service.stop(); }

        settingsManager.stop();

        Log.out("Exiting.\n");
        System.exit(0);
    } catch (Exception e) { Log.trace(e); } }

    // Static Getters

    public static Sig getSig() { return sig; }

    public static File getHome() { return home; }

    public static LocalSystem getSystem() { return system; }

    public static File getHome(String sub) { return new File(home, sub); }

    public static SettingsManager getSettingsManager() { return settingsManager; }

    public static GuiManager getGui() { return guiManager; }

    public static DatabaseManager getDatabaseManager() { return databaseManager; }

    public static WebServer getWebServer() { return webServer; }

    public static ApiServer getApi() { return apiServer; }

    public static Vsol4Service getVsol4Manager() { return vsol4Service; }

    public static OrthancService getOrthancManager() { return orthancService; }

}
