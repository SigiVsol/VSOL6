package be.vsol.vsol6.model.enums;

public enum Sex {
    M, F, X;

    public static Sex parse(String sex) {
        return switch (sex.toUpperCase()) {
            case "M" -> M;
            case "F", "V" -> F;
            default -> X;
        };
    }
}
