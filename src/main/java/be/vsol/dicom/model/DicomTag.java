package be.vsol.dicom.model;

import be.vsol.util.Bytes;

public enum DicomTag {
    FileMetaInformationGroupLength("0002,0000", VR.UnsignedLong),
    FileMetaInformationVersion("0002,0001", VR.OtherByteString),
    MediaStorageSOPClassUID("0002,0002", VR.UniqueIdentifier),
    MediaStorageSOPInstanceUID("0002,0003", VR.UniqueIdentifier),
    TransferSyntaxUID("0002,0010", VR.UniqueIdentifier),
    ImplementationClassUID("0002,0012", VR.UniqueIdentifier),
    ImplementationVersionName("0002,0013", VR.ShortString),

    SOPClassUID("0008,0016", VR.UniqueIdentifier),
    SOPInstanceUID("0008,0018", VR.UniqueIdentifier),
    StudyDate("0008,0020", VR.Date),
    SeriesDate("0008,0021", VR.Date),
    AcquisitionDate("0008,0022", VR.Date),
    ContentDate("0008,0023", VR.Date),
    StudyTime("0008,0030", VR.Time),
    SeriesTime("0008,0031", VR.Time),
    AcquisitionTime("0008,0032", VR.Time),
    ContentTime("0008,0033", VR.Time),
    AccessionNumber("0008,0050", VR.ShortString),
    Modality("0008,0060", VR.CodeString),
    PresentationIntentType("0008,0068", VR.CodeString),
    Manufacturer("0008,0070", VR.LongString),
    InstitutionName("0008,0080", VR.LongString),
    ReferringPhysicianName("0008,0090", VR.PersonName),
    StudyDescription("0008,1030", VR.LongString),
    SeriesDescription("0008,103E", VR.LongString),
    ManufacturerModelName("0008,1090", VR.LongString),

    PatientName("0010,0010", VR.PersonName),
    PatientID("0010,0020", VR.LongString),
    PatientBirthDate("0010,0030", VR.Date),
    PatientSex("0010,0040", VR.CodeString),
    ResponsiblePerson("0010,2297", VR.PersonName),
    PatientComments("0010,4000", VR.LongText),

    BodyPartExamined("0018,0015", VR.CodeString),
    SoftwareVersions("0018,1020", VR.LongString),
    ImagerPixelSpacing("0018,1164", VR.DecimalString),
    AcquisitionDeviceProcessingDescription("0018,1400", VR.LongString),
    AcquisitionDeviceProcessingCode("0018,1401", VR.LongString),
    DetectorType("0018,7004", VR.CodeString),
    DetectorConfiguration("0018,7005", VR.CodeString),
    DetectorDescription("0018,7006", VR.LongText),
    DetectorID("0018,700A", VR.ShortString),

    StudyInstanceUID("0020,000D", VR.UniqueIdentifier),
    SeriesInstanceUID("0020,000E", VR.UniqueIdentifier),
    StudyID("0020,0010", VR.ShortString),
    SeriesNumber("0020,0011", VR.IntegerString),
    InstanceNumber("0020,0013", VR.IntegerString),
    PatientOrientation("0020,0020", VR.CodeString),
    Laterality("0020,0060", VR.CodeString),
    ImageLaterality("0020,0062", VR.CodeString),

    SamplesPerPixel("0028,0002", VR.UnsignedShort),
    PhotometricInterpretation("0028,0004", VR.CodeString),
    Rows("0028,0010", VR.UnsignedShort),
    Columns("0028,0011", VR.UnsignedShort),
    PixelSpacing("0028,0030", VR.DecimalString),
    BitsAllocated("0028,0100", VR.UnsignedShort),
    BitsStored("0028,0101", VR.UnsignedShort),
    HighBit("0028,0102", VR.UnsignedShort),
    PixelRepresentation("0028,0103", VR.UnsignedShort),
    BurnedInAnnotation("0028,0301", VR.CodeString),
    PixelIntensityRelationship("0028,1040", VR.CodeString),
    PixelIntensityRelationshipSign("0028,1041", VR.SignedShort),
    WindowCenter("0028,1050", VR.DecimalString),
    WindowWidth("0028,1051", VR.DecimalString),
    RescaleIntercept("0028,1052", VR.DecimalString),
    RescaleSlope("0028,1053", VR.DecimalString),
    RescaleType("0028,1054", VR.LongString),
    LossyImageCompression("0028,2110", VR.CodeString),
    LossyImageCompressionRatio("0028,2112", VR.DecimalString),
    LossyImageCompressionMethod("0028,2114", VR.CodeString),

    @Deprecated StudyComments("0032,4000", VR.LongText),

    CommentsOnRadiationDose("0040,0310", VR.ShortText),

    EncapsulatedDocument("0042,0011", VR.OtherByteString),
    MIMETypeOfEncapsulatedDocument("0042,0012", VR.LongString),

    ImageDisplayFormat("2010,0010", VR.ShortText),

    PresentationLUTShape("2050,0020", VR.CodeString),

    PixelData("7FE0,0010", VR.OB_or_OW)
    ;

    private final String tag;
    private final VR vr;

    // Constructors

    DicomTag(String tag, VR vr) {
        this.tag = tag;
        this.vr = vr;
    }

    // Getters

    public String getTag() { return this.tag; }

    public VR getVr() { return this.vr; }

    public boolean isMeta() { return tag.matches("0002,...."); }

    // Static Methods

    public static DicomTag get(byte[] group, byte[] element) {
        String tag = Bytes.getHexString(group) + "," + Bytes.getHexString(element);
        return get(tag);
    }

    public static DicomTag get(String tag) {
        for (DicomTag dicomTag : DicomTag.values()) {
            if (dicomTag.getTag().equals(tag)) {
                return dicomTag;
            }
        }

        return null;
    }

}
