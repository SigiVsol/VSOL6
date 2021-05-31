package be.vsol.test.sigi;

import be.vsol.tools.LocalStorage;

import java.io.File;
import java.util.Base64;

public class TestLocalStorage {
    public static void main(String[] args) {
        LocalStorage localStorage = new LocalStorage(new File("C:/Sandbox/localStorage"));

//        localStorage.set("user.id", "123-456");


        System.out.println(localStorage.get("user.id", "?"));




    }
}
