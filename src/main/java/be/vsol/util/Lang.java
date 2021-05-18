package be.vsol.util;

import be.vsol.tools.CaseReport;
import be.vsol.tools.Csv;

public class Lang {

    private static Csv csv;

    public static String get(String key, String lang) { return get(key, lang, 1); }

    public static String get(String key, String lang, int count, String... substitutions) {
        if (csv == null) {
            csv = new Csv("lang/translations.csv", ';', true);
        }

        String result = csv.getValue(key, lang);
        if (result == null || result.isEmpty()) {
            result = csv.getValue(key, "en");
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

        return result;
    }

}
