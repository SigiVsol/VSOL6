package be.vsol.dicom.model;

import java.util.Vector;

public class StudyInstance extends DicomInstance {

    private final Vector<SeriesInstance> series = new Vector<>();

    // Constructors

    public StudyInstance(String uid) {
        super(uid);
    }

    // Methods

    @Override public String toString() {
        String result = uid;
        for (SeriesInstance seriesInstance : series) {
            result += "\n\t" + seriesInstance;
        }
        return result;
    }

    // Getters

    public Vector<SeriesInstance> getSeries() { return series; }

}
