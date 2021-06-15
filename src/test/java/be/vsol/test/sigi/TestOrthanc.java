package be.vsol.test.sigi;

import be.vsol.dicom.Dicom;
import be.vsol.dicom.model.SOPInstance;
import be.vsol.dicom.model.SeriesInstance;
import be.vsol.dicom.model.StudyInstance;
import be.vsol.orthanc.Orthanc;
import be.vsol.util.FileSys;

import java.io.File;

public class TestOrthanc {

    private static final String path = "C:/Sandbox/orthanc";

    public static void main(String[] args) {
        Orthanc orthanc = new Orthanc();
        orthanc.start("localhost", 8800, 2500);


    }

}
