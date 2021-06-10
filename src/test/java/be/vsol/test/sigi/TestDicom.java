package be.vsol.test.sigi;

import be.vsol.dicom.Dicom;
import be.vsol.dicom.DicomAttribute;
import be.vsol.dicom.DicomOutputStream;
import be.vsol.dicom.model.DicomTag;
import be.vsol.dicom.model.VR;
import be.vsol.img.Jpg;
import be.vsol.util.FileSys;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.TreeMap;

public class TestDicom {

    private static final String path = "C:/Sandbox/dicom";

    public static void main(String[] args) {
//        readDicom();
//        compareDicomTags();
//        dicomAttributes();

//        copyDicom();

        createDicom();

//        outputStream();

//        img();
    }

    private static void readDicom() {
        File file = new File("C:/Sandbox/dicom/6.dcm");
        Dicom dicom = new Dicom(file);

        System.out.println(dicom.getAttributes());

        File copy = new File("C:/Sandbox/dicom/6-copy.dcm");
        FileSys.writeBytes(copy, dicom.getBytes());

        DicomAttribute pixelData = dicom.getAttributes().get(DicomTag.PixelData);
        Jpg jpg = pixelData.getValueAsJpg();

        File jpgFile = new File("C:/Sandbox/dicom/6.jpg");
        FileSys.writeBytes(jpgFile, jpg.getBytes());
    }

    private static void copyDicom() {
        File file = new File("C:/Sandbox/dicom/6.dcm");
        File copy = new File("C:/Sandbox/dicom/6-copy.dcm");

        Dicom dicom = new Dicom(file);

        DicomAttribute pixelData = dicom.getAttributes().get(DicomTag.PixelData);
        Jpg jpg = pixelData.getValueAsJpg();

        dicom.generateUIDs();
        dicom.putAttribute(new DicomAttribute(jpg));

        FileSys.writeBytes(copy, dicom.getBytes());
    }

    private static void compareDicomTags() {
        DicomTag a = DicomTag.PixelData;
        DicomTag b = DicomTag.AcquisitionDate;

        TreeMap<DicomTag, String> set = new TreeMap<>();
        set.put(a, "A");
        set.put(b, "B");

        System.out.println(set);
    }

    private static void createDicom() {
        File file = new File(path, "6.jpg");
        Jpg jpg = new Jpg(file);
        Dicom dicom = new Dicom(jpg);

        FileSys.writeBytes(new File("C:/Sandbox/dicom/6-composed.dcm"), dicom.getBytes());
    }

    private static void outputStream() {
        DicomOutputStream out = new DicomOutputStream();

        out.writeAttribute(new DicomAttribute(DicomTag.Item, null, new byte[4]));

        FileSys.writeBytes(new File("C:/Sandbox/dicom/stream"), out.toByteArray());
    }

    private static void img() {
        File file = new File(path, "6.dcm");
        Dicom dicom = new Dicom(file);
        Jpg jpg = dicom.getAttributes().get(DicomTag.PixelData).getValueAsJpg();
        BufferedImage bufferedImage = jpg.getBufferedImage();

        System.out.println(bufferedImage.getWidth() + " x " + bufferedImage.getHeight());

        System.out.println(bufferedImage.getType() == BufferedImage.TYPE_BYTE_GRAY);
    }

}
