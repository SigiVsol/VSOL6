package be.vsol.util;

import be.vsol.tools.CaseReport;
import be.vsol.tools.Csv;

import java.util.Arrays;

public class Lang {

    private enum Punctuation { None, Period, QuestionMark, ExclamationPoint }

    private static Csv csv;

    public static String get(String key, String lang) {
        String[] subs = key.split("\\|", 3);

        if (subs.length == 1) {
            return get(key, lang, 1);
        } else if (subs.length == 2) {
            return get(subs[0], lang, Int.parse(subs[1], 1));
        } else {
            String[] substitutions = subs[2].split("\\|", -1);
            return get(subs[0], lang, Int.parse(subs[1], 1), substitutions);
        }
    }

    public static String get(String key, String lang, String... substitutions) { return get(key, lang, 1, substitutions); }

    public static String get(String key, String lang, int count, String... substitutions) {
        if (key.isEmpty()) return "";

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

        String result = csv.getValue(key.toLowerCase(), lang);
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
