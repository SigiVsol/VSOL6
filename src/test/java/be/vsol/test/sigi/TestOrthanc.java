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

        StudyInstance studyInstance = orthanc.getStudyInstance("9ee3de2f-185c3598-fbea901c-eea4f020-7e6dbd28");

        SeriesInstance seriesInstance = studyInstance.getSeries().firstElement();
        SOPInstance sopInstance = seriesInstance.getInstances().firstElement();

        orthanc.loadDicomInto(sopInstance);

        Dicom dicom = sopInstance.getDicom();



        FileSys.writeBytes(new File(path, "1.dcm"), dicom.getBytes());
    }

}
