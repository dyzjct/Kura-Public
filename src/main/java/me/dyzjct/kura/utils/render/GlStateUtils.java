package me.dyzjct.kura.utils.render;

import net.minecraft.client.*;
import kotlin.jvm.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.gui.*;
import org.lwjgl.opengl.*;

public class GlStateUtils
{
    public static Minecraft mc;
    private static boolean colorLock;
    private static int bindProgram;
    
    public static boolean getColorLock() {
        return GlStateUtils.colorLock;
    }
    
    @JvmStatic
    public static boolean useVbo() {
        return GlStateUtils.mc.gameSettings.useVbo;
    }
    
    @JvmStatic
    public static void matrix(final boolean state) {
        if (state) {
            GL11.glPushMatrix();
        }
        else {
            GL11.glPopMatrix();
        }
    }
    
    @JvmStatic
    public static void blend(final boolean state) {
        if (state) {
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        }
        else {
            GlStateManager.disableBlend();
        }
    }
    
    @JvmStatic
    public static void alpha(final boolean state) {
        if (state) {
            GlStateManager.enableAlpha();
        }
        else {
            GlStateManager.disableAlpha();
        }
    }
    
    @JvmStatic
    public static void smooth(final boolean state) {
        if (state) {
            GlStateManager.shadeModel(7425);
        }
        else {
            GlStateManager.shadeModel(7424);
        }
    }
    
    @JvmStatic
    public static void lineSmooth(final boolean state) {
        if (state) {
            GL11.glEnable(2848);
            GL11.glHint(3154, 4354);
        }
        else {
            GL11.glDisable(2848);
            GL11.glHint(3154, 4352);
        }
    }
    
    @JvmStatic
    public static void hintPolygon(final boolean state) {
        if (state) {
            GL11.glHint(3155, 4354);
        }
        else {
            GL11.glHint(3155, 4352);
        }
    }
    
    @JvmStatic
    public static void depth(final boolean state) {
        if (state) {
            GlStateManager.enableDepth();
        }
        else {
            GlStateManager.disableDepth();
        }
    }
    
    @JvmStatic
    public static void depthMask(final boolean state) {
        GlStateManager.depthMask(state);
    }
    
    @JvmStatic
    public static void texture2d(final boolean state) {
        if (state) {
            GlStateManager.enableTexture2D();
        }
        else {
            GlStateManager.disableTexture2D();
        }
    }
    
    @JvmStatic
    public static void cull(final boolean state) {
        if (state) {
            GlStateManager.enableCull();
        }
        else {
            GlStateManager.disableCull();
        }
    }
    
    @JvmStatic
    public static void lighting(final boolean state) {
        if (state) {
            GlStateManager.enableLighting();
        }
        else {
            GlStateManager.disableLighting();
        }
    }
    
    @JvmStatic
    public static void polygon(final boolean state) {
        if (state) {
            GlStateManager.enablePolygonOffset();
            GlStateManager.doPolygonOffset(1.0f, -1500000.0f);
        }
        else {
            GlStateManager.disablePolygonOffset();
            GlStateManager.doPolygonOffset(1.0f, 1500000.0f);
        }
    }
    
    @JvmStatic
    public static void smoothTexture() {
        GL11.glTexParameteri(3553, 10241, 9729);
        GL11.glTexParameteri(3553, 10240, 9729);
    }
    
    @JvmStatic
    public static void resetColour() {
        RenderUtils.glColor(1, 1, 1, 1);
    }
    
    @JvmStatic
    public static void colorLock(final boolean state) {
        GlStateUtils.colorLock = state;
    }
    
    @JvmStatic
    public static void resetTexParam() {
        GlStateManager.bindTexture(0);
        GL11.glTexParameteri(3553, 10240, 9729);
        GL11.glTexParameteri(3553, 10241, 9986);
        GL11.glTexParameteri(3553, 10242, 10497);
        GL11.glTexParameteri(3553, 10243, 10497);
        GL11.glTexParameteri(3553, 33085, 1000);
        GL11.glTexParameteri(3553, 33083, 1000);
        GL11.glTexParameteri(3553, 33082, -1000);
    }
    
    @JvmStatic
    public static void rescale(final double width, final double height) {
        GlStateManager.clear(256);
        GlStateManager.viewport(0, 0, GlStateUtils.mc.displayWidth, GlStateUtils.mc.displayHeight);
        GlStateManager.matrixMode(5889);
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0.0, width, height, 0.0, 1000.0, 3000.0);
        GlStateManager.matrixMode(5888);
        GlStateManager.loadIdentity();
        GlStateManager.translate(0.0f, 0.0f, -2000.0f);
    }
    
    @JvmStatic
    public static void rescaleActual() {
        rescale(GlStateUtils.mc.displayWidth, GlStateUtils.mc.displayHeight);
    }
    
    @JvmStatic
    public static void rescaleMc() {
        final ScaledResolution resolution = new ScaledResolution(GlStateUtils.mc);
        rescale(resolution.getScaledWidth_double(), resolution.getScaledHeight_double());
    }
    
    public static void useProgram(final int id) {
        if (id != GlStateUtils.bindProgram) {
            GL20.glUseProgram(id);
            GlStateUtils.bindProgram = id;
        }
    }
    
    static {
        GlStateUtils.mc = Minecraft.getMinecraft();
        GlStateUtils.bindProgram = 0;
    }
}
