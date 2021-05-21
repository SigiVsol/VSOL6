package be.vsol.vsol6.model.setting;

import be.vsol.database.annotations.Db;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class GuiConfig extends Config {

    @Db private int width, height, x, y;
    @Db private boolean maximized, undecorated;

    public GuiConfig() {
        super("guiConfigs");
    }

    public GuiConfig(Stage stage) {
        this();
        width = (int) stage.getWidth();
        height = (int) stage.getHeight();
        x = (int) stage.getX();
        y = (int) stage.getY();
        maximized = stage.isMaximized();
        undecorated = stage.getStyle() == StageStyle.UNDECORATED;
    }

    // Getters

    public int getWidth() { return width; }

    public int getHeight() { return height; }

    public int getX() { return x; }

    public int getY() { return y; }

    public boolean isMaximized() { return maximized; }

    public boolean isUndecorated() { return undecorated; }

    // Setters

    public void setWidth(int width) { this.width = width; }

    public void setHeight(int height) { this.height = height; }

    public void setX(int x) { this.x = x; }

    public void setY(int y) { this.y = y; }

    public void setMaximized(boolean maximized) { this.maximized = maximized; }

    public void setUndecorated(boolean undecorated) { this.undecorated = undecorated; }

}
