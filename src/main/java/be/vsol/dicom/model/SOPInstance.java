package be.vsol.dicom.model;

import be.vsol.dicom.Dicom;

public class SOPInstance extends DicomInstance {

    private Dicom dicom;

    // Constructors

    public SOPInstance(String uid) {
        super(uid);
    }

    public SOPInstance(String uid, Dicom dicom) {
        super(uid);
        this.dicom = dicom;
    }

    // Getters

    public Dicom getDicom() { return dicom; }

    // Setters

    public void setDicom(Dicom dicom) { this.dicom = dicom; }

}
