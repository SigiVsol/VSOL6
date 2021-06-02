package be.vsol.vsol4.model;

public enum Vsol4Sex {
    M, F, U, O;

    public static Vsol4Sex parse(String sex) {
        return switch (sex.toUpperCase()) {
            case "M" -> M;
            case "F", "V" -> F;
            case "O" -> O;
            default -> U;
        };
    }
}
