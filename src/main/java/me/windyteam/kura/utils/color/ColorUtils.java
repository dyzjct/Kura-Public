package me.windyteam.kura.utils.color;

import java.awt.*;

public class ColorUtils {
    public static Color calculateChangeColor(Color oldColor, Color newColor, int step, int currentStep) {
        int r = Math.max(0, Math.min(255, oldColor.getRed() + (newColor.getRed() - oldColor.getRed()) * currentStep / step));
        int g = Math.max(0, Math.min(255, oldColor.getGreen() + (newColor.getGreen() - oldColor.getGreen()) * currentStep / step));
        int b = Math.max(0, Math.min(255, oldColor.getBlue() + (newColor.getBlue() - oldColor.getBlue()) * currentStep / step));
        int a = Math.max(0, Math.min(255, oldColor.getAlpha() + (newColor.getAlpha() - oldColor.getAlpha()) * currentStep / step));
        return new Color(r, g, b, a);
    }

    public static int changeAlpha(int var0, int var1) {
        var0 &= 16777215;
        return var1 << 24 | var0;
    }

    public static Integer calculateAlphaChangeColor(int oldAlpha, int newAlpha, int step, int currentStep) {
        return Math.max(0, Math.min(255, oldAlpha + (newAlpha - oldAlpha) * Math.max(0, Math.min(step, currentStep)) / step));
    }

    public static Color brightness(Color colorToDarker, float bright) {
        float[] hsv = Color.RGBtoHSB(colorToDarker.getRed(), colorToDarker.getGreen(), colorToDarker.getBlue(), null);
        return new Color(Color.HSBtoRGB(hsv[0], hsv[1], bright));
    }

    public static Color getColor(int hex) {
        return new Color(hex);
    }

    public static int getRed(int hex) {
        return hex >> 16 & 0xFF;
    }

    public static int getGreen(int hex) {
        return hex >> 8 & 0xFF;
    }

    public static int getBlue(int hex) {
        return hex & 0xFF;
    }

    public static int getHoovered(int color, boolean isHoovered) {
        return isHoovered ? (color & 0x7F7F7F) << 1 : color;
    }

    public static int getHoovered(Color color, boolean isHoovered) {
        return isHoovered ? (color.getRGB() & 0x7F7F7F) << 1 : color.getRGB();
    }

    public static float toF(int i) {
        return i / 255f;
    }

    public static float toF(double d) {
        return (float) (d / 255f);
    }

    public static int rgbToInt(int r, int g, int b, int a) {
        return (r << 16) | (g << 8) | (b) | (a << 24);
    }

    public static int rgbToInt(int r, int g, int b) {
        return (r << 16) | (g << 8) | (b);
    }
}

