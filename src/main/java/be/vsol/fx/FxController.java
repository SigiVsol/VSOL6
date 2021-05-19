package be.vsol.fx;

import javafx.scene.Node;

public abstract class FxController<N extends Node> {

    private N root;

    // Methods

    public void setRoot(N root) {
        this.root = root;
    }

    public void init() {
        // ready (but not necessary) to override
    }

    // Getters

    public N getRoot() { return root; }

}
