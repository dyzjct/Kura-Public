package me.windyteam.kura.utils.gl;

import me.windyteam.kura.utils.Rainbow;
import me.windyteam.kura.utils.Wrapper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Arrays;
import java.util.Objects;

import static org.lwjgl.opengl.GL11.*;

public class XG42Tessellator extends Tessellator {
    public static XG42Tessellator INSTANCE = new XG42Tessellator();
    public static Minecraft mc = Minecraft.getMinecraft();
    public static ICamera camera = new Frustum();

    public XG42Tessellator() {
        super(0x200000);
    }

    public static void prepare(int mode) {
        prepareGL();
        begin(mode);
    }

    public static void glCleanup() {
        glEnable(3553);
        glEnable(2929);
        GL11.glDepthMask(true);
        glDisable(3042);
        GL11.glPopMatrix();
    }

    public static void glRestore() {
        GlStateManager.enableCull();
        GlStateManager.enableAlpha();
        GlStateManager.shadeModel(GL11.GL_FLAT);
    }

    public static void drawGradientRect(final int x, final int y, final int w, final int h, final int startColor, final int endColor) {
        final float f = (startColor >> 24 & 0xFF) / 255.0f;
        final float f2 = (startColor >> 16 & 0xFF) / 255.0f;
        final float f3 = (startColor >> 8 & 0xFF) / 255.0f;
        final float f4 = (startColor & 0xFF) / 255.0f;
        final float f5 = (endColor >> 24 & 0xFF) / 255.0f;
        final float f6 = (endColor >> 16 & 0xFF) / 255.0f;
        final float f7 = (endColor >> 8 & 0xFF) / 255.0f;
        final float f8 = (endColor & 0xFF) / 255.0f;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(7425);
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder vertexbuffer = tessellator.getBuffer();
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        vertexbuffer.pos(x + (double) w, y, 0.0).color(f2, f3, f4, f).endVertex();
        vertexbuffer.pos(x, y, 0.0).color(f2, f3, f4, f).endVertex();
        vertexbuffer.pos(x, y + (double) h, 0.0).color(f6, f7, f8, f5).endVertex();
        vertexbuffer.pos(x + (double) w, y + (double) h, 0.0).color(f6, f7, f8, f5).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }
    public static void color(int color) {
        float f = (float) (color >> 24 & 255) / 255.0f;
        float f1 = (float) (color >> 16 & 255) / 255.0f;
        float f2 = (float) (color >> 8 & 255) / 255.0f;
        float f3 = (float) (color & 255) / 255.0f;
        GL11.glColor4f(f1, f2, f3, f);
    }

    public static void drawRect(float x1, float y1, float x2, float y2, int color) {
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glPushMatrix();
        color(color);
        GL11.glBegin(7);
        GL11.glVertex2d(x2, y1);
        GL11.glVertex2d(x1, y1);
        GL11.glVertex2d(x1, y2);
        GL11.glVertex2d(x2, y2);
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
        GL11.glPopMatrix();
        Gui.drawRect(0, 0, 0, 0, 0);
    }

    public static double[] rPos() {
        try {
            return new double[]{Minecraft.getMinecraft().getRenderManager().renderPosX, Minecraft.getMinecraft().getRenderManager().renderPosY, Minecraft.getMinecraft().getRenderManager().renderPosZ};
        } catch (Exception e) {
            return new double[]{0.0, 0.0, 0.0};
        }
    }

    public static void prepare(String mode_requested) {
        int mode = 0;
        if (mode_requested.equalsIgnoreCase("quads")) {
            mode = 7;
        } else if (mode_requested.equalsIgnoreCase("lines")) {
            mode = 1;
        }
        prepare_gl();
        begin(mode);
    }

    public static void glSetup() {
        GL11.glPushMatrix();
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        GL11.glLineWidth(2.0F);
    }

    public static void prepare_gl() {
        //GL11.glBlendFunc(770, 771);
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.glLineWidth(1.5f);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.enableAlpha();
        GlStateManager.color(1.0f, 1.0f, 1.0f);
        GL11.glLineWidth(2.0f);
    }

    public static void draw_cube_line(BlockPos blockPos, int argb, String sides) {
        int a = argb >>> 24 & 0xFF;
        int r = argb >>> 16 & 0xFF;
        int g = argb >>> 8 & 0xFF;
        int b = argb & 0xFF;
        draw_cube_line(blockPos, r, g, b, a, sides);
    }

    public static void draw_cube_line(float x, float y, float z, int argb, String sides) {
        int a = argb >>> 24 & 0xFF;
        int r = argb >>> 16 & 0xFF;
        int g = argb >>> 8 & 0xFF;
        int b = argb & 0xFF;
        draw_cube_line(INSTANCE.getBuffer(), x, y, z, 1.0f, 0.5645f, 1.0f, r, g, b, a, sides);
    }

    public static void draw_cube_line(BlockPos blockPos, int r, int g, int b, int a, String sides) {
        draw_cube_line(INSTANCE.getBuffer(), blockPos.getX(), blockPos.getY(), blockPos.getZ(), 1.0f, 1.0f, 1.0f, r, g, b, a, sides);
    }

    public static void drawBox2(final AxisAlignedBB bb, final int r, final int g, final int b, final int a, final int sides) {
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        if ((sides & 0x1) != 0x0) {
            bufferBuilder.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, a).endVertex();
            bufferBuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, a).endVertex();
            bufferBuilder.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, a).endVertex();
            bufferBuilder.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, a).endVertex();
        }
        if ((sides & 0x2) != 0x0) {
            bufferBuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(r, g, b, a).endVertex();
            bufferBuilder.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, a).endVertex();
            bufferBuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(r, g, b, a).endVertex();
            bufferBuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(r, g, b, a).endVertex();
        }
        if ((sides & 0x4) != 0x0) {
            bufferBuilder.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, a).endVertex();
            bufferBuilder.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, a).endVertex();
            bufferBuilder.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, a).endVertex();
            bufferBuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(r, g, b, a).endVertex();
        }
        if ((sides & 0x8) != 0x0) {
            bufferBuilder.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, a).endVertex();
            bufferBuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, a).endVertex();
            bufferBuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(r, g, b, a).endVertex();
            bufferBuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(r, g, b, a).endVertex();
        }
        if ((sides & 0x10) != 0x0) {
            bufferBuilder.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, a).endVertex();
            bufferBuilder.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, a).endVertex();
            bufferBuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(r, g, b, a).endVertex();
            bufferBuilder.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, a).endVertex();
        }
        if ((sides & 0x20) != 0x0) {
            bufferBuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, a).endVertex();
            bufferBuilder.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, a).endVertex();
            bufferBuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(r, g, b, a).endVertex();
            bufferBuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(r, g, b, a).endVertex();
        }
        tessellator.draw();
    }

    public static void drawBoxSmall(float x, float y, float z, int argb, int sides) {
        int a = argb >>> 24 & 0xFF;
        int r = argb >>> 16 & 0xFF;
        int g = argb >>> 8 & 0xFF;
        int b = argb & 0xFF;
        drawBox(INSTANCE.getBuffer(), x, y, z, 0.25f, 0.25f, 0.25f, r, g, b, a, sides);
    }

    public static void prepareGL() {
        GL11.glBlendFunc(770, 771);
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.glLineWidth(1.5f);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.enableAlpha();
        GlStateManager.color(1.0f, 1.0f, 1.0f);
    }

    public static void begin(int mode) {
        INSTANCE.getBuffer().begin(mode, DefaultVertexFormats.POSITION_COLOR);
    }

    public static void release() {
        render();
        releaseGL();
    }

    public static void render() {
        INSTANCE.draw();
    }

    public static void releaseGL() {
        GlStateManager.enableCull();
        GlStateManager.depthMask(true);
        GlStateManager.enableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.enableDepth();
        GlStateManager.color(1.0f, 1.0f, 1.0f);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public static void drawFace(BlockPos blockPos, int argb, int sides) {
        int a = argb >>> 24 & 0xFF;
        int r = argb >>> 16 & 0xFF;
        int g = argb >>> 8 & 0xFF;
        int b = argb & 0xFF;
        drawFace(blockPos, r, g, b, a, sides);
    }

    public static void drawFace(BlockPos blockPos, int r, int g, int b, int a, int sides) {
        drawFace(INSTANCE.getBuffer(), blockPos.x, blockPos.y, blockPos.z, 1.0f, 1.0f, 1.0f, r, g, b, a, sides);
    }

    public static void drawFace(BufferBuilder buffer, float x, float y, float z, float w, float h, float d, int r, int g, int b, int a, int sides) {
        if ((sides & 1) != 0) {
            buffer.pos(x + w, y, z).color(r, g, b, a).endVertex();
            buffer.pos(x + w, y, z + d).color(r, g, b, a).endVertex();
            buffer.pos(x, y, z + d).color(r, g, b, a).endVertex();
            buffer.pos(x, y, z).color(r, g, b, a).endVertex();
        }
    }

    public static void drawFace(BufferBuilder buffer, AxisAlignedBB bb, int r, int g, int b, int a, int sides) {

        if ((sides & 1) != 0) {
            buffer.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, a).endVertex();
            buffer.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, a).endVertex();
            buffer.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, a).endVertex();
            buffer.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, a).endVertex();
        }
    }


    public static void drawFullBox(AxisAlignedBB bb, BlockPos blockPos, float width, int red, int green, int blue, int alpha) {
        prepare(7);
        drawBox(blockPos, red, green, blue, alpha, 63);
        release();
        drawBoundingBox(bb, width, red, green, blue, 255);
    }

    public static void drawGayBox(AxisAlignedBB bb, BlockPos blockPos, float width, int rainbow, int red, int green, int blue, int alpha) {
        prepare(7);
        drawBox(blockPos, Rainbow.getRainbow(10.0f, 1.0f, 1.0f), 63);
        release();
        drawBoundingBox(bb, width, red, green, blue, 255);
    }

    public static void drawBoundingGay(AxisAlignedBB bb, float width, int r, int g, int b, int alpha) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glLineWidth(width);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex();
        tessellator.draw();
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, alpha).endVertex();
        tessellator.draw();
        bufferbuilder.begin(1, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(r, g, b, alpha).endVertex();
        tessellator.draw();
        GL11.glDisable(2848);
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void drawBox(AxisAlignedBB bb, int argb, int sides) {
        int a = argb >>> 24 & 0xFF;
        int r = argb >>> 16 & 0xFF;
        int g = argb >>> 8 & 0xFF;
        int b = argb & 0xFF;
        drawBox(INSTANCE.getBuffer(), bb, r, g, b, a, sides);
    }

    public static void drawBox(BlockPos blockPos, int argb, int sides) {
        int a = argb >>> 24 & 0xFF;
        int r = argb >>> 16 & 0xFF;
        int g = argb >>> 8 & 0xFF;
        int b = argb & 0xFF;
        drawBox(blockPos, r, g, b, a, sides);
    }

    public static void drawHalfBox(BlockPos blockPos, int argb, int sides) {
        int a = argb >>> 24 & 0xFF;
        int r = argb >>> 16 & 0xFF;
        int g = argb >>> 8 & 0xFF;
        int b = argb & 0xFF;
        drawHalfBox(blockPos, r, g, b, a, sides);
    }

    public static void drawHalfBox(float x, float y, float z, int argb, int sides) {
        int a = argb >>> 24 & 0xFF;
        int r = argb >>> 16 & 0xFF;
        int g = argb >>> 8 & 0xFF;
        int b = argb & 0xFF;
        drawBox(INSTANCE.getBuffer(), x, y, z, 1.0f, 0.5f, 1.0f, r, g, b, a, sides);
    }

    public static void drawBox(float x, float y, float z, int argb, int sides) {
        int a = argb >>> 24 & 0xFF;
        int r = argb >>> 16 & 0xFF;
        int g = argb >>> 8 & 0xFF;
        int b = argb & 0xFF;
        drawBox(INSTANCE.getBuffer(), x, y, z, 1.0f, 1.0f, 1.0f, r, g, b, a, sides);
    }

    public static void drawBox(BlockPos blockPos, int r, int g, int b, int a, int sides) {
        drawBox(INSTANCE.getBuffer(), blockPos.getX(), blockPos.getY(), blockPos.getZ(), 1.0f, 1.0f, 1.0f, r, g, b, a, sides);
    }

    public static void drawHalfBox(BlockPos blockPos, int r, int g, int b, int a, int sides) {
        drawBox(INSTANCE.getBuffer(), blockPos.getX(), blockPos.getY(), blockPos.getZ(), 1.0f, 0.5f, 1.0f, r, g, b, a, sides);
    }

    public static void drawBox(Vec3d vec3d, int r, int g, int b, int a, int sides) {
        drawBox(INSTANCE.getBuffer(), (float) vec3d.x, (float) vec3d.y, (float) vec3d.z, 1.0f, 1.0f, 1.0f, r, g, b, a, sides);
    }

    public static void draw_cube_line(BufferBuilder buffer, float x, float y, float z, float w, float h, float d, int r, int g, int b, int a, String sides) {
        if (Arrays.asList(sides.split("-")).contains("downwest") || sides.equalsIgnoreCase("all")) {
            buffer.pos(x, y, z).color(r, g, b, a).endVertex();
            buffer.pos(x, y, z + d).color(r, g, b, a).endVertex();
        }
        if (Arrays.asList(sides.split("-")).contains("upwest") || sides.equalsIgnoreCase("all")) {
            buffer.pos(x, y + h, z).color(r, g, b, a).endVertex();
            buffer.pos(x, y + h, z + d).color(r, g, b, a).endVertex();
        }
        if (Arrays.asList(sides.split("-")).contains("downeast") || sides.equalsIgnoreCase("all")) {
            buffer.pos(x + w, y, z).color(r, g, b, a).endVertex();
            buffer.pos(x + w, y, z + d).color(r, g, b, a).endVertex();
        }
        if (Arrays.asList(sides.split("-")).contains("upeast") || sides.equalsIgnoreCase("all")) {
            buffer.pos(x + w, y + h, z).color(r, g, b, a).endVertex();
            buffer.pos(x + w, y + h, z + d).color(r, g, b, a).endVertex();
        }
        if (Arrays.asList(sides.split("-")).contains("downnorth") || sides.equalsIgnoreCase("all")) {
            buffer.pos(x, y, z).color(r, g, b, a).endVertex();
            buffer.pos(x + w, y, z).color(r, g, b, a).endVertex();
        }
        if (Arrays.asList(sides.split("-")).contains("upnorth") || sides.equalsIgnoreCase("all")) {
            buffer.pos(x, y + h, z).color(r, g, b, a).endVertex();
            buffer.pos(x + w, y + h, z).color(r, g, b, a).endVertex();
        }
        if (Arrays.asList(sides.split("-")).contains("downsouth") || sides.equalsIgnoreCase("all")) {
            buffer.pos(x, y, z + d).color(r, g, b, a).endVertex();
            buffer.pos(x + w, y, z + d).color(r, g, b, a).endVertex();
        }
        if (Arrays.asList(sides.split("-")).contains("upsouth") || sides.equalsIgnoreCase("all")) {
            buffer.pos(x, y + h, z + d).color(r, g, b, a).endVertex();
            buffer.pos(x + w, y + h, z + d).color(r, g, b, a).endVertex();
        }
        if (Arrays.asList(sides.split("-")).contains("nortwest") || sides.equalsIgnoreCase("all")) {
            buffer.pos(x, y, z).color(r, g, b, a).endVertex();
            buffer.pos(x, y + h, z).color(r, g, b, a).endVertex();
        }
        if (Arrays.asList(sides.split("-")).contains("norteast") || sides.equalsIgnoreCase("all")) {
            buffer.pos(x + w, y, z).color(r, g, b, a).endVertex();
            buffer.pos(x + w, y + h, z).color(r, g, b, a).endVertex();
        }
        if (Arrays.asList(sides.split("-")).contains("southweast") || sides.equalsIgnoreCase("all")) {
            buffer.pos(x, y, z + d).color(r, g, b, a).endVertex();
            buffer.pos(x, y + h, z + d).color(r, g, b, a).endVertex();
        }
        if (Arrays.asList(sides.split("-")).contains("southeast") || sides.equalsIgnoreCase("all")) {
            buffer.pos(x + w, y, z + d).color(r, g, b, a).endVertex();
            buffer.pos(x + w, y + h, z + d).color(r, g, b, a).endVertex();
        }
    }

    public static BufferBuilder getBufferBuilder() {
        return INSTANCE.getBuffer();
    }

    public static void drawHead(BufferBuilder buffer, float x, float y, float z, float w, float h, float d, int r, int g, int b, int a, int sides) {
        if ((sides & 2) != 0) {
            buffer.pos(x + w, y + h, z).color(r, g, b, a).endVertex();
            buffer.pos(x, y + h, z).color(r, g, b, a).endVertex();
            buffer.pos(x, y + h, z + d).color(r, g, b, a).endVertex();
            buffer.pos(x + w, y + h, z + d).color(r, g, b, a).endVertex();
        }
    }

    public static void drawBox(BufferBuilder buffer, float x, float y, float z, float w, float h, float d, int r, int g, int b, int a, int sides) {
        if ((sides & 1) != 0) {
            buffer.pos(x + w, y, z).color(r, g, b, a).endVertex();
            buffer.pos(x + w, y, z + d).color(r, g, b, a).endVertex();
            buffer.pos(x, y, z + d).color(r, g, b, a).endVertex();
            buffer.pos(x, y, z).color(r, g, b, a).endVertex();
        }
        if ((sides & 2) != 0) {
            buffer.pos(x + w, y + h, z).color(r, g, b, a).endVertex();
            buffer.pos(x, y + h, z).color(r, g, b, a).endVertex();
            buffer.pos(x, y + h, z + d).color(r, g, b, a).endVertex();
            buffer.pos(x + w, y + h, z + d).color(r, g, b, a).endVertex();
        }
        if ((sides & 4) != 0) {
            buffer.pos(x + w, y, z).color(r, g, b, a).endVertex();
            buffer.pos(x, y, z).color(r, g, b, a).endVertex();
            buffer.pos(x, y + h, z).color(r, g, b, a).endVertex();
            buffer.pos(x + w, y + h, z).color(r, g, b, a).endVertex();
        }
        if ((sides & 8) != 0) {
            buffer.pos(x, y, z + d).color(r, g, b, a).endVertex();
            buffer.pos(x + w, y, z + d).color(r, g, b, a).endVertex();
            buffer.pos(x + w, y + h, z + d).color(r, g, b, a).endVertex();
            buffer.pos(x, y + h, z + d).color(r, g, b, a).endVertex();
        }
        if ((sides & 0x10) != 0) {
            buffer.pos(x, y, z).color(r, g, b, a).endVertex();
            buffer.pos(x, y, z + d).color(r, g, b, a).endVertex();
            buffer.pos(x, y + h, z + d).color(r, g, b, a).endVertex();
            buffer.pos(x, y + h, z).color(r, g, b, a).endVertex();
        }
        if ((sides & 0x20) != 0) {
            buffer.pos(x + w, y, z + d).color(r, g, b, a).endVertex();
            buffer.pos(x + w, y, z).color(r, g, b, a).endVertex();
            buffer.pos(x + w, y + h, z).color(r, g, b, a).endVertex();
            buffer.pos(x + w, y + h, z + d).color(r, g, b, a).endVertex();
        }
    }

    public static void drawBox(BufferBuilder buffer, AxisAlignedBB bb, int r, int g, int b, int a, int sides) {
        if ((sides & 1) != 0) {
            buffer.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, a).endVertex();
            buffer.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, a).endVertex();
            buffer.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, a).endVertex();
            buffer.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, a).endVertex();
        }
        if ((sides & 2) != 0) {
            buffer.pos(bb.maxX, bb.maxY, bb.minZ).color(r, g, b, a).endVertex();
            buffer.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, a).endVertex();
            buffer.pos(bb.minX, bb.maxY, bb.maxZ).color(r, g, b, a).endVertex();
            buffer.pos(bb.maxX, bb.maxY, bb.maxZ).color(r, g, b, a).endVertex();
        }
        if ((sides & 4) != 0) {
            buffer.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, a).endVertex();
            buffer.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, a).endVertex();
            buffer.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, a).endVertex();
            buffer.pos(bb.maxX, bb.maxY, bb.minZ).color(r, g, b, a).endVertex();
        }
        if ((sides & 8) != 0) {
            buffer.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, a).endVertex();
            buffer.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, a).endVertex();
            buffer.pos(bb.maxX, bb.maxY, bb.maxZ).color(r, g, b, a).endVertex();
            buffer.pos(bb.minX, bb.maxY, bb.maxZ).color(r, g, b, a).endVertex();
        }
        if ((sides & 0x10) != 0) {
            buffer.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, a).endVertex();
            buffer.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, a).endVertex();
            buffer.pos(bb.minX, bb.maxY, bb.maxZ).color(r, g, b, a).endVertex();
            buffer.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, a).endVertex();
        }
        if ((sides & 0x20) != 0) {
            buffer.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, a).endVertex();
            buffer.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, a).endVertex();
            buffer.pos(bb.maxX, bb.maxY, bb.minZ).color(r, g, b, a).endVertex();
            buffer.pos(bb.maxX, bb.maxY, bb.maxZ).color(r, g, b, a).endVertex();
        }
    }

    public static void drawSmallBox(Vec3d vec3d, int r, int g, int b, int a, int sides) {
        drawBox(INSTANCE.getBuffer(), (float) vec3d.x, (float) vec3d.y, (float) vec3d.z, 0.3f, 0.3f, 0.3f, r, g, b, a, sides);
    }

    public static void drawLines(BufferBuilder buffer, float x, float y, float z, float w, float h, float d, int r, int g, int b, int a, int sides) {
        if ((sides & 0x11) != 0) {
            buffer.pos(x, y, z).color(r, g, b, a).endVertex();
            buffer.pos(x, y, z + d).color(r, g, b, a).endVertex();
        }
        if ((sides & 0x12) != 0) {
            buffer.pos(x, y + h, z).color(r, g, b, a).endVertex();
            buffer.pos(x, y + h, z + d).color(r, g, b, a).endVertex();
        }
        if ((sides & 0x21) != 0) {
            buffer.pos(x + w, y, z).color(r, g, b, a).endVertex();
            buffer.pos(x + w, y, z + d).color(r, g, b, a).endVertex();
        }
        if ((sides & 0x22) != 0) {
            buffer.pos(x + w, y + h, z).color(r, g, b, a).endVertex();
            buffer.pos(x + w, y + h, z + d).color(r, g, b, a).endVertex();
        }
        if ((sides & 5) != 0) {
            buffer.pos(x, y, z).color(r, g, b, a).endVertex();
            buffer.pos(x + w, y, z).color(r, g, b, a).endVertex();
        }
        if ((sides & 6) != 0) {
            buffer.pos(x, y + h, z).color(r, g, b, a).endVertex();
            buffer.pos(x + w, y + h, z).color(r, g, b, a).endVertex();
        }
        if ((sides & 9) != 0) {
            buffer.pos(x, y, z + d).color(r, g, b, a).endVertex();
            buffer.pos(x + w, y, z + d).color(r, g, b, a).endVertex();
        }
        if ((sides & 0xA) != 0) {
            buffer.pos(x, y + h, z + d).color(r, g, b, a).endVertex();
            buffer.pos(x + w, y + h, z + d).color(r, g, b, a).endVertex();
        }
        if ((sides & 0x14) != 0) {
            buffer.pos(x, y, z).color(r, g, b, a).endVertex();
            buffer.pos(x, y + h, z).color(r, g, b, a).endVertex();
        }
        if ((sides & 0x24) != 0) {
            buffer.pos(x + w, y, z).color(r, g, b, a).endVertex();
            buffer.pos(x + w, y + h, z).color(r, g, b, a).endVertex();
        }
        if ((sides & 0x18) != 0) {
            buffer.pos(x, y, z + d).color(r, g, b, a).endVertex();
            buffer.pos(x, y + h, z + d).color(r, g, b, a).endVertex();
        }
        if ((sides & 0x28) != 0) {
            buffer.pos(x + w, y, z + d).color(r, g, b, a).endVertex();
            buffer.pos(x + w, y + h, z + d).color(r, g, b, a).endVertex();
        }
    }

    public static void drawBoundingBox(BlockPos bb, float width, int argb) {
        int a = argb >>> 24 & 0xFF;
        int r = argb >>> 16 & 0xFF;
        int g = argb >>> 8 & 0xFF;
        int b = argb & 0xFF;
        drawBoundingBox(bb, width, r, g, b, a);
    }

    public static void drawBoundingBox(AxisAlignedBB bb, float width, int argb) {
        int a = argb >>> 24 & 255;
        int r = argb >>> 16 & 255;
        int g = argb >>> 8 & 255;
        int b = argb & 255;
        XG42Tessellator.drawBoundingBox(bb, width, r, g, b, a);
    }

    public static void drawBoundingBoxKA(BlockPos bb, float width, int argb) {
        int a = argb >>> 24 & 0xFF;
        int r = argb >>> 16 & 0xFF;
        int g = argb >>> 8 & 0xFF;
        int b = argb & 0xFF;
        drawBoundingBoxKA(bb, width, r, g, b, a);
    }

    public static void drawBoundingBoxKA(BlockPos pos, float width, int red, int green, int blue, int alpha) {
        drawBoundingBox(getBoundingFromPos(pos), width, red, green, blue, alpha);
    }

    public static void drawBoundingBox(BlockPos pos, float width, int red, int green, int blue, int alpha) {
        drawBoundingBox(getBoundingFromPos(pos), width, red, green, blue, alpha);
    }

    public static void drawBoundingBox(AxisAlignedBB bb, int qwq) {
        drawBoundingBox(bb, qwq, qwq, qwq, qwq);
    }

    public static void drawBoundingBox(AxisAlignedBB bb, int r, int g, int b, int alpha) {
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glDisable(2929);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3553);
        GL11.glDepthMask(false);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex();
        tessellator.draw();
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, alpha).endVertex();
        tessellator.draw();
        bufferbuilder.begin(1, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(r, g, b, alpha).endVertex();
        tessellator.draw();
        GL11.glDisable(2848);
        GL11.glDepthMask(true);
        GL11.glEnable(2929);
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }

    public static void drawBoundingBox(AxisAlignedBB bb, float width, int red, int green, int blue, int alpha) {
        GL11.glLineWidth(width);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GlStateManager.color(red / 255f, green / 255f, blue / 255f, alpha / 255f);
        drawBoundingBox(bb);
        GlStateManager.color(1, 1, 1, 1);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
    }

    public static void drawBoundingBox(AxisAlignedBB boundingBox) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexBuffer = tessellator.getBuffer();
        vertexBuffer.begin(3, DefaultVertexFormats.POSITION);
        vertexBuffer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).endVertex();
        vertexBuffer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).endVertex();
        vertexBuffer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).endVertex();
        vertexBuffer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).endVertex();
        vertexBuffer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).endVertex();
        tessellator.draw();
        vertexBuffer.begin(3, DefaultVertexFormats.POSITION);
        vertexBuffer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).endVertex();
        vertexBuffer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).endVertex();
        vertexBuffer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).endVertex();
        vertexBuffer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).endVertex();
        vertexBuffer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).endVertex();
        tessellator.draw();
        vertexBuffer.begin(1, DefaultVertexFormats.POSITION);
        vertexBuffer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).endVertex();
        vertexBuffer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).endVertex();
        vertexBuffer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).endVertex();
        vertexBuffer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).endVertex();
        vertexBuffer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).endVertex();
        vertexBuffer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).endVertex();
        vertexBuffer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).endVertex();
        vertexBuffer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).endVertex();
        tessellator.draw();
    }

    public static void drawBoundingBoxKA(AxisAlignedBB bb, float width, int r, int g, int b, int alpha) {
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glDisable(2929);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3553);
        GL11.glDepthMask(false);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glLineWidth(width);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX - bb.minX, bb.maxY - bb.minY, bb.maxZ - bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX - bb.minX, bb.maxY - bb.minY, bb.maxZ - bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX - bb.minX, bb.maxY - bb.minY, bb.maxZ - bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX - bb.minX, bb.maxY - bb.minY, bb.maxZ - bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex();
        tessellator.draw();
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX - bb.minX, bb.maxY - bb.minY, bb.maxZ - bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX - bb.minX, bb.maxY - bb.minY, bb.maxZ - bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX - bb.minX, bb.maxY - bb.minY, bb.maxZ - bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX - bb.minX, bb.maxY - bb.minY, bb.maxZ - bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, alpha).endVertex();
        tessellator.draw();
        bufferbuilder.begin(1, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(r, g, b, alpha).endVertex();
        tessellator.draw();
        GL11.glDisable(2848);
        GL11.glDepthMask(true);
        GL11.glEnable(2929);
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }

    public static void drawBoundingBoxBlockPos(BlockPos bp, float width, int r, int g, int b, int alpha) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glLineWidth(width);
        Minecraft mc = Minecraft.getMinecraft();
        double x = (double) bp.x - mc.getRenderManager().viewerPosX;
        double y = (double) bp.y - mc.getRenderManager().viewerPosY;
        double z = (double) bp.z - mc.getRenderManager().viewerPosZ;
        AxisAlignedBB bb = new AxisAlignedBB(x, y, z, x + 1.0, y + 1.0, z + 1.0);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex();
        tessellator.draw();
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, alpha).endVertex();
        tessellator.draw();
        bufferbuilder.begin(1, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(r, g, b, alpha).endVertex();
        tessellator.draw();
        GL11.glDisable(2848);
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void drawBoundingBoxBlockPos(Vec3d hitVec, float width, int r, int g, int b, int alpha) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glLineWidth(width);
        Minecraft mc = Minecraft.getMinecraft();
        double x = hitVec.x - mc.getRenderManager().viewerPosX;
        double y = hitVec.y - mc.getRenderManager().viewerPosY;
        double z = hitVec.z - mc.getRenderManager().viewerPosZ;
        AxisAlignedBB bb = new AxisAlignedBB(x, y, z, x + 1.0, y + 1.0, z + 1.0);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex();
        tessellator.draw();
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, alpha).endVertex();
        tessellator.draw();
        bufferbuilder.begin(1, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(r, g, b, alpha).endVertex();
        tessellator.draw();
        GL11.glDisable(2848);
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void drawBoundingBoxBottomBlockPos(BlockPos bp, float width, int r, int g, int b, int alpha) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glLineWidth(width);
        Minecraft mc = Minecraft.getMinecraft();
        double x = (double) bp.x - mc.getRenderManager().viewerPosX;
        double y = (double) bp.y - mc.getRenderManager().viewerPosY;
        double z = (double) bp.z - mc.getRenderManager().viewerPosZ;
        AxisAlignedBB bb = new AxisAlignedBB(x, y, z, x + 1.0, y + 1.0, z + 1.0);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex();
        tessellator.draw();
        GL11.glDisable(2848);
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void drawBoxBottom(BufferBuilder buffer, float x, float y, float z, float w, float h, float d, int r, int g, int b, int a) {
        buffer.pos(x + w, y, z).color(r, g, b, a).endVertex();
        buffer.pos(x + w, y, z + d).color(r, g, b, a).endVertex();
        buffer.pos(x, y, z + d).color(r, g, b, a).endVertex();
        buffer.pos(x, y, z).color(r, g, b, a).endVertex();
    }

    public static void drawBoxBottom(BlockPos blockPos, int argb) {
        int a = argb >>> 24 & 0xFF;
        int r = argb >>> 16 & 0xFF;
        int g = argb >>> 8 & 0xFF;
        int b = argb & 0xFF;
        drawBoxBottom(blockPos, r, g, b, a);
    }

    public static void drawBoxBottom(float x, float y, float z, int argb) {
        int a = argb >>> 24 & 0xFF;
        int r = argb >>> 16 & 0xFF;
        int g = argb >>> 8 & 0xFF;
        int b = argb & 0xFF;
        drawBoxBottom(INSTANCE.getBuffer(), x, y, z, 1.0f, 1.0f, 1.0f, r, g, b, a);
    }

    public static void drawBoxBottom(BlockPos blockPos, int r, int g, int b, int a) {
        drawBoxBottom(INSTANCE.getBuffer(), blockPos.x, blockPos.y, blockPos.z, 1.0f, 1.0f, 1.0f, r, g, b, a);
    }

    public static void glBillboard(float x, float y, float z) {
        float scale = 0.02666667f;
        GlStateManager.translate((double) x - Minecraft.getMinecraft().getRenderManager().renderPosX, (double) y - Minecraft.getMinecraft().getRenderManager().renderPosY, (double) z - Minecraft.getMinecraft().getRenderManager().renderPosZ);
        GlStateManager.glNormal3f(0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(-Minecraft.getMinecraft().player.rotationYaw, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(Minecraft.getMinecraft().player.rotationPitch, Minecraft.getMinecraft().gameSettings.thirdPersonView == 2 ? -1.0f : 1.0f, 0.0f, 0.0f);
        GlStateManager.scale(-scale, -scale, scale);
    }

    public static void glBillboardDistanceScaled(float x, float y, float z, EntityPlayer player, float scale) {
        glBillboard(x, y, z);
        int distance = (int) player.getDistance(x, y, z);
        float scaleDistance = (float) distance / 2.0f / (2.0f + (2.0f - scale));
        if (scaleDistance < 1.0f) {
            scaleDistance = 1.0f;
        }
        GlStateManager.scale(scaleDistance, scaleDistance, scaleDistance);
    }

    public static void drawBBBox(AxisAlignedBB BB, Color colour, int alpha, float lineWidth, boolean outline) {
        AxisAlignedBB bb = new AxisAlignedBB(BB.minX - mc.getRenderManager().viewerPosX, BB.minY - mc.getRenderManager().viewerPosY, BB.minZ - mc.getRenderManager().viewerPosZ, BB.maxX - mc.getRenderManager().viewerPosX, BB.maxY - mc.getRenderManager().viewerPosY, BB.maxZ - mc.getRenderManager().viewerPosZ);
        camera.setPosition(Objects.requireNonNull(mc.getRenderViewEntity()).posX, mc.getRenderViewEntity().posY, mc.getRenderViewEntity().posZ);
        if (camera.isBoundingBoxInFrustum(new AxisAlignedBB(bb.minX + mc.getRenderManager().viewerPosX, bb.minY + mc.getRenderManager().viewerPosY, bb.minZ + mc.getRenderManager().viewerPosZ, bb.maxX + mc.getRenderManager().viewerPosX, bb.maxY + mc.getRenderManager().viewerPosY, bb.maxZ + mc.getRenderManager().viewerPosZ))) {
            prepare(GL_QUADS);
            GlStateManager.enableBlend();
            GlStateManager.disableDepth();
            GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
            GlStateManager.disableTexture2D();
            GlStateManager.depthMask(false);
            GL11.glEnable(2848);
            GL11.glHint(3154, 4354);
            if (outline) {
                drawBoundingBox(bb, lineWidth, colour.getRed(), colour.getGreen(), colour.getBlue(), 255);
            }
            RenderGlobal.renderFilledBox(bb, (float) colour.getRed() / 255.0f, (float) colour.getGreen() / 255.0f, (float) colour.getBlue() / 255.0f, alpha / 255.0f);
            GL11.glDisable(2848);
            GlStateManager.depthMask(true);
            GlStateManager.enableDepth();
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
            release();
        }
    }

    public static void drawBox(Vec3d hitVec, int argb, int sides) {
        int a = argb >>> 24 & 0xFF;
        int r = argb >>> 16 & 0xFF;
        int g = argb >>> 8 & 0xFF;
        int b = argb & 0xFF;
        drawBox(hitVec, r, g, b, a, sides);
    }

    public static void drawFullBox(BlockPos pos, float width, int argb) {
        int a = argb >>> 24 & 0xFF;
        int r = argb >>> 16 & 0xFF;
        int g = argb >>> 8 & 0xFF;
        int b = argb & 0xFF;
        drawFullBox(pos, width, r, g, b, a);
    }

    public static void drawFullBox(BlockPos pos, float width, int red, int green, int blue, int alpha) {
        drawBoundingFullBox(getBoundingFromPos(pos), red, green, blue, alpha);
        drawBoundingBox(getBoundingFromPos(pos), width, red, green, blue, 255);
    }

    public static void drawFullBox(Vec3d pos, float width, int argb) {
        int a = argb >>> 24 & 0xFF;
        int r = argb >>> 16 & 0xFF;
        int g = argb >>> 8 & 0xFF;
        int b = argb & 0xFF;
        drawFullBox(pos, width, r, g, b, a);
    }

    public static void drawFullBox(Vec3d pos, float width, int red, int green, int blue, int alpha) {
        drawBoundingFullBox(getBoundingFromPos(pos), red, green, blue, alpha);
        drawBoundingBox(getBoundingFromPos(pos), width, red, green, blue, 255);
    }

    public static void drawFullBox(AxisAlignedBB bb, float width, int argb) {
        int a = argb >>> 24 & 0xFF;
        int r = argb >>> 16 & 0xFF;
        int g = argb >>> 8 & 0xFF;
        int b = argb & 0xFF;
        drawFullBox(bb, width, r, g, b, a);
    }

    public static void drawFullBox(AxisAlignedBB bb, float width, int red, int green, int blue, int alpha) {
        drawBoundingFullBox(bb, red, green, blue, alpha);
        drawBoundingBox(bb, width, red, green, blue, 255);
    }

    public static void drawBoundingFullBox(AxisAlignedBB bb, int red, int green, int blue, int alpha) {
        GlStateManager.color((float) red / 255.0f, (float) green / 255.0f, (float) blue / 255.0f, (float) alpha / 255.0f);
        drawFilledBox(bb);
    }

    public static void drawBoundingFullBox(BlockPos pos, int red, int green, int blue, int alpha) {
        drawBoundingFullBox(getBoundingFromPos(pos), red, green, blue, alpha);
    }

    public static AxisAlignedBB getBoundingFromPos(BlockPos render) {
        IBlockState iBlockState = Wrapper.mc.world.getBlockState(render);
        Vec3d interp = interpolateEntity(Wrapper.mc.player, Wrapper.mc.getRenderPartialTicks());
        return iBlockState.getSelectedBoundingBox(Wrapper.mc.world, render).expand(0.002f, 0.002f, 0.002f).offset(-interp.x, -interp.y, -interp.z);
    }

    public static AxisAlignedBB getBoundingFromPos(Vec3d renders) {
        BlockPos render = new BlockPos(renders);
        IBlockState iBlockState = Wrapper.mc.world.getBlockState(render);
        Vec3d interp = interpolateEntity(Wrapper.mc.player, Wrapper.mc.getRenderPartialTicks());
        return iBlockState.getSelectedBoundingBox(Wrapper.mc.world, render).expand(0.002f, 0.002f, 0.002f).offset(-interp.x, -interp.y, -interp.z);
    }

    public static Vec3d interpolateEntity(Entity entity, float time) {
        return new Vec3d(entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) time, entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) time, entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) time);
    }

    public static Vec3d interpolateEntityClose(Entity entity, float renderPartialTicks) {
        return new Vec3d(calculateDistanceWithPartialTicks(entity.posX, entity.lastTickPosX, renderPartialTicks) - Wrapper.mc.getRenderManager().renderPosX, calculateDistanceWithPartialTicks(entity.posY, entity.lastTickPosY, renderPartialTicks) - Wrapper.mc.getRenderManager().renderPosY, calculateDistanceWithPartialTicks(entity.posZ, entity.lastTickPosZ, renderPartialTicks) - Wrapper.mc.getRenderManager().renderPosZ);
    }

    public static double calculateDistanceWithPartialTicks(double n, double n2, float renderPartialTicks) {
        return n2 + (n - n2) * renderPartialTicks;
    }

    public static void drawFilledBox(AxisAlignedBB axisAlignedBB) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexbuffer = tessellator.getBuffer();
        vertexbuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        vertexbuffer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        vertexbuffer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        vertexbuffer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        vertexbuffer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        vertexbuffer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        vertexbuffer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        vertexbuffer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        vertexbuffer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        vertexbuffer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        vertexbuffer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        vertexbuffer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        vertexbuffer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        vertexbuffer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        vertexbuffer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        vertexbuffer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        vertexbuffer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        vertexbuffer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        vertexbuffer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        vertexbuffer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        vertexbuffer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        vertexbuffer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        vertexbuffer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        vertexbuffer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        vertexbuffer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        vertexbuffer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        vertexbuffer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        vertexbuffer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        vertexbuffer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        vertexbuffer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        vertexbuffer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        vertexbuffer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        vertexbuffer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        vertexbuffer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        vertexbuffer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        vertexbuffer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        vertexbuffer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        vertexbuffer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        vertexbuffer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        vertexbuffer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        vertexbuffer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        vertexbuffer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        vertexbuffer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        vertexbuffer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        vertexbuffer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        vertexbuffer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        vertexbuffer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        vertexbuffer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        vertexbuffer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        tessellator.draw();
    }

    public static void drawBoxTest(float x, float y, float z, float w, float h, float d, int r, int g, int b, int a, int sides) {
        GL11.glPushMatrix();
        GL11.glBlendFunc(770, 771);
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        GlStateManager.glLineWidth(1f);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableDepth();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.enableAlpha();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1f);
        RenderUtils.setColor(r, g, b, a);
        GL11.glBegin(7);
        if ((sides & 1) != 0) {
            GL11.glVertex3d(x + w, y, z);
            GL11.glVertex3d(x + w, y, z + d);
            GL11.glVertex3d(x, y, z + d);
            GL11.glVertex3d(x, y, z);
        }
        if ((sides & 2) != 0) {
            GL11.glVertex3d(x + w, y + h, z);
            GL11.glVertex3d(x, y + h, z);
            GL11.glVertex3d(x, y + h, z + d);
            GL11.glVertex3d(x + w, y + h, z + d);
        }
        if ((sides & 4) != 0) {
            GL11.glVertex3d(x + w, y, z);
            GL11.glVertex3d(x, y, z);
            GL11.glVertex3d(x, y + h, z);
            GL11.glVertex3d(x + w, y + h, z);
        }
        if ((sides & 8) != 0) {
            GL11.glVertex3d(x, y, z + d);
            GL11.glVertex3d(x + w, y, z + d);
            GL11.glVertex3d(x + w, y + h, z + d);
            GL11.glVertex3d(x, y + h, z + d);
        }
        if ((sides & 0x10) != 0) {
            GL11.glVertex3d(x, y, z);
            GL11.glVertex3d(x, y, z + d);
            GL11.glVertex3d(x, y + h, z + d);
            GL11.glVertex3d(x, y + h, z);
        }
        if ((sides & 0x20) != 0) {
            GL11.glVertex3d(x + w, y, z + d);
            GL11.glVertex3d(x + w, y, z);
            GL11.glVertex3d(x + w, y + h, z);
            GL11.glVertex3d(x + w, y + h, z + d);
        }
        GL11.glEnd();
        GlStateManager.enableCull();
        GlStateManager.depthMask(true);
        GlStateManager.enableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.enableDepth();
        GlStateManager.color(1.0f, 1.0f, 1.0f);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glPopMatrix();
    }

    public static void drawBoxTest(AxisAlignedBB bb, Color color, int sides) {
        drawBoxTest(bb, color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha(), sides);
    }

    public static void drawBoxTest(AxisAlignedBB bb, int r, int g, int b, int a, int sides) {
        drawBoxTest((float) bb.minX, (float) bb.minY, (float) bb.minZ, (float) bb.maxX - (float) bb.minX, (float) bb.maxY - (float) bb.minY, (float) bb.maxZ - (float) bb.minZ, r, g, b, a, sides);
    }

    public static void drawBoxTests(final AxisAlignedBB bb, final int r, final int g, final int b, final int a, final int sides) {
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        if ((sides & 0x1) != 0x0) {
            bufferBuilder.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, a).endVertex();
            bufferBuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, a).endVertex();
            bufferBuilder.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, a).endVertex();
            bufferBuilder.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, a).endVertex();
        }
        if ((sides & 0x2) != 0x0) {
            bufferBuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(r, g, b, a).endVertex();
            bufferBuilder.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, a).endVertex();
            bufferBuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(r, g, b, a).endVertex();
            bufferBuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(r, g, b, a).endVertex();
        }
        if ((sides & 0x4) != 0x0) {
            bufferBuilder.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, a).endVertex();
            bufferBuilder.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, a).endVertex();
            bufferBuilder.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, a).endVertex();
            bufferBuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(r, g, b, a).endVertex();
        }
        if ((sides & 0x8) != 0x0) {
            bufferBuilder.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, a).endVertex();
            bufferBuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, a).endVertex();
            bufferBuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(r, g, b, a).endVertex();
            bufferBuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(r, g, b, a).endVertex();
        }
        if ((sides & 0x10) != 0x0) {
            bufferBuilder.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, a).endVertex();
            bufferBuilder.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, a).endVertex();
            bufferBuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(r, g, b, a).endVertex();
            bufferBuilder.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, a).endVertex();
        }
        if ((sides & 0x20) != 0x0) {
            bufferBuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, a).endVertex();
            bufferBuilder.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, a).endVertex();
            bufferBuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(r, g, b, a).endVertex();
            bufferBuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(r, g, b, a).endVertex();
        }
        tessellator.draw();
    }
}

