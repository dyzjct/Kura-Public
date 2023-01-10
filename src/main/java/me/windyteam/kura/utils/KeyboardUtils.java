package me.windyteam.kura.utils;

import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

public class KeyboardUtils {
    public static boolean isShiftDown() {
        return Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54);
    }

    public static boolean isCtrlDown() {
        if (Minecraft.IS_RUNNING_ON_MAC) {
            return Keyboard.isKeyDown(219) || Keyboard.isKeyDown(220);
        }
        return Keyboard.isKeyDown(29) || Keyboard.isKeyDown(157);
    }

    public static boolean isAltDown() {
        return Keyboard.isKeyDown(56) || Keyboard.isKeyDown(184);
    }

    public static boolean isDown(int key) {
        return Keyboard.isKeyDown(key);
    }

    public static String getClipboardString() {
        try {
            Transferable transferable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
            if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                return (String) transferable.getTransferData(DataFlavor.stringFlavor);
            }
        } catch (Exception exception) {
            // empty catch block
        }
        return "";
    }
}

