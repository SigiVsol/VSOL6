package be.vsol.vsol6;

import be.vsol.http.HttpServer;
import be.vsol.tools.Job;
import be.vsol.tools.Service;
import be.vsol.tools.Sig;
import be.vsol.util.Log;
import be.vsol.vsol6.controller.http.ServerHandler;
import be.vsol.vsol6.model.LocalSystem;
import be.vsol.vsol6.services.*;
import be.vsol.vsol6.session.Session;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.File;
import java.time.LocalDate;
import java.time.Month;
import java.util.Vector;

public class Vsol6 extends Application {

    private static final Sig sig = new Sig("VSOL6", 0, 0, 5, Sig.Publisher.SIGI_DEV, LocalDate.of(2021, Month.MAY, 15));

    private static boolean cloud;
    private static File home;
    private static LocalSystem system;
    private static Session systemSession;

    private static Parameters parameters;

    private static final Vector<Service> services = new Vector<>(); // will contain the following:
    private static GuiManager guiManager;
    private static DatabaseManager databaseManager;
    private static HttpServer httpServer;
    private static Vsol4Service vsol4Service;
    private static OrthancService orthancService;

    // Static Method

    public static void main(String[] args) {
        launch(args);
    }

    // Methods

    @Override public void start(Stage primaryStage) { try {
        parameters = getParameters();
        cloud = getParameters().getUnnamed().contains("cloud");
        home = new File(getParameters().getNamed().getOrDefault("home", sig.getFolder()));
        Log.init(new File(home, "logs"), sig.getAppTitle());
        Log.out("Starting " + sig + ".");

        system = new LocalSystem();
        systemSession = new Session(system, null, null);

        if (!cloud) {
            services.add(guiManager = new GuiManager(primaryStage));
            guiManager.showSplash();
        }

        new Job(() -> {
            services.add(databaseManager = new DatabaseManager(getParameters().getNamed()));
            services.add(httpServer = new HttpServer(sig.toString(), systemSession.getServerConfig().getPort(), new ServerHandler()));
            services.add(vsol4Service = new Vsol4Service());
            services.add(orthancService = new OrthancService());

            for (Service service : services) { service.start(); }

            if (guiManager != null) {
                guiManager.showApp();
            }
        });
    } catch (Exception e) { Log.trace(e); System.exit(1); } }

    @Override public void stop() { try {
        for (Service service : services) { service.stop(); }

        Log.out("Exiting.\n");
        System.exit(0);
    } catch (Exception e) { Log.trace(e); } }

    // Static Getters

    public static Parameters getParams() { return parameters; }

    public static Sig getSig() { return sig; }
    
    public static boolean isCloud() { return cloud; }

    public static File getHome() { return home; }

    public static File getHome(String sub) { return new File(home, sub); }

    public static GuiManager getGui() { return guiManager; }

    public static DatabaseManager getDatabaseManager() { return databaseManager; }

    public static HttpServer getHttpServer() { return httpServer; }

    public static Vsol4Service getVsol4Manager() { return vsol4Service; }

    public static OrthancService getOrthancManager() { return orthancService; }

    public static LocalSystem getSystem() { return system; }

    public static Session getSystemSession() { return systemSession; }

}
