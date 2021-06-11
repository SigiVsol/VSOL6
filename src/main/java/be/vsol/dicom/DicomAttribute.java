package be.vsol.dicom;

import be.vsol.dicom.model.DicomTag;
import be.vsol.dicom.model.DicomTag.Name;
import be.vsol.dicom.model.VR;
import be.vsol.img.Jpg;
import be.vsol.util.Bytes;

import java.nio.ByteBuffer;

public class DicomAttribute {

    private final DicomTag dicomTag;
    private final VR vr;
    private byte[] value;
    private boolean undefinedLength = false;

    // Constructors

    public DicomAttribute(DicomTag dicomTag, VR vr, byte[] value) {
        this.dicomTag = dicomTag;
        this.vr = vr;
        this.value = value;
    }

    public DicomAttribute(DicomTag dicomTag, String value) {
        this.dicomTag = dicomTag;
        this.vr = dicomTag.getVr();

        this.value = value.getBytes();
    }

    public DicomAttribute(DicomTag dicomTag, long value) {
        this.dicomTag = dicomTag;
        this.vr = dicomTag.getVr();
        if (dicomTag.getVr().isFixedLength()) {
            this.value = Bytes.getByteArray(value, dicomTag.getVr().getMinLength());
        } else {
            this.value = ("" + value).getBytes();
        }
    }

    public DicomAttribute(DicomTag dicomTag, double value) {
        this.dicomTag = dicomTag;
        this.vr = dicomTag.getVr();
        if (dicomTag.getVr().isFixedLength()) {
            this.value = ByteBuffer.allocate(dicomTag.getVr().getMinLength()).putDouble(value).array();
        } else {
            this.value = ("" + value).getBytes();
        }
    }

    public DicomAttribute(Jpg jpg) {
        this.dicomTag = DicomTag.get(Name.PixelData);
        this.vr = VR.OtherByteString;
        this.undefinedLength = true;

        DicomOutputStream out = new DicomOutputStream();
        out.writeAttribute(new DicomAttribute(DicomTag.get(Name.Item), null, new byte[4]));
        out.writeAttribute(new DicomAttribute(DicomTag.get(Name.Item), null, jpg.getBytes()));

        this.value = out.toByteArray();
    }

    // Methods

    @Override public String toString() {
        return switch (dicomTag.getVr().getClassType().getSimpleName()) {
            case "String" -> getValueAsString();
            case "Integer" -> "" + getValueAsInt();
            default -> "[" + Bytes.getSizeString(getLength()) + "]";
        };
    }

    public String getValueAsString() {
        return new String(value).trim();
    }

    public int getValueAsInt() {
        return Bytes.getInt(value);
    }

    public Jpg getValueAsJpg() {
        if (getLength() < 0) {
            DicomInputStream in = new DicomInputStream(value);
            while (in.hasAttributes()) {
                DicomAttribute attribute = in.readAttribute(true); // undefined length -> must be explicit
                if (attribute != null && attribute.getLength() > 128) {
                    return new Jpg(attribute.getValue());
                }
            }
        } else {
            return new Jpg(value);
        }

        return null;
    }

    // Getters

    public DicomTag getDicomTag() { return dicomTag; }

    public byte[] getValue() { return value; }

    public VR getVr() { return vr; }

    public int getLength() { return value == null ? 0 : undefinedLength ? -1 : value.length; }

    // Setters

    public void setValue(byte[] value) { this.value = value; }

    public void setUndefinedLength(boolean undefinedLength) { this.undefinedLength = undefinedLength; }

}
