package be.vsol.test.sigi;

import be.vsol.dicom.Dicom;
import be.vsol.dicom.DicomAttribute;
import be.vsol.dicom.DicomOutputStream;
import be.vsol.dicom.model.DicomTag;
import be.vsol.dicom.model.DicomTag.Name;
import be.vsol.img.Jpg;
import be.vsol.util.Debug;
import be.vsol.util.FileSys;
import be.vsol.util.Resource;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.TreeMap;

public class TestDicom {

    private static final String path = "C:/Sandbox/dicom";

    public static void main(String[] args) {
        readDicom();
//        compareDicomTags();
//        dicomAttributes();

//        copyDicom();

//        createDicom();

//        outputStream();

//        img();

//        tag();
    }

    private static void readDicom() {
        File file = new File("C:/Sandbox/dicom/6.dcm");
        Dicom dicom = new Dicom(file);

        System.out.println(dicom.getAttributes());

        File copy = new File("C:/Sandbox/dicom/6-copy.dcm");
        FileSys.writeBytes(copy, dicom.getBytes());

        DicomAttribute pixelData = dicom.getAttributes().get(Name.PixelData.getTag());
        Jpg jpg = pixelData.getValueAsJpg();

        File jpgFile = new File("C:/Sandbox/dicom/6.jpg");
        FileSys.writeBytes(jpgFile, jpg.getBytes());
    }

    private static void copyDicom() {
        File file = new File("C:/Sandbox/dicom/6.dcm");
        File copy = new File("C:/Sandbox/dicom/6-copy.dcm");

        Dicom dicom = new Dicom(file);

        DicomAttribute pixelData = dicom.getAttributes().get(Name.PixelData.getTag());
        Jpg jpg = pixelData.getValueAsJpg();

        dicom.generateUIDs();
        dicom.putAttribute(new DicomAttribute(jpg));

        FileSys.writeBytes(copy, dicom.getBytes());
    }

    private static void createDicom() {
        File file = new File(path, "6.jpg");
        Jpg jpg = new Jpg(file);
        Dicom dicom = new Dicom(jpg);

        FileSys.writeBytes(new File("C:/Sandbox/dicom/6-composed.dcm"), dicom.getBytes());
    }

    private static void outputStream() {
        DicomOutputStream out = new DicomOutputStream();

        out.writeAttribute(new DicomAttribute(DicomTag.get(Name.Item), null, new byte[4]));

        FileSys.writeBytes(new File("C:/Sandbox/dicom/stream"), out.toByteArray());
    }

    private static void img() {
        File file = new File(path, "6.dcm");
        Dicom dicom = new Dicom(file);
        Jpg jpg = dicom.getAttributes().get(Name.PixelData.getTag()).getValueAsJpg();
        BufferedImage bufferedImage = jpg.getBufferedImage();

        System.out.println(bufferedImage.getWidth() + " x " + bufferedImage.getHeight());

        System.out.println(bufferedImage.getType() == BufferedImage.TYPE_BYTE_GRAY);
    }

    private static void tag() {
        Debug.start("tags");
        Debug.stop();

//        System.out.println(DicomTag.map);
    }

}
