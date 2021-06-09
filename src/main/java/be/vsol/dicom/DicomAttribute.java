package be.vsol.dicom;

import be.vsol.dicom.model.DicomTag;
import be.vsol.util.Bytes;

public class DicomAttribute {

    private final DicomTag dicomTag;
    private byte[] value;
    private boolean undefinedLength = false;

    // Constructors

    public DicomAttribute(DicomTag dicomTag) {
        this.dicomTag = dicomTag;
        this.value = new byte[0];
    }

    public DicomAttribute(DicomTag dicomTag, byte[] value) {
        this.dicomTag = dicomTag;
        this.value = value;
    }

    // Methods

    @Override public String toString() {
        return dicomTag.toString();
    }

    public String getValueAsString() {
        return new String(value).trim();
    }

    public int getValueAsInt() {
        return Bytes.getInt(value);
    }

    // Getters

    public DicomTag getDicomTag() { return dicomTag; }

    public byte[] getValue() { return value; }

    public int getLength() { return value == null ? 0 : value.length; }

    public boolean isUndefinedLength() { return undefinedLength; }

    // Setters

    public void setValue(byte[] value) { this.value = value; }

    public void setUndefinedLength(boolean undefinedLength) { this.undefinedLength = undefinedLength; }

}
