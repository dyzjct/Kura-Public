package me.windyteam.kura.gui.settingpanel.utils;

import me.windyteam.kura.module.modules.client.CustomFont;
import me.windyteam.kura.utils.font.CFontRenderer;
import me.windyteam.kura.module.modules.client.CustomFont;
import me.windyteam.kura.utils.font.CFontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class GLUtil {
    public static CFontRenderer getFontRenderer() {
        return CustomFont.getSetPanFontFont();
    }

    public static void setColor(Color color) {
        GL11.glColor4f((float) color.getRed() / 255.0f, (float) color.getGreen() / 255.0f, (float) color.getBlue() / 255.0f, (float) color.getAlpha() / 255.0f);
    }

    public static void setColor(int rgba) {
        int r = rgba & 0xFF;
        int g = rgba >> 8 & 0xFF;
        int b = rgba >> 16 & 0xFF;
        int a = rgba >> 24 & 0xFF;
        GL11.glColor4b((byte) r, (byte) g, (byte) b, (byte) a);
    }

    public static int toRGBA(Color c) {
        return c.getRed() | c.getGreen() << 8 | c.getBlue() << 16 | c.getAlpha() << 24;
    }

    public static void drawRect(int mode, int x, int y, int width, int height, int color) {
        float f = (float) (color >> 16 & 0xFF) / 255.0f;
        float f1 = (float) (color >> 8 & 0xFF) / 255.0f;
        float f2 = (float) (color & 0xFF) / 255.0f;
        float f3 = (float) (color >> 24 & 0xFF) / 255.0f;
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glColor4f(f, f1, f2, f3);
        GL11.glBegin(mode);
        GL11.glVertex2d(x + width, y);
        GL11.glVertex2d(x, y);
        GL11.glVertex2d(x, y + height);
        GL11.glVertex2d(x + width, y + height);
        GL11.glEnd();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
    }

    public static void drawRect(int mode, double x, double y, double width, double height, int color) {
        float f = (float) (color >> 16 & 0xFF) / 255.0f;
        float f1 = (float) (color >> 8 & 0xFF) / 255.0f;
        float f2 = (float) (color & 0xFF) / 255.0f;
        float f3 = (float) (color >> 24 & 0xFF) / 255.0f;
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glColor4f(f, f1, f2, f3);
        GL11.glBegin(mode);
        GL11.glVertex2d(x + width, y);
        GL11.glVertex2d(x, y);
        GL11.glVertex2d(x, y + height);
        GL11.glVertex2d(x + width, y + height);
        GL11.glEnd();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
    }

    public static void drawGradientRect(int mode, double x, double y, double width, double height, int startColor, int endColor) {
        float f3 = (float) (startColor >> 24 & 0xFF) / 255.0f;
        float f = (float) (startColor >> 16 & 0xFF) / 255.0f;
        float f1 = (float) (startColor >> 8 & 0xFF) / 255.0f;
        float f2 = (float) (startColor & 0xFF) / 255.0f;
        float f7 = (float) (endColor >> 24 & 0xFF) / 255.0f;
        float f4 = (float) (endColor >> 16 & 0xFF) / 255.0f;
        float f5 = (float) (endColor >> 8 & 0xFF) / 255.0f;
        float f6 = (float) (endColor & 0xFF) / 255.0f;
        GL11.glDisable(3553);
        GL11.glEnable(3042);
        GL11.glShadeModel(7425);
        GL11.glBlendFunc(770, 771);
        if (mode == 7 || mode == 8) {
            GL11.glBegin(4);
            GL11.glColor4f(f, f1, f2, f3);
            GL11.glVertex2d(x + width, y);
            GL11.glColor4f(f4, f5, f6, f7);
            GL11.glVertex2d(x, y);
            GL11.glColor4f(f4, f5, f6, f7);
            GL11.glVertex2d(x, y + height);
            GL11.glColor4f(f4, f5, f6, f7);
            GL11.glVertex2d(x, y + height);
            GL11.glColor4f(f, f1, f2, f3);
            GL11.glVertex2d(x + width, y + height);
            GL11.glColor4f(f, f1, f2, f3);
            GL11.glVertex2d(x + width, y);
            GL11.glEnd();
        } else {
            GL11.glBegin(mode);
            GL11.glColor4f(f4, f5, f6, f7);
            GL11.glVertex2d(x, y);
            GL11.glColor4f(f, f1, f2, f3);
            GL11.glVertex2d(x + width, y);
            GL11.glColor4f(f, f1, f2, f3);
            GL11.glVertex2d(x + width, y + height);
            GL11.glColor4f(f4, f5, f6, f7);
            GL11.glVertex2d(x, y + height);
            GL11.glEnd();
        }
        GL11.glEnable(3553);
        GL11.glDisable(3042);
    }

    public static void drawTriangle(double x1, double y1, double x2, double y2, double x3, double y3, Color color) {
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GL11.glColor4f((float) color.getRed() / 255.0f, (float) color.getGreen() / 255.0f, (float) color.getBlue() / 255.0f, (float) color.getAlpha() / 255.0f);
        GL11.glBegin(6);
        GL11.glVertex2d(x1, y1);
        GL11.glVertex2d(x2, y2);
        GL11.glVertex2d(x3, y3);
        GL11.glEnd();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
    }
}

