package be.vsol.test.sigi;

public class TestRegex {

    public static void main(String[] args) {
        String x = "nl,en-US;q=0.9,en;q=0.8,nl-BE;q=0.7,fr-BE;q=0.6";
        String[] subs = x.split(",", -1);

        for (String sub : subs) {
            if (sub.matches("([nl]|[en]|[fr]|[de]).*")) {
                System.out.println(sub.substring(0, 2));
//                break;
            } else {
                System.out.println("---");
            }
        }


    }

}
