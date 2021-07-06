package be.vsol.vsol6.controller;

import be.vsol.fx.util.ImageIcon;
import be.vsol.util.*;
import be.vsol.vsol6.controller.fx.App;
import be.vsol.vsol6.controller.fx.FxController;
import be.vsol.vsol6.controller.fx.Splash;
import be.vsol.vsol6.controller.fx.app.Content;
import be.vsol.vsol6.controller.fx.app.Dialog;
import be.vsol.vsol6.controller.fx.app.Explorer;
import be.vsol.vsol6.controller.fx.Login;
import be.vsol.vsol6.model.config.Config;
import be.vsol.vsol6.model.config.Setting;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class Gui {

    private final Ctrl ctrl;
    private final Stage splashStage, primaryStage;

    private App app;
    private Content content;
    private Login login;
    private Explorer explorer;
    private Dialog dialog;

    // Constructors

    public Gui(Ctrl ctrl, Stage primaryStage) {
        this.ctrl = ctrl;
        this.splashStage = new Stage(StageStyle.UNDECORATED);
        this.primaryStage = primaryStage;

        setTitleAndIcon(primaryStage, splashStage);
    }

    public void showSplashScreen() {
        Splash splash = loadFxml("splash");
        if (splash != null) {
            splashStage.setScene(new Scene(splash.getRoot()));
            splashStage.show();
        } else {
            Log.err("Can't display splash screen.");
        }
    }

    public void start(Config config) {
        loadFXMLs();

        primaryStage.setWidth(config.gui.width);
        primaryStage.setHeight(config.gui.height);
        primaryStage.setX(config.gui.x);
        primaryStage.setY(config.gui.y);
        primaryStage.setMaximized(config.gui.maximized);
        if (config.gui.undecorated) primaryStage.initStyle(StageStyle.UNDECORATED);

        primaryStage.widthProperty().addListener((observable, oldValue, newValue) -> saveSetting(new Setting("gui.width", newValue.intValue())));
        primaryStage.heightProperty().addListener((observable, oldValue, newValue) -> saveSetting(new Setting("gui.height", newValue.intValue())));
        primaryStage.xProperty().addListener((observable, oldValue, newValue) -> saveSetting(new Setting("gui.x", newValue.intValue())));
        primaryStage.yProperty().addListener((observable, oldValue, newValue) -> saveSetting(new Setting("gui.y", newValue.intValue())));

        Platform.runLater(() -> {
            primaryStage.setScene(new Scene(app.getRoot()));
            splashStage.hide();
            primaryStage.show();
        });

        app.start();
    }

    public App getApp() { return app; }

    public Explorer getExplorer() { return explorer; }

    public Content getContent() {
        return content;
    }

    public Login getLogin() {
        return login;
    }

    public Dialog getDialog() { return  dialog;}

    private void loadFXMLs() {
        app = loadFxml("app");
        content = loadFxml("app/content");
        login = loadFxml("login");
        explorer = new Explorer(ctrl, "www.sporza.be");
        dialog = loadFxml("app/dialog");
    }

    private void saveSetting(Setting setting) {
        if (!primaryStage.isMaximized()) {
            Task.run("save " + setting.getKey(), 500, () -> ctrl.getSystemSession().saveSystem(setting));
        }
    }

    private void setTitleAndIcon(Stage... stages) {
        for (Stage stage : stages) {
            stage.setTitle(ctrl.getSig().toString());
            stage.getIcons().add(ImageIcon.get(true, "logo", 128));
        }
    }

    private <N extends Node, C extends FxController<N>> C loadFxml(String resource) {
        resource = Str.addon(resource, "fxml/", ".fxml");

        FXMLLoader fxmlLoader = new FXMLLoader();
        try {
            N root = fxmlLoader.load(Resource.getInputStream(resource));
            C controller = fxmlLoader.getController();
            controller.load(ctrl, root);
            return controller;
        } catch (IOException e) {
            Log.trace(e);
            return null;
        }
    }
}
