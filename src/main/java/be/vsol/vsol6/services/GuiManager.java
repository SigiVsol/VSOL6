package be.vsol.vsol6.services;

import be.vsol.fx.FxController;
import be.vsol.tools.Service;
import be.vsol.util.Icon;
import be.vsol.util.Log;
import be.vsol.util.Resource;
import be.vsol.util.Str;
import be.vsol.vsol6.Vsol6;
import be.vsol.vsol6.controller.fx.App;
import be.vsol.vsol6.controller.fx.Splash;
import be.vsol.vsol6.controller.fx.app.Explorer;
import be.vsol.vsol6.controller.fx.app.Login;
import be.vsol.vsol6.controller.fx.app.Settings;
import be.vsol.vsol6.model.setting.gui;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class GuiManager implements Service {

    private boolean running = false;
    private final Stage primaryStage;

    private final Splash splash;
    private App app;
    private Login login;
    private Explorer explorer;
    private Settings settings;

    // Constructor

    public GuiManager(Stage primaryStage) {
        this.primaryStage = primaryStage;

        primaryStage.getIcons().add(Icon.getImage(true, "logo", 64));
        primaryStage.setTitle(Vsol6.getSig().getAppTitle());

        primaryStage.setWidth(gui.width);
        primaryStage.setHeight(gui.height);
        if (gui.x != null) primaryStage.setX(gui.x);
        if (gui.y != null) primaryStage.setX(gui.y);
        primaryStage.setMaximized(gui.maximized);
        if (gui.undecorated) primaryStage.initStyle(StageStyle.UNDECORATED);

        splash = loadFxml("Splash");
    }

    // Methods

    public void showSplash(String text) {
        splash.setText(text);
        show(splash);
    }

    public void showApp() {
        show(app);
    }

    @Override public void start() {
        app = loadFxml("App");
        login = loadFxml("app/Login");
        settings = loadFxml("app/Settings");
        explorer = new Explorer();

        running = true;
    }

    @Override public void stop() {
        running = false;

        Vsol6.getSettingsManager().save(gui.class, "width", primaryStage.getWidth(), Vsol6.getSystem(), null, null);

        System.out.println(primaryStage.getWidth() + " x " + primaryStage.getHeight());
    }

//    private void addListeners() {
//        primaryStage.widthProperty().addListener((observable, oldValue, newValue) -> {
//            if (!primaryStage.isMaximized() && !primaryStage.isIconified()) {
////                width = newValue.intValue();
//            }
//        });
//        primaryStage.heightProperty().addListener((observable, oldValue, newValue) -> {
//            if (!primaryStage.isMaximized() && !primaryStage.isIconified()) {
////                height = newValue.intValue();
//            }
//        });
//        primaryStage.xProperty().addListener((observable, oldValue, newValue) -> {
//            if (!primaryStage.isMaximized() && !primaryStage.isIconified()) {
////                x = newValue.intValue();
//            }
//        });
//        primaryStage.yProperty().addListener((observable, oldValue, newValue) -> {
//            if (!primaryStage.isMaximized() && !primaryStage.isIconified()) {
////                y = newValue.intValue();
//            }
//        });
//        primaryStage.maximizedProperty().addListener((observable, oldValue, newValue) -> {
////            maximized = newValue;
//        });
//    }

    private <C extends FxController<?>> void show(C controller) {
        Platform.runLater(() -> {
            primaryStage.setScene(new Scene((Parent) controller.getRoot()));
            primaryStage.show();
        });
    }

    private <N extends Node, C extends FxController<N>> C loadFxml(String resource) {
        resource = Str.addon(resource, "fxml/", ".fxml");

        FXMLLoader fxmlLoader = new FXMLLoader();
        try {
            N root = fxmlLoader.load(Resource.getInputStream(resource));
            C controller = fxmlLoader.getController();
            controller.setRoot(root);
            controller.init();
            return controller;
        } catch (IOException e) {
            Log.trace(e);
            return null;
        }
    }

    // Getters

    public Stage getPrimaryStage() { return primaryStage; }

    public App getApp() { return app; }

    public Login getLogin() { return login; }

    public Explorer getExplorer() { return explorer; }

    public Settings getSettings() { return settings; }

    @Override public boolean isRunning() { return running; }
}
