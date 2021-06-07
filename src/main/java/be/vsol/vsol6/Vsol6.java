package be.vsol.vsol6;

import be.vsol.util.Log;
import javafx.application.Application;
import javafx.stage.Stage;

public class Vsol6 extends Application {

    private Ctrl ctrl;

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * This method is called on launch of the JavaFX-program. It creates a Ctrl object, which will handle the program's further flow.
     * @param primaryStage The program's main JavaFX-stage
     */
    @Override public void start(Stage primaryStage) {
        try {
            ctrl = new Ctrl(getParameters().getNamed(), primaryStage);
        } catch (Exception e) {
            Log.trace(e);
        }
    }

    /**
     * This method will be called automatically when the last window of the GUI closes, so redirect to Ctrl.exit()
     */
    @Override public void stop() throws Exception {
        if (ctrl != null) ctrl.exit();
    }























    ////    private final File home;
//
//
//
//
//    private final Vector<Service> services = new Vector<>();
//
//    private GuiService guiService;
//    private DbService dbService;
//    private Vsol4Service vsol4Service;
//    private API api;
//
//    // Static Method
//
//    public static void main(String[] args) {
//        launch(args);
//    }
//
//    // Methods
//
//    @Override public void start(Stage primaryStage) {
//        try {
//            File home = new File(getParameters().getNamed().getOrDefault("home", sig.getFolder()));
//            Log.init(new File(home, "logs"), sig.getAppTitle(), getParameters().getUnnamed().contains("debug"));
//            Log.out("Starting " + sig + ".");
//
//            LocalStorage localStorage = new LocalStorage(new File(home, "data/localStorage"));
//            Map<String, String> params = getParameters().getNamed();
//            JSONObject jsonDefaults = new JSONObject(Resource.getString("config/defaults.json"));
//
//            Config appConfig = new Session(jsonDefaults, params).getConfig();
//
//            new Console(this).start();
//            if (appConfig.gui.visible) Gui.showSplashScreen(sig);
//
//            new Job(() -> {
//
//            });
//
//
//
////
//
//
//
////            Gui gui = new Gui(primaryStage);
////            Console console = new Console(this);
//
//
//
//
//
//
//
//
//
//
//
//
//
//
////            GuiService guiService = new GuiService(home, primaryStage, variables, localStorage);
////            DbService dbService = new DbService(home, programConfig.db);
////            Vsol4Service vsol4Service = new Vsol4Service();
//////            OrthancService orthancService = new OrthancService();
//////            ServerService serverService = new ServerService();
//////            ConsoleService consoleService = new ConsoleService(this);
////
////            if (programConfig.gui.visible) {
////                guiService.showSplash();
////            }
////
////            new Job(() -> {
//////                dbService.start();
////
////
////
////
////            });
//
//
////            DbService dbService = new DbService(home, programConfig.db);
////            dbService.start();
////
////
////
////            LocalSystem system = new LocalSystem();
////            Vsol4Service vsol4Service = new Vsol4Service(programConfig.vsol4);
////            OrthancService orthancService = new OrthancService(programConfig.orthanc);
////
////            GuiService guiService = new GuiService();
////            ServerService serverService = new ServerService();
////            ConsoleService consoleService = new ConsoleService();
////
////            if (programConfig.gui.visible) {
////                guiService.showSplash();
////            }
////
//
//
//
////            API api = switch (programConfig.app.backend) {
////                case vsol4 -> new Vsol4API();
////                default -> null;
////            };
//
//
//
//
////        if (programSession.getConfig().gui.visible) {
////            services.add(guiService = new GuiService(sig, home, localStorage, primaryStage));
////            guiService.showSplash(); // show splash screen while loading everything else in a thread
////        }
////
////        new Job(() -> {
////            services.add(databaseService = new DatabaseService(home, programSession));
////
////            Session systemSession = new Session(jsonDefaults, getParameters().getNamed(), databaseService, system, null, null);
////
////            switch (systemSession.getConfig().app.backend) {
////                case vsol4 -> {
////                    services.add(vsol4Service = new Vsol4Service(systemSession));
////                    api = new Vsol4API(vsol4Service);
////                }
////                // TODO bridge, vsol6
////            }
////
////            services.add(new OrthancService());
////            services.add(new ConsoleService(this));
////
////            services.add(new ServerService(systemSession, variables, api));
////
////            for (Service service : services) {
////                service.start();
////            }
////
////            if (guiService != null) {
////                guiService.showApp(systemSession);
////            }
////        });
//        } catch (Exception e) {
//            Log.trace(e);
//            System.exit(1);
//        }
//    }
//
//    @Override public void stop() {
//        try {
//            for (Service service : services) {
//                service.stop();
//            }
//
//            Log.out("Exiting.\n");
//            System.exit(0);
//        } catch (Exception e) {
//            Log.trace(e);
//        }
//    }
//
//    // Getters
//
//    public Sig getSig() { return sig; }

}
