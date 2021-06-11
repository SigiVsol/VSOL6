package be.vsol.dicom.model;

import java.util.Objects;

public abstract class DicomInstance {

    protected String uid;

    // Constructors

    public DicomInstance(String uid) {
        this.uid = uid;
    }

    // Methods

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SOPInstance that = (SOPInstance) o;
        return uid.equals(that.uid);
    }

    @Override public int hashCode() {
        return Objects.hash(uid);
    }

    // Getters

    public String getUid() { return uid; }

}
