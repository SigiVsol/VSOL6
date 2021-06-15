package be.vsol.vsol6.controller.backend;

import be.vsol.dicom.Dicom;
import be.vsol.dicom.model.SOPInstance;
import be.vsol.dicom.model.SeriesInstance;
import be.vsol.dicom.model.StudyInstance;
import be.vsol.vsol6.controller.Ctrl;

public abstract class DicomStorage {

    protected final Ctrl ctrl;

    // Constructors

    public DicomStorage(Ctrl ctrl) {
        this.ctrl = ctrl;
    }

    // Abstract Methods

    public abstract StudyInstance getStudyInstance(String studyID);

    public abstract Dicom getFirstDicomOf(StudyInstance studyInstance);

}
