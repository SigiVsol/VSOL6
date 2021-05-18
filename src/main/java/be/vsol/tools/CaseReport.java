package be.vsol.tools;

public class CaseReport {

    private final String string;
    private int upper = 0, lower = 0, other = 0, target = 0;

    // Constructors

    public CaseReport(String string) {
        this(string, null);
    }

    public CaseReport(String string, Character target) {
        this.string = string;
        for (char c : string.toCharArray()) {
            if (isUpperCase(c)) {
                this.upper++;
            } else if (isLowerCase(c)) {
                this.lower++;
            } else {
                this.other++;
            }

            if (target != null && c == target) {
                this.target++;
            }
        }
    }

    private boolean isUpperCase(char c) {
        return c >= 'A' && c <= 'Z';
    }

    private boolean isLowerCase(char c) {
        return c >= 'a' && c <= 'z';
    }

    // Getters

    public int getUpper() { return upper; }

    public int getLower() { return lower; }

    public int getOther() { return other; }

    public int getTarget() { return target; }

    public boolean isCapitalized() {
       return string.length() > 1 && isUpperCase(string.charAt(0)) && upper == 1 && lower > 0;
    }

    public boolean isUpperCase() {
        return upper > 0 && lower == 0;
    }

    public boolean isLowerCase() {
        return lower > 0 && upper == 0;
    }

}
