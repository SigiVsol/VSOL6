package be.vsol.dicom.model;

import be.vsol.util.Bool;
import be.vsol.util.Bytes;
import be.vsol.util.Log;
import be.vsol.util.Resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

public class DicomTag {

    private static final HashMap<String, DicomTag> map = new HashMap<>();

    private final String tag;
    private final VR vr;
    private final String name;
    private final boolean retired;

    public DicomTag(String tag, VR vr, String name, boolean retired) {
        this.tag = tag;
        this.vr = vr;
        this.name = name;
        this.retired = retired;
    }

    // Methods

    @Override public String toString() {
        return "(" + tag + ") " + name;
    }

    // Getters

    public String getTag() { return tag; }

    public VR getVr() { return vr; }

    public String getName() { return name; }

    public boolean isRetired() { return retired; }

    public boolean isMeta() { return tag.matches("0002,...."); }

    public byte[] getBytes() {
        String a = tag.substring(0, 2);
        String b = tag.substring(2, 4);
        String c = tag.substring(5, 7);
        String d = tag.substring(7, 9);

        return new byte[] { Bytes.toByte(b), Bytes.toByte(a), Bytes.toByte(d), Bytes.toByte(c) };
    }

    // Static

    private static void loadMap() {
        String resource = "dicom/tags.csv";
        BufferedReader reader = new BufferedReader(new InputStreamReader(Resource.getInputStream(resource)));

        try {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] subs = line.split(";", 4);
                DicomTag dicomTag = new DicomTag(subs[0], VR.get(subs[1]), subs[2], Bool.parse(subs[3], false));
                map.put(subs[0], dicomTag);
            }

            reader.close();
        } catch (IOException e) {
            Log.trace(e);
        }
    }

    public static DicomTag get(Name name) {
        return get(name.getTag());
    }

    public static DicomTag get(byte[] group, byte[] element) {
        String tag = Bytes.getHexString(group) + "," + Bytes.getHexString(element);
        return get(tag);
    }

    public static DicomTag get(String tag) {
        if (tag.equals("0000,0000")) return null;
        if (map.isEmpty()) {
            loadMap();
        }

        DicomTag result = map.get(tag);
        if (result == null) {
            Log.err("DicomTag (" + tag + ") not found");
            result = new DicomTag(tag, VR.Unknown, "?", false);
        }
        return result;
    }

    public enum Name {
        FileMetaInformationGroupLength("0002,0000"),
        FileMetaInformationVersion("0002,0001"),
        MediaStorageSOPClassUID("0002,0002"),
        MediaStorageSOPInstanceUID("0002,0003"),
        TransferSyntaxUID("0002,0010"),
        ImplementationClassUID("0002,0012"),
        ImplementationVersionName("0002,0013"),

        SOPClassUID("0008,0016"),
        SOPInstanceUID("0008,0018"),
        StudyDate("0008,0020"),
        SeriesDate("0008,0021"),
        AcquisitionDate("0008,0022"),
        ContentDate("0008,0023"),
        StudyTime("0008,0030"),
        SeriesTime("0008,0031"),
        AcquisitionTime("0008,0032"),
        ContentTime("0008,0033"),
        AccessionNumber("0008,0050"),
        Modality("0008,0060"),
        PresentationIntentType("0008,0068"),
        Manufacturer("0008,0070"),
        InstitutionName("0008,0080"),
        ReferringPhysicianName("0008,0090"),
        StudyDescription("0008,1030"),
        SeriesDescription("0008,103E"),
        ManufacturerModelName("0008,1090"),

        PatientName("0010,0010"),
        PatientID("0010,0020"),
        PatientBirthDate("0010,0030"),
        PatientSex("0010,0040"),
        ResponsiblePerson("0010,2297"),
        PatientComments("0010,4000"),

        BodyPartExamined("0018,0015"),
        SoftwareVersions("0018,1020"),
        ImagerPixelSpacing("0018,1164"),
        AcquisitionDeviceProcessingDescription("0018,1400"),
        AcquisitionDeviceProcessingCode("0018,1401"),
        DetectorType("0018,7004"),
        DetectorConfiguration("0018,7005"),
        DetectorDescription("0018,7006"),
        DetectorID("0018,700A"),

        StudyInstanceUID("0020,000D"),
        SeriesInstanceUID("0020,000E"),
        StudyID("0020,0010"),
        SeriesNumber("0020,0011"),
        InstanceNumber("0020,0013"),
        PatientOrientation("0020,0020"),
        Laterality("0020,0060"),
        ImageLaterality("0020,0062"),

        SamplesPerPixel("0028,0002"),
        PhotometricInterpretation("0028,0004"),
        Rows("0028,0010"),
        Columns("0028,0011"),
        PixelSpacing("0028,0030"),
        BitsAllocated("0028,0100"),
        BitsStored("0028,0101"),
        HighBit("0028,0102"),
        PixelRepresentation("0028,0103"),
        BurnedInAnnotation("0028,0301"),
        PixelIntensityRelationship("0028,1040"),
        PixelIntensityRelationshipSign("0028,1041"),
        WindowCenter("0028,1050"),
        WindowWidth("0028,1051"),
        RescaleIntercept("0028,1052"),
        RescaleSlope("0028,1053"),
        RescaleType("0028,1054"),
        LossyImageCompression("0028,2110"),
        LossyImageCompressionRatio("0028,2112"),
        LossyImageCompressionMethod("0028,2114"),

        @Deprecated StudyComments("0032,4000"),

        CommentsOnRadiationDose("0040,0310"),

        EncapsulatedDocument("0042,0011"),
        MIMETypeOfEncapsulatedDocument("0042,0012"),

        ImageDisplayFormat("2010,0010"),

        PresentationLUTShape("2050,0020"),

        PixelData("7FE0,0010"),

        Item("FFFE,E000"),
        ItemDelimitationItem("FFFE,E00D"),
        SequenceDelimitationItem("FFFE,E0DD");

        private final String tag;

        Name(String tag) { this.tag = tag; }

        public String getTag() { return tag; }
    }

}
