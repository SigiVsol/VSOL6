package be.vsol.dicom.model;

import be.vsol.dicom.Dicom;

public class SOPInstance extends DicomInstance {

    private final SeriesInstance seriesInstance;
    private Dicom dicom;
    private boolean active = true;

    // Constructors

    public SOPInstance(String uid, SeriesInstance seriesInstance) {
        super(uid);
        this.seriesInstance = seriesInstance;
    }

    // Methods

    @Override public String toString() {
        return uid;
    }


    // Getters

    public SeriesInstance getSeriesInstance() { return seriesInstance; }

    public StudyInstance getStudyInstance() { return seriesInstance == null ? null : seriesInstance.getStudyInstance(); }

    public Dicom getDicom() { return dicom; }

    public boolean isActive() { return active; }

    // Setters

    public void setDicom(Dicom dicom) { this.dicom = dicom; }

    public void setActive(boolean active) { this.active = active; }

}
