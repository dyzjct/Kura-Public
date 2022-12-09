package me.dyzjct.kura.utils.gl;

import me.dyzjct.kura.module.modules.client.CustomFont;
import me.dyzjct.kura.utils.font.CFontRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL41;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class RenderUtils {
    public static void setColor(Color color) {
        GL11.glColor4f((float) color.getRed() / 255.0f, (float) color.getGreen() / 255.0f, (float) color.getBlue() / 255.0f, (float) color.getAlpha() / 255.0f);
    }

    public static void setColor(int color) {
        setColor(new Color(color));
    }

    public static void setColor(int red, int green, int blue, int alpha) {
        setColor(new Color(red, green, blue, alpha));
    }

    public static void setColor(int red, int green, int blue) {
        setColor(red, green, blue, 255);
    }

    public static void setLineWidth(float width) {
        GL11.glLineWidth(width);
    }

    public static void bindTexture(int textureId) {
        GL11.glBindTexture(3553, textureId);
    }

    public static CFontRenderer getFontRender() {
        return CustomFont.getIDKFont();
    }

    public static void drawLine(double x1, double y1, double x2, double y2, float lineWidth, Color ColorStart, Color ColorEnd) {
        GL11.glDisable(3553);
        GL11.glEnable(3042);
        GL11.glLineWidth(lineWidth);
        GL11.glShadeModel(7425);
        GL11.glBegin(2);
        setColor(ColorStart);
        GL11.glVertex2d(x1, y1);
        setColor(ColorEnd);
        GL11.glVertex2d(x2, y2);
        GL11.glEnd();
        GL11.glDisable(3042);
        GL11.glEnable(3553);
    }

    public static Color alpha(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }

    public static void drawBlockBox(BlockPos pos, Color color, Boolean outline) {
        double x = pos.getX();
        double y = pos.getY();
        double z = pos.getZ();

        color = alpha(color, color.getAlpha() - 20);

        x -= Minecraft.getMinecraft().getRenderManager().viewerPosX;
        y -= Minecraft.getMinecraft().getRenderManager().viewerPosY;
        z -= Minecraft.getMinecraft().getRenderManager().viewerPosZ;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.disableLighting();
        GlStateManager.disableTexture2D();
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        AxisAlignedBB axisAlignedBB = new AxisAlignedBB(0, 0, 0, 1, 1, 1);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(outline ? 1 : 7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferbuilder.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferbuilder.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferbuilder.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferbuilder.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferbuilder.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferbuilder.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferbuilder.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferbuilder.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferbuilder.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferbuilder.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferbuilder.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferbuilder.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferbuilder.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferbuilder.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferbuilder.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferbuilder.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferbuilder.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferbuilder.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferbuilder.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferbuilder.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferbuilder.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferbuilder.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferbuilder.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferbuilder.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferbuilder.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferbuilder.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferbuilder.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferbuilder.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferbuilder.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferbuilder.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferbuilder.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferbuilder.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferbuilder.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferbuilder.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferbuilder.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferbuilder.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferbuilder.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferbuilder.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferbuilder.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferbuilder.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferbuilder.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferbuilder.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferbuilder.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferbuilder.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferbuilder.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferbuilder.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferbuilder.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        tessellator.draw();

        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        GlStateManager.translate(-x, -y, -z);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }

    public static void drawLine(double x1, double y1, double x2, double y2, float lineWidth, Color color) {
        drawLine(x1, y1, x2, y2, lineWidth, color, color);
    }

    public static void drawArc(double cx, double cy, double r, double start_angle, double end_angle, int num_segments) {
        GL11.glDisable(3553);
        GL11.glEnable(3042);
        GL11.glBegin(4);
        int i = (int) ((double) num_segments / (360.0 / start_angle)) + 1;
        while ((double) i <= (double) num_segments / (360.0 / end_angle)) {
            double previousangle = Math.PI * 2 * (double) (i - 1) / (double) num_segments;
            double angle = Math.PI * 2 * (double) i / (double) num_segments;
            GL11.glVertex2f((float) cx, (float) cy);
            GL11.glVertex2f((float) (cx + Math.cos(angle) * r), (float) (cy + Math.sin(angle) * r));
            GL11.glVertex2f((float) (cx + Math.cos(previousangle) * r), (float) (cy + Math.sin(previousangle) * r));
            ++i;
        }
        GL11.glEnd();
        GL11.glDisable(3042);
        GL11.glEnable(3553);
    }

    public static void drawArcOutline(double cx, double cy, double r, double start_angle, double end_angle, int num_segments) {
        GL11.glDisable(3553);
        GL11.glEnable(3042);
        GL11.glBegin(3);
        int i = (int) ((double) num_segments / (360.0 / start_angle));
        while ((double) i <= (double) num_segments / (360.0 / end_angle)) {
            double angle = Math.PI * 2 * (double) i / (double) num_segments;
            GL11.glVertex2d(cx + Math.cos(angle) * r, cy + Math.sin(angle) * r);
            ++i;
        }
        GL11.glEnd();
        GL11.glDisable(3042);
        GL11.glEnable(3553);
    }

    public static void drawCircle(double cx, double cy, double radius) {
        drawArc(cx, cy, radius, 0.0, 360.0, 16);
    }

    public static void drawCircleOutline(double cx, double cy, double radius) {
        drawArcOutline(cx, cy, radius, 0.0, 360.0, 16);
    }

    public static void drawCircle(double cx, double cy, double radius, Color color) {
        setColor(color);
        drawArc(cx, cy, radius, 0.0, 360.0, 16);
    }

    public static void drawCircleOutline(double cx, double cy, double radius, Color color) {
        setColor(color);
        drawArcOutline(cx, cy, radius, 0.0, 360.0, 16);
    }

    public static void drawColoredCircle(double x, double y, double radius, float saturation, float brightness) {
        GL11.glDisable(3553);
        GL11.glPushMatrix();
        GL11.glLineWidth(1.0f);
        GL11.glEnable(2848);
        GL11.glShadeModel(7425);
        GL11.glBegin(3);
        for (int i = 0; i < 360; ++i) {
            setColor(Color.HSBtoRGB(0.0f, 0.0f, brightness));
            GL11.glVertex2d(x, y);
            setColor(Color.HSBtoRGB((float) i / 360.0f, saturation, brightness));
            GL11.glVertex2d(x + Math.sin(Math.toRadians(i)) * radius, y + Math.cos(Math.toRadians(i)) * radius);
        }
        GL11.glEnd();
        GL11.glShadeModel(7424);
        GL11.glDisable(2848);
        GL11.glPopMatrix();
        GL11.glEnable(3553);
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

    public static void drawTriangleOutline(double x1, double y1, double x2, double y2, double x3, double y3, Color color) {
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GL11.glColor4f((float) color.getRed() / 255.0f, (float) color.getGreen() / 255.0f, (float) color.getBlue() / 255.0f, (float) color.getAlpha() / 255.0f);
        GL11.glBegin(2);
        GL11.glVertex2d(x1, y1);
        GL11.glVertex2d(x2, y2);
        GL11.glVertex2d(x3, y3);
        GL11.glEnd();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
    }

    public static void drawGradientRectOutline(double x, double y, double width, double height, GradientDirection direction, Color startColor, Color endColor) {
        GL11.glDisable(3553);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glShadeModel(7425);
        Color[] result = checkColorDirection(direction, startColor, endColor);
        GL11.glBegin(2);
        GL11.glColor4f((float) result[2].getRed() / 255.0f, (float) result[2].getGreen() / 255.0f, (float) result[2].getBlue() / 255.0f, (float) result[2].getAlpha() / 255.0f);
        GL11.glVertex2d(x + width, y);
        GL11.glColor4f((float) result[3].getRed() / 255.0f, (float) result[3].getGreen() / 255.0f, (float) result[3].getBlue() / 255.0f, (float) result[3].getAlpha() / 255.0f);
        GL11.glVertex2d(x, y);
        GL11.glColor4f((float) result[0].getRed() / 255.0f, (float) result[0].getGreen() / 255.0f, (float) result[0].getBlue() / 255.0f, (float) result[0].getAlpha() / 255.0f);
        GL11.glVertex2d(x, y + height);
        GL11.glColor4f((float) result[1].getRed() / 255.0f, (float) result[1].getGreen() / 255.0f, (float) result[1].getBlue() / 255.0f, (float) result[1].getAlpha() / 255.0f);
        GL11.glVertex2d(x + width, y + height);
        GL11.glEnd();
        GL11.glDisable(3042);
        GL11.glEnable(3553);
    }

    public static void drawGradientRect(double x, double y, double width, double height, GradientDirection direction, Color startColor, Color endColor) {
        GL11.glDisable(3553);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glShadeModel(7425);
        Color[] result = checkColorDirection(direction, startColor, endColor);
        GL11.glBegin(7);
        setColor(result[0]);
        GL11.glVertex2d(x + width, y);
        setColor(result[1]);
        GL11.glVertex2d(x, y);
        setColor(result[2]);
        GL11.glVertex2d(x, y + height);
        setColor(result[3]);
        GL11.glVertex2d(x + width, y + height);
        GL11.glEnd();
        GL11.glDisable(3042);
        GL11.glEnable(3553);
    }

    public static void drawRectOutline(double x, double y, double width, double height, Color color) {
        drawGradientRectOutline(x, y, width, height, GradientDirection.Normal, color, color);
    }

    public static void drawRect(double x, double y, double width, double height, Color color) {
        drawGradientRect(x, y, width, height, GradientDirection.Normal, color, color);
    }

    public static void drawRoundedRectangle(double x, double y, double width, double height, double radius, GradientDirection direction, Color startColor, Color endColor) {
        if (width < radius * 2.0 || height < radius * 2.0) {
            return;
        }
        GL11.glShadeModel(7425);
        Color[] result = checkColorDirection(direction, startColor, endColor);
        GL11.glColor4f((float) result[0].getRed() / 255.0f, (float) result[0].getGreen() / 255.0f, (float) result[0].getBlue() / 255.0f, (float) result[0].getAlpha() / 255.0f);
        drawArc(x + width - radius, y + height - radius, radius, 0.0, 90.0, 16);
        GL11.glColor4f((float) result[1].getRed() / 255.0f, (float) result[1].getGreen() / 255.0f, (float) result[1].getBlue() / 255.0f, (float) result[1].getAlpha() / 255.0f);
        drawArc(x + radius, y + height - radius, radius, 90.0, 180.0, 16);
        GL11.glColor4f((float) result[2].getRed() / 255.0f, (float) result[2].getGreen() / 255.0f, (float) result[2].getBlue() / 255.0f, (float) result[2].getAlpha() / 255.0f);
        drawArc(x + radius, y + radius, radius, 180.0, 270.0, 16);
        GL11.glColor4f((float) result[3].getRed() / 255.0f, (float) result[3].getGreen() / 255.0f, (float) result[3].getBlue() / 255.0f, (float) result[3].getAlpha() / 255.0f);
        drawArc(x + width - radius, y + radius, radius, 270.0, 360.0, 16);
        drawGradientRect(x + radius, y, width - radius * 2.0, radius, GradientDirection.LeftToRight, result[2], result[3]);
        drawGradientRect(x + radius, y + height - radius, width - radius * 2.0, radius, GradientDirection.LeftToRight, result[1], result[0]);
        drawGradientRect(x, y + radius, radius, height - radius * 2.0, GradientDirection.DownToUp, result[1], result[2]);
        drawGradientRect(x + width - radius, y + radius, radius, height - radius * 2.0, GradientDirection.DownToUp, result[0], result[3]);
        drawGradientRect(x + radius, y + radius, width - radius * 2.0, height - radius * 2.0, direction, startColor, endColor);
        GL11.glShadeModel(7424);
    }

    public static void drawRoundedRectangle2(double x, double y, double width, double height, double radius, GradientDirection direction, Color startColor, Color endColor) {
        if (width < radius * 2.0 || height < radius * 2.0) {
            return;
        }
        GL11.glShadeModel(7425);
        Color[] result = checkColorDirection(direction, startColor, endColor);
        GL11.glColor4f((float) result[0].getRed() / 255.0f, (float) result[0].getGreen() / 255.0f, (float) result[0].getBlue() / 255.0f, (float) result[0].getAlpha() / 255.0f);
        drawArc(x + width - radius, y + height - radius, radius, 0.0, 90.0, 16);
        GL11.glColor4f((float) result[1].getRed() / 255.0f, (float) result[1].getGreen() / 255.0f, (float) result[1].getBlue() / 255.0f, (float) result[1].getAlpha() / 255.0f);
        drawArc(x + radius, y + height - radius, radius, 90.0, 180.0, 16);
        GL11.glColor4f((float) result[2].getRed() / 255.0f, (float) result[2].getGreen() / 255.0f, (float) result[2].getBlue() / 255.0f, (float) result[2].getAlpha() / 255.0f);
        drawArc(x + radius, y + radius, radius, 180.0, 270.0, 16);
        GL11.glColor4f((float) result[3].getRed() / 255.0f, (float) result[3].getGreen() / 255.0f, (float) result[3].getBlue() / 255.0f, (float) result[3].getAlpha() / 255.0f);
        drawArc(x + width - radius, y + radius, radius, 270.0, 360.0, 16);
        GL11.glShadeModel(7424);
    }

    public static void drawRoundedRectangle(double x, double y, double width, double height, double radius, Color color) {
        drawRoundedRectangle(x, y, width, height, radius, GradientDirection.Normal, color, color);
    }

    public static void drawRoundedRectangle(int x, int y, int width, int height, int radius, Color color) {
        drawRoundedRectangle(x, y, width, height, radius, GradientDirection.Normal, color, color);
    }

    public static void drawRoundedRectangle(int x, int y, int width, int height, int radius, GradientDirection direction, Color startColor, Color endColor) {
        if (width < radius * 2.0 || height < radius * 2.0) {
            return;
        }
        GL11.glShadeModel(7425);
        Color[] result = checkColorDirection(direction, startColor, endColor);
        GL11.glColor4f((float) result[0].getRed() / 255.0f, (float) result[0].getGreen() / 255.0f, (float) result[0].getBlue() / 255.0f, (float) result[0].getAlpha() / 255.0f);
        drawArc(x + width - radius, y + height - radius, radius, 0.0, 90.0, 16);
        GL11.glColor4f((float) result[1].getRed() / 255.0f, (float) result[1].getGreen() / 255.0f, (float) result[1].getBlue() / 255.0f, (float) result[1].getAlpha() / 255.0f);
        drawArc(x + radius, y + height - radius, radius, 90.0, 180.0, 16);
        GL11.glColor4f((float) result[2].getRed() / 255.0f, (float) result[2].getGreen() / 255.0f, (float) result[2].getBlue() / 255.0f, (float) result[2].getAlpha() / 255.0f);
        drawArc(x + radius, y + radius, radius, 180.0, 270.0, 16);
        GL11.glColor4f((float) result[3].getRed() / 255.0f, (float) result[3].getGreen() / 255.0f, (float) result[3].getBlue() / 255.0f, (float) result[3].getAlpha() / 255.0f);
        drawArc(x + width - radius, y + radius, radius, 270.0, 360.0, 16);
        drawGradientRect(x + radius, y, width - radius * 2.0, radius, GradientDirection.LeftToRight, result[2], result[3]);
        drawGradientRect(x + radius, y + height - radius, width - radius * 2.0, radius, GradientDirection.LeftToRight, result[1], result[0]);
        drawGradientRect(x, y + radius, radius, height - radius * 2.0, GradientDirection.DownToUp, result[1], result[2]);
        drawGradientRect(x + width - radius, y + radius, radius, height - radius * 2.0, GradientDirection.DownToUp, result[0], result[3]);
        drawGradientRect(x + radius, y + radius, width - radius * 2.0, height - radius * 2.0, direction, startColor, endColor);
        GL11.glShadeModel(7424);
    }

    public static void drawRoundedRectangleOutline(double x, double y, double width, double height, double radius, float lineWidth, GradientDirection direction, Color startColor, Color endColor) {
        Color[] result = checkColorDirection(direction, startColor, endColor);
        GL11.glLineWidth(lineWidth);
        setColor(result[0]);
        drawArcOutline(x + width - radius, y + height - radius, radius, 0.0, 90.0, 16);
        setColor(result[1]);
        drawArcOutline(x + radius, y + height - radius, radius, 90.0, 180.0, 16);
        setColor(result[2]);
        drawArcOutline(x + radius, y + radius, radius, 180.0, 270.0, 16);
        setColor(result[3]);
        drawArcOutline(x + width - radius, y + radius, radius, 270.0, 360.0, 16);
        drawLine(x + radius, y, x + width - radius, y, lineWidth, result[2], result[3]);
        drawLine(x + radius, y + height, x + width - radius, y + height, lineWidth, result[1], result[0]);
        drawLine(x, y + radius, x, y + height - radius, lineWidth, result[1], result[2]);
        drawLine(x + width, y + radius, x + width, y + height - radius, lineWidth, result[0], result[3]);
    }

    public static void drawRoundedRectangleOutline(double x, double y, double width, double height, double radius, float lineWidth, Color color) {
        drawRoundedRectangleOutline(x, y, width, height, radius, lineWidth, GradientDirection.Normal, color, color);
    }

    public static void drawHalfRoundedRectangle(double x, double y, double width, double height, double radius, HalfRoundedDirection direction, Color color) {
        setColor(color);
        if (direction == HalfRoundedDirection.Top) {
            drawArc(x + radius - 0.5, y + radius, radius, 180.0, 270.0, 50);
            drawArc(x + width - radius, y + radius, radius, 275.0, 360.0, 50);
            drawRect(x, y + radius, radius, height - radius, color);
            drawRect(x + width - radius, y + radius, radius, height - radius, color);
            drawRect(x + radius - 1.0 - 0.5, y, width - radius * 2.0 + 2.0 + 0.5, height + 2.0, color);
        } else if (direction == HalfRoundedDirection.Bottom) {
            drawArc(x + radius, y + height - radius, radius, 90.0, 180.0, 50);
            drawArc(x + width - radius, y + height - radius, radius, 0.0, 90.0, 50);
            drawRect(x, y + radius, radius, height - radius * 2.0, color);
            drawRect(x + width - radius, y + radius, radius, height - radius * 2.0, color);
        } else if (direction == HalfRoundedDirection.Left) {
            drawArc(x + radius, y + radius, radius, 180.0, 270.0, 50);
            drawArc(x + radius, y + height - radius, radius, 90.0, 180.0, 50);
            drawRect(x, y + radius, width, height - radius * 2.0, color);
            drawRect(x + radius, y, width - radius * 2.0, radius, color);
            drawRect(x + radius, y + height - radius, width - radius * 2.0, radius, color);
        } else if (direction == HalfRoundedDirection.Right) {
            drawArc(x + width - radius, y + radius, radius, 270.0, 360.0, 50);
            drawArc(x + width - radius, y + height - radius, radius, 0.0, 90.0, 50);
            drawRect(x, y, width - radius, radius, color);
            drawRect(x, y + height - radius, width - radius, radius, color);
        }
    }

    public static GLTexture loadTexture(InputStream file) {
        try {
            PNGDecoder decoder = new PNGDecoder(file);
            ByteBuffer buffer = ByteBuffer.allocateDirect(4 * decoder.getWidth() * decoder.getHeight());
            decoder.decode(buffer, decoder.getWidth() * 4, PNGDecoder.Format.RGBA);
            buffer.flip();
            int id = GL11.glGenTextures();
            GL11.glBindTexture(3553, id);
            GL11.glPixelStorei(3317, 1);
            GL11.glTexParameterf(3553, 10241, 9728.0f);
            GL11.glTexParameterf(3553, 10240, 9728.0f);
            GL11.glTexImage2D(3553, 0, 6408, decoder.getWidth(), decoder.getHeight(), 0, 6408, 5121, buffer);
            GL11.glBindTexture(3553, 0);
            return new GLTexture(id);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void drawTexture(double x, double y, double width, double height) {
        drawTexture(x, y, width, height, 255);
    }

    public static void drawTexture(double x, double y, double width, double height, int alpha) {
        GL11.glEnable(3553);
        GL11.glEnable(3042);
        setColor(255, 255, 255, alpha);
        GL11.glPushMatrix();
        GL11.glBegin(4);
        GL11.glTexCoord2f(1.0f, 0.0f);
        GL11.glVertex2d(x + width, y);
        GL11.glTexCoord2f(0.0f, 0.0f);
        GL11.glVertex2d(x, y);
        GL11.glTexCoord2f(0.0f, 1.0f);
        GL11.glVertex2d(x, y + height);
        GL11.glTexCoord2f(0.0f, 1.0f);
        GL11.glVertex2d(x, y + height);
        GL11.glTexCoord2f(1.0f, 1.0f);
        GL11.glVertex2d(x + width, y + height);
        GL11.glTexCoord2f(1.0f, 0.0f);
        GL11.glVertex2d(x + width, y);
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glDisable(3042);
    }

    public static void setViewPort(double x, double y, double width, double height) {
        GL41.glClearDepthf(1.0f);
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glColorMask(false, false, false, false);
        GL11.glDepthFunc(513);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);

        drawRect(x, y, width, height, Color.WHITE);

        GL11.glColorMask(true, true, true, true);
        GL11.glDepthMask(true);
        GL11.glDepthFunc(514);
    }

    public static void clearViewPort() {
        GL41.glClearDepthf(1.0f);
        GL11.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glClear(1280);
        GL11.glDisable(2929);
        GL11.glDepthFunc(515);
        GL11.glDepthMask(false);
    }

    private static Color[] checkColorDirection(GradientDirection direction, Color start, Color end) {
        Color[] dir = new Color[4];
        if (direction == GradientDirection.Normal) {
            for (int a = 0; a < dir.length; ++a) {
                dir[a] = new Color(start.getRed(), start.getGreen(), start.getBlue(), start.getAlpha());
            }
        } else if (direction == GradientDirection.DownToUp) {
            dir[0] = new Color(start.getRed(), start.getGreen(), start.getBlue(), start.getAlpha());
            dir[1] = new Color(start.getRed(), start.getGreen(), start.getBlue(), start.getAlpha());
            dir[2] = new Color(end.getRed(), end.getGreen(), end.getBlue(), end.getAlpha());
            dir[3] = new Color(end.getRed(), end.getGreen(), end.getBlue(), end.getAlpha());
        } else if (direction == GradientDirection.UpToDown) {
            dir[0] = new Color(end.getRed(), end.getGreen(), end.getBlue(), end.getAlpha());
            dir[1] = new Color(end.getRed(), end.getGreen(), end.getBlue(), end.getAlpha());
            dir[2] = new Color(start.getRed(), start.getGreen(), start.getBlue(), start.getAlpha());
            dir[3] = new Color(start.getRed(), start.getGreen(), start.getBlue(), start.getAlpha());
        } else if (direction == GradientDirection.RightToLeft) {
            dir[0] = new Color(start.getRed(), start.getGreen(), start.getBlue(), start.getAlpha());
            dir[1] = new Color(end.getRed(), end.getGreen(), end.getBlue(), end.getAlpha());
            dir[2] = new Color(end.getRed(), end.getGreen(), end.getBlue(), end.getAlpha());
            dir[3] = new Color(start.getRed(), start.getGreen(), start.getBlue(), start.getAlpha());
        } else if (direction == GradientDirection.LeftToRight) {
            dir[0] = new Color(end.getRed(), end.getGreen(), end.getBlue(), end.getAlpha());
            dir[1] = new Color(start.getRed(), start.getGreen(), start.getBlue(), start.getAlpha());
            dir[2] = new Color(start.getRed(), start.getGreen(), start.getBlue(), start.getAlpha());
            dir[3] = new Color(end.getRed(), end.getGreen(), end.getBlue(), end.getAlpha());
        } else {
            for (int a = 0; a < dir.length; ++a) {
                dir[a] = new Color(255, 255, 255);
            }
        }
        return dir;
    }

    public enum GradientDirection {
        LeftToRight,
        RightToLeft,
        UpToDown,
        DownToUp,
        Normal

    }

    public enum HalfRoundedDirection {
        Top,
        Bottom,
        Left,
        Right

    }
}

