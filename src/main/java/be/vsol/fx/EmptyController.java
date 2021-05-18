package be.vsol.fx;

import javafx.scene.layout.BorderPane;

public class EmptyController extends FxController<BorderPane> {

    public EmptyController() {
        setRoot(new BorderPane());
    }

}
