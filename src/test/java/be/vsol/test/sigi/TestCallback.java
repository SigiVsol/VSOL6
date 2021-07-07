package be.vsol.test.sigi;

import be.vsol.tools.StrCallback;

public class TestCallback {

    public static void main(String[] args) {
        getString("Enter name", name -> {
            System.out.println("Your name is " + name + ".");
        });
    }

    private static void getString(String text, StrCallback callback) {
        System.out.println(text);
        String result = "Sigi"; // read
        callback.invoke(result);
    }

}
