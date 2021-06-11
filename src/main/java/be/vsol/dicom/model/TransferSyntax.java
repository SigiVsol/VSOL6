package be.vsol.dicom.model;

public enum TransferSyntax {
    ImplicitVREndian("1.2.840.10008.1.2"),
    ExplicitVRLittleEndian("1.2.840.10008.1.2.1"),
    DeflatedExplicitVRLittleEndian("1.2.840.10008.1.2.1.99"),
    ExplicitVRBigEndian("1.2.840.10008.1.2.2"),
    JPEGBaselineProcess1("1.2.840.10008.1.2.4.50"),
    JPEGBaselineProcesses2And4("1.2.840.10008.1.2.4.51"),
    @Deprecated JPEGExtendedProcesses3And5("1.2.840.10008.1.2.4.52"),
    @Deprecated JPEGSpectralSelectionNonHierarchicalProcesses6And8("1.2.840.10008.1.2.4.53"),
    @Deprecated JPEGSpectralSelectionNonHierarchicalProcesses7And9("1.2.840.10008.1.2.4.54"),
    @Deprecated JPEGFullProgressionNonHierarchicalProcesses10And12("1.2.840.10008.1.2.4.55"),
    @Deprecated JPEGFullProgressionNonHierarchicalProcesses11And13("1.2.840.10008.1.2.4.56"),
    JPEGLosslessNonHierarchicalProcesses14("1.2.840.10008.1.2.4.57"),
    @Deprecated JPEGLosslessNonHierarchicalProcesses15("1.2.840.10008.1.2.4.58"),
    @Deprecated JPEGExtendedHierarchicalProcesses16And18("1.2.840.10008.1.2.4.59"),
    @Deprecated JPEGExtendedHierarchicalProcesses17And19("1.2.840.10008.1.2.4.60"),
    @Deprecated JPEGSpectralSelectionHierarchicalProcesses20And22("1.2.840.10008.1.2.4.61"),
    @Deprecated JPEGSpectralSelectionHierarchicalProcesses21And23("1.2.840.10008.1.2.4.62"),
    @Deprecated JPEGFullProgressionHierarchicalProcesses24And26("1.2.840.10008.1.2.4.63"),
    @Deprecated JPEGFullProgressionHierarchicalProcesses25And27("1.2.840.10008.1.2.4.64"),
    @Deprecated JPEGLosslessNonHierarchicalProcess28("1.2.840.10008.1.2.4.65"),
    @Deprecated JPEGLosslessNonHierarchicalProcess29("1.2.840.10008.1.2.4.66"),
    JPEGLosslessNonHierarchicalFirstOrderPredictionProcesses14SelectionValue1("1.2.840.10008.1.2.4.70"),
    JPEGLSLosslessImageCompression("1.2.840.10008.1.2.4.80"),
    JPEGLSLossyNearLosslessImageCompression("1.2.840.10008.1.2.4.81"),
    JPEG2000ImageCompressionLosslessOnly("1.2.840.10008.1.2.4.90"),
    JPEG2000ImageCompression("1.2.840.10008.1.2.4.91"),
    JPEG2000Part2MulticomponentImageCompressionLosslessOnly("1.2.840.10008.1.2.4.92"),
    JPEG2000Part2MulticomponentImageCompression("1.2.840.10008.1.2.4.93"),
    JPIPReferenced("1.2.840.10008.1.2.4.94"),
    JPIPReferencedDeflate("1.2.840.10008.1.2.4.95"),
    RLELossless("1.2.840.10008.1.2.5"),
    RFC2557MIMEEncapsulation("1.2.840.10008.1.2.6.1"),
    MPEG2MainProfileMainLevel("1.2.840.10008.1.2.4.100"),
    MPEG_4AVC_H_264_HighProfile_Level4_1("1.2.840.10008.1.2.4.102"),
    MPEG_4AVC_H_264_BDCompatibleHighProfile_level4_1("1.2.840.10008.1.2.4.103"),
    ;

    private final String uid;

    TransferSyntax(String uid) {
        this.uid = uid;
    }

    // Getters

    public String getUid() { return uid; }

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
            if (transferSyntax.getUid().equals(code)) {
                return transferSyntax;
            }
        }
        return null;
    }
    
}
