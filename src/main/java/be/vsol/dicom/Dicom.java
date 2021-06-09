package be.vsol.dicom;

import be.vsol.dicom.model.DicomTag;
import be.vsol.dicom.model.TransferSyntax;
import be.vsol.tools.ByteArray;
import be.vsol.tools.ContentType;
import be.vsol.util.FileSys;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.TreeMap;

public class Dicom implements ByteArray, ContentType {

    private final TreeMap<DicomTag, DicomAttribute> attributes = new TreeMap<>();

    // Constructors

    public Dicom() {

    }

    public Dicom(File file) {
        this(FileSys.readBytes(file));
    }

    public Dicom(byte[] bytes) {
        DicomInputStream in = new DicomInputStream(bytes);
        in.readPreamble();

        DicomAttribute metaLength = in.readAttribute(true);
        byte[] meta = in.readNBytes(metaLength.getValueAsInt());
        byte[] data = in.readAllBytes();
        in.close();

        in = new DicomInputStream(meta);
        while (in.hasAttributes()) {
            DicomAttribute attribute = in.readAttribute(true); // meta tags are always explicit
            attributes.put(attribute.getDicomTag(), attribute);
        }
        in.close();

        in = new DicomInputStream(data);
        boolean explicit = getTransferSyntax().isExplicit();
        while (in.hasAttributes()) {
            DicomAttribute attribute = in.readAttribute(explicit);
            System.out.println(attribute.getDicomTag() + ": " + attribute.getLength());
            attributes.put(attribute.getDicomTag(), attribute);
        }
        in.close();
    }

    // Getters

    @Override public byte[] getBytes() {
        ByteArrayOutputStream metaOut = new ByteArrayOutputStream();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

//        for (String key : metaTags.keySet()) {
//            DicomTag tag = metaTags.get(key);
//            Bytes.write(metaOut, tag.getOutput());
//        }
//
//        ULTag fileMetaInformationGroupLength = new ULTag("0002,0000", metaOut.size());
//
//        Bytes.write(out, new byte[128]); // preamble
//        Bytes.write(out, "DICM".getBytes());
//        Bytes.write(out, fileMetaInformationGroupLength.getOutput());
//        Bytes.write(out, metaOut.toByteArray());
//
//        for (String key : dataTags.keySet()) {
//            DicomTag tag = dataTags.get(key);
//            Bytes.write(out, tag.getOutput());
//        }

        return out.toByteArray();
    }

    @Override public String getContentType() { return "application/dicom"; }

    public TreeMap<DicomTag, DicomAttribute> getAttributes() { return attributes; }

    public TransferSyntax getTransferSyntax() {
        return TransferSyntax.get(attributes.get(DicomTag.TransferSyntaxUID).getValue());
    }

}
