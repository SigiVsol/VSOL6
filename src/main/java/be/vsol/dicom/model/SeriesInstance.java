package be.vsol.dicom.model;

import java.util.Vector;

public class SeriesInstance extends DicomInstance {

    private final StudyInstance studyInstance;
    private final Vector<SOPInstance> instances = new Vector<>();

    // Constructors

    public SeriesInstance(String uid, StudyInstance studyInstance) {
        super(uid);
        this.studyInstance = studyInstance;
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

    public Vector<SOPInstance> getActiveInstances() {
        Vector<SOPInstance> result = new Vector<>();
        for (SOPInstance sopInstance : instances) {
            if (sopInstance.isActive()) {
                result.add(sopInstance);
            }
        }
        return result;
    }

    public StudyInstance getStudyInstance() { return studyInstance; }

}
