package be.vsol.vsol6;

import be.vsol.fx.FxConfig;
import be.vsol.http.HttpServer;
import be.vsol.orthanc.OrthancConfig;
import be.vsol.tools.Job;
import be.vsol.tools.Log;
import be.vsol.tools.Sig;
import be.vsol.tools.type.Int;
import be.vsol.vsol4.Vsol4Config;
import be.vsol.vsol6.controller.http.ApiHandler;
import be.vsol.vsol6.controller.http.WebHandler;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.File;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Map;

public class Vsol6 extends Application {

    private static final Sig sig = new Sig("VSOL6", 0, 0, 1, Sig.Publisher.SIGI_DEV, LocalDate.of(2021, Month.MAY, 15));

    private static File home;
    private static boolean cloud;

    private static FxConfig fxConfig;
    private static Vsol4Config vsol4Config;
    private static OrthancConfig orthancConfig;

    public static void main(String[] args) {
        launch(args);
    }

    @Override public void start(Stage primaryStage) {
        Map<String, String> namedParams = getParameters().getNamed();
        List<String> unnamedParams = getParameters().getUnnamed();

        try {
            home = new File(namedParams.getOrDefault("home", "C:/" + sig.getAppTitle()));
            Log.init(new File(home, "logs"), sig.getAppTitle());
            Log.out("Starting " + sig + ".");

            cloud = unnamedParams.contains("cloud");

            if (!cloud) {
                fxConfig = new FxConfig(new File(home, "config/gui.json"), namedParams);
                fxConfig.show(primaryStage);
            }
            vsol4Config = new Vsol4Config(new File(home, "config/vsol4.json"), namedParams);
            orthancConfig = new OrthancConfig(new File(home, "config/orthanc.json"), namedParams);

            new Job(() -> {
                new HttpServer(sig.getAppTitle() + " API Server", Int.parse(namedParams.get("api.port"), 8101), new ApiHandler());
                new HttpServer(sig.getAppTitle() + " Web Server", Int.parse(namedParams.get("web.port"), 8102), new WebHandler());
            });

        } catch (Exception e) {
            Log.trace(e);
            System.exit(1);
        }
    }

    @Override public void stop() {
        try {
            Log.out("Exiting.\n");

            if (fxConfig != null) fxConfig.save();
            if (vsol4Config != null) vsol4Config.save();
            if (orthancConfig != null) orthancConfig.save();

            System.exit(0);
        } catch (Exception e) {
            Log.trace(e);
        }
    }

    // Static getters

    public static boolean isCloud() { return cloud; }
    public static Sig getSig() { return sig; }
    public static File getHome() { return home; }

}
