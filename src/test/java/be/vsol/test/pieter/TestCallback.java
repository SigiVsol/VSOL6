package be.vsol.test.pieter;

import be.vsol.tools.Callback;

public class TestCallback {

    public static void main(String[] args) {
        inform("pieter",  () -> {
            System.out.println("sigi");
        });

    }

    public static void inform(String text, Callback callback) {
        System.out.println(text);
        callback.invoke();
    }
}
