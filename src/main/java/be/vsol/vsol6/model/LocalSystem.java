package be.vsol.vsol6.model;

import be.vsol.util.Uid;

public class LocalSystem {

    private String uid, name;

    public LocalSystem() {
        String check = Uid.getMachineUuid();
    }

    @Override public String toString() {
        return "System";
    }

}
