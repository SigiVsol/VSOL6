package be.vsol.vsol6.services;

import be.vsol.fx.FxController;
import be.vsol.tools.Service;
import be.vsol.tools.Sig;
import be.vsol.util.*;
import be.vsol.vsol6.Vsol6;
import be.vsol.vsol6.controller.fx.App;
import be.vsol.vsol6.controller.fx.Splash;
import be.vsol.vsol6.controller.fx.app.Explorer;
import be.vsol.vsol6.controller.fx.app.Login;
import be.vsol.vsol6.controller.fx.app.Settings;
import be.vsol.vsol6.model.config.Config;
import be.vsol.vsol6.model.config.Setting;
import be.vsol.vsol6.session.Session;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;

public class GuiService implements Service {

    private final Stage splashStage, primaryStage;
    private final Sig sig;
    private final File home;

    private Session session;

    private final Splash splash;
    private App app;
    private Login login;
    private Explorer explorer;
    private Settings settings;

    // Constructor

    public GuiService(Sig sig, File home, Stage primaryStage) {
        this.sig = sig;
        this.home = home;
        this.primaryStage = primaryStage;
        this.splashStage = new Stage(StageStyle.UNDECORATED);

        splash = loadFxml("splash");
    }

    // Methods

    @Override public void start() {
        app = loadFxml("app");
        login = loadFxml("app/login");
        settings = loadFxml("app/settings");
        explorer = new Explorer();
    }

    @Override public void stop() { }

    public void showSplash() {
        setTitleAndLogo(splashStage);

        splashStage.setScene(new Scene(splash.getRoot()));
        splashStage.show();
    }

    public void showApp(Session session) {
        this.session = session;

        Platform.runLater(() -> {
            splashStage.hide();

            setTitleAndLogo(primaryStage);

            Config.gui gui = session.getConfig().gui;
            primaryStage.setWidth(gui.width);
            primaryStage.setHeight(gui.height);
            primaryStage.setX(gui.x);
            primaryStage.setY(gui.y);
            primaryStage.setMaximized(gui.maximized);
            if (gui.undecorated) primaryStage.initStyle(StageStyle.UNDECORATED);

            primaryStage.setScene(new Scene(app.getRoot()));

            explorer.loadUrl("http://localhost:8100");
            app.show(explorer);

            primaryStage.show();
            addListeners();
        });
    }

    private void addListeners() {
        primaryStage.widthProperty().addListener((observable, oldValue, newValue) -> {
            if (!primaryStage.isMaximized()) {
                Task.run("save gui.width", 500, () -> session.saveSystem(new Setting("gui.width", newValue.intValue())));
            }
        });

        primaryStage.heightProperty().addListener((observable, oldValue, newValue) -> {
            if (!primaryStage.isMaximized()) {
                Task.run("save gui.height", 500, () -> session.saveSystem(new Setting("gui.height", newValue.intValue())));
            }
        });

        primaryStage.xProperty().addListener((observable, oldValue, newValue) -> {
            if (!primaryStage.isMaximized()) {
                Task.run("save gui.x", 500, () -> session.saveSystem(new Setting("gui.x", newValue.intValue())));
            }
        });

        primaryStage.yProperty().addListener((observable, oldValue, newValue) -> {
            if (!primaryStage.isMaximized()) {
                Task.run("save gui.y", 500, () -> session.saveSystem(new Setting("gui.y", newValue.intValue())));
            }
        });

        primaryStage.maximizedProperty().addListener((observable, oldValue, newValue) -> session.saveSystem(new Setting("gui.maximized", newValue)));
    }

    private void setTitleAndLogo(Stage stage) {
        stage.setTitle(sig.getAppTitle());
        stage.getIcons().add(Icon.getImage(true, "logo", 64));
    }

    private <N extends Node, C extends FxController<N>> C loadFxml(String resource) {
        resource = Str.addon(resource, "fxml/", ".fxml");

        FXMLLoader fxmlLoader = new FXMLLoader();
        try {
            N root = fxmlLoader.load(Resource.getInputStream(resource));
            C controller = fxmlLoader.getController();
            controller.setRoot(root);
            controller.setGuiService(this);
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

    public Sig getSig() { return sig; }

    public File getHome() { return home; }

}