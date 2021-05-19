package be.vsol.util;

import be.vsol.tools.CaseReport;
import be.vsol.tools.Csv;
import be.vsol.vsol6.model.setting.vsol6;

public class Lang {

    private enum Punctuation { None, Period, QuestionMark, ExclamationPoint }

    private static Csv csv;

    public static String get(String key) { return get(key, 1); }

    public static String get(String key, int count, String... substitutions) {
        if (csv == null) {
            csv = new Csv("lang/translations.csv", ';', true);
        }

        Punctuation punctuation = switch (key.charAt(key.length() - 1)) {
            case '.' -> Punctuation.Period;
            case '?' -> Punctuation.QuestionMark;
            case '!' -> Punctuation.ExclamationPoint;
            default -> Punctuation.None;
        };

        if (punctuation != Punctuation.None) key = key.substring(0, key.length() - 1);

        String result = csv.getValue(key.toLowerCase(), vsol6.language);
        if (result == null || result.isEmpty()) {
            result = csv.getValue(key.toLowerCase(), "en");
        }
        if (result == null || result.isEmpty()) {
            result = key.replace("_", " ");
        }

        String[] parts = result.split("\\|", 2);
        if (parts.length > 1) {
            if (count == 1) {
                result = parts[0];
            } else {
                if (parts[1].startsWith("+")) {
                    result = parts[0] + parts[1].substring(1);
                } else {
                    result = parts[1];
                }
            }
        }

        CaseReport caseReport = new CaseReport(key);
        if (caseReport.isUpperCase()) result = result.toUpperCase();
        else if (caseReport.isCapitalized()) result = Str.capitalize(result);

        for (int i = 0; i < substitutions.length; i++) {
            result = result.replace("[" + i + "]", substitutions[i]);
        }

        result += switch (punctuation) {
            case None -> "";
            case Period -> ".";
            case QuestionMark -> "?";
            case ExclamationPoint -> "!";
        };

        return result;
    }

}
