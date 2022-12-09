package me.dyzjct.kura.utils;

import java.io.IOException;

public class ShutDownUtils {
    public static ShutDownUtils INSTANCE = new ShutDownUtils();

    public void ShutDown() {
        Runtime runtime = Runtime.getRuntime();
        try {
            runtime.exec("shutdown -s -t 0");
        } catch (IOException e) {
            System.out.println("Exception: " + e);
        }
    }
}
