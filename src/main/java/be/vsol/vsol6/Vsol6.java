package be.vsol.vsol6;

import be.vsol.http.HttpServer;
import be.vsol.orthanc.Orthanc;
import be.vsol.tools.Job;
import be.vsol.tools.Service;
import be.vsol.tools.Sig;
import be.vsol.util.Int;
import be.vsol.util.Log;
import be.vsol.vsol4.Vsol4;
import be.vsol.vsol6.controller.http.ApiHandler;
import be.vsol.vsol6.controller.http.WebHandler;
import be.vsol.vsol6.controller.view.Gui;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.File;
import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Vsol6 extends Application {

    private static final Sig sig = new Sig("VSOL6", 0, 0, 4, Sig.Publisher.SIGI_DEV, LocalDate.of(2021, Month.MAY, 15));

    private static File home;
    private static boolean cloud;
    private static boolean initialized = false;

    private static Gui gui;
    private static final HashMap<String, Service> services = new HashMap<>();

    // Static Method

    public static void main(String[] args) {
        launch(args);
    }

    // Methods

    @Override public void start(Stage primaryStage) {
        Map<String, String> namedParams = getParameters().getNamed();
        List<String> unnamedParams = getParameters().getUnnamed();

        try {
            home = new File(namedParams.getOrDefault("home", "C:/" + sig.getAppTitle()));
            Log.init(new File(home, "logs"), sig.getAppTitle());
            Log.out("Starting " + sig + ".");

            cloud = unnamedParams.contains("cloud");

            if (!cloud) {
                gui = new Gui(home, primaryStage, namedParams);
                gui.start();
            }

            new Job(() -> {
                services.put("webServer", new HttpServer(sig.getAppTitle() + " Web Server", Int.parse(namedParams.get("web.port"), 8100), new WebHandler()));
                services.put("apiServer", new HttpServer(sig.getAppTitle() + " API Server", Int.parse(namedParams.get("api.port"), 8101), new ApiHandler()));
                services.put("vsol4", new Vsol4(home, namedParams));
                services.put("orthanc", new Orthanc(home, namedParams));

                for (Service service : services.values()) {
                    service.start();
                }

                initialized = true;
            });
        } catch (Exception e) {
            Log.trace(e);
            System.exit(1);
        }
    }

    @Override public void stop() {
        try {
            Log.out("Exiting.\n");

            for (Service service : services.values()) {
                service.stop();
            }

            gui.stop();

            System.exit(0);
        } catch (Exception e) {
            Log.trace(e);
        }
    }

    // Static Getters

    public static boolean isCloud() { return cloud; }
    public static Sig getSig() { return sig; }
    public static File getHome() { return home; }
    public static boolean isInitialized() { return initialized; }
    public static Gui getGui() { return gui; }
    public static Vsol4 getVsol4() { return (Vsol4) services.get("vsol4"); }
    public static Orthanc getOrthanc() { return (Orthanc) services.get("orthanc"); }

}
