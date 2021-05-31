package be.vsol.fx;

import be.vsol.vsol6.services.GuiService;
import javafx.scene.Node;

public abstract class FxController<N extends Node> {

    protected N root;
    protected GuiService gui;

    // Methods

    public void setRoot(N root) {
        this.root = root;
    }

    public void setGuiService(GuiService guiService) {
        this.gui = guiService;
    }

    public void init() {
        // ready (but not necessary) to override
    }

    // Getters

    public N getRoot() { return root; }

    public GuiService getGui() { return gui; }

}
