package me.dyzjct.kura.utils.render;

import net.minecraft.client.*;
import net.minecraft.client.entity.*;
import net.minecraftforge.common.*;
import org.lwjgl.opengl.*;
import me.dyzjct.kura.module.modules.render.*;
import java.awt.*;
import net.minecraft.util.math.*;
import me.dyzjct.kura.utils.gl.*;
import net.minecraft.client.model.*;
import net.minecraft.client.renderer.*;
import net.minecraft.entity.*;

public class TotemPopCham
{
    private static final Minecraft mc;
    EntityOtherPlayerMP player;
    ModelPlayer playerModel;
    Long startTime;
    double alphaFill;
    double alphaLine;
    
    public TotemPopCham(final EntityOtherPlayerMP player, final ModelPlayer playerModel, final Long startTime, double alphaFill) {
        MinecraftForge.EVENT_BUS.register((Object)this);
        this.player = player;
        this.playerModel = playerModel;
        this.startTime = startTime;
        this.alphaFill = alphaFill;
        this.alphaLine = alphaFill;
        if (player != null && TotemPopCham.mc.world != null && TotemPopCham.mc.player != null) {
            GL11.glLineWidth(1.0f);
            final Color fillColorS = new Color((int)PopChams.INSTANCE.rF.getValue(), (int)PopChams.INSTANCE.bF.getValue(), (int)PopChams.INSTANCE.gF.getValue(), (int)PopChams.INSTANCE.aF.getValue());
            int fillA = fillColorS.getAlpha();
            final long time = System.currentTimeMillis() - this.startTime - ((Number)PopChams.INSTANCE.fadestart.getValue()).longValue();
            if (System.currentTimeMillis() - this.startTime > ((Number)PopChams.INSTANCE.fadestart.getValue()).longValue()) {
                double normal = this.normalize((double)time, 0.0, ((Number)PopChams.INSTANCE.fadetime.getValue()).doubleValue());
                normal = MathHelper.clamp(normal, 0.0, 1.0);
                normal = -normal + 1.0;
                fillA *= (int)normal;
            }
            final Color fillColor = newAlpha(fillColorS, fillA);
            if (this.player != null && this.playerModel != null) {
                MelonTessellator.prepare(7);
                GL11.glPushAttrib(1048575);
                GL11.glEnable(2881);
                GL11.glEnable(2848);
                if (alphaFill > 1.0) {
                    alphaFill -= (double)PopChams.INSTANCE.fadetime.getValue();
                }
                final Color fillFinal = new Color(fillColor.getRed(), fillColor.getGreen(), fillColor.getBlue(), (int)alphaFill);
                glColor(fillFinal);
                GL11.glPolygonMode(1032, 6914);
                renderEntity((EntityLivingBase)this.player, (ModelBase)this.playerModel, this.player.limbSwing, this.player.limbSwingAmount, 1.0f);
                GL11.glPolygonMode(1032, 6913);
                renderEntity((EntityLivingBase)this.player, (ModelBase)this.playerModel, this.player.limbSwing, this.player.limbSwingAmount, 1.0f);
                GL11.glPolygonMode(1032, 6914);
                GL11.glPopAttrib();
                MelonTessellator.release();
            }
        }
    }
    
    public static void renderEntity(final EntityLivingBase entity, final ModelBase modelBase, final float limbSwing, final float limbSwingAmount, final float scale) {
        if (TotemPopCham.mc.getRenderManager() != null) {
            final float partialTicks = TotemPopCham.mc.getRenderPartialTicks();
            final double x = entity.posX - TotemPopCham.mc.getRenderManager().viewerPosX;
            double y = entity.posY - TotemPopCham.mc.getRenderManager().viewerPosY;
            final double z = entity.posZ - TotemPopCham.mc.getRenderManager().viewerPosZ;
            GlStateManager.pushMatrix();
            if (entity.isSneaking()) {
                y -= 0.125;
            }
            renderLivingAt(x, y, z);
            prepareRotations(entity);
            final float f9 = prepareScale(entity, scale);
            GlStateManager.enableAlpha();
            modelBase.setLivingAnimations(entity, limbSwing, limbSwingAmount, partialTicks);
            modelBase.setRotationAngles(limbSwing, limbSwingAmount, entity.rotationYaw, entity.rotationYawHead, entity.rotationPitch, f9, (Entity)entity);
            modelBase.render((Entity)entity, limbSwing, limbSwingAmount, entity.rotationYaw, entity.rotationYawHead, entity.rotationPitch, f9);
            GlStateManager.popMatrix();
        }
    }
    
    public static void renderLivingAt(final double x, final double y, final double z) {
        GlStateManager.translate((float)x, (float)y, (float)z);
    }
    
    public static float prepareScale(final EntityLivingBase entity, final float scale) {
        GlStateManager.enableRescaleNormal();
        GlStateManager.scale(-1.0f, -1.0f, 1.0f);
        final double widthX = entity.getRenderBoundingBox().maxX - entity.getRenderBoundingBox().minX;
        final double widthZ = entity.getRenderBoundingBox().maxZ - entity.getRenderBoundingBox().minZ;
        GlStateManager.scale(scale + widthX, (double)(scale * entity.height), scale + widthZ);
        GlStateManager.translate(0.0f, -1.501f, 0.0f);
        return 0.0625f;
    }
    
    public static void prepareRotations(final EntityLivingBase entityLivingBase) {
        GlStateManager.rotate(180.0f - entityLivingBase.rotationYaw, 0.0f, 1.0f, 0.0f);
    }
    
    public static Color newAlpha(final Color color, final int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }
    
    public static void glColor(final Color color) {
        GL11.glColor4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
    }
    
    public double normalize(final double value, final double min, final double max) {
        return (value - min) / (max - min);
    }
    
    static {
        mc = Minecraft.getMinecraft();
    }
}
