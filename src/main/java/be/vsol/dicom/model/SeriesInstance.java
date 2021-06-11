package be.vsol.dicom.model;

import java.util.Vector;

public class SeriesInstance extends DicomInstance {

    private final Vector<SOPInstance> instances = new Vector<>();

    // Constructors

    public SeriesInstance(String uid) {
        super(uid);
    }

    // Methods

    @Override public String toString() {
        String result = uid;
        for (SOPInstance instance : instances) {
            result += "\n\t\t" + instance;
        }
        return result;
    }

    // Getters

    public Vector<SOPInstance> getInstances() { return instances; }

}
