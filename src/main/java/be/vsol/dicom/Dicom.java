package be.vsol.dicom;

import be.vsol.tools.ByteArray;
import be.vsol.tools.ContentType;

public class Dicom implements ByteArray, ContentType {

    private final byte[] bytes;

    // Constructors

    public Dicom(byte[] bytes) {
        this.bytes = bytes;
    }

    // Getters

    @Override public byte[] getBytes() { return bytes; }

    @Override public String getContentType() { return "application/dicom"; }

}
