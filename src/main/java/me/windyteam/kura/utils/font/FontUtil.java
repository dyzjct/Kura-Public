package me.windyteam.kura.utils.font;

import me.windyteam.kura.utils.color.GSColor;
import net.minecraft.client.Minecraft;

public class FontUtil {

    private static final Minecraft mc = Minecraft.getMinecraft();

    public static float drawStringWithShadow(String text, int x, int y, GSColor color) {
        return mc.fontRenderer.drawStringWithShadow(text, x, y, color.getRGB());
    }
    public static float drawString(String text, int x, int y, GSColor color) {
        return mc.fontRenderer.drawString(text, x, y, color.getRGB());
    }

    public static int getStringWidth(String string) {
        return mc.fontRenderer.getStringWidth(string);
    }

    public static int getFontHeight(boolean customFont) {
        return mc.fontRenderer.FONT_HEIGHT;
    }
}