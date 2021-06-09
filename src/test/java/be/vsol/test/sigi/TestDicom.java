package be.vsol.test.sigi;

import be.vsol.dicom.Dicom;
import be.vsol.dicom.DicomAttribute;
import be.vsol.dicom.model.DicomTag;

import java.io.File;
import java.util.TreeMap;
import java.util.TreeSet;

public class TestDicom {

    public static void main(String[] args) {
        readDicom();
//        compareDicomTags();
//        dicomAttributes();
    }

    private static void readDicom() {
        File file = new File("C:/Sandbox/dicom/6.dcm");
        Dicom dicom = new Dicom(file);

        System.out.println(dicom.getAttributes());
    }

    private static void compareDicomTags() {
        DicomTag a = DicomTag.PixelData;
        DicomTag b = DicomTag.AcquisitionDate;

        TreeMap<DicomTag, String> set = new TreeMap<>();
        set.put(a, "A");
        set.put(b, "B");

        System.out.println(set);
    }

    private static void dicomAttributes() {
        DicomAttribute a = new DicomAttribute(DicomTag.BodyPartExamined, "KNEE".getBytes());
        DicomAttribute b = new DicomAttribute(DicomTag.PatientName, "Sigi Janssens".getBytes());

        TreeMap<DicomTag, DicomAttribute> map = new TreeMap<>();
        map.put(a.getDicomTag(), a);
        map.put(b.getDicomTag(), b);

        System.out.println(map);
    }

}
