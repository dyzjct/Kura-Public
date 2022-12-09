package me.dyzjct.kura.gui.clickgui.util;

import me.dyzjct.kura.manager.GuiManager;
import me.dyzjct.kura.utils.Rainbow;
import me.dyzjct.kura.utils.render.RenderUtils;
import org.lwjgl.opengl.GL11;

public class SpecialRender {
    public static void draw(double x, double y, double width, double height, double spacing, int change) {
        GL11.glDisable((int)3553);
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glShadeModel((int)7425);
        GL11.glEnable((int)2848);
        GL11.glBegin((int)2);
        SpecialRender.setsRainbow(0L);
        GL11.glVertex2d((double)x, (double)y);
        int d = 0;
        int s = 0;
        while ((double)s <= height / spacing) {
            SpecialRender.setsRainbow((long)change * (long)s);
            GL11.glVertex2d((double)x, (double)(y + spacing * (double)s));
            d = s++;
        }
        GL11.glVertex2d((double)x, (double)(y + height));
        GL11.glVertex2d((double)(x + width), (double)(y + height));
        s = 0;
        while ((double)s <= height / spacing) {
            SpecialRender.setsRainbow((long)change * (long)(d - s));
            GL11.glVertex2d((double)(x + width), (double)(y + height - spacing * (double)s));
            ++s;
        }
        SpecialRender.setsRainbow(0L);
        GL11.glVertex2d((double)(x + width), (double)y);
        GL11.glEnd();
        GL11.glDisable((int)2848);
        GL11.glDisable((int)3042);
        GL11.glEnable((int)3553);
    }

    public static void drawSLine(double x, double y1, double y2, int height, int start, int change) {
        GL11.glDisable((int)3553);
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glShadeModel((int)7425);
        GL11.glEnable((int)2848);
        double miny = Math.min(y1, y2);
        double maxy = Math.max(y1, y2);
        GL11.glBegin((int)3);
        SpecialRender.setsRainbow(start);
        GL11.glVertex2d((double)x, (double)miny);
        int d = 0;
        int s = 0;
        while ((double)s <= (maxy - miny) / (double)height) {
            SpecialRender.setsRainbow((long)start + (long)change * (long)s);
            GL11.glVertex2d((double)x, (double)(miny + (double)(height * s)));
            d = s++;
        }
        SpecialRender.setsRainbow((long)start + (long)change * (long)(d + 1));
        GL11.glVertex2d((double)x, (double)maxy);
        GL11.glEnd();
        GL11.glDisable((int)2848);
        GL11.glDisable((int)3042);
        GL11.glEnable((int)3553);
    }

    private static void setsRainbow(long add2) {
        RenderUtils.setColor(Rainbow.getRainbow(((Float)GuiManager.getINSTANCE().getColorINSTANCE().rainbowSpeed.getValue()).floatValue(), ((Float)GuiManager.getINSTANCE().getColorINSTANCE().rainbowSaturation.getValue()).floatValue(), ((Float)GuiManager.getINSTANCE().getColorINSTANCE().rainbowBrightness.getValue()).floatValue(), add2));
    }
}

