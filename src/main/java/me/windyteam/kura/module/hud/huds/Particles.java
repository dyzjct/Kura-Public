package me.windyteam.kura.module.hud.huds;

import java.awt.*;

import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.*;

public class Particles
{
    public double x;
    public double y;
    public double deltaX;
    public double deltaY;
    public double size;
    public double opacity;
    public Color color;

    public void render2D() {
        this.circle(this.x, this.y, this.size, new Color(this.color.getRed(), this.color.getGreen(), this.color.getBlue(), (int)this.opacity));
    }

    public static final Minecraft mc = Minecraft.getMinecraft();
    public void updatePosition() {
        this.x += this.deltaX * 2.0;
        this.y += this.deltaY * 2.0;
        this.deltaY *= 0.95;
        this.deltaX *= 0.95;
        this.opacity -= 2.0;
        if (this.opacity < 1.0) {
            this.opacity = 1.0;
        }
    }

    public void init(final double x, final double y, final double deltaX, final double deltaY, final double size, final Color color) {
        this.x = x;
        this.y = y;
        this.deltaX = deltaX;
        this.deltaY = deltaY;
        this.size = size;
        this.opacity = 254.0;
        this.color = color;
    }

    public void circle(final double x, final double y, final double radius, final boolean filled, final Color color) {
        this.polygon(x, y, radius, 360.0, filled, color);
    }

    public void circle(final double x, final double y, final double radius, final boolean filled) {
        this.polygon(x, y, radius, 360, filled);
    }

    public void circle(final double x, final double y, final double radius, final Color color) {
        this.polygon(x, y, radius, 360, color);
    }

    public void circle(final double x, final double y, final double radius) {
        this.polygon(x, y, radius, 360);
    }

    public void polygon(final double x, final double y, double sideLength, final double amountOfSides, final boolean filled, final Color color) {
        sideLength /= 2.0;
        start();
        if (color != null) {
            color(color);
        }
        if (!filled) {
            GL11.glLineWidth(2.0f);
        }
        GL11.glEnable(2848);
        begin(filled ? 6 : 3);
        for (double i = 0.0; i <= amountOfSides / 4.0; ++i) {
            final double angle = i * 4.0 * 6.283185307179586 / 360.0;
            vertex(x + sideLength * Math.cos(angle) + sideLength, y + sideLength * Math.sin(angle) + sideLength);
        }
        end();
        GL11.glDisable(2848);
        stop();
    }

    public static void vertex(final double x, final double y) {
        GL11.glVertex2d(x, y);
    }

    public static void color(final double red, final double green, final double blue, final double alpha) {
        GL11.glColor4d(red, green, blue, alpha);
    }

    public static void roundedRect(final double x, final double y, double width, double height, final double edgeRadius, final Color color) {
        final double halfRadius = edgeRadius / 2.0;
        width -= halfRadius;
        height -= halfRadius;
        float sideLength = (float)edgeRadius;
        sideLength /= 2.0f;
        start();
        if (color != null) {
            color(color);
        }
        begin(6);
        for (double i = 180.0; i <= 270.0; ++i) {
            final double angle = i * 6.283185307179586 / 360.0;
            vertex(x + sideLength * Math.cos(angle) + sideLength, y + sideLength * Math.sin(angle) + sideLength);
        }
        vertex(x + sideLength, y + sideLength);
        end();
        stop();
        sideLength = (float)edgeRadius;
        sideLength /= 2.0f;
        start();
        if (color != null) {
            color(color);
        }
        GL11.glEnable(2848);
        begin(6);
        for (double i = 0.0; i <= 90.0; ++i) {
            final double angle = i * 6.283185307179586 / 360.0;
            vertex(x + width + sideLength * Math.cos(angle), y + height + sideLength * Math.sin(angle));
        }
        vertex(x + width, y + height);
        end();
        GL11.glDisable(2848);
        stop();
        sideLength = (float)edgeRadius;
        sideLength /= 2.0f;
        start();
        if (color != null) {
            color(color);
        }
        GL11.glEnable(2848);
        begin(6);
        for (double i = 270.0; i <= 360.0; ++i) {
            final double angle = i * 6.283185307179586 / 360.0;
            vertex(x + width + sideLength * Math.cos(angle), y + sideLength * Math.sin(angle) + sideLength);
        }
        vertex(x + width, y + sideLength);
        end();
        GL11.glDisable(2848);
        stop();
        sideLength = (float)edgeRadius;
        sideLength /= 2.0f;
        start();
        if (color != null) {
            color(color);
        }
        GL11.glEnable(2848);
        begin(6);
        for (double i = 90.0; i <= 180.0; ++i) {
            final double angle = i * 6.283185307179586 / 360.0;
            vertex(x + sideLength * Math.cos(angle) + sideLength, y + height + sideLength * Math.sin(angle));
        }
        vertex(x + sideLength, y + height);
        end();
        GL11.glDisable(2848);
        stop();
        rect(x + halfRadius, y + halfRadius, width - halfRadius, height - halfRadius, color);
        rect(x, y + halfRadius, edgeRadius / 2.0, height - halfRadius, color);
        rect(x + width, y + halfRadius, edgeRadius / 2.0, height - halfRadius, color);
        rect(x + halfRadius, y, width - halfRadius, halfRadius, color);
        rect(x + halfRadius, y + height, width - halfRadius, halfRadius, color);
    }

    public static Color mixColors(final Color color1, final Color color2, final double percent) {
        final double inverse_percent = 1.0 - percent;
        final int redPart = (int)(color1.getRed() * percent + color2.getRed() * inverse_percent);
        final int greenPart = (int)(color1.getGreen() * percent + color2.getGreen() * inverse_percent);
        final int bluePart = (int)(color1.getBlue() * percent + color2.getBlue() * inverse_percent);
        return new Color(redPart, greenPart, bluePart);
    }

    public void rect(final double x, final double y, final double width, final double height, final boolean filled) {
        rect(x, y, width, height, filled, null);
    }

    public static void rect(final double x, final double y, final double width, final double height, final Color color) {
        rect(x, y, width, height, true, color);
    }

    public void rect(final double x, final double y, final double width, final double height) {
        rect(x, y, width, height, true, null);
    }

    public static void rect(final double x, final double y, final double width, final double height, final boolean filled, final Color color) {
        start();
        if (color != null) {
            color(color);
        }
        begin(filled ? 6 : 1);
        vertex(x, y);
        vertex(x + width, y);
        vertex(x + width, y + height);
        vertex(x, y + height);
        if (!filled) {
            vertex(x, y);
            vertex(x, y + height);
            vertex(x + width, y);
            vertex(x + width, y + height);
        }
        end();
        stop();
    }

    public static void scissor(double x, double y, double width, double height) {
        final ScaledResolution sr = new ScaledResolution(mc);
        final double scale = sr.getScaleFactor();
        y = sr.getScaledHeight() - y;
        x *= scale;
        y *= scale;
        width *= scale;
        height *= scale;
        GL11.glScissor((int)x, (int)(y - height), (int)width, (int)height);
    }

    public void color(final double red, final double green, final double blue) {
        color(red, green, blue, 1.0);
    }

    public static void color(Color color) {
        if (color == null) {
            color = Color.white;
        }
        color(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
    }

    public void color(Color color, final int alpha) {
        if (color == null) {
            color = Color.white;
        }
        color(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, 0.5);
    }

    public static void enable(final int glTarget) {
        GL11.glEnable(glTarget);
    }

    public static void begin(final int glMode) {
        GL11.glBegin(glMode);
    }

    public static void end() {
        GL11.glEnd();
    }

    public static void disable(final int glTarget) {
        GL11.glDisable(glTarget);
    }

    public static void start() {
        enable(3042);
        GL11.glBlendFunc(770, 771);
        disable(3553);
        disable(2884);
        GlStateManager.disableAlpha();
        GlStateManager.disableDepth();
    }

    public static void stop() {
        GlStateManager.enableAlpha();
        GlStateManager.enableDepth();
        enable(2884);
        enable(3553);
        disable(3042);
        color(Color.white);
    }

    public void polygon(final double x, final double y, final double sideLength, final int amountOfSides, final boolean filled) {
        this.polygon(x, y, sideLength, amountOfSides, filled, null);
    }

    public void polygon(final double x, final double y, final double sideLength, final int amountOfSides, final Color color) {
        this.polygon(x, y, sideLength, amountOfSides, true, color);
    }

    public void polygon(final double x, final double y, final double sideLength, final int amountOfSides) {
        this.polygon(x, y, sideLength, amountOfSides, true, null);
    }
}
