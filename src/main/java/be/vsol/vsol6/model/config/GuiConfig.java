package be.vsol.vsol6.model.config;

import be.vsol.database.annotations.Db;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
public class GuiConfig extends Config {

    @Db private int width, height, x, y;
    @Db private boolean maximized, undecorated;
    @Db private boolean leftHanded;

    public GuiConfig() {
        super("gui");
    }

    public GuiConfig(Stage stage, GuiConfig other) {
        this();

        if (stage.isIconified() || stage.isMaximized()) {
            width = other.getWidth();
            height = other.getHeight();
            x = other.getX();
            y = other.getY();
        } else {
            width = (int) stage.getWidth();
            height = (int) stage.getHeight();
            x = (int) stage.getX();
            y = (int) stage.getY();
        }

        maximized = stage.isMaximized();
        undecorated = stage.getStyle() == StageStyle.UNDECORATED;
        leftHanded = other.isLeftHanded();
    }

    // Getters

    public int getWidth() { return width; }

    public int getHeight() { return height; }

    public int getX() { return x; }

    public int getY() { return y; }

    public boolean isMaximized() { return maximized; }

    public boolean isUndecorated() { return undecorated; }

    public boolean isLeftHanded() { return leftHanded; }

    // Setters

    public void setWidth(int width) { this.width = width; }

    public void setHeight(int height) { this.height = height; }

    public void setX(int x) { this.x = x; }

    public void setY(int y) { this.y = y; }

    public void setMaximized(boolean maximized) { this.maximized = maximized; }

    public void setUndecorated(boolean undecorated) { this.undecorated = undecorated; }

    public void setLeftHanded(boolean leftHanded) { this.leftHanded = leftHanded; }

}
