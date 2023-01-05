package me.dyzjct.kura.utils.fn;

import java.awt.Color;
import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.Chunk;
import org.lwjgl.opengl.GL11;

public class ESPUtil implements MC {
  public static void boundingESPBox(AxisAlignedBB box, Color c, float lineWidth) {
    int r = c.getRed();
    int g = c.getGreen();
    int b = c.getBlue();
    int a = c.getAlpha();
    double x = box.minX - (Minecraft.getMinecraft().getRenderManager()).viewerPosX;
    double y = box.minY - (Minecraft.getMinecraft().getRenderManager()).viewerPosY;
    double z = box.minZ - (Minecraft.getMinecraft().getRenderManager()).viewerPosZ;
    GL11.glBlendFunc(770, 771);
    GL11.glEnable(3042);
    GL11.glLineWidth(lineWidth);
    GL11.glDisable(3553);
    GL11.glDisable(2929);
    GL11.glDepthMask(false);
    GL11.glColor4d(0.0D, 1.0D, 0.0D, 0.15000000596046448D);
    AxisAlignedBB bb = new AxisAlignedBB(x, y, z, x - box.minX + box.maxX, y - box.minY + box.maxY, z - box.minZ + box.maxZ);
    RenderGlobal.drawSelectionBoundingBox(bb, 0.00390625F * r, 0.00390625F * g, 0.00390625F * b, 0.00390625F * a);
    GL11.glEnable(3553);
    GL11.glEnable(2929);
    GL11.glDepthMask(true);
    GL11.glDisable(3042);
  }
  
  public static void boundingESPBoxFilled(AxisAlignedBB box, Color c) {
    int r = c.getRed();
    int g = c.getGreen();
    int b = c.getBlue();
    int a = c.getAlpha();
    double x = box.minX - (Minecraft.getMinecraft().getRenderManager()).viewerPosX;
    double y = box.minY - (Minecraft.getMinecraft().getRenderManager()).viewerPosY;
    double z = box.minZ - (Minecraft.getMinecraft().getRenderManager()).viewerPosZ;
    GL11.glBlendFunc(770, 771);
    GL11.glEnable(3042);
    GL11.glLineWidth(2.0F);
    GL11.glDisable(3553);
    GL11.glDisable(2929);
    GL11.glDepthMask(false);
    GL11.glColor4d((0.00390625F * r), (0.00390625F * g), (0.00390625F * b), (0.00390625F * a));
    GL11.glBegin(7);
    AxisAlignedBB bb = new AxisAlignedBB(x, y, z, x - box.minX + box.maxX, y - box.minY + box.maxY, z - box.minZ + box.maxZ);
    GL11.glVertex3d(bb.minX, bb.minY, bb.minZ);
    GL11.glVertex3d(bb.maxX, bb.minY, bb.minZ);
    GL11.glVertex3d(bb.maxX, bb.minY, bb.maxZ);
    GL11.glVertex3d(bb.minX, bb.minY, bb.maxZ);
    GL11.glVertex3d(bb.minX, bb.maxY, bb.minZ);
    GL11.glVertex3d(bb.minX, bb.maxY, bb.maxZ);
    GL11.glVertex3d(bb.maxX, bb.maxY, bb.maxZ);
    GL11.glVertex3d(bb.maxX, bb.maxY, bb.minZ);
    GL11.glVertex3d(bb.minX, bb.minY, bb.minZ);
    GL11.glVertex3d(bb.minX, bb.maxY, bb.minZ);
    GL11.glVertex3d(bb.maxX, bb.maxY, bb.minZ);
    GL11.glVertex3d(bb.maxX, bb.minY, bb.minZ);
    GL11.glVertex3d(bb.maxX, bb.minY, bb.minZ);
    GL11.glVertex3d(bb.maxX, bb.maxY, bb.minZ);
    GL11.glVertex3d(bb.maxX, bb.maxY, bb.maxZ);
    GL11.glVertex3d(bb.maxX, bb.minY, bb.maxZ);
    GL11.glVertex3d(bb.minX, bb.minY, bb.maxZ);
    GL11.glVertex3d(bb.maxX, bb.minY, bb.maxZ);
    GL11.glVertex3d(bb.maxX, bb.maxY, bb.maxZ);
    GL11.glVertex3d(bb.minX, bb.maxY, bb.maxZ);
    GL11.glVertex3d(bb.minX, bb.minY, bb.minZ);
    GL11.glVertex3d(bb.minX, bb.minY, bb.maxZ);
    GL11.glVertex3d(bb.minX, bb.maxY, bb.maxZ);
    GL11.glVertex3d(bb.minX, bb.maxY, bb.minZ);
    GL11.glEnd();
    GL11.glEnable(3553);
    GL11.glEnable(2929);
    GL11.glDepthMask(true);
    GL11.glDisable(3042);
  }
  
  public static void drawOpenGradientBoxBB(AxisAlignedBB bb, Color startColor, Color endColor) {
    for (EnumFacing face : EnumFacing.values())
      drawGradientPlaneBB(bb.grow(0.0020000000949949026D), face, startColor, endColor); 
  }
  
  public static void drawGradientPlaneBB(AxisAlignedBB bb, EnumFacing face, Color startColor, Color endColor) {
    Tessellator tessellator = Tessellator.getInstance();
    BufferBuilder builder = tessellator.getBuffer();
    bb = new AxisAlignedBB(bb.minX - (mc.getRenderManager()).viewerPosX, bb.minY - (mc.getRenderManager()).viewerPosY, bb.minZ - (mc.getRenderManager()).viewerPosZ, bb.maxX - (mc.getRenderManager()).viewerPosX, bb.maxY - (mc.getRenderManager()).viewerPosY, bb.maxZ - (mc.getRenderManager()).viewerPosZ);
    float red = startColor.getRed() / 255.0F;
    float green = startColor.getGreen() / 255.0F;
    float blue = startColor.getBlue() / 255.0F;
    float alpha = startColor.getAlpha() / 255.0F;
    float red1 = endColor.getRed() / 255.0F;
    float green1 = endColor.getGreen() / 255.0F;
    float blue1 = endColor.getBlue() / 255.0F;
    float alpha1 = endColor.getAlpha() / 255.0F;
    double x1 = 0.0D;
    double y1 = 0.0D;
    double z1 = 0.0D;
    double x2 = 0.0D;
    double y2 = 0.0D;
    double z2 = 0.0D;
    switch (face) {
      case DOWN:
        x1 = bb.minX;
        x2 = bb.maxX;
        y1 = bb.minY;
        y2 = bb.minY;
        z1 = bb.minZ;
        z2 = bb.maxZ;
        break;
      case UP:
        x1 = bb.minX;
        x2 = bb.maxX;
        y1 = bb.maxY;
        y2 = bb.maxY;
        z1 = bb.minZ;
        z2 = bb.maxZ;
        break;
      case EAST:
        x1 = bb.maxX;
        x2 = bb.maxX;
        y1 = bb.minY;
        y2 = bb.maxY;
        z1 = bb.minZ;
        z2 = bb.maxZ;
        break;
      case WEST:
        x1 = bb.minX;
        x2 = bb.minX;
        y1 = bb.minY;
        y2 = bb.maxY;
        z1 = bb.minZ;
        z2 = bb.maxZ;
        break;
      case SOUTH:
        x1 = bb.minX;
        x2 = bb.maxX;
        y1 = bb.minY;
        y2 = bb.maxY;
        z1 = bb.maxZ;
        z2 = bb.maxZ;
        break;
      case NORTH:
        x1 = bb.minX;
        x2 = bb.maxX;
        y1 = bb.minY;
        y2 = bb.maxY;
        z1 = bb.minZ;
        z2 = bb.minZ;
        break;
    } 
    GlStateManager.pushMatrix();
    GlStateManager.disableTexture2D();
    GlStateManager.enableBlend();
    GlStateManager.disableAlpha();
    GlStateManager.depthMask(false);
    GlStateManager.disableDepth();
    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
    builder.begin(5, DefaultVertexFormats.POSITION_COLOR);
    if (face == EnumFacing.EAST || face == EnumFacing.WEST || face == EnumFacing.NORTH || face == EnumFacing.SOUTH) {
      builder.pos(x1, y1, z1).color(red, green, blue, alpha).endVertex();
      builder.pos(x1, y1, z1).color(red, green, blue, alpha).endVertex();
      builder.pos(x1, y1, z1).color(red, green, blue, alpha).endVertex();
      builder.pos(x1, y1, z2).color(red, green, blue, alpha).endVertex();
      builder.pos(x1, y2, z1).color(red1, green1, blue1, alpha1).endVertex();
      builder.pos(x1, y2, z2).color(red1, green1, blue1, alpha1).endVertex();
      builder.pos(x1, y2, z2).color(red1, green1, blue1, alpha1).endVertex();
      builder.pos(x1, y1, z2).color(red, green, blue, alpha).endVertex();
      builder.pos(x2, y2, z2).color(red1, green1, blue1, alpha1).endVertex();
      builder.pos(x2, y1, z2).color(red, green, blue, alpha).endVertex();
      builder.pos(x2, y1, z2).color(red, green, blue, alpha).endVertex();
      builder.pos(x2, y1, z1).color(red, green, blue, alpha).endVertex();
      builder.pos(x2, y2, z2).color(red1, green1, blue1, alpha1).endVertex();
      builder.pos(x2, y2, z1).color(red1, green1, blue1, alpha1).endVertex();
      builder.pos(x2, y2, z1).color(red1, green1, blue1, alpha1).endVertex();
      builder.pos(x2, y1, z1).color(red, green, blue, alpha).endVertex();
      builder.pos(x1, y2, z1).color(red1, green1, blue1, alpha1).endVertex();
      builder.pos(x1, y1, z1).color(red, green, blue, alpha).endVertex();
      builder.pos(x1, y1, z1).color(red, green, blue, alpha).endVertex();
      builder.pos(x2, y1, z1).color(red, green, blue, alpha).endVertex();
      builder.pos(x1, y1, z2).color(red, green, blue, alpha).endVertex();
      builder.pos(x2, y1, z2).color(red, green, blue, alpha).endVertex();
      builder.pos(x2, y1, z2).color(red, green, blue, alpha).endVertex();
      builder.pos(x1, y2, z1).color(red1, green1, blue1, alpha1).endVertex();
      builder.pos(x1, y2, z1).color(red1, green1, blue1, alpha1).endVertex();
      builder.pos(x1, y2, z2).color(red1, green1, blue1, alpha1).endVertex();
      builder.pos(x2, y2, z1).color(red1, green1, blue1, alpha1).endVertex();
      builder.pos(x2, y2, z2).color(red1, green1, blue1, alpha1).endVertex();
      builder.pos(x2, y2, z2).color(red1, green1, blue1, alpha1).endVertex();
      builder.pos(x2, y2, z2).color(red1, green1, blue1, alpha1).endVertex();
    } else if (face == EnumFacing.UP) {
      builder.pos(x1, y1, z1).color(red1, green1, blue1, alpha1).endVertex();
      builder.pos(x1, y1, z1).color(red1, green1, blue1, alpha1).endVertex();
      builder.pos(x1, y1, z1).color(red1, green1, blue1, alpha1).endVertex();
      builder.pos(x1, y1, z2).color(red1, green1, blue1, alpha1).endVertex();
      builder.pos(x1, y2, z1).color(red1, green1, blue1, alpha1).endVertex();
      builder.pos(x1, y2, z2).color(red1, green1, blue1, alpha1).endVertex();
      builder.pos(x1, y2, z2).color(red1, green1, blue1, alpha1).endVertex();
      builder.pos(x1, y1, z2).color(red1, green1, blue1, alpha1).endVertex();
      builder.pos(x2, y2, z2).color(red1, green1, blue1, alpha1).endVertex();
      builder.pos(x2, y1, z2).color(red1, green1, blue1, alpha1).endVertex();
      builder.pos(x2, y1, z2).color(red1, green1, blue1, alpha1).endVertex();
      builder.pos(x2, y1, z1).color(red1, green1, blue1, alpha1).endVertex();
      builder.pos(x2, y2, z2).color(red1, green1, blue1, alpha1).endVertex();
      builder.pos(x2, y2, z1).color(red1, green1, blue1, alpha1).endVertex();
      builder.pos(x2, y2, z1).color(red1, green1, blue1, alpha1).endVertex();
      builder.pos(x2, y1, z1).color(red1, green1, blue1, alpha1).endVertex();
      builder.pos(x1, y2, z1).color(red1, green1, blue1, alpha1).endVertex();
      builder.pos(x1, y1, z1).color(red1, green1, blue1, alpha1).endVertex();
      builder.pos(x1, y1, z1).color(red1, green1, blue1, alpha1).endVertex();
      builder.pos(x2, y1, z1).color(red1, green1, blue1, alpha1).endVertex();
      builder.pos(x1, y1, z2).color(red1, green1, blue1, alpha1).endVertex();
      builder.pos(x2, y1, z2).color(red1, green1, blue1, alpha1).endVertex();
      builder.pos(x2, y1, z2).color(red1, green1, blue1, alpha1).endVertex();
      builder.pos(x1, y2, z1).color(red1, green1, blue1, alpha1).endVertex();
      builder.pos(x1, y2, z1).color(red1, green1, blue1, alpha1).endVertex();
      builder.pos(x1, y2, z2).color(red1, green1, blue1, alpha1).endVertex();
      builder.pos(x2, y2, z1).color(red1, green1, blue1, alpha1).endVertex();
      builder.pos(x2, y2, z2).color(red1, green1, blue1, alpha1).endVertex();
      builder.pos(x2, y2, z2).color(red1, green1, blue1, alpha1).endVertex();
      builder.pos(x2, y2, z2).color(red1, green1, blue1, alpha1).endVertex();
    } else if (face == EnumFacing.DOWN) {
      builder.pos(x1, y1, z1).color(red, green, blue, alpha).endVertex();
      builder.pos(x1, y1, z1).color(red, green, blue, alpha).endVertex();
      builder.pos(x1, y1, z1).color(red, green, blue, alpha).endVertex();
      builder.pos(x1, y1, z2).color(red, green, blue, alpha).endVertex();
      builder.pos(x1, y2, z1).color(red, green, blue, alpha).endVertex();
      builder.pos(x1, y2, z2).color(red, green, blue, alpha).endVertex();
      builder.pos(x1, y2, z2).color(red, green, blue, alpha).endVertex();
      builder.pos(x1, y1, z2).color(red, green, blue, alpha).endVertex();
      builder.pos(x2, y2, z2).color(red, green, blue, alpha).endVertex();
      builder.pos(x2, y1, z2).color(red, green, blue, alpha).endVertex();
      builder.pos(x2, y1, z2).color(red, green, blue, alpha).endVertex();
      builder.pos(x2, y1, z1).color(red, green, blue, alpha).endVertex();
      builder.pos(x2, y2, z2).color(red, green, blue, alpha).endVertex();
      builder.pos(x2, y2, z1).color(red, green, blue, alpha).endVertex();
      builder.pos(x2, y2, z1).color(red, green, blue, alpha).endVertex();
      builder.pos(x2, y1, z1).color(red, green, blue, alpha).endVertex();
      builder.pos(x1, y2, z1).color(red, green, blue, alpha).endVertex();
      builder.pos(x1, y1, z1).color(red, green, blue, alpha).endVertex();
      builder.pos(x1, y1, z1).color(red, green, blue, alpha).endVertex();
      builder.pos(x2, y1, z1).color(red, green, blue, alpha).endVertex();
      builder.pos(x1, y1, z2).color(red, green, blue, alpha).endVertex();
      builder.pos(x2, y1, z2).color(red, green, blue, alpha).endVertex();
      builder.pos(x2, y1, z2).color(red, green, blue, alpha).endVertex();
      builder.pos(x1, y2, z1).color(red, green, blue, alpha).endVertex();
      builder.pos(x1, y2, z1).color(red, green, blue, alpha).endVertex();
      builder.pos(x1, y2, z2).color(red, green, blue, alpha).endVertex();
      builder.pos(x2, y2, z1).color(red, green, blue, alpha).endVertex();
      builder.pos(x2, y2, z2).color(red, green, blue, alpha).endVertex();
      builder.pos(x2, y2, z2).color(red, green, blue, alpha).endVertex();
      builder.pos(x2, y2, z2).color(red, green, blue, alpha).endVertex();
    } 
    tessellator.draw();
    GlStateManager.depthMask(true);
    GlStateManager.enableDepth();
    GlStateManager.disableBlend();
    GlStateManager.enableAlpha();
    GlStateManager.enableTexture2D();
    GlStateManager.popMatrix();
  }
  
  public static void drawGradientBlockOutline(AxisAlignedBB bb, Color startColor, Color endColor, float linewidth) {
    bb = new AxisAlignedBB(bb.minX - (mc.getRenderManager()).viewerPosX, bb.minY - (mc.getRenderManager()).viewerPosY, bb.minZ - (mc.getRenderManager()).viewerPosZ, bb.maxX - (mc.getRenderManager()).viewerPosX, bb.maxY - (mc.getRenderManager()).viewerPosY, bb.maxZ - (mc.getRenderManager()).viewerPosZ);
    float red = startColor.getRed() / 255.0F;
    float green = startColor.getGreen() / 255.0F;
    float blue = startColor.getBlue() / 255.0F;
    float alpha = startColor.getAlpha() / 255.0F;
    float red1 = endColor.getRed() / 255.0F;
    float green1 = endColor.getGreen() / 255.0F;
    float blue1 = endColor.getBlue() / 255.0F;
    float alpha1 = endColor.getAlpha() / 255.0F;
    GlStateManager.pushMatrix();
    GlStateManager.enableBlend();
    GlStateManager.disableDepth();
    GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
    GlStateManager.disableTexture2D();
    GlStateManager.depthMask(false);
    GL11.glEnable(2848);
    GL11.glHint(3154, 4354);
    GL11.glLineWidth(linewidth);
    Tessellator tessellator = Tessellator.getInstance();
    BufferBuilder bufferbuilder = tessellator.getBuffer();
    bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
    bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red1, green1, blue1, alpha1).endVertex();
    bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red1, green1, blue1, alpha1).endVertex();
    bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red1, green1, blue1, alpha1).endVertex();
    bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red1, green1, blue1, alpha1).endVertex();
    bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red1, green1, blue1, alpha1).endVertex();
    bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
    bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
    bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red1, green1, blue1, alpha1).endVertex();
    bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red1, green1, blue1, alpha1).endVertex();
    bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
    bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
    bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
    bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
    bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red1, green1, blue1, alpha1).endVertex();
    bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
    bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
    tessellator.draw();
    GL11.glDisable(2848);
    GlStateManager.depthMask(true);
    GlStateManager.enableDepth();
    GlStateManager.enableTexture2D();
    GlStateManager.disableBlend();
    GlStateManager.popMatrix();
  }
  
  public static void chunkEsp(Chunk c, Color col, double yPos) {
    GL11.glBlendFunc(770, 771);
    GL11.glEnable(3042);
    GL11.glLineWidth(2.0F);
    GL11.glDisable(3553);
    GL11.glDisable(2929);
    GL11.glDepthMask(false);
    double startX = c.getPos().getXStart() - (Minecraft.getMinecraft().getRenderManager()).viewerPosX;
    double endX = c.getPos().getXEnd() - (Minecraft.getMinecraft().getRenderManager()).viewerPosX;
    double y = yPos - (Minecraft.getMinecraft().getRenderManager()).viewerPosY;
    double startZ = c.getPos().getZStart() - (Minecraft.getMinecraft().getRenderManager()).viewerPosZ;
    double endZ = c.getPos().getZEnd() - (Minecraft.getMinecraft().getRenderManager()).viewerPosZ;
    GL11.glColor4f(col.getRed() / 255.0F, col.getGreen() / 255.0F, col.getBlue() / 255.0F, col.getAlpha() / 255.0F);
    GL11.glBegin(1);
    GL11.glVertex3d(startX, y, startZ);
    GL11.glVertex3d(endX, y, startZ);
    GL11.glVertex3d(endX, y, startZ);
    GL11.glVertex3d(endX, y, endZ);
    GL11.glVertex3d(endX, y, endZ);
    GL11.glVertex3d(startX, y, endZ);
    GL11.glVertex3d(startX, y, endZ);
    GL11.glVertex3d(startX, y, startZ);
    GL11.glEnd();
    GL11.glEnable(3553);
    GL11.glEnable(2929);
    GL11.glDepthMask(true);
    GL11.glDisable(3042);
  }
  
  public static void renderLineList(ArrayList<Vec3d> list, Color color) {
    GL11.glBlendFunc(770, 771);
    GL11.glEnable(3042);
    GL11.glLineWidth(2.0F);
    GL11.glDisable(3553);
    GL11.glDisable(2929);
    GL11.glDepthMask(false);
    GL11.glBegin(1);
    GL11.glColor4f(0.00390625F * color.getRed(), 0.00390625F * color.getGreen(), 0.00390625F * color.getBlue(), 0.00390625F * color.getAlpha());
    for (int i = 0; i < list.size() - 1; i++) {
      double x = ((Vec3d)list.get(i)).x - (mc.getRenderManager()).renderPosX;
      double y = ((Vec3d)list.get(i)).y - (mc.getRenderManager()).renderPosY;
      double z = ((Vec3d)list.get(i)).z - (mc.getRenderManager()).renderPosZ;
      GL11.glVertex3d(x, y, z);
      x = ((Vec3d)list.get(i + 1)).x - (mc.getRenderManager()).renderPosX;
      y = ((Vec3d)list.get(i + 1)).y - (mc.getRenderManager()).renderPosY;
      z = ((Vec3d)list.get(i + 1)).z - (mc.getRenderManager()).renderPosZ;
      GL11.glVertex3d(x, y, z);
    } 
    GL11.glEnd();
    GL11.glEnable(3553);
    GL11.glEnable(2929);
    GL11.glDepthMask(true);
    GL11.glDisable(3042);
  }
  
  public static Vec3d getClientLookVec() {
    RenderManager r = mc.getRenderManager();
    float f = 0.017453292F;
    float pi = 3.1415927F;
    float f1 = MathHelper.cos(-r.playerViewY * f - pi);
    float f2 = MathHelper.sin(-r.playerViewY * f - pi);
    float f3 = -MathHelper.cos(-r.playerViewX * f);
    float f4 = MathHelper.sin(-r.playerViewX * f);
    return new Vec3d((f2 * f3), f4, (f1 * f3));
  }
  
  public static AxisAlignedBB getRenderBB(Entity entity) {
    double p_188388_2_ = mc.getRenderPartialTicks();
    return new AxisAlignedBB(entity.boundingBox.minX - 0.05D + (entity.posX - entity.lastTickPosX) * p_188388_2_ - entity.posX - entity.lastTickPosX, entity.boundingBox.minY + (entity.posY - entity.lastTickPosY) * p_188388_2_ - entity.posY - entity.lastTickPosY, entity.boundingBox.minZ - 0.05D + (entity.posZ - entity.lastTickPosZ) * p_188388_2_ - entity.posZ - entity.lastTickPosZ, entity.boundingBox.maxX + 0.05D + (entity.posX - entity.lastTickPosX) * p_188388_2_ - entity.posX - entity.lastTickPosX, entity.boundingBox.maxY + 0.1D + (entity.posY - entity.lastTickPosY) * p_188388_2_ - entity.posY - entity.lastTickPosY, entity.boundingBox.maxZ + 0.05D + (entity.posZ - entity.lastTickPosZ) * p_188388_2_ - entity.posZ - entity.lastTickPosZ);
  }
  
  public static void drawBox(BlockPos pos, Color color) {
    boundingESPBoxFilled(new AxisAlignedBB(pos), color);
  }
  
  public static void drawOutline(BlockPos pos, Color color) {
    boundingESPBox(new AxisAlignedBB(pos), color, 2.0F);
  }
}
