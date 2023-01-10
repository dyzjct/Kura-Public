package me.windyteam.kura.utils.render.gui;

import java.awt.Color;

public class Rainbow {
    public static int getRainbow(float speed, float saturation, float brightness) {
        float hue = (System.currentTimeMillis() % (int)(speed * 1000)) / (speed * 1000);
        int color = Color.HSBtoRGB(hue , saturation, brightness);
        return color;
    }

    public static int getRainbow(float speed, float saturation, float brightness, long add) {
        float hue = ((System.currentTimeMillis() + add) % (int)(speed * 1000)) / (speed * 1000);
        int color = Color.HSBtoRGB(hue , saturation, brightness);
        return color;
    }
}
