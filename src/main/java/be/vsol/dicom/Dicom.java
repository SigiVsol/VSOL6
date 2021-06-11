package be.vsol.dicom;

import be.vsol.dicom.model.DicomTag;
import be.vsol.dicom.model.DicomTag.Name;
import be.vsol.dicom.model.PhotometricInterpretation;
import be.vsol.dicom.model.TransferSyntax;
import be.vsol.dicom.util.DicomUidGenerator;
import be.vsol.img.Jpg;
import be.vsol.tools.ByteArray;
import be.vsol.tools.ContentType;
import be.vsol.util.FileSys;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.TreeMap;

public class Dicom implements ByteArray, ContentType {

    private final TreeMap<String, DicomAttribute> attributes = new TreeMap<>();

    // Constructors

    public Dicom(Jpg jpg) {
        generateUIDs();

        BufferedImage bufferedImage = jpg.getBufferedImage();

        putAttribute(new DicomAttribute(DicomTag.get(Name.TransferSyntaxUID), TransferSyntax.JPEGBaselineProcess1.getUid()));
        putAttribute(new DicomAttribute(DicomTag.get(Name.Rows), bufferedImage.getHeight()));
        putAttribute(new DicomAttribute(DicomTag.get(Name.Columns), bufferedImage.getWidth()));

        putAttribute(new DicomAttribute(DicomTag.get(Name.PhotometricInterpretation), PhotometricInterpretation.get(bufferedImage).getTerm()));
        putAttribute(new DicomAttribute(DicomTag.get(Name.SamplesPerPixel), bufferedImage.getType() == BufferedImage.TYPE_BYTE_GRAY ? 1 : 3));

        putAttribute(new DicomAttribute(DicomTag.get(Name.PixelRepresentation), 0));
        putAttribute(new DicomAttribute(DicomTag.get(Name.BitsAllocated), 8));
        putAttribute(new DicomAttribute(DicomTag.get(Name.BitsStored), 8));
        putAttribute(new DicomAttribute(DicomTag.get(Name.HighBit), 7));

        putAttribute(new DicomAttribute(DicomTag.get(Name.RescaleIntercept), 0.0));
        putAttribute(new DicomAttribute(DicomTag.get(Name.RescaleSlope), 1.0));
        putAttribute(new DicomAttribute(DicomTag.get(Name.RescaleType), "US")); // = unspecified

        putAttribute(new DicomAttribute(DicomTag.get(Name.LossyImageCompression), jpg.getCompressionRatio() == 1 ? "00" : "01"));
        putAttribute(new DicomAttribute(DicomTag.get(Name.LossyImageCompressionMethod), jpg.getCompressionMethod()));
        putAttribute(new DicomAttribute(DicomTag.get(Name.LossyImageCompressionRatio), jpg.getCompressionRatio()));

        putAttribute(new DicomAttribute(jpg));
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
            putAttribute(attribute);
        }
        in.close();

        in = new DicomInputStream(data);
        boolean explicit = getTransferSyntax().isExplicit();
        while (in.hasAttributes()) {
            DicomAttribute attribute = in.readAttribute(explicit);
            if (attribute != null)
                putAttribute(attribute);
        }
        in.close();
    }

    // Methods

    public void generateUIDs() {
        putAttribute(new DicomAttribute(DicomTag.get(Name.StudyInstanceUID), DicomUidGenerator.get()));
        putAttribute(new DicomAttribute(DicomTag.get(Name.SeriesInstanceUID), DicomUidGenerator.get()));

        String sopInstanceUid = DicomUidGenerator.get();
        putAttribute(new DicomAttribute(DicomTag.get(Name.SOPInstanceUID), sopInstanceUid));
        putAttribute(new DicomAttribute(DicomTag.get(Name.MediaStorageSOPInstanceUID), sopInstanceUid));
    }

    public void putAttribute(DicomAttribute attribute) {
        this.attributes.put(attribute.getDicomTag().getTag(), attribute);
    }

    // Getters

    @Override public byte[] getBytes() {
        DicomOutputStream metaOut = new DicomOutputStream();
        DicomOutputStream dataOut = new DicomOutputStream();
        for (String tag : attributes.keySet()) {
            if (DicomTag.get(tag).isMeta()) {
                metaOut.writeAttribute(attributes.get(tag));
            } else {
                dataOut.writeAttribute(attributes.get(tag));
            }
        }

        metaOut.close();
        dataOut.close();

        DicomOutputStream out = new DicomOutputStream();
        out.writePreamble();
        out.writeAttribute(new DicomAttribute(DicomTag.get(Name.FileMetaInformationGroupLength), metaOut.size()));
        out.writeBytes(metaOut.toByteArray());
        out.writeBytes(dataOut.toByteArray());
        if (out.size() % 2 == 1) out.writeBytes(new byte[1]); // dicom files need to have an even amount of bytes in total
        out.close();
        return out.toByteArray();
    }

    @Override public String getContentType() { return "application/dicom"; }

    public TreeMap<String, DicomAttribute> getAttributes() { return attributes; }

    public int get(String tag, int defaultValue) {
        if (attributes.containsKey(tag)) return attributes.get(tag).getValueAsInt();
        else return defaultValue;
    }

    public TransferSyntax getTransferSyntax() {
        return TransferSyntax.get(attributes.get(Name.TransferSyntaxUID.getTag()).getValue());
    }

}














