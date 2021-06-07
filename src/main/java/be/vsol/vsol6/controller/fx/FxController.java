package be.vsol.vsol6.controller.fx;

import be.vsol.vsol6.Ctrl;
import javafx.scene.Node;

public abstract class FxController<N extends Node> {

    protected Ctrl ctrl;
    protected N root;

    // Methods

    public FxController() { }

    public FxController(Ctrl ctrl, N root) {
        load(ctrl, root);
    }

    public void load(Ctrl ctrl, N root) {
        this.ctrl = ctrl;
        this.root = root;
        init();
    }

    public void init() {
        // ready (but not necessary) to override
    }

    // Getters

    public N getRoot() { return root; }

}
