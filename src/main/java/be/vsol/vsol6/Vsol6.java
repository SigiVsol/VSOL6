package be.vsol.vsol6;

import be.vsol.tools.Job;
import be.vsol.tools.LocalStorage;
import be.vsol.tools.Service;
import be.vsol.tools.Sig;
import be.vsol.util.Icon;
import be.vsol.util.Log;
import be.vsol.util.Resource;
import be.vsol.vsol6.controller.api.API;
import be.vsol.vsol6.controller.api.Vsol4API;
import be.vsol.vsol6.model.LocalSystem;
import be.vsol.vsol6.model.config.Config;
import be.vsol.vsol6.services.*;
import be.vsol.vsol6.session.Session;
import javafx.application.Application;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.io.File;
import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class Vsol6 extends Application {

    private static final Sig sig = new Sig("VSOL6", 0, 0, 6, Sig.Publisher.SIGI_DEV, LocalDate.of(2021, Month.MAY, 15));

//    private final File home;




    private final Vector<Service> services = new Vector<>();

    private GuiService guiService;
    private DbService dbService;
    private Vsol4Service vsol4Service;
    private API api;

    // Static Method

    public static void main(String[] args) {
        launch(args);
    }

    // Methods

    @Override public void start(Stage primaryStage) {
        try {
            File home = new File(getParameters().getNamed().getOrDefault("home", sig.getFolder()));
            Log.init(new File(home, "logs"), sig.getAppTitle(), getParameters().getUnnamed().contains("debug"));
            Log.out("Starting " + sig + ".");
            Log.debug("Debug mode is on.");

            LocalStorage localStorage = new LocalStorage(new File(home, "data/localStorage"));
            Map<String, String> variables = getVariables();
            Map<String, String> params = getParameters().getNamed();
            JSONObject jsonDefaults = new JSONObject(Resource.getString("config/defaults.json"));

            Config programConfig = new Session(jsonDefaults, params).getConfig();

            GuiService guiService = new GuiService(home, primaryStage, variables, localStorage);
            DbService dbService = new DbService(home, programConfig.db);
            Vsol4Service vsol4Service = new Vsol4Service();
//            OrthancService orthancService = new OrthancService();
//            ServerService serverService = new ServerService();
//            ConsoleService consoleService = new ConsoleService(this);

            if (programConfig.gui.visible) {
                guiService.showSplash();
            }

            new Job(() -> {
//                dbService.start();




            });


//            DbService dbService = new DbService(home, programConfig.db);
//            dbService.start();
//
//
//
//            LocalSystem system = new LocalSystem();
//            Vsol4Service vsol4Service = new Vsol4Service(programConfig.vsol4);
//            OrthancService orthancService = new OrthancService(programConfig.orthanc);
//
//            GuiService guiService = new GuiService();
//            ServerService serverService = new ServerService();
//            ConsoleService consoleService = new ConsoleService();
//
//            if (programConfig.gui.visible) {
//                guiService.showSplash();
//            }
//



//            API api = switch (programConfig.app.backend) {
//                case vsol4 -> new Vsol4API();
//                default -> null;
//            };




//        if (programSession.getConfig().gui.visible) {
//            services.add(guiService = new GuiService(sig, home, localStorage, primaryStage));
//            guiService.showSplash(); // show splash screen while loading everything else in a thread
//        }
//
//        new Job(() -> {
//            services.add(databaseService = new DatabaseService(home, programSession));
//
//            Session systemSession = new Session(jsonDefaults, getParameters().getNamed(), databaseService, system, null, null);
//
//            switch (systemSession.getConfig().app.backend) {
//                case vsol4 -> {
//                    services.add(vsol4Service = new Vsol4Service(systemSession));
//                    api = new Vsol4API(vsol4Service);
//                }
//                // TODO bridge, vsol6
//            }
//
//            services.add(new OrthancService());
//            services.add(new ConsoleService(this));
//
//            services.add(new ServerService(systemSession, variables, api));
//
//            for (Service service : services) {
//                service.start();
//            }
//
//            if (guiService != null) {
//                guiService.showApp(systemSession);
//            }
//        });
        } catch (Exception e) {
            Log.trace(e);
            System.exit(1);
        }
    }

    @Override public void stop() {
        try {
            for (Service service : services) {
                service.stop();
            }

            Log.out("Exiting.\n");
            System.exit(0);
        } catch (Exception e) {
            Log.trace(e);
        }
    }

    private Map<String, String> getVariables() {
        HashMap<String, String> result = new HashMap<>();
        result.put("app.name", sig.getAppTitle());
        result.put("app.version", sig.getVersion());
        return result;
    }

    // Getters



}
