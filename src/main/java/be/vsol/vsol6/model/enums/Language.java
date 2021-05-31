package be.vsol.vsol6.model.enums;

public enum Language {
    en, nl, fr, de;

    public static Language parse(String language) {
        return switch (language.toLowerCase()) {
            case "nl" -> nl;
            case "fr" -> fr;
            case "de" -> de;
            default -> en;
        };
    }

    public static String getRegex() {
        String list = "";
        for (Language language : values()) {
            list += (list.isEmpty() ? "" : "|") + language.toString();
        }
        return "(" + list + ").*";
    }

    public static Language getDefault() { return en; }

}
