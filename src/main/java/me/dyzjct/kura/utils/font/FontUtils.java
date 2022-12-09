package me.dyzjct.kura.utils.font;

import me.dyzjct.kura.Kura;
import net.minecraft.client.Minecraft;

public class FontUtils {
    public static CFontRenderer Calibri = new CFontRenderer(new CFont.CustomFont("/assets/fonts/Calibri.ttf", 18.0f, 0), true, false);
    public static CFontRenderer Comfortaa = new CFontRenderer(new CFont.CustomFont("/assets/fonts/Comfortaa.ttf", 18.0f, 0), true, false);
    public static CFontRenderer FoughtKnight = new CFontRenderer(new CFont.CustomFont("/assets/fonts/FoughtKnight.ttf", 18.0f, 0), true, false);
    public static CFontRenderer Goldman = new CFontRenderer(new CFont.CustomFont("/assets/fonts/Goldman.ttf", 18.0f, 0), true, false);
    public static CFontRenderer LemonMilk = new CFontRenderer(new CFont.CustomFont("/assets/fonts/LEMONMILK.ttf", 18.0f, 0, 0), true, false);
    public static CFontRenderer ModernSpace = new CFontRenderer(new CFont.CustomFont("/assets/fonts/MODERN-SPACE.ttf", 18.0f, 0), true, false);
    public static CFontRenderer Montserrat = new CFontRenderer(new CFont.CustomFont("/assets/fonts/Montserrat.ttf", 18.0f, 0), true, false);
    public static CFontRenderer Ninjago = new CFontRenderer(new CFont.CustomFont("/assets/fonts/Ninjago.ttf", 18.0f, 0), true, false);
    public static CFontRenderer Roboto = new CFontRenderer(new CFont.CustomFont("/assets/fonts/Roboto.ttf", 18.0f, 0), true, false);
    public static RFontRenderer RCalibri = new RFontRenderer(new CFont.CustomFont("/assets/fonts/Calibri.ttf", 18.0f, 0), true, false);
    public static RFontRenderer RComfortaa = new RFontRenderer(new CFont.CustomFont("/assets/fonts/Comfortaa.ttf", 18.0f, 0), true, false);
    public static RFontRenderer RFoughtKnight = new RFontRenderer(new CFont.CustomFont("/assets/fonts/FoughtKnight.ttf", 18.0f, 0), true, false);
    public static RFontRenderer RGoldman = new RFontRenderer(new CFont.CustomFont("/assets/fonts/Goldman.ttf", 18.0f, 0), true, false);
    public static RFontRenderer RLemonMilk = new RFontRenderer(new CFont.CustomFont("/assets/fonts/LEMONMILK.ttf", 18.0f, 0, 0), true, false);
    public static RFontRenderer RModernSpace = new RFontRenderer(new CFont.CustomFont("/assets/fonts/MODERN-SPACE.ttf", 18.0f, 0), true, false);
    public static RFontRenderer RMontserrat = new RFontRenderer(new CFont.CustomFont("/assets/fonts/Montserrat.ttf", 18.0f, 0), true, false);
    public static RFontRenderer RNinjago = new RFontRenderer(new CFont.CustomFont("/assets/fonts/Ninjago.ttf", 18.0f, 0), true, false);
    public static RFontRenderer RRoboto = new RFontRenderer(new CFont.CustomFont("/assets/fonts/Roboto.ttf", 18.0f, 0), true, false);

    private static final Minecraft mc = Minecraft.getMinecraft();

    public static float drawStringWithShadow(String text, int x, int y, int color) {
        return mc.fontRenderer.drawStringWithShadow(text, x, y, color);
    }

    public static int getStringWidth(String str) {
        return mc.fontRenderer.getStringWidth(str);
    }

    public static int getFontHeight(boolean customFont) {
        if (customFont) return Kura.fontRenderer.getHeight();
        else return mc.fontRenderer.FONT_HEIGHT;
    }

    public static int getFontHeight() {
        return mc.fontRenderer.FONT_HEIGHT;
    }

    public static float drawKeyStringWithShadow(String text, int x, int y, int color) {
        return mc.fontRenderer.drawStringWithShadow(text, x, y, color);
    }
}

