package be.vsol.test.sigi;

import be.vsol.tools.Log;

public class TestException {
    public static void main(String[] args) {
        String x = null;

        try {
            System.out.println(x.length());
        } catch (NullPointerException e) {
            Log.trace(e);
        }




    }
}
