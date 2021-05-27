package be.vsol.util;

public class Filter {

    public static boolean matches(String filter, String... fields) {

        if (filter.isBlank()) return true;
        filter = Str.normalize(filter);

        String[] subs = filter.split(" ");

        // try to find every sub (word in the filter). When a word is not found -> filter doesn't match
        for (String sub : subs) {
            boolean found = false;
            for (String field : fields) {
                if (Str.normalize(field).contains(sub)) {
                    found = true;
                    break;
                }
            }

            if (!found) return false;
        }

        return true;
    }


}
