package me.dyzjct.kura.gui.settingpanel.utils;

public class UserValueChangeListener {
    public static boolean isValueChange = false;

    public static void ValueChange() {
        isValueChange = true;
    }

    public static void reset() {
        isValueChange = false;
    }
}

