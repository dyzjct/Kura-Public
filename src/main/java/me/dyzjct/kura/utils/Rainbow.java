package me.dyzjct.kura.utils;

import java.awt.Color;

public class Rainbow {
    public static int getRainbow(float speed, float saturation, float brightness) {
        float hue = (float)(System.currentTimeMillis() % 11520L) / 11520.0f * speed;
        return Color.HSBtoRGB(hue, saturation, brightness);
    }

    public static Color getRainbowColor(float speed, float saturation, float brightness) {
        return new Color(Rainbow.getRainbow(speed, saturation, brightness));
    }

    public static Color getRainbowColor(float speed, float saturation, float brightness, long add) {
        return new Color(Rainbow.getRainbow(speed, saturation, brightness, add));
    }

    public static int getRainbow(float speed, float saturation, float brightness, long add) {
        float hue = (float)((System.currentTimeMillis() + add) % 11520L) / 11520.0f * speed;
        return Color.HSBtoRGB(hue, saturation, brightness);
    }
}

