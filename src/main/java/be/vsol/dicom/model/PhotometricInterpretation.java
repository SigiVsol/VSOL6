package be.vsol.dicom.model;

import java.awt.image.BufferedImage;

public enum PhotometricInterpretation {

    Monochrome1("MONOCHROME1"),
    Monochrome2("MONOCHROME2"),
    PaletteColor("PALETTE COLOR"),
    Rgb("RGB"),
    @Deprecated Hsv("HSV"),
    @Deprecated Argb("ARGB"),
    @Deprecated Cmyk("CMYK"),
    YbrFull("YBR_FULL"),
    YbrFull422("YBR_FULL_422"),
    @Deprecated YbrPartial422("YBR_PARTIAL_422"),
    YbrPartial420("YBR_PARTIAL_420"),
    YbrIrreversibleColorTransition("YBR_ICT"),
    YbrReversibleColorTransition("YBR_RCT")
    ;

    private final String term;

    PhotometricInterpretation(String term) {
        this.term = term;
    }

    // Getters

    public String getTerm() { return term; }

    // Static Methods

    public static PhotometricInterpretation get(String term) {
        for (PhotometricInterpretation photometricInterpretation : PhotometricInterpretation.values()) {
            if (photometricInterpretation.getTerm().equals(term)) {
                return photometricInterpretation;
            }
        }
        return null;
    }

    public static PhotometricInterpretation get(BufferedImage bufferedImage) {
        return bufferedImage.getType() == BufferedImage.TYPE_BYTE_GRAY ? Monochrome2 : Rgb;
    }

}
