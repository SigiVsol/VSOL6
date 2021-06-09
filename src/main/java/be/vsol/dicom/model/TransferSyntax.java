package be.vsol.dicom.model;

public enum TransferSyntax {
    ImplicitVREndian("1.2.840.10008.1.2"),
    ExplicitVRLittleEndian("1.2.840.10008.1.2.1"),
    DeflatedExplicitVRLittleEndian("1.2.840.10008.1.2.1.99"),
    ExplicitVRBigEndian("1.2.840.10008.1.2.2"),

    JPEGBaselineProcess1("1.2.840.10008.1.2.4.50"),
    JPEGBaselineProcesses2And4("1.2.840.10008.1.2.4.51"),
    JPEGLosslessNonHierarchicalProcesses14("1.2.840.10008.1.2.4.57"),

    MPEG2MainProfileMainLevel("1.2.840.10008.1.2.4.100"),
    MPEG_4AVC_H_264_HighProfile_Level4_1("1.2.840.10008.1.2.4.102"),
    MPEG_4AVC_H_264_BDCompatibleHighProfile_level4_1("1.2.840.10008.1.2.4.103"),
    ;

    private final String code;

    TransferSyntax(String code) {
        this.code = code;
    }

    // Getters

    public String getCode() { return code; }

    public boolean isExplicit() {
        return this != ImplicitVREndian;
    }

    // Static Methods

    public static TransferSyntax get(byte[] code) {
        String string = new String(code).trim();
        return get(string);
    }

    public static TransferSyntax get(String code) {
        for (TransferSyntax transferSyntax : TransferSyntax.values()) {
            if (transferSyntax.getCode().equals(code)) {
                return transferSyntax;
            }
        }
        return null;
    }
    
}
