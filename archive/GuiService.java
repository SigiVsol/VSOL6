//package be.vsol.vsol6.services;
//
//import be.vsol.tools.LocalStorage;
//import be.vsol.util.*;
//import be.vsol.vsol6.controller.fx.App;
//import be.vsol.vsol6.controller.fx.FxController;
//import be.vsol.vsol6.controller.fx.Splash;
//import be.vsol.vsol6.controller.fx.app.Explorer;
//import be.vsol.vsol6.controller.fx.Login;
//import be.vsol.vsol6.controller.fx.app.Settings;
//import be.vsol.vsol6.model.config.Config;
//import be.vsol.vsol6.model.config.Setting;
//import be.vsol.vsol6.session.SessionOld;
//import javafx.application.Platform;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Node;
//import javafx.scene.Scene;
//import javafx.stage.Stage;
//import javafx.stage.StageStyle;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.Map;
//
//public class GuiService {
//
//    private final Stage splashStage, primaryStage;
//    private final File home;
//    private final LocalStorage localStorage;
//
//    private final Splash splash;
//
//    private App app;
//    private Login login;
//    private Explorer explorer;
//    private Settings settings;
//
////    private SessionOld localSessionOld;
//
//    // Constructor
//
//    public GuiService(File home, Stage primaryStage, Map<String, String> variables, LocalStorage localStorage) {
//        this.home = home;
//        this.localStorage = localStorage;
//        this.primaryStage = primaryStage;
//        this.splashStage = new Stage(StageStyle.UNDECORATED);
//
//        splash = loadFxml("splash");
//    }
//
//    // Methods
//
//    public void showSplash() {
//        setTitleAndLogo(splashStage);
//
//        splashStage.setScene(new Scene(splash.getRoot()));
//        splashStage.show();
//    }
//
//    public void start() {
//        app = loadFxml("app");
//        login = loadFxml("app/login");
//        settings = loadFxml("app/settings");
//        explorer = new Explorer(this);
//    }
//
////    @Override public void stop() { }
//
//
//
//    public void showApp(SessionOld systemSessionOld) {
////        Organization organization = api.getOrganization(localStorage.get("organization.id", null));
////        User user = api.getUser(localStorage.get("user.id", null));
////        new Session(jsonDefaults, getParameters().getNamed(), databaseService, system, organization, user);
//
//        Platform.runLater(() -> {
//            setTitleAndLogo(primaryStage);
//
//            Config.gui gui = systemSessionOld.getConfig().gui;
//            primaryStage.setWidth(gui.width);
//            primaryStage.setHeight(gui.height);
//            primaryStage.setX(gui.x);
//            primaryStage.setY(gui.y);
//            primaryStage.setMaximized(gui.maximized);
//            if (gui.undecorated) primaryStage.initStyle(StageStyle.UNDECORATED);
//            primaryStage.setScene(new Scene(app.getRoot()));
//            addListeners(systemSessionOld);
//
//            app.home();
//
//            splashStage.hide();
//            primaryStage.show();
//        });
//    }
//
//    private void addListeners(SessionOld systemSessionOld) {
//        primaryStage.widthProperty().addListener((observable, oldValue, newValue) -> {
//            if (!primaryStage.isMaximized()) {
//                Task.run("save gui.width", 500, () -> systemSessionOld.saveSystem(new Setting("gui.width", newValue.intValue())));
//            }
//        });
//
//        primaryStage.heightProperty().addListener((observable, oldValue, newValue) -> {
//            if (!primaryStage.isMaximized()) {
//                Task.run("save gui.height", 500, () -> systemSessionOld.saveSystem(new Setting("gui.height", newValue.intValue())));
//            }
//        });
//
//        primaryStage.xProperty().addListener((observable, oldValue, newValue) -> {
//            if (!primaryStage.isMaximized()) {
//                Task.run("save gui.x", 500, () -> systemSessionOld.saveSystem(new Setting("gui.x", newValue.intValue())));
//            }
//        });
//
//        primaryStage.yProperty().addListener((observable, oldValue, newValue) -> {
//            if (!primaryStage.isMaximized()) {
//                Task.run("save gui.y", 500, () -> systemSessionOld.saveSystem(new Setting("gui.y", newValue.intValue())));
//            }
//        });
//
//        primaryStage.maximizedProperty().addListener((observable, oldValue, newValue) -> systemSessionOld.saveSystem(new Setting("gui.maximized", newValue)));
//    }
//
//    private void setTitleAndLogo(Stage stage) {
////        stage.setTitle(sig.getAppTitle());
//        stage.getIcons().add(Icon.getImage(true, "logo", 64));
//    }
//
//    private <N extends Node, C extends FxController<N>> C loadFxml(String resource) {
//        resource = Str.addon(resource, "fxml/", ".fxml");
//
//        FXMLLoader fxmlLoader = new FXMLLoader();
//        try {
//            N root = fxmlLoader.load(Resource.getInputStream(resource));
//            C controller = fxmlLoader.getController();
////            controller.setRoot(root);
////            controller.setGuiService(this);
//            controller.init();
//            return controller;
//        } catch (IOException e) {
//            Log.trace(e);
//            return null;
//        }
//    }
//
//    // Getters
//
//    public Stage getPrimaryStage() { return primaryStage; }
//
//    public App getApp() { return app; }
//
//    public Login getLogin() { return login; }
//
//    public Explorer getExplorer() { return explorer; }
//
//    public Settings getSettings() { return settings; }
//
//    public File getHome() { return home; }
//
//    public SessionOld getLocalSession() { return localSessionOld; }
//
//}
