package me.windyteam.kura.gui.clickgui.util;

import me.windyteam.kura.manager.GuiManager;
import me.windyteam.kura.utils.Rainbow;
import me.windyteam.kura.utils.render.RenderUtils;
import org.lwjgl.opengl.GL11;

public class SpecialRender {
//    public static void draw(double x, double y, double width, double height, double spacing, int change) {
//        GL11.glDisable((int)3553);
//        GL11.glEnable((int)3042);
//        GL11.glBlendFunc((int)770, (int)771);
//        GL11.glShadeModel((int)7425);
//        GL11.glEnable((int)2848);
//        GL11.glBegin((int)2);
//        SpecialRender.setsRainbow(0L);
//        GL11.glVertex2d((double)x, (double)y);
//        int d = 0;
//        int s = 0;
//        while ((double)s <= height / spacing) {
//            SpecialRender.setsRainbow((long)change * (long)s);
//            GL11.glVertex2d((double)x, (double)(y + spacing * (double)s));
//            d = s++;
//        }
//        GL11.glVertex2d((double)x, (double)(y + height));
//        GL11.glVertex2d((double)(x + width), (double)(y + height));
//        s = 0;
//        while ((double)s <= height / spacing) {
//            SpecialRender.setsRainbow((long)change * (long)(d - s));
//            GL11.glVertex2d((double)(x + width), (double)(y + height - spacing * (double)s));
//            ++s;
//        }
//        SpecialRender.setsRainbow(0L);
//        GL11.glVertex2d((double)(x + width), (double)y);
//        GL11.glEnd();
//        GL11.glDisable((int)2848);
//        GL11.glDisable((int)3042);
//        GL11.glEnable((int)3553);
//    }

    public static void draw(double x, double y, double width, double height, double spacing, float lineWidth, int change) {
        GL11.glDisable(3553);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glShadeModel(7425);
        GL11.glEnable(2848);
        RenderUtils.setLineWidth(lineWidth);
        GL11.glBegin(2);
        SpecialRender.setsRainbow(0L);
        GL11.glVertex2d(x, y);
        int d = 0;
        int s = 0;
        while ((double) s <= height / spacing) {
            SpecialRender.setsRainbow((long) change * (long) s);
            GL11.glVertex2d(x, y + spacing * (double) s);
            d = s++;
        }
        GL11.glVertex2d(x, y + height);
        GL11.glVertex2d(x + width, y + height);
        s = 0;
        while ((double) s <= height / spacing) {
            SpecialRender.setsRainbow((long) change * (long) (d - s));
            GL11.glVertex2d(x + width, y + height - spacing * (double) s);
            ++s;
        }
        SpecialRender.setsRainbow(0L);
        GL11.glVertex2d(x + width, y);
        GL11.glEnd();
        GL11.glDisable(2848);
        GL11.glDisable(3042);
        GL11.glEnable(3553);
    }

//    public static void drawSLine(double x, double y1, double y2, int height, int start, int change) {
//        GL11.glDisable((int)3553);
//        GL11.glEnable((int)3042);
//        GL11.glBlendFunc((int)770, (int)771);
//        GL11.glShadeModel((int)7425);
//        GL11.glEnable((int)2848);
//        double miny = Math.min(y1, y2);
//        double maxy = Math.max(y1, y2);
//        GL11.glBegin((int)3);
//        SpecialRender.setsRainbow(start);
//        GL11.glVertex2d((double)x, (double)miny);
//        int d = 0;
//        int s = 0;
//        while ((double)s <= (maxy - miny) / (double)height) {
//            SpecialRender.setsRainbow((long)start + (long)change * (long)s);
//            GL11.glVertex2d((double)x, (double)(miny + (double)(height * s)));
//            d = s++;
//        }
//        SpecialRender.setsRainbow((long)start + (long)change * (long)(d + 1));
//        GL11.glVertex2d((double)x, (double)maxy);
//        GL11.glEnd();
//        GL11.glDisable((int)2848);
//        GL11.glDisable((int)3042);
//        GL11.glEnable((int)3553);
//    }

    public static void drawSLine(double x, double y1, double y2, int height, float lineWidth, int start, int change) {
        GL11.glDisable(3553);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glShadeModel(7425);
        GL11.glEnable(2848);
        RenderUtils.setLineWidth(lineWidth);
        double miny = Math.min(y1, y2);
        double maxy = Math.max(y1, y2);
        GL11.glBegin(3);
        SpecialRender.setsRainbow(start);
        GL11.glVertex2d(x, miny);
        int d = 0;
        int s = 0;
        while ((double) s <= (maxy - miny) / (double) height) {
            SpecialRender.setsRainbow(start + (long) change * s);
            GL11.glVertex2d(x, miny + (double) (height * s));
            d = s++;
        }
        SpecialRender.setsRainbow((long) start + (long) change * (long) (d + 1));
        GL11.glVertex2d(x, maxy);
        GL11.glEnd();
        GL11.glDisable(2848);
        GL11.glDisable(3042);
        GL11.glEnable(3553);
    }

    private static void setsRainbow(long add2) {
        RenderUtils.setColor(Rainbow.getRainbow(GuiManager.getINSTANCE().getColorINSTANCE().rainbowSpeed.getValue().floatValue(), GuiManager.getINSTANCE().getColorINSTANCE().rainbowSaturation.getValue().floatValue(), GuiManager.getINSTANCE().getColorINSTANCE().rainbowBrightness.getValue().floatValue(), add2));
    }
}

