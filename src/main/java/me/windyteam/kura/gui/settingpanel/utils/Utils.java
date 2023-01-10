package me.windyteam.kura.gui.settingpanel.utils;

import java.awt.Point;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Mouse;

public class Utils {
    public static String formatTime(long l) {
        long minutes = l / 1000L / 60L;
        long seconds = (l -= minutes * 1000L * 60L) / 1000L;
        l -= seconds * 1000L;
        StringBuilder sb = new StringBuilder();
        if (minutes != 0L) {
            sb.append(minutes).append("min ");
        }
        if (seconds != 0L) {
            sb.append(seconds).append("s ");
        }
        if (l != 0L || minutes == 0L && seconds == 0L) {
            sb.append(l).append("ms ");
        }
        return sb.substring(0, sb.length() - 1);
    }

    public static Point calculateMouseLocation() {
        int scaleFactor;
        Minecraft minecraft = Minecraft.getMinecraft();
        int scale = minecraft.gameSettings.guiScale;
        if (scale == 0) {
            scale = 1000;
        }
        for (scaleFactor = 0; scaleFactor < scale && minecraft.displayWidth / (scaleFactor + 1) >= 320 && minecraft.displayHeight / (scaleFactor + 1) >= 240; ++scaleFactor) {
        }
        return new Point(Mouse.getX() / scaleFactor, minecraft.displayHeight / scaleFactor - Mouse.getY() / scaleFactor - 1);
    }
}

