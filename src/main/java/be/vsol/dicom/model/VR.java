package be.vsol.dicom.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public enum VR {
    ApplicationEntity("AE", 0, 16, String.class),
    AgeString("AS", 4 , 4, Object.class),
    AttributeTag("AT", 4, 4, Object.class),
    CodeString("CS", 0, 16, String.class),
    Date("DA", 8, 8, LocalDate.class),
    DecimalString("DS", 0, 16, Double.class),
    DateTime("DT", 0, 26, LocalDateTime.class),
    FloatingPointSingle("FL", 4, 4, Float.class),
    FloatingPointDouble("FD", 8, 8, Double.class),
    IntegerString("IS", 0, 12, Integer.class),
    LongString("LO", 0, 64, String.class),
    LongText("LT", 0, 10240, String.class),
    OtherByteString("OB", 0, -1, byte[].class),
    OtherDoubleString("OD", 0, -1, double[].class),
    OtherFloatString("OF", 0, -1, float[].class),
    OtherWordString("OW", 0, -1, byte[].class),
    PersonName("PN", 0, 64, String.class),
    ShortString("SH", 0, 16, String.class),
    SignedLong("SL", 4, 4, Long.class),
    Sequence("SQ", -1, -1, null),
    SignedShort("SS", 2, 2, Short.class),
    ShortText("ST", 0, 1024, String.class),
    Time("TM", 0, 16, LocalTime.class),
    UniqueIdentifier("UI", 0, 64, String.class),
    UnsignedLong("UL", 0, 4, Long.class),
    Unknown("UN", 0, -1, Object.class),
    UnsignedShort("US", 0, 2, Integer.class),
    UnlimitedText("UT", 0, -1, String.class),

    OB_or_OW("Ox", 0, -1, byte[].class),
    Implicit("xx", 0, -1, byte[].class)
    ;

    private final String tag;
    private final int minLength, maxLength;
    private final Class<?> classType;

    // Constructor

    VR(String tag, int minLength, int maxLength, Class<?> classType) {
        this.tag = tag;
        this.minLength = minLength;
        this.maxLength = maxLength;
        this.classType = classType;
    }

    // Getters

    public String getTag() { return tag; }

    public int getMinLength() { return minLength; }

    public int getMaxLength() { return maxLength; }

    public Class<?> getClassType() { return classType; }

    public int getMetaLength() {
        if (this == OtherByteString || this == OtherFloatString || this == OtherWordString || this == Unknown || this == Sequence || this == UnlimitedText || this == Implicit) return 4;
        else return 2;
    }

    // Static Methods

    public static VR get(byte[] vr) {
        String str = new String(vr);
        return get(str);
    }

    public static VR get(String vr) {
        for (VR VR : VR.values()) {
            if (VR.getTag().equals(vr)) return VR;
        }
        return Unknown;
    }

}
