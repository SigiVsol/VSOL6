package be.vsol.fx;

import be.vsol.interfaces.JsonField;
import be.vsol.interfaces.StrMapField;
import be.vsol.tools.Config;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.util.Map;

public class FxConfig extends Config {

    @JsonField @StrMapField private int width = 800, height = 600;
    @JsonField @StrMapField private Integer x = null, y = null;
    @JsonField @StrMapField private boolean maximized = false, undecorated = false, leftHanded = false;

    public FxConfig(File file, Map<String, String> namedParams) {
        super(file);
        super.load(namedParams, "gui");
    }

    public void show(Stage primaryStage) {
        primaryStage.setWidth(width);
        primaryStage.setHeight(height);
        if (x != null) primaryStage.setX(x);
        if (y != null) primaryStage.setY(y);
        primaryStage.setMaximized(maximized);
        if (undecorated) primaryStage.initStyle(StageStyle.UNDECORATED);

        primaryStage.widthProperty().addListener((observable, oldValue, newValue) -> {
            if (!primaryStage.isMaximized() && !primaryStage.isIconified()) {
                width = newValue.intValue();
            }
        });
        primaryStage.heightProperty().addListener((observable, oldValue, newValue) -> {
            if (!primaryStage.isMaximized() && !primaryStage.isIconified()) {
                height = newValue.intValue();
            }
        });
        primaryStage.xProperty().addListener((observable, oldValue, newValue) -> {
            if (!primaryStage.isMaximized() && !primaryStage.isIconified()) {
                x = newValue.intValue();
            }
        });
        primaryStage.yProperty().addListener((observable, oldValue, newValue) -> {
            if (!primaryStage.isMaximized() && !primaryStage.isIconified()) {
                y = newValue.intValue();
            }
        });
        primaryStage.maximizedProperty().addListener((observable, oldValue, newValue) -> {
            maximized = newValue;
        });

        primaryStage.show();
    }

    public boolean isLeftHanded() { return leftHanded; }

    public void setUndecorated(boolean undecorated) { this.undecorated = undecorated; }
    public void setLeftHanded(boolean leftHanded) { this.leftHanded = leftHanded; }

}
