package me.dyzjct.kura.utils.gl

import me.dyzjct.kura.utils.Rainbow
import me.dyzjct.kura.utils.Wrapper
import me.dyzjct.kura.utils.animations.sq
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderGlobal
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.culling.Frustum
import net.minecraft.client.renderer.culling.ICamera
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.lang.Math.toRadians
import java.util.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

object MelonTessellator : Tessellator(2097152) {
    var mc = Minecraft.getMinecraft()
    var camera: ICamera = Frustum()
    fun prepare(mode: Int) {
        prepareGL()
        begin(mode)
    }

    fun glCleanup() {
        GL11.glEnable(3553)
        GL11.glEnable(2929)
        GL11.glDepthMask(true)
        GL11.glDisable(3042)
        GL11.glPopMatrix()
    }

    @JvmStatic
    fun glRestore() {
        GlStateManager.enableCull()
        GlStateManager.enableAlpha()
        GlStateManager.shadeModel(GL11.GL_FLAT)
    }

    fun color(color: Int) {
        val f = (color shr 24 and 255).toFloat() / 255.0f
        val f1 = (color shr 16 and 255).toFloat() / 255.0f
        val f2 = (color shr 8 and 255).toFloat() / 255.0f
        val f3 = (color and 255).toFloat() / 255.0f
        GL11.glColor4f(f1, f2, f3, f)
    }

    @JvmStatic
    fun drawBlockOutline(bb: AxisAlignedBB, color: Color, alphaVal: Float, linewidth: Float) {
        val red = color.red / 255.0f
        val green = color.green / 255.0f
        val blue = color.blue / 255.0f
        GlStateManager.pushMatrix()
        GlStateManager.enableBlend()
        GlStateManager.disableDepth()
        GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1)
        GlStateManager.disableTexture2D()
        GlStateManager.depthMask(false)
        GL11.glEnable(2848)
        GL11.glHint(3154, 4354)
        GL11.glLineWidth(linewidth)
        val tessellator = getInstance()
        val bufferbuilder = tessellator.buffer
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR)
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, alphaVal).endVertex()
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red, green, blue, alphaVal).endVertex()
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red, green, blue, alphaVal).endVertex()
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red, green, blue, alphaVal).endVertex()
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, alphaVal).endVertex()
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alphaVal).endVertex()
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, alphaVal).endVertex()
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red, green, blue, alphaVal).endVertex()
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red, green, blue, alphaVal).endVertex()
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, alphaVal).endVertex()
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, alphaVal).endVertex()
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, alphaVal).endVertex()
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, alphaVal).endVertex()
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red, green, blue, alphaVal).endVertex()
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, alphaVal).endVertex()
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alphaVal).endVertex()
        tessellator.draw()
        GL11.glDisable(2848)
        GlStateManager.depthMask(true)
        GlStateManager.enableDepth()
        GlStateManager.enableTexture2D()
        GlStateManager.disableBlend()
        GlStateManager.popMatrix()
    }

    fun drawPlane(x: Double, y: Double, z: Double, bb: AxisAlignedBB, width: Float, color: Int) {
        GL11.glPushMatrix()
        GL11.glTranslated(x, y, z)
        drawPlane(bb, width, color)
        GL11.glPopMatrix()
    }

    fun drawPlane(axisalignedbb: AxisAlignedBB, width: Float, color: Int) {
        GlStateManager.pushMatrix()
        GlStateManager.glLineWidth(width)
        GlStateManager.enableBlend()
        GlStateManager.disableDepth()
        GlStateManager.tryBlendFuncSeparate(
            GlStateManager.SourceFactor.SRC_ALPHA,
            GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
            GlStateManager.SourceFactor.ONE,
            GlStateManager.DestFactor.ONE
        )
        GlStateManager.disableTexture2D()
        GlStateManager.depthMask(false)
        GL11.glEnable(GL11.GL_LINE_SMOOTH)
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST)
        drawPlane(axisalignedbb, color)
        GL11.glDisable(GL11.GL_LINE_SMOOTH)
        GlStateManager.depthMask(true)
        GlStateManager.enableDepth()
        GlStateManager.enableTexture2D()
        GlStateManager.disableBlend()
        GlStateManager.popMatrix()
    }

    fun drawPlane(boundingBox: AxisAlignedBB, color: Int) {
        val alpha = (color shr 24 and 0xFF) / 255.0f
        val red = (color shr 16 and 0xFF) / 255.0f
        val green = (color shr 8 and 0xFF) / 255.0f
        val blue = (color and 0xFF) / 255.0f
        val minX = boundingBox.minX
        val minY = boundingBox.minY
        val minZ = boundingBox.minZ
        val maxX = boundingBox.maxX
        val maxY = boundingBox.maxY
        val maxZ = boundingBox.maxZ
        val tessellator = getInstance()
        val bufferbuilder = tessellator.buffer
        bufferbuilder.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR)
        bufferbuilder.pos(minX, minY, minZ).color(red, green, blue, alpha).endVertex()
        bufferbuilder.pos(maxX, minY, maxZ).color(red, green, blue, alpha).endVertex()
        bufferbuilder.pos(minX, minY, maxZ).color(red, green, blue, 0f).endVertex()
        bufferbuilder.pos(maxZ, minY, minZ).color(red, green, blue, alpha).endVertex()
        tessellator.draw()
    }

    @JvmStatic
    fun drawRect(x1: Float, y1: Float, x2: Float, y2: Float, color: Int) {
        GL11.glPushMatrix()
        GL11.glEnable(3042)
        GL11.glDisable(3553)
        GL11.glBlendFunc(770, 771)
        GL11.glEnable(2848)
        GL11.glPushMatrix()
        color(color)
        GL11.glBegin(7)
        GL11.glVertex2d(x2.toDouble(), y1.toDouble())
        GL11.glVertex2d(x1.toDouble(), y1.toDouble())
        GL11.glVertex2d(x1.toDouble(), y2.toDouble())
        GL11.glVertex2d(x2.toDouble(), y2.toDouble())
        GL11.glEnd()
        GL11.glPopMatrix()
        GL11.glEnable(3553)
        GL11.glDisable(3042)
        GL11.glDisable(2848)
        GL11.glPopMatrix()
        Gui.drawRect(0, 0, 0, 0, 0)
    }

    @JvmStatic
    fun drawSolidBlockESP(x: Double, y: Double, z: Double, red: Float, green: Float, blue: Float, alpha: Float) {
        prepare(7)
        glColor(red, green, blue, alpha)
        //drawBBBox(new AxisAlignedBB(x, y, z, x + 1.0, y + 1.0, z + 1.0), new Color(red, green, blue), (int) alpha, 1.5f, true);
        drawBoundingBox(AxisAlignedBB(x, y, z, x + 1.0, y + 1.0, z + 1.0))
        release()
    }

    @JvmStatic
    fun rPos(): DoubleArray {
        return try {
            doubleArrayOf(
                Minecraft.getMinecraft().getRenderManager().renderPosX,
                Minecraft.getMinecraft().getRenderManager().renderPosY,
                Minecraft.getMinecraft().getRenderManager().renderPosZ
            )
        } catch (e: Exception) {
            doubleArrayOf(
                0.0,
                0.0,
                0.0
            )
        }
    }

    @JvmStatic
    fun prepare(mode_requested: String) {
        var mode = 0
        if (mode_requested.equals("quads", ignoreCase = true)) {
            mode = 7
        } else if (mode_requested.equals("lines", ignoreCase = true)) {
            mode = 1
        }
        prepare_gl()
        begin(mode)
    }

    fun glSetup() {
        GL11.glPushMatrix()
        GL11.glBlendFunc(770, 771)
        GL11.glEnable(3042)
        GL11.glDisable(3553)
        GL11.glDisable(2929)
        GL11.glDepthMask(false)
        GL11.glLineWidth(2.0f)
    }

    fun prepare_gl() {
        //GL11.glBlendFunc(770, 771);
        GlStateManager.tryBlendFuncSeparate(
            GlStateManager.SourceFactor.SRC_ALPHA,
            GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
            GlStateManager.SourceFactor.ONE,
            GlStateManager.DestFactor.ZERO
        )
        GlStateManager.glLineWidth(1.5f)
        GlStateManager.disableTexture2D()
        GlStateManager.depthMask(false)
        GlStateManager.enableBlend()
        GlStateManager.disableDepth()
        GlStateManager.disableLighting()
        GlStateManager.disableCull()
        GlStateManager.enableAlpha()
        GlStateManager.color(1.0f, 1.0f, 1.0f)
        GL11.glLineWidth(2.0f)
    }

    fun draw_cube_line(blockPos: BlockPos, argb: Int, sides: String) {
        val a = argb ushr 24 and 0xFF
        val r = argb ushr 16 and 0xFF
        val g = argb ushr 8 and 0xFF
        val b = argb and 0xFF
        draw_cube_line(blockPos, r, g, b, a, sides)
    }

    fun draw_cube_line(x: Float, y: Float, z: Float, argb: Int, sides: String) {
        val a = argb ushr 24 and 0xFF
        val r = argb ushr 16 and 0xFF
        val g = argb ushr 8 and 0xFF
        val b = argb and 0xFF
        draw_cube_line(buffer, x, y, z, 1.0f, 0.5645f, 1.0f, r, g, b, a, sides)
    }

    fun draw_cube_line(blockPos: BlockPos, r: Int, g: Int, b: Int, a: Int, sides: String) {
        draw_cube_line(
            buffer,
            blockPos.getX().toFloat(),
            blockPos.getY().toFloat(),
            blockPos.getZ().toFloat(),
            1.0f,
            1.0f,
            1.0f,
            r,
            g,
            b,
            a,
            sides
        )
    }

    fun drawBox2(bb: AxisAlignedBB, r: Int, g: Int, b: Int, a: Int, sides: Int) {
        val tessellator = getInstance()
        val bufferBuilder = tessellator.buffer
        bufferBuilder.begin(7, DefaultVertexFormats.POSITION_COLOR)
        if (sides and 0x1 != 0x0) {
            bufferBuilder.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, a).endVertex()
            bufferBuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, a).endVertex()
            bufferBuilder.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, a).endVertex()
            bufferBuilder.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, a).endVertex()
        }
        if (sides and 0x2 != 0x0) {
            bufferBuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(r, g, b, a).endVertex()
            bufferBuilder.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, a).endVertex()
            bufferBuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(r, g, b, a).endVertex()
            bufferBuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(r, g, b, a).endVertex()
        }
        if (sides and 0x4 != 0x0) {
            bufferBuilder.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, a).endVertex()
            bufferBuilder.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, a).endVertex()
            bufferBuilder.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, a).endVertex()
            bufferBuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(r, g, b, a).endVertex()
        }
        if (sides and 0x8 != 0x0) {
            bufferBuilder.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, a).endVertex()
            bufferBuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, a).endVertex()
            bufferBuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(r, g, b, a).endVertex()
            bufferBuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(r, g, b, a).endVertex()
        }
        if (sides and 0x10 != 0x0) {
            bufferBuilder.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, a).endVertex()
            bufferBuilder.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, a).endVertex()
            bufferBuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(r, g, b, a).endVertex()
            bufferBuilder.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, a).endVertex()
        }
        if (sides and 0x20 != 0x0) {
            bufferBuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, a).endVertex()
            bufferBuilder.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, a).endVertex()
            bufferBuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(r, g, b, a).endVertex()
            bufferBuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(r, g, b, a).endVertex()
        }
        tessellator.draw()
    }

    fun drawBoxSmall(x: Float, y: Float, z: Float, argb: Int, sides: Int) {
        val a = argb ushr 24 and 0xFF
        val r = argb ushr 16 and 0xFF
        val g = argb ushr 8 and 0xFF
        val b = argb and 0xFF
        drawBox(buffer, x, y, z, 0.25f, 0.25f, 0.25f, r, g, b, a, sides)
    }

    @JvmStatic
    fun prepareGL() {
        GL11.glBlendFunc(770, 771)
        GlStateManager.tryBlendFuncSeparate(
            GlStateManager.SourceFactor.SRC_ALPHA,
            GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
            GlStateManager.SourceFactor.ONE,
            GlStateManager.DestFactor.ZERO
        )
        GlStateManager.glLineWidth(1.5f)
        GlStateManager.disableTexture2D()
        GlStateManager.depthMask(false)
        GlStateManager.enableBlend()
        GlStateManager.disableDepth()
        GlStateManager.disableLighting()
        GlStateManager.disableCull()
        GlStateManager.enableAlpha()
        GlStateManager.color(1.0f, 1.0f, 1.0f)
    }

    fun begin(mode: Int) {
        buffer.begin(mode, DefaultVertexFormats.POSITION_COLOR)
    }

    @JvmStatic
    fun release() {
        render()
        releaseGL()
    }

    fun render() {
        draw()
    }

    @JvmStatic
    fun releaseGL() {
        GlStateManager.enableCull()
        GlStateManager.depthMask(true)
        GlStateManager.enableTexture2D()
        GlStateManager.enableBlend()
        GlStateManager.enableDepth()
        GlStateManager.color(1.0f, 1.0f, 1.0f)
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
    }

    fun drawFace(blockPos: BlockPos, argb: Int, sides: Int) {
        val a = argb ushr 24 and 0xFF
        val r = argb ushr 16 and 0xFF
        val g = argb ushr 8 and 0xFF
        val b = argb and 0xFF
        drawFace(blockPos, r, g, b, a, sides)
    }

    fun drawFace(blockPos: BlockPos, r: Int, g: Int, b: Int, a: Int, sides: Int) {
        drawFace(
            buffer,
            blockPos.x.toFloat(),
            blockPos.y.toFloat(),
            blockPos.z.toFloat(),
            1.0f,
            1.0f,
            1.0f,
            r,
            g,
            b,
            a,
            sides
        )
    }

    fun drawFace(
        buffer: BufferBuilder,
        x: Float,
        y: Float,
        z: Float,
        w: Float,
        h: Float,
        d: Float,
        r: Int,
        g: Int,
        b: Int,
        a: Int,
        sides: Int
    ) {
        if (sides and 1 != 0) {
            buffer.pos((x + w).toDouble(), y.toDouble(), z.toDouble()).color(r, g, b, a).endVertex()
            buffer.pos((x + w).toDouble(), y.toDouble(), (z + d).toDouble()).color(r, g, b, a).endVertex()
            buffer.pos(x.toDouble(), y.toDouble(), (z + d).toDouble()).color(r, g, b, a).endVertex()
            buffer.pos(x.toDouble(), y.toDouble(), z.toDouble()).color(r, g, b, a).endVertex()
        }
    }

    fun drawFace(buffer: BufferBuilder, bb: AxisAlignedBB, r: Int, g: Int, b: Int, a: Int, sides: Int) {
        if (sides and 1 != 0) {
            buffer.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, a).endVertex()
            buffer.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, a).endVertex()
            buffer.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, a).endVertex()
            buffer.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, a).endVertex()
        }
    }

    fun drawFullBox(bb: AxisAlignedBB, blockPos: BlockPos, width: Float, red: Int, green: Int, blue: Int, alpha: Int) {
        prepare(7)
        drawBox(blockPos, red, green, blue, alpha, 63)
        release()
        drawBoundingBox(bb, width, red.toFloat(), green.toFloat(), blue.toFloat(), 255f)
    }

    fun drawGayBox(
        bb: AxisAlignedBB,
        blockPos: BlockPos,
        width: Float,
        rainbow: Int,
        red: Int,
        green: Int,
        blue: Int,
        alpha: Int
    ) {
        prepare(7)
        drawBox(blockPos, Rainbow.getRainbow(10.0f, 1.0f, 1.0f), 63)
        release()
        drawBoundingBox(bb, width, red.toFloat(), green.toFloat(), blue.toFloat(), 255f)
    }

    fun drawBoundingGay(bb: AxisAlignedBB, width: Float, r: Int, g: Int, b: Int, alpha: Int) {
        GlStateManager.pushMatrix()
        GlStateManager.enableBlend()
        GlStateManager.disableDepth()
        GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1)
        GlStateManager.disableTexture2D()
        GlStateManager.depthMask(false)
        GL11.glEnable(2848)
        GL11.glHint(3154, 4354)
        GL11.glLineWidth(width)
        val tessellator = getInstance()
        val bufferbuilder = tessellator.buffer
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR)
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex()
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex()
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex()
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex()
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex()
        tessellator.draw()
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR)
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, alpha).endVertex()
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(r, g, b, alpha).endVertex()
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(r, g, b, alpha).endVertex()
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(r, g, b, alpha).endVertex()
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, alpha).endVertex()
        tessellator.draw()
        bufferbuilder.begin(1, DefaultVertexFormats.POSITION_COLOR)
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex()
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, alpha).endVertex()
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex()
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(r, g, b, alpha).endVertex()
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex()
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(r, g, b, alpha).endVertex()
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex()
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(r, g, b, alpha).endVertex()
        tessellator.draw()
        GL11.glDisable(2848)
        GlStateManager.depthMask(true)
        GlStateManager.enableDepth()
        GlStateManager.enableTexture2D()
        GlStateManager.disableBlend()
        GlStateManager.popMatrix()
    }

    fun drawBox(bb: AxisAlignedBB, argb: Int, sides: Int) {
        val a = argb ushr 24 and 0xFF
        val r = argb ushr 16 and 0xFF
        val g = argb ushr 8 and 0xFF
        val b = argb and 0xFF
        drawBox(buffer, bb, r, g, b, a, sides)
    }

    fun drawBox(blockPos: BlockPos, argb: Int, sides: Int) {
        val a = argb ushr 24 and 0xFF
        val r = argb ushr 16 and 0xFF
        val g = argb ushr 8 and 0xFF
        val b = argb and 0xFF
        drawBox(blockPos, r, g, b, a, sides)
    }

    fun drawHalfBox(blockPos: BlockPos, argb: Int, sides: Int) {
        val a = argb ushr 24 and 0xFF
        val r = argb ushr 16 and 0xFF
        val g = argb ushr 8 and 0xFF
        val b = argb and 0xFF
        drawHalfBox(blockPos, r, g, b, a, sides)
    }

    fun drawHalfBox(x: Float, y: Float, z: Float, argb: Int, sides: Int) {
        val a = argb ushr 24 and 0xFF
        val r = argb ushr 16 and 0xFF
        val g = argb ushr 8 and 0xFF
        val b = argb and 0xFF
        drawBox(buffer, x, y, z, 1.0f, 0.5f, 1.0f, r, g, b, a, sides)
    }

    fun drawBox(x: Float, y: Float, z: Float, argb: Int, sides: Int) {
        val a = argb ushr 24 and 0xFF
        val r = argb ushr 16 and 0xFF
        val g = argb ushr 8 and 0xFF
        val b = argb and 0xFF
        drawBox(buffer, x, y, z, 1.0f, 1.0f, 1.0f, r, g, b, a, sides)
    }

    fun drawBox(blockPos: BlockPos, r: Int, g: Int, b: Int, a: Int, sides: Int) {
        drawBox(
            buffer,
            blockPos.getX().toFloat(),
            blockPos.getY().toFloat(),
            blockPos.getZ().toFloat(),
            1.0f,
            1.0f,
            1.0f,
            r,
            g,
            b,
            a,
            sides
        )
    }

    fun drawHalfBox(blockPos: BlockPos, r: Int, g: Int, b: Int, a: Int, sides: Int) {
        drawBox(
            buffer,
            blockPos.getX().toFloat(),
            blockPos.getY().toFloat(),
            blockPos.getZ().toFloat(),
            1.0f,
            0.5f,
            1.0f,
            r,
            g,
            b,
            a,
            sides
        )
    }

    fun drawBox(vec3d: Vec3d, r: Int, g: Int, b: Int, a: Int, sides: Int) {
        drawBox(
            buffer,
            vec3d.x.toFloat(),
            vec3d.y.toFloat(),
            vec3d.z.toFloat(),
            1.0f,
            1.0f,
            1.0f,
            r,
            g,
            b,
            a,
            sides
        )
    }

    fun draw_cube_line(
        buffer: BufferBuilder,
        x: Float,
        y: Float,
        z: Float,
        w: Float,
        h: Float,
        d: Float,
        r: Int,
        g: Int,
        b: Int,
        a: Int,
        sides: String
    ) {
        if (Arrays.asList(*sides.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
                .contains("downwest") || sides.equals("all", ignoreCase = true)
        ) {
            buffer.pos(x.toDouble(), y.toDouble(), z.toDouble()).color(r, g, b, a).endVertex()
            buffer.pos(x.toDouble(), y.toDouble(), (z + d).toDouble()).color(r, g, b, a).endVertex()
        }
        if (Arrays.asList(*sides.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
                .contains("upwest") || sides.equals("all", ignoreCase = true)
        ) {
            buffer.pos(x.toDouble(), (y + h).toDouble(), z.toDouble()).color(r, g, b, a).endVertex()
            buffer.pos(x.toDouble(), (y + h).toDouble(), (z + d).toDouble()).color(r, g, b, a).endVertex()
        }
        if (Arrays.asList(*sides.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
                .contains("downeast") || sides.equals("all", ignoreCase = true)
        ) {
            buffer.pos((x + w).toDouble(), y.toDouble(), z.toDouble()).color(r, g, b, a).endVertex()
            buffer.pos((x + w).toDouble(), y.toDouble(), (z + d).toDouble()).color(r, g, b, a).endVertex()
        }
        if (Arrays.asList(*sides.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
                .contains("upeast") || sides.equals("all", ignoreCase = true)
        ) {
            buffer.pos((x + w).toDouble(), (y + h).toDouble(), z.toDouble()).color(r, g, b, a).endVertex()
            buffer.pos((x + w).toDouble(), (y + h).toDouble(), (z + d).toDouble()).color(r, g, b, a).endVertex()
        }
        if (Arrays.asList(*sides.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
                .contains("downnorth") || sides.equals("all", ignoreCase = true)
        ) {
            buffer.pos(x.toDouble(), y.toDouble(), z.toDouble()).color(r, g, b, a).endVertex()
            buffer.pos((x + w).toDouble(), y.toDouble(), z.toDouble()).color(r, g, b, a).endVertex()
        }
        if (Arrays.asList(*sides.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
                .contains("upnorth") || sides.equals("all", ignoreCase = true)
        ) {
            buffer.pos(x.toDouble(), (y + h).toDouble(), z.toDouble()).color(r, g, b, a).endVertex()
            buffer.pos((x + w).toDouble(), (y + h).toDouble(), z.toDouble()).color(r, g, b, a).endVertex()
        }
        if (Arrays.asList(*sides.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
                .contains("downsouth") || sides.equals("all", ignoreCase = true)
        ) {
            buffer.pos(x.toDouble(), y.toDouble(), (z + d).toDouble()).color(r, g, b, a).endVertex()
            buffer.pos((x + w).toDouble(), y.toDouble(), (z + d).toDouble()).color(r, g, b, a).endVertex()
        }
        if (Arrays.asList(*sides.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
                .contains("upsouth") || sides.equals("all", ignoreCase = true)
        ) {
            buffer.pos(x.toDouble(), (y + h).toDouble(), (z + d).toDouble()).color(r, g, b, a).endVertex()
            buffer.pos((x + w).toDouble(), (y + h).toDouble(), (z + d).toDouble()).color(r, g, b, a).endVertex()
        }
        if (Arrays.asList(*sides.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
                .contains("nortwest") || sides.equals("all", ignoreCase = true)
        ) {
            buffer.pos(x.toDouble(), y.toDouble(), z.toDouble()).color(r, g, b, a).endVertex()
            buffer.pos(x.toDouble(), (y + h).toDouble(), z.toDouble()).color(r, g, b, a).endVertex()
        }
        if (Arrays.asList(*sides.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
                .contains("norteast") || sides.equals("all", ignoreCase = true)
        ) {
            buffer.pos((x + w).toDouble(), y.toDouble(), z.toDouble()).color(r, g, b, a).endVertex()
            buffer.pos((x + w).toDouble(), (y + h).toDouble(), z.toDouble()).color(r, g, b, a).endVertex()
        }
        if (Arrays.asList(*sides.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
                .contains("southweast") || sides.equals("all", ignoreCase = true)
        ) {
            buffer.pos(x.toDouble(), y.toDouble(), (z + d).toDouble()).color(r, g, b, a).endVertex()
            buffer.pos(x.toDouble(), (y + h).toDouble(), (z + d).toDouble()).color(r, g, b, a).endVertex()
        }
        if (Arrays.asList(*sides.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
                .contains("southeast") || sides.equals("all", ignoreCase = true)
        ) {
            buffer.pos((x + w).toDouble(), y.toDouble(), (z + d).toDouble()).color(r, g, b, a).endVertex()
            buffer.pos((x + w).toDouble(), (y + h).toDouble(), (z + d).toDouble()).color(r, g, b, a).endVertex()
        }
    }

    val bufferBuilder: BufferBuilder
        get() = buffer

    fun drawHead(
        buffer: BufferBuilder,
        x: Float,
        y: Float,
        z: Float,
        w: Float,
        h: Float,
        d: Float,
        r: Int,
        g: Int,
        b: Int,
        a: Int,
        sides: Int
    ) {
        if (sides and 2 != 0) {
            buffer.pos((x + w).toDouble(), (y + h).toDouble(), z.toDouble()).color(r, g, b, a).endVertex()
            buffer.pos(x.toDouble(), (y + h).toDouble(), z.toDouble()).color(r, g, b, a).endVertex()
            buffer.pos(x.toDouble(), (y + h).toDouble(), (z + d).toDouble()).color(r, g, b, a).endVertex()
            buffer.pos((x + w).toDouble(), (y + h).toDouble(), (z + d).toDouble()).color(r, g, b, a).endVertex()
        }
    }

    fun drawBox(
        buffer: BufferBuilder,
        x: Float,
        y: Float,
        z: Float,
        w: Float,
        h: Float,
        d: Float,
        r: Int,
        g: Int,
        b: Int,
        a: Int,
        sides: Int
    ) {
        if (sides and 1 != 0) {
            buffer.pos((x + w).toDouble(), y.toDouble(), z.toDouble()).color(r, g, b, a).endVertex()
            buffer.pos((x + w).toDouble(), y.toDouble(), (z + d).toDouble()).color(r, g, b, a).endVertex()
            buffer.pos(x.toDouble(), y.toDouble(), (z + d).toDouble()).color(r, g, b, a).endVertex()
            buffer.pos(x.toDouble(), y.toDouble(), z.toDouble()).color(r, g, b, a).endVertex()
        }
        if (sides and 2 != 0) {
            buffer.pos((x + w).toDouble(), (y + h).toDouble(), z.toDouble()).color(r, g, b, a).endVertex()
            buffer.pos(x.toDouble(), (y + h).toDouble(), z.toDouble()).color(r, g, b, a).endVertex()
            buffer.pos(x.toDouble(), (y + h).toDouble(), (z + d).toDouble()).color(r, g, b, a).endVertex()
            buffer.pos((x + w).toDouble(), (y + h).toDouble(), (z + d).toDouble()).color(r, g, b, a).endVertex()
        }
        if (sides and 4 != 0) {
            buffer.pos((x + w).toDouble(), y.toDouble(), z.toDouble()).color(r, g, b, a).endVertex()
            buffer.pos(x.toDouble(), y.toDouble(), z.toDouble()).color(r, g, b, a).endVertex()
            buffer.pos(x.toDouble(), (y + h).toDouble(), z.toDouble()).color(r, g, b, a).endVertex()
            buffer.pos((x + w).toDouble(), (y + h).toDouble(), z.toDouble()).color(r, g, b, a).endVertex()
        }
        if (sides and 8 != 0) {
            buffer.pos(x.toDouble(), y.toDouble(), (z + d).toDouble()).color(r, g, b, a).endVertex()
            buffer.pos((x + w).toDouble(), y.toDouble(), (z + d).toDouble()).color(r, g, b, a).endVertex()
            buffer.pos((x + w).toDouble(), (y + h).toDouble(), (z + d).toDouble()).color(r, g, b, a).endVertex()
            buffer.pos(x.toDouble(), (y + h).toDouble(), (z + d).toDouble()).color(r, g, b, a).endVertex()
        }
        if (sides and 0x10 != 0) {
            buffer.pos(x.toDouble(), y.toDouble(), z.toDouble()).color(r, g, b, a).endVertex()
            buffer.pos(x.toDouble(), y.toDouble(), (z + d).toDouble()).color(r, g, b, a).endVertex()
            buffer.pos(x.toDouble(), (y + h).toDouble(), (z + d).toDouble()).color(r, g, b, a).endVertex()
            buffer.pos(x.toDouble(), (y + h).toDouble(), z.toDouble()).color(r, g, b, a).endVertex()
        }
        if (sides and 0x20 != 0) {
            buffer.pos((x + w).toDouble(), y.toDouble(), (z + d).toDouble()).color(r, g, b, a).endVertex()
            buffer.pos((x + w).toDouble(), y.toDouble(), z.toDouble()).color(r, g, b, a).endVertex()
            buffer.pos((x + w).toDouble(), (y + h).toDouble(), z.toDouble()).color(r, g, b, a).endVertex()
            buffer.pos((x + w).toDouble(), (y + h).toDouble(), (z + d).toDouble()).color(r, g, b, a).endVertex()
        }
    }

    fun drawBox(buffer: BufferBuilder, bb: AxisAlignedBB, r: Int, g: Int, b: Int, a: Int, sides: Int) {
        if (sides and 1 != 0) {
            buffer.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, a).endVertex()
            buffer.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, a).endVertex()
            buffer.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, a).endVertex()
            buffer.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, a).endVertex()
        }
        if (sides and 2 != 0) {
            buffer.pos(bb.maxX, bb.maxY, bb.minZ).color(r, g, b, a).endVertex()
            buffer.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, a).endVertex()
            buffer.pos(bb.minX, bb.maxY, bb.maxZ).color(r, g, b, a).endVertex()
            buffer.pos(bb.maxX, bb.maxY, bb.maxZ).color(r, g, b, a).endVertex()
        }
        if (sides and 4 != 0) {
            buffer.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, a).endVertex()
            buffer.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, a).endVertex()
            buffer.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, a).endVertex()
            buffer.pos(bb.maxX, bb.maxY, bb.minZ).color(r, g, b, a).endVertex()
        }
        if (sides and 8 != 0) {
            buffer.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, a).endVertex()
            buffer.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, a).endVertex()
            buffer.pos(bb.maxX, bb.maxY, bb.maxZ).color(r, g, b, a).endVertex()
            buffer.pos(bb.minX, bb.maxY, bb.maxZ).color(r, g, b, a).endVertex()
        }
        if (sides and 0x10 != 0) {
            buffer.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, a).endVertex()
            buffer.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, a).endVertex()
            buffer.pos(bb.minX, bb.maxY, bb.maxZ).color(r, g, b, a).endVertex()
            buffer.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, a).endVertex()
        }
        if (sides and 0x20 != 0) {
            buffer.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, a).endVertex()
            buffer.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, a).endVertex()
            buffer.pos(bb.maxX, bb.maxY, bb.minZ).color(r, g, b, a).endVertex()
            buffer.pos(bb.maxX, bb.maxY, bb.maxZ).color(r, g, b, a).endVertex()
        }
    }

    fun drawSmallBox(vec3d: Vec3d, r: Int, g: Int, b: Int, a: Int, sides: Int) {
        drawBox(
            buffer,
            vec3d.x.toFloat(),
            vec3d.y.toFloat(),
            vec3d.z.toFloat(),
            0.3f,
            0.3f,
            0.3f,
            r,
            g,
            b,
            a,
            sides
        )
    }

    fun drawLines(
        buffer: BufferBuilder,
        x: Float,
        y: Float,
        z: Float,
        w: Float,
        h: Float,
        d: Float,
        r: Int,
        g: Int,
        b: Int,
        a: Int,
        sides: Int
    ) {
        if (sides and 0x11 != 0) {
            buffer.pos(x.toDouble(), y.toDouble(), z.toDouble()).color(r, g, b, a).endVertex()
            buffer.pos(x.toDouble(), y.toDouble(), (z + d).toDouble()).color(r, g, b, a).endVertex()
        }
        if (sides and 0x12 != 0) {
            buffer.pos(x.toDouble(), (y + h).toDouble(), z.toDouble()).color(r, g, b, a).endVertex()
            buffer.pos(x.toDouble(), (y + h).toDouble(), (z + d).toDouble()).color(r, g, b, a).endVertex()
        }
        if (sides and 0x21 != 0) {
            buffer.pos((x + w).toDouble(), y.toDouble(), z.toDouble()).color(r, g, b, a).endVertex()
            buffer.pos((x + w).toDouble(), y.toDouble(), (z + d).toDouble()).color(r, g, b, a).endVertex()
        }
        if (sides and 0x22 != 0) {
            buffer.pos((x + w).toDouble(), (y + h).toDouble(), z.toDouble()).color(r, g, b, a).endVertex()
            buffer.pos((x + w).toDouble(), (y + h).toDouble(), (z + d).toDouble()).color(r, g, b, a).endVertex()
        }
        if (sides and 5 != 0) {
            buffer.pos(x.toDouble(), y.toDouble(), z.toDouble()).color(r, g, b, a).endVertex()
            buffer.pos((x + w).toDouble(), y.toDouble(), z.toDouble()).color(r, g, b, a).endVertex()
        }
        if (sides and 6 != 0) {
            buffer.pos(x.toDouble(), (y + h).toDouble(), z.toDouble()).color(r, g, b, a).endVertex()
            buffer.pos((x + w).toDouble(), (y + h).toDouble(), z.toDouble()).color(r, g, b, a).endVertex()
        }
        if (sides and 9 != 0) {
            buffer.pos(x.toDouble(), y.toDouble(), (z + d).toDouble()).color(r, g, b, a).endVertex()
            buffer.pos((x + w).toDouble(), y.toDouble(), (z + d).toDouble()).color(r, g, b, a).endVertex()
        }
        if (sides and 0xA != 0) {
            buffer.pos(x.toDouble(), (y + h).toDouble(), (z + d).toDouble()).color(r, g, b, a).endVertex()
            buffer.pos((x + w).toDouble(), (y + h).toDouble(), (z + d).toDouble()).color(r, g, b, a).endVertex()
        }
        if (sides and 0x14 != 0) {
            buffer.pos(x.toDouble(), y.toDouble(), z.toDouble()).color(r, g, b, a).endVertex()
            buffer.pos(x.toDouble(), (y + h).toDouble(), z.toDouble()).color(r, g, b, a).endVertex()
        }
        if (sides and 0x24 != 0) {
            buffer.pos((x + w).toDouble(), y.toDouble(), z.toDouble()).color(r, g, b, a).endVertex()
            buffer.pos((x + w).toDouble(), (y + h).toDouble(), z.toDouble()).color(r, g, b, a).endVertex()
        }
        if (sides and 0x18 != 0) {
            buffer.pos(x.toDouble(), y.toDouble(), (z + d).toDouble()).color(r, g, b, a).endVertex()
            buffer.pos(x.toDouble(), (y + h).toDouble(), (z + d).toDouble()).color(r, g, b, a).endVertex()
        }
        if (sides and 0x28 != 0) {
            buffer.pos((x + w).toDouble(), y.toDouble(), (z + d).toDouble()).color(r, g, b, a).endVertex()
            buffer.pos((x + w).toDouble(), (y + h).toDouble(), (z + d).toDouble()).color(r, g, b, a).endVertex()
        }
    }

    fun drawBoundingBox(bb: BlockPos?, width: Float, argb: Int) {
        val a = argb ushr 24 and 0xFF
        val r = argb ushr 16 and 0xFF
        val g = argb ushr 8 and 0xFF
        val b = argb and 0xFF
        drawBoundingBox(bb, width, r, g, b, a)
    }

    fun drawBoundingBox(bb: AxisAlignedBB, width: Float, argb: Int) {
        val a = argb ushr 24 and 255
        val r = argb ushr 16 and 255
        val g = argb ushr 8 and 255
        val b = argb and 255
        drawBoundingBox(bb, width, r.toFloat(), g.toFloat(), b.toFloat(), a.toFloat())
    }

    fun drawBoundingBoxKA(bb: BlockPos?, width: Float, argb: Int) {
        val a = argb ushr 24 and 0xFF
        val r = argb ushr 16 and 0xFF
        val g = argb ushr 8 and 0xFF
        val b = argb and 0xFF
        drawBoundingBoxKA(bb, width, r, g, b, a)
    }

    fun drawBoundingBoxKA(pos: BlockPos?, width: Float, red: Int, green: Int, blue: Int, alpha: Int) {
        drawBoundingBox(getBoundingFromPos(pos), width, red.toFloat(), green.toFloat(), blue.toFloat(), alpha.toFloat())
    }

    fun drawBoundingBox(pos: BlockPos?, width: Float, red: Int, green: Int, blue: Int, alpha: Int) {
        drawBoundingBox(getBoundingFromPos(pos), width, red.toFloat(), green.toFloat(), blue.toFloat(), alpha.toFloat())
    }

    fun drawBoundingBox(bb: AxisAlignedBB, qwq: Int) {
        drawBoundingBox(bb, qwq, qwq, qwq, qwq)
    }

    fun drawBoundingBox(bb: AxisAlignedBB, r: Int, g: Int, b: Int, alpha: Int) {
        GL11.glPushMatrix()
        GL11.glEnable(3042)
        GL11.glDisable(2929)
        GL11.glBlendFunc(770, 771)
        GL11.glDisable(3553)
        GL11.glDepthMask(false)
        GL11.glEnable(2848)
        GL11.glHint(3154, 4354)
        val tessellator = getInstance()
        val bufferbuilder = tessellator.buffer
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR)
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex()
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex()
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex()
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex()
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex()
        tessellator.draw()
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR)
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, alpha).endVertex()
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(r, g, b, alpha).endVertex()
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(r, g, b, alpha).endVertex()
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(r, g, b, alpha).endVertex()
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, alpha).endVertex()
        tessellator.draw()
        bufferbuilder.begin(1, DefaultVertexFormats.POSITION_COLOR)
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex()
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, alpha).endVertex()
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex()
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(r, g, b, alpha).endVertex()
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex()
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(r, g, b, alpha).endVertex()
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex()
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(r, g, b, alpha).endVertex()
        tessellator.draw()
        GL11.glDisable(2848)
        GL11.glDepthMask(true)
        GL11.glEnable(2929)
        GL11.glEnable(3553)
        GL11.glDisable(3042)
        GL11.glPopMatrix()
    }

    fun drawBoundingBox(bb: AxisAlignedBB, width: Float, red: Float, green: Float, blue: Float, alpha: Float) {
        GL11.glLineWidth(width)
        glColor(red, green, blue, alpha)
        drawBoundingBox(bb)
    }

    fun drawBoundingBox(boundingBox: AxisAlignedBB) {
        val tessellator = getInstance()
        val vertexBuffer = tessellator.buffer
        vertexBuffer.begin(3, DefaultVertexFormats.POSITION)
        vertexBuffer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).endVertex()
        vertexBuffer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).endVertex()
        vertexBuffer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).endVertex()
        vertexBuffer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).endVertex()
        vertexBuffer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).endVertex()
        tessellator.draw()
        vertexBuffer.begin(3, DefaultVertexFormats.POSITION)
        vertexBuffer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).endVertex()
        vertexBuffer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).endVertex()
        vertexBuffer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).endVertex()
        vertexBuffer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).endVertex()
        vertexBuffer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).endVertex()
        tessellator.draw()
        vertexBuffer.begin(1, DefaultVertexFormats.POSITION)
        vertexBuffer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).endVertex()
        vertexBuffer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).endVertex()
        vertexBuffer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).endVertex()
        vertexBuffer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).endVertex()
        vertexBuffer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).endVertex()
        vertexBuffer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).endVertex()
        vertexBuffer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).endVertex()
        vertexBuffer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).endVertex()
        tessellator.draw()
    }

    fun drawBoundingBoxKA(bb: AxisAlignedBB, width: Float, r: Int, g: Int, b: Int, alpha: Int) {
        GL11.glPushMatrix()
        GL11.glEnable(3042)
        GL11.glDisable(2929)
        GL11.glBlendFunc(770, 771)
        GL11.glDisable(3553)
        GL11.glDepthMask(false)
        GL11.glEnable(2848)
        GL11.glHint(3154, 4354)
        GL11.glLineWidth(width)
        val tessellator = getInstance()
        val bufferbuilder = tessellator.buffer
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR)
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex()
        bufferbuilder.pos(bb.maxX - bb.minX, bb.maxY - bb.minY, bb.maxZ - bb.minZ).color(r, g, b, alpha).endVertex()
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex()
        bufferbuilder.pos(bb.maxX - bb.minX, bb.maxY - bb.minY, bb.maxZ - bb.minZ).color(r, g, b, alpha).endVertex()
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex()
        bufferbuilder.pos(bb.maxX - bb.minX, bb.maxY - bb.minY, bb.maxZ - bb.minZ).color(r, g, b, alpha).endVertex()
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex()
        bufferbuilder.pos(bb.maxX - bb.minX, bb.maxY - bb.minY, bb.maxZ - bb.minZ).color(r, g, b, alpha).endVertex()
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex()
        tessellator.draw()
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR)
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, alpha).endVertex()
        bufferbuilder.pos(bb.maxX - bb.minX, bb.maxY - bb.minY, bb.maxZ - bb.minZ).color(r, g, b, alpha).endVertex()
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(r, g, b, alpha).endVertex()
        bufferbuilder.pos(bb.maxX - bb.minX, bb.maxY - bb.minY, bb.maxZ - bb.minZ).color(r, g, b, alpha).endVertex()
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(r, g, b, alpha).endVertex()
        bufferbuilder.pos(bb.maxX - bb.minX, bb.maxY - bb.minY, bb.maxZ - bb.minZ).color(r, g, b, alpha).endVertex()
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(r, g, b, alpha).endVertex()
        bufferbuilder.pos(bb.maxX - bb.minX, bb.maxY - bb.minY, bb.maxZ - bb.minZ).color(r, g, b, alpha).endVertex()
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, alpha).endVertex()
        tessellator.draw()
        bufferbuilder.begin(1, DefaultVertexFormats.POSITION_COLOR)
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex()
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, alpha).endVertex()
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex()
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(r, g, b, alpha).endVertex()
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex()
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(r, g, b, alpha).endVertex()
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex()
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(r, g, b, alpha).endVertex()
        tessellator.draw()
        GL11.glDisable(2848)
        GL11.glDepthMask(true)
        GL11.glEnable(2929)
        GL11.glEnable(3553)
        GL11.glDisable(3042)
        GL11.glPopMatrix()
    }

    fun drawBoundingBoxBlockPos(bp: BlockPos, width: Float, r: Int, g: Int, b: Int, alpha: Int) {
        GlStateManager.pushMatrix()
        GlStateManager.enableBlend()
        GlStateManager.disableDepth()
        GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1)
        GlStateManager.disableTexture2D()
        GlStateManager.depthMask(false)
        GL11.glEnable(2848)
        GL11.glHint(3154, 4354)
        GL11.glLineWidth(width)
        val mc = Minecraft.getMinecraft()
        val x = bp.x.toDouble() - mc.getRenderManager().viewerPosX
        val y = bp.y.toDouble() - mc.getRenderManager().viewerPosY
        val z = bp.z.toDouble() - mc.getRenderManager().viewerPosZ
        val bb = AxisAlignedBB(x, y, z, x + 1.0, y + 1.0, z + 1.0)
        val tessellator = getInstance()
        val bufferbuilder = tessellator.buffer
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR)
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex()
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex()
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex()
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex()
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex()
        tessellator.draw()
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR)
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, alpha).endVertex()
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(r, g, b, alpha).endVertex()
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(r, g, b, alpha).endVertex()
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(r, g, b, alpha).endVertex()
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, alpha).endVertex()
        tessellator.draw()
        bufferbuilder.begin(1, DefaultVertexFormats.POSITION_COLOR)
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex()
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, alpha).endVertex()
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex()
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(r, g, b, alpha).endVertex()
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex()
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(r, g, b, alpha).endVertex()
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex()
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(r, g, b, alpha).endVertex()
        tessellator.draw()
        GL11.glDisable(2848)
        GlStateManager.depthMask(true)
        GlStateManager.enableDepth()
        GlStateManager.enableTexture2D()
        GlStateManager.disableBlend()
        GlStateManager.popMatrix()
    }

    @JvmStatic
    fun drawBoundingBoxBlockPos(hitVec: Vec3d, width: Float, r: Int, g: Int, b: Int, alpha: Int) {
        GlStateManager.pushMatrix()
        GlStateManager.enableBlend()
        GlStateManager.disableDepth()
        GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1)
        GlStateManager.disableTexture2D()
        GlStateManager.depthMask(false)
        GL11.glEnable(2848)
        GL11.glHint(3154, 4354)
        GL11.glLineWidth(width)
        val mc = Minecraft.getMinecraft()
        val x = hitVec.x - mc.getRenderManager().viewerPosX
        val y = hitVec.y - mc.getRenderManager().viewerPosY
        val z = hitVec.z - mc.getRenderManager().viewerPosZ
        val bb = AxisAlignedBB(x, y, z, x + 1.0, y + 1.0, z + 1.0)
        val tessellator = getInstance()
        val bufferbuilder = tessellator.buffer
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR)
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex()
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex()
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex()
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex()
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex()
        tessellator.draw()
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR)
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, alpha).endVertex()
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(r, g, b, alpha).endVertex()
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(r, g, b, alpha).endVertex()
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(r, g, b, alpha).endVertex()
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, alpha).endVertex()
        tessellator.draw()
        bufferbuilder.begin(1, DefaultVertexFormats.POSITION_COLOR)
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex()
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, alpha).endVertex()
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex()
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(r, g, b, alpha).endVertex()
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex()
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(r, g, b, alpha).endVertex()
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex()
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(r, g, b, alpha).endVertex()
        tessellator.draw()
        GL11.glDisable(2848)
        GlStateManager.depthMask(true)
        GlStateManager.enableDepth()
        GlStateManager.enableTexture2D()
        GlStateManager.disableBlend()
        GlStateManager.popMatrix()
    }

    fun drawBoundingBoxBottomBlockPos(bp: BlockPos, width: Float, r: Int, g: Int, b: Int, alpha: Int) {
        GlStateManager.pushMatrix()
        GlStateManager.enableBlend()
        GlStateManager.disableDepth()
        GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1)
        GlStateManager.disableTexture2D()
        GlStateManager.depthMask(false)
        GL11.glEnable(2848)
        GL11.glHint(3154, 4354)
        GL11.glLineWidth(width)
        val mc = Minecraft.getMinecraft()
        val x = bp.x.toDouble() - mc.getRenderManager().viewerPosX
        val y = bp.y.toDouble() - mc.getRenderManager().viewerPosY
        val z = bp.z.toDouble() - mc.getRenderManager().viewerPosZ
        val bb = AxisAlignedBB(x, y, z, x + 1.0, y + 1.0, z + 1.0)
        val tessellator = getInstance()
        val bufferbuilder = tessellator.buffer
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR)
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex()
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex()
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex()
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex()
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex()
        tessellator.draw()
        GL11.glDisable(2848)
        GlStateManager.depthMask(true)
        GlStateManager.enableDepth()
        GlStateManager.enableTexture2D()
        GlStateManager.disableBlend()
        GlStateManager.popMatrix()
    }

    fun drawBoxBottom(
        buffer: BufferBuilder,
        x: Float,
        y: Float,
        z: Float,
        w: Float,
        h: Float,
        d: Float,
        r: Int,
        g: Int,
        b: Int,
        a: Int
    ) {
        buffer.pos((x + w).toDouble(), y.toDouble(), z.toDouble()).color(r, g, b, a).endVertex()
        buffer.pos((x + w).toDouble(), y.toDouble(), (z + d).toDouble()).color(r, g, b, a).endVertex()
        buffer.pos(x.toDouble(), y.toDouble(), (z + d).toDouble()).color(r, g, b, a).endVertex()
        buffer.pos(x.toDouble(), y.toDouble(), z.toDouble()).color(r, g, b, a).endVertex()
    }

    fun drawBoxBottom(blockPos: BlockPos, argb: Int) {
        val a = argb ushr 24 and 0xFF
        val r = argb ushr 16 and 0xFF
        val g = argb ushr 8 and 0xFF
        val b = argb and 0xFF
        drawBoxBottom(blockPos, r, g, b, a)
    }

    fun drawBoxBottom(x: Float, y: Float, z: Float, argb: Int) {
        val a = argb ushr 24 and 0xFF
        val r = argb ushr 16 and 0xFF
        val g = argb ushr 8 and 0xFF
        val b = argb and 0xFF
        drawBoxBottom(buffer, x, y, z, 1.0f, 1.0f, 1.0f, r, g, b, a)
    }

    fun drawBoxBottom(blockPos: BlockPos, r: Int, g: Int, b: Int, a: Int) {
        drawBoxBottom(
            buffer,
            blockPos.x.toFloat(),
            blockPos.y.toFloat(),
            blockPos.z.toFloat(),
            1.0f,
            1.0f,
            1.0f,
            r,
            g,
            b,
            a
        )
    }

    fun glBillboard(x: Float, y: Float, z: Float) {
        val scale = 0.02666667f
        GlStateManager.translate(
            x.toDouble() - Minecraft.getMinecraft().getRenderManager().renderPosX,
            y.toDouble() - Minecraft.getMinecraft().getRenderManager().renderPosY,
            z.toDouble() - Minecraft.getMinecraft().getRenderManager().renderPosZ
        )
        GlStateManager.glNormal3f(0.0f, 1.0f, 0.0f)
        GlStateManager.rotate(-Minecraft.getMinecraft().player.rotationYaw, 0.0f, 1.0f, 0.0f)
        GlStateManager.rotate(
            Minecraft.getMinecraft().player.rotationPitch,
            if (Minecraft.getMinecraft().gameSettings.thirdPersonView == 2) -1.0f else 1.0f,
            0.0f,
            0.0f
        )
        GlStateManager.scale(-scale, -scale, scale)
    }

    fun glBillboardDistanceScaled(x: Float, y: Float, z: Float, player: EntityPlayer, scale: Float) {
        glBillboard(x, y, z)
        val distance = player.getDistance(x.toDouble(), y.toDouble(), z.toDouble()).toInt()
        var scaleDistance = distance.toFloat() / 2.0f / (2.0f + (2.0f - scale))
        if (scaleDistance < 1.0f) {
            scaleDistance = 1.0f
        }
        GlStateManager.scale(scaleDistance, scaleDistance, scaleDistance)
    }

    fun drawText(pos: Vec3d, scale: Float, text: String?) {
        GlStateManager.pushMatrix()
        glBillboardDistanceScaled(pos.x.toFloat(), pos.y.toFloat(), pos.z.toFloat(), mc.player, scale)
        GlStateManager.disableDepth()
        GlStateManager.translate(-(mc.fontRenderer.getStringWidth(text).toDouble() / 2.0), 0.0, 0.0)
        mc.fontRenderer.drawStringWithShadow(text, 0.0f, 0.0f, -5592406)
        GlStateManager.popMatrix()
    }

    fun drawBBBox(BB: AxisAlignedBB, colour: Color, alpha: Int, lineWidth: Float, outline: Boolean) {
        val bb = AxisAlignedBB(
            BB.minX - mc.getRenderManager().viewerPosX,
            BB.minY - mc.getRenderManager().viewerPosY,
            BB.minZ - mc.getRenderManager().viewerPosZ,
            BB.maxX - mc.getRenderManager().viewerPosX,
            BB.maxY - mc.getRenderManager().viewerPosY,
            BB.maxZ - mc.getRenderManager().viewerPosZ
        )
        camera.setPosition(
            Objects.requireNonNull(mc.getRenderViewEntity())!!.posX,
            mc.getRenderViewEntity()!!.posY,
            mc.getRenderViewEntity()!!.posZ
        )
        if (camera.isBoundingBoxInFrustum(
                AxisAlignedBB(
                    bb.minX + mc.getRenderManager().viewerPosX,
                    bb.minY + mc.getRenderManager().viewerPosY,
                    bb.minZ + mc.getRenderManager().viewerPosZ,
                    bb.maxX + mc.getRenderManager().viewerPosX,
                    bb.maxY + mc.getRenderManager().viewerPosY,
                    bb.maxZ + mc.getRenderManager().viewerPosZ
                )
            )
        ) {
            prepare(GL11.GL_QUADS)
            GL11.glEnable(GL11.GL_LINE_SMOOTH)
            GL11.glShadeModel(GL11.GL_SMOOTH)
            if (outline) {
                //glColor(colour.getRed(),colour.getGreen(),colour.getBlue(),255);
                //drawBox(bb);
                drawBoundingBox(
                    bb,
                    lineWidth,
                    colour.red.toFloat(),
                    colour.green.toFloat(),
                    colour.blue.toFloat(),
                    255f
                )
            }
            RenderGlobal.renderFilledBox(
                bb,
                colour.red / 255.0f,
                colour.green / 255.0f,
                colour.blue / 255.0f,
                alpha / 255.0f
            )
            //glColor(colour.red, colour.green, colour.blue, alpha)
            //drawBox(bb)
            GL11.glDisable(GL11.GL_LINE_SMOOTH)
            release()
        }
    }

    fun drawFade(axisAlignedBB: AxisAlignedBB, color: Color, alpha: Int, lineWidth: Float) {
        camera.setPosition(
            Objects.requireNonNull(mc.getRenderViewEntity())!!.posX,
            mc.getRenderViewEntity()!!.posY,
            mc.getRenderViewEntity()!!.posZ
        )
        if (camera.isBoundingBoxInFrustum(
                AxisAlignedBB(
                    axisAlignedBB.minX + mc.getRenderManager().viewerPosX,
                    axisAlignedBB.minY + mc.getRenderManager().viewerPosY,
                    axisAlignedBB.minZ + mc.getRenderManager().viewerPosZ,
                    axisAlignedBB.maxX + mc.getRenderManager().viewerPosX,
                    axisAlignedBB.maxY + mc.getRenderManager().viewerPosY,
                    axisAlignedBB.maxZ + mc.getRenderManager().viewerPosZ
                )
            )
        ) {
            prepare(7)
            GL11.glLineWidth(lineWidth)
            drawBoundingBox(
                axisAlignedBB,
                lineWidth,
                color.red.toFloat(),
                color.green.toFloat(),
                color.blue.toFloat(),
                255f
            )
            glColor(color.red, color.green, color.blue, alpha)
            drawBox(axisAlignedBB)
            release()
        }
    }

    fun boxESP(blockPos: BlockPos, color: Color, alpha: Int, lineWidth: Float, progress: Float, mode: Int) {
        val axisAlignedBB = AxisAlignedBB(
            blockPos.getX().toDouble() - mc.getRenderManager().viewerPosX,
            blockPos.getY().toDouble() - mc.getRenderManager().viewerPosY,
            blockPos.getZ().toDouble() - mc.getRenderManager().viewerPosZ,
            (blockPos.getX() + 1).toDouble() - mc.getRenderManager().viewerPosX,
            (blockPos.getY() + 1).toDouble() - mc.getRenderManager().viewerPosY,
            (blockPos.getZ() + 1).toDouble() - mc.getRenderManager().viewerPosZ
        )
        boxESP(axisAlignedBB, color, alpha, lineWidth, progress, mode)
    }

    fun boxESP(axisAlignedBB: AxisAlignedBB, color: Color, alpha: Int, lineWidth: Float, progress: Float, mode: Int) {
        camera.setPosition(
            Objects.requireNonNull(mc.getRenderViewEntity())!!.posX,
            mc.getRenderViewEntity()!!.posY,
            mc.getRenderViewEntity()!!.posZ
        )
        if (camera.isBoundingBoxInFrustum(
                AxisAlignedBB(
                    axisAlignedBB.minX + mc.getRenderManager().viewerPosX,
                    axisAlignedBB.minY + mc.getRenderManager().viewerPosY,
                    axisAlignedBB.minZ + mc.getRenderManager().viewerPosZ,
                    axisAlignedBB.maxX + mc.getRenderManager().viewerPosX,
                    axisAlignedBB.maxY + mc.getRenderManager().viewerPosY,
                    axisAlignedBB.maxZ + mc.getRenderManager().viewerPosZ
                )
            )
        ) {
            var d: Double
            var d2: Double
            var d3: Double
            var d4: Double
            var d5: Double
            var d6: Double
            val d8: Double = if (progress == 0f) {
                mc.playerController.curBlockDamageMP.toDouble()
            } else {
                progress.toDouble()
            }
            //double d8 = mc.playerController.curBlockDamageMP;
            d6 = axisAlignedBB.minX + 1 - square(d8)
            d5 = axisAlignedBB.minY + 1 - square(d8)
            d4 = axisAlignedBB.minZ + 1 - square(d8)
            d3 = axisAlignedBB.maxX - 1 + square(d8)
            d2 = axisAlignedBB.maxY - 1 + square(d8)
            d = axisAlignedBB.maxZ - 1 + square(d8)
            when (mode) {
                1 -> {
                    d6 = axisAlignedBB.minX + 1
                    d5 = axisAlignedBB.minY + 1 - square(d8)
                    d4 = axisAlignedBB.minZ + 1
                    d3 = axisAlignedBB.maxX - 1
                    d2 = axisAlignedBB.maxY - 1 + square(d8)
                    d = axisAlignedBB.maxZ - 1
                }

                2 -> {
                    d6 = axisAlignedBB.minX + 1
                    d5 = axisAlignedBB.minY + 1
                    d4 = axisAlignedBB.minZ + 1
                    d3 = axisAlignedBB.maxX - 1
                    d2 = axisAlignedBB.maxY - 1 + square(d8)
                    d = axisAlignedBB.maxZ - 1
                }

                3 -> {
                    d6 = axisAlignedBB.minX + 1
                    d5 = axisAlignedBB.minY + 1 - square(d8)
                    d4 = axisAlignedBB.minZ + 1
                    d3 = axisAlignedBB.maxX - 1
                    d2 = axisAlignedBB.maxY - 1
                    d = axisAlignedBB.maxZ - 1
                }

                4 -> {
                    d6 = axisAlignedBB.minX + 1 - square(sin(PI * d8 * d8))
                    d5 = axisAlignedBB.minY + 1 - square(d8)
                    d4 = axisAlignedBB.minZ + 1 - square(cos(PI * d8 * d8))
                    d3 = axisAlignedBB.maxX - 1 + square(sin(PI * -d8 * -d8))
                    d2 = axisAlignedBB.maxY - 1 + square(d8)
                    d = axisAlignedBB.maxZ - 1 + square(cos(PI * -d8 * -d8))
                }

                5 -> {
                    d6 = axisAlignedBB.minX + 1 - square(cos(4f / 3f * PI * d8 * d8))
                    d5 = axisAlignedBB.minY + 1 - square(d8)
                    d4 = axisAlignedBB.minZ + 1 - square(sin(4f / 3f * PI * d8 * d8))
                    d3 = axisAlignedBB.maxX - 1 + square(sin(4f / 3f * PI * -d8 * -d8))
                    d2 = axisAlignedBB.maxY - 1 + square(d8)
                    d = axisAlignedBB.maxZ - 1 + square(cos(4f / 3f * PI * -d8 * -d8))
                }

                6 -> {
                    d6 = axisAlignedBB.minX + 1 - square(1f / 2f * d8 * 0.5)
                    d5 = axisAlignedBB.minY + 1 - square(sin(PI * d8 * d8))
                    d4 = axisAlignedBB.minZ + 1 - square(1f / 2f * d8 * 0.5)
                    d3 = axisAlignedBB.maxX - 1 + square(1f / 2f * d8 * 0.5)
                    d2 = axisAlignedBB.maxY - 1 + square(cos(PI * d8 * d8))
                    d = axisAlignedBB.maxZ - 1 + square(1f / 2f * d8 * 0.5)
                }

                7 -> {
                    val sin = sin(toRadians(d8 * 360f))
                    val cos = cos(toRadians(d8 * 180f))
                    d6 = axisAlignedBB.minX + 1 - square(sin)
                    d5 = axisAlignedBB.minY + 1
                    d4 = axisAlignedBB.minZ + square(MathHelper.clamp(1 * sin, 0.0, 1.0))
                    d3 = axisAlignedBB.maxX - 1 + square(cos)
                    d2 = axisAlignedBB.maxY - 1
                    d = axisAlignedBB.maxZ - square(MathHelper.clamp(1 * cos, 0.0, 1.0))
                }
            }
            val newBB = AxisAlignedBB(d6, d5, d4, d3, d2, d)
            //Color c = new Color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f);
            //drawBBBox(axisAlignedBB2, c, n, lineWidth, true);
            //RenderUtils3D.drawFilledBox(newBB, new Color((float) color.getRed() / 255f, (float) color.getGreen() / 255f, (float) color.getBlue() / 255f, (float) alpha / 255f).getRGB());
            //RenderUtils3D.drawBlockOutline(newBB, new Color((float) color.getRed() / 255f, (float) color.getGreen() / 255f, (float) color.getBlue() / 255f, 1f), 1f);
            prepare(7)
            drawBoundingBox(newBB, lineWidth, color.red.toFloat(), color.green.toFloat(), color.blue.toFloat(), 255f)
            RenderGlobal.renderFilledBox(
                newBB,
                color.red / 255.0f,
                color.green / 255.0f,
                color.blue / 255.0f,
                alpha / 255.0f
            )
            release()
        }
    }

    fun square(`val`: Double): Double {
        return `val`.sq / 2
    }

    fun glColor(red2: Int, green2: Int, blue2: Int, alpha: Int) {
        GL11.glColor4f(
            red2.toFloat() / 255.0f,
            green2.toFloat() / 255.0f,
            blue2.toFloat() / 255.0f,
            alpha.toFloat() / 255.0f
        )
    }

    fun glColor(red2: Float, green2: Float, blue2: Float, alpha: Float) {
        GL11.glColor4f(red2 / 255.0f, green2 / 255.0f, blue2 / 255.0f, alpha / 255.0f)
    }

    fun glColor(color: Float) {
        GL11.glColor4f(color, color, color, color)
    }

    fun drawBox(bb: AxisAlignedBB?) {
        if (bb == null) {
            return
        }
        GL11.glBegin(7)
        GL11.glVertex3f(bb.minX.toFloat(), bb.minY.toFloat(), bb.maxZ.toFloat())
        GL11.glVertex3f(bb.maxX.toFloat(), bb.minY.toFloat(), bb.maxZ.toFloat())
        GL11.glVertex3f(bb.maxX.toFloat(), bb.maxY.toFloat(), bb.maxZ.toFloat())
        GL11.glVertex3f(bb.minX.toFloat(), bb.maxY.toFloat(), bb.maxZ.toFloat())
        GL11.glEnd()
        GL11.glBegin(7)
        GL11.glVertex3f(bb.maxX.toFloat(), bb.minY.toFloat(), bb.maxZ.toFloat())
        GL11.glVertex3f(bb.minX.toFloat(), bb.minY.toFloat(), bb.maxZ.toFloat())
        GL11.glVertex3f(bb.minX.toFloat(), bb.maxY.toFloat(), bb.maxZ.toFloat())
        GL11.glVertex3f(bb.maxX.toFloat(), bb.maxY.toFloat(), bb.maxZ.toFloat())
        GL11.glEnd()
        GL11.glBegin(7)
        GL11.glVertex3f(bb.minX.toFloat(), bb.minY.toFloat(), bb.minZ.toFloat())
        GL11.glVertex3f(bb.minX.toFloat(), bb.minY.toFloat(), bb.maxZ.toFloat())
        GL11.glVertex3f(bb.minX.toFloat(), bb.maxY.toFloat(), bb.maxZ.toFloat())
        GL11.glVertex3f(bb.minX.toFloat(), bb.maxY.toFloat(), bb.minZ.toFloat())
        GL11.glEnd()
        GL11.glBegin(7)
        GL11.glVertex3f(bb.minX.toFloat(), bb.minY.toFloat(), bb.maxZ.toFloat())
        GL11.glVertex3f(bb.minX.toFloat(), bb.minY.toFloat(), bb.minZ.toFloat())
        GL11.glVertex3f(bb.minX.toFloat(), bb.maxY.toFloat(), bb.minZ.toFloat())
        GL11.glVertex3f(bb.minX.toFloat(), bb.maxY.toFloat(), bb.maxZ.toFloat())
        GL11.glEnd()
        GL11.glBegin(7)
        GL11.glVertex3f(bb.maxX.toFloat(), bb.minY.toFloat(), bb.maxZ.toFloat())
        GL11.glVertex3f(bb.maxX.toFloat(), bb.minY.toFloat(), bb.minZ.toFloat())
        GL11.glVertex3f(bb.maxX.toFloat(), bb.maxY.toFloat(), bb.minZ.toFloat())
        GL11.glVertex3f(bb.maxX.toFloat(), bb.maxY.toFloat(), bb.maxZ.toFloat())
        GL11.glEnd()
        GL11.glBegin(7)
        GL11.glVertex3f(bb.maxX.toFloat(), bb.minY.toFloat(), bb.minZ.toFloat())
        GL11.glVertex3f(bb.maxX.toFloat(), bb.minY.toFloat(), bb.maxZ.toFloat())
        GL11.glVertex3f(bb.maxX.toFloat(), bb.maxY.toFloat(), bb.maxZ.toFloat())
        GL11.glVertex3f(bb.maxX.toFloat(), bb.maxY.toFloat(), bb.minZ.toFloat())
        GL11.glEnd()
        GL11.glBegin(7)
        GL11.glVertex3f(bb.minX.toFloat(), bb.minY.toFloat(), bb.minZ.toFloat())
        GL11.glVertex3f(bb.maxX.toFloat(), bb.minY.toFloat(), bb.minZ.toFloat())
        GL11.glVertex3f(bb.maxX.toFloat(), bb.maxY.toFloat(), bb.minZ.toFloat())
        GL11.glVertex3f(bb.minX.toFloat(), bb.maxY.toFloat(), bb.minZ.toFloat())
        GL11.glEnd()
        GL11.glBegin(7)
        GL11.glVertex3f(bb.maxX.toFloat(), bb.minY.toFloat(), bb.minZ.toFloat())
        GL11.glVertex3f(bb.minX.toFloat(), bb.minY.toFloat(), bb.minZ.toFloat())
        GL11.glVertex3f(bb.minX.toFloat(), bb.maxY.toFloat(), bb.minZ.toFloat())
        GL11.glVertex3f(bb.maxX.toFloat(), bb.maxY.toFloat(), bb.minZ.toFloat())
        GL11.glEnd()
        GL11.glBegin(7)
        GL11.glVertex3f(bb.minX.toFloat(), bb.maxY.toFloat(), bb.minZ.toFloat())
        GL11.glVertex3f(bb.maxX.toFloat(), bb.maxY.toFloat(), bb.minZ.toFloat())
        GL11.glVertex3f(bb.maxX.toFloat(), bb.maxY.toFloat(), bb.maxZ.toFloat())
        GL11.glVertex3f(bb.minX.toFloat(), bb.maxY.toFloat(), bb.maxZ.toFloat())
        GL11.glEnd()
        GL11.glBegin(7)
        GL11.glVertex3f(bb.maxX.toFloat(), bb.maxY.toFloat(), bb.minZ.toFloat())
        GL11.glVertex3f(bb.minX.toFloat(), bb.maxY.toFloat(), bb.minZ.toFloat())
        GL11.glVertex3f(bb.minX.toFloat(), bb.maxY.toFloat(), bb.maxZ.toFloat())
        GL11.glVertex3f(bb.maxX.toFloat(), bb.maxY.toFloat(), bb.maxZ.toFloat())
        GL11.glEnd()
        GL11.glBegin(7)
        GL11.glVertex3f(bb.minX.toFloat(), bb.minY.toFloat(), bb.minZ.toFloat())
        GL11.glVertex3f(bb.maxX.toFloat(), bb.minY.toFloat(), bb.minZ.toFloat())
        GL11.glVertex3f(bb.maxX.toFloat(), bb.minY.toFloat(), bb.maxZ.toFloat())
        GL11.glVertex3f(bb.minX.toFloat(), bb.minY.toFloat(), bb.maxZ.toFloat())
        GL11.glEnd()
        GL11.glBegin(7)
        GL11.glVertex3f(bb.maxX.toFloat(), bb.minY.toFloat(), bb.minZ.toFloat())
        GL11.glVertex3f(bb.minX.toFloat(), bb.minY.toFloat(), bb.minZ.toFloat())
        GL11.glVertex3f(bb.minX.toFloat(), bb.minY.toFloat(), bb.maxZ.toFloat())
        GL11.glVertex3f(bb.maxX.toFloat(), bb.minY.toFloat(), bb.maxZ.toFloat())
        GL11.glEnd()
    }

    @JvmStatic
    fun drawBox(hitVec: Vec3d, argb: Int, sides: Int) {
        val a = argb ushr 24 and 0xFF
        val r = argb ushr 16 and 0xFF
        val g = argb ushr 8 and 0xFF
        val b = argb and 0xFF
        drawBox(hitVec, r, g, b, a, sides)
    }

    fun drawFullBox(pos: BlockPos?, width: Float, argb: Int) {
        val a = argb ushr 24 and 0xFF
        val r = argb ushr 16 and 0xFF
        val g = argb ushr 8 and 0xFF
        val b = argb and 0xFF
        drawFullBox(pos, width, r, g, b, a)
    }

    fun drawFullBox(pos: BlockPos?, width: Float, red: Int, green: Int, blue: Int, alpha: Int) {
        drawBoundingFullBox(getBoundingFromPos(pos), red, green, blue, alpha)
        drawBoundingBox(getBoundingFromPos(pos), width, red.toFloat(), green.toFloat(), blue.toFloat(), 255f)
    }

    fun drawFullBox(pos: Vec3d?, width: Float, argb: Int) {
        val a = argb ushr 24 and 0xFF
        val r = argb ushr 16 and 0xFF
        val g = argb ushr 8 and 0xFF
        val b = argb and 0xFF
        drawFullBox(pos, width, r, g, b, a)
    }

    fun drawFullBox(pos: Vec3d?, width: Float, red: Int, green: Int, blue: Int, alpha: Int) {
        drawBoundingFullBox(getBoundingFromPos(pos), red, green, blue, alpha)
        drawBoundingBox(getBoundingFromPos(pos), width, red.toFloat(), green.toFloat(), blue.toFloat(), 255f)
    }

    fun drawFullBox(bb: AxisAlignedBB, width: Float, argb: Int) {
        val a = argb ushr 24 and 0xFF
        val r = argb ushr 16 and 0xFF
        val g = argb ushr 8 and 0xFF
        val b = argb and 0xFF
        drawFullBox(bb, width, r, g, b, a)
    }

    fun drawFullBox(bb: AxisAlignedBB, width: Float, red: Int, green: Int, blue: Int, alpha: Int) {
        drawBoundingFullBox(bb, red, green, blue, alpha)
        drawBoundingBox(bb, width, red.toFloat(), green.toFloat(), blue.toFloat(), 255f)
    }

    fun drawBoundingFullBox(bb: AxisAlignedBB, red: Int, green: Int, blue: Int, alpha: Int) {
        GlStateManager.color(
            red.toFloat() / 255.0f,
            green.toFloat() / 255.0f,
            blue.toFloat() / 255.0f,
            alpha.toFloat() / 255.0f
        )
        drawFilledBox(bb)
    }

    fun drawBoundingFullBox(pos: BlockPos?, red: Int, green: Int, blue: Int, alpha: Int) {
        drawBoundingFullBox(getBoundingFromPos(pos), red, green, blue, alpha)
    }

    fun getBoundingFromPos(render: BlockPos?): AxisAlignedBB {
        val iBlockState = Wrapper.mc.world.getBlockState(render)
        val interp = interpolateEntity(Wrapper.mc.player, Wrapper.mc.renderPartialTicks)
        return iBlockState.getSelectedBoundingBox(Wrapper.mc.world, render).expand(0.002, 0.002, 0.002)
            .offset(-interp.x, -interp.y, -interp.z)
    }

    fun getBoundingFromPos(renders: Vec3d?): AxisAlignedBB {
        val render = BlockPos(renders)
        val iBlockState = Wrapper.mc.world.getBlockState(render)
        val interp = interpolateEntity(Wrapper.mc.player, Wrapper.mc.renderPartialTicks)
        return iBlockState.getSelectedBoundingBox(Wrapper.mc.world, render).expand(0.002, 0.002, 0.002)
            .offset(-interp.x, -interp.y, -interp.z)
    }

    fun interpolateEntity(entity: Entity, time: Float): Vec3d {
        return Vec3d(
            entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * time.toDouble(),
            entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * time.toDouble(),
            entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * time.toDouble()
        )
    }

    fun interpolateEntityClose(entity: Entity, renderPartialTicks: Float): Vec3d {
        return Vec3d(
            calculateDistanceWithPartialTicks(
                entity.posX,
                entity.lastTickPosX,
                renderPartialTicks
            ) - Wrapper.mc.getRenderManager().renderPosX,
            calculateDistanceWithPartialTicks(
                entity.posY,
                entity.lastTickPosY,
                renderPartialTicks
            ) - Wrapper.mc.getRenderManager().renderPosY,
            calculateDistanceWithPartialTicks(
                entity.posZ,
                entity.lastTickPosZ,
                renderPartialTicks
            ) - Wrapper.mc.getRenderManager().renderPosZ
        )
    }

    fun calculateDistanceWithPartialTicks(n: Double, n2: Double, renderPartialTicks: Float): Double {
        return n2 + (n - n2) * renderPartialTicks
    }

    fun drawFilledBox(axisAlignedBB: AxisAlignedBB) {
        val tessellator = getInstance()
        val vertexbuffer = tessellator.buffer
        vertexbuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION)
        vertexbuffer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex()
        vertexbuffer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex()
        vertexbuffer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex()
        vertexbuffer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex()
        vertexbuffer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex()
        vertexbuffer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex()
        vertexbuffer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex()
        vertexbuffer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex()
        vertexbuffer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex()
        vertexbuffer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex()
        vertexbuffer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex()
        vertexbuffer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex()
        vertexbuffer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex()
        vertexbuffer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex()
        vertexbuffer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex()
        vertexbuffer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex()
        vertexbuffer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex()
        vertexbuffer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex()
        vertexbuffer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex()
        vertexbuffer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex()
        vertexbuffer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex()
        vertexbuffer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex()
        vertexbuffer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex()
        vertexbuffer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex()
        vertexbuffer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex()
        vertexbuffer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex()
        vertexbuffer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex()
        vertexbuffer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex()
        vertexbuffer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex()
        vertexbuffer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex()
        vertexbuffer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex()
        vertexbuffer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex()
        vertexbuffer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex()
        vertexbuffer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex()
        vertexbuffer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex()
        vertexbuffer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex()
        vertexbuffer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex()
        vertexbuffer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex()
        vertexbuffer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex()
        vertexbuffer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex()
        vertexbuffer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex()
        vertexbuffer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex()
        vertexbuffer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex()
        vertexbuffer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex()
        vertexbuffer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex()
        vertexbuffer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex()
        vertexbuffer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex()
        vertexbuffer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex()
        tessellator.draw()
    }

    fun drawBoxTest(
        x: Float,
        y: Float,
        z: Float,
        w: Float,
        h: Float,
        d: Float,
        r: Int,
        g: Int,
        b: Int,
        a: Int,
        sides: Int
    ) {
        GL11.glPushMatrix()
        GL11.glBlendFunc(770, 771)
        GlStateManager.tryBlendFuncSeparate(
            GlStateManager.SourceFactor.SRC_ALPHA,
            GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
            GlStateManager.SourceFactor.ONE,
            GlStateManager.DestFactor.ZERO
        )
        GlStateManager.shadeModel(GL11.GL_SMOOTH)
        GlStateManager.glLineWidth(1f)
        GlStateManager.disableTexture2D()
        GlStateManager.depthMask(false)
        GlStateManager.enableBlend()
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GlStateManager.disableDepth()
        GlStateManager.disableLighting()
        GlStateManager.disableCull()
        GlStateManager.enableAlpha()
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1f)
        RenderUtils.setColor(r, g, b, a)
        GL11.glBegin(7)
        if (sides and 1 != 0) {
            GL11.glVertex3d((x + w).toDouble(), y.toDouble(), z.toDouble())
            GL11.glVertex3d((x + w).toDouble(), y.toDouble(), (z + d).toDouble())
            GL11.glVertex3d(x.toDouble(), y.toDouble(), (z + d).toDouble())
            GL11.glVertex3d(x.toDouble(), y.toDouble(), z.toDouble())
        }
        if (sides and 2 != 0) {
            GL11.glVertex3d((x + w).toDouble(), (y + h).toDouble(), z.toDouble())
            GL11.glVertex3d(x.toDouble(), (y + h).toDouble(), z.toDouble())
            GL11.glVertex3d(x.toDouble(), (y + h).toDouble(), (z + d).toDouble())
            GL11.glVertex3d((x + w).toDouble(), (y + h).toDouble(), (z + d).toDouble())
        }
        if (sides and 4 != 0) {
            GL11.glVertex3d((x + w).toDouble(), y.toDouble(), z.toDouble())
            GL11.glVertex3d(x.toDouble(), y.toDouble(), z.toDouble())
            GL11.glVertex3d(x.toDouble(), (y + h).toDouble(), z.toDouble())
            GL11.glVertex3d((x + w).toDouble(), (y + h).toDouble(), z.toDouble())
        }
        if (sides and 8 != 0) {
            GL11.glVertex3d(x.toDouble(), y.toDouble(), (z + d).toDouble())
            GL11.glVertex3d((x + w).toDouble(), y.toDouble(), (z + d).toDouble())
            GL11.glVertex3d((x + w).toDouble(), (y + h).toDouble(), (z + d).toDouble())
            GL11.glVertex3d(x.toDouble(), (y + h).toDouble(), (z + d).toDouble())
        }
        if (sides and 0x10 != 0) {
            GL11.glVertex3d(x.toDouble(), y.toDouble(), z.toDouble())
            GL11.glVertex3d(x.toDouble(), y.toDouble(), (z + d).toDouble())
            GL11.glVertex3d(x.toDouble(), (y + h).toDouble(), (z + d).toDouble())
            GL11.glVertex3d(x.toDouble(), (y + h).toDouble(), z.toDouble())
        }
        if (sides and 0x20 != 0) {
            GL11.glVertex3d((x + w).toDouble(), y.toDouble(), (z + d).toDouble())
            GL11.glVertex3d((x + w).toDouble(), y.toDouble(), z.toDouble())
            GL11.glVertex3d((x + w).toDouble(), (y + h).toDouble(), z.toDouble())
            GL11.glVertex3d((x + w).toDouble(), (y + h).toDouble(), (z + d).toDouble())
        }
        GL11.glEnd()
        GlStateManager.enableCull()
        GlStateManager.depthMask(true)
        GlStateManager.enableTexture2D()
        GlStateManager.enableBlend()
        GlStateManager.enableDepth()
        GlStateManager.color(1.0f, 1.0f, 1.0f)
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
        GL11.glPopMatrix()
    }

    fun drawBoxTest(bb: AxisAlignedBB, color: Color, sides: Int) {
        drawBoxTest(bb, color.red, color.green, color.blue, color.alpha, sides)
    }

    fun drawBoxTest(bb: AxisAlignedBB, r: Int, g: Int, b: Int, a: Int, sides: Int) {
        drawBoxTest(
            bb.minX.toFloat(),
            bb.minY.toFloat(),
            bb.minZ.toFloat(),
            bb.maxX.toFloat() - bb.minX.toFloat(),
            bb.maxY.toFloat() - bb.minY.toFloat(),
            bb.maxZ.toFloat() - bb.minZ.toFloat(),
            r,
            g,
            b,
            a,
            sides
        )
    }

    fun drawBoxTests(bb: AxisAlignedBB, r: Int, g: Int, b: Int, a: Int, sides: Int) {
        val tessellator = getInstance()
        val bufferBuilder = tessellator.buffer
        bufferBuilder.begin(7, DefaultVertexFormats.POSITION_COLOR)
        if (sides and 0x1 != 0x0) {
            bufferBuilder.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, a).endVertex()
            bufferBuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, a).endVertex()
            bufferBuilder.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, a).endVertex()
            bufferBuilder.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, a).endVertex()
        }
        if (sides and 0x2 != 0x0) {
            bufferBuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(r, g, b, a).endVertex()
            bufferBuilder.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, a).endVertex()
            bufferBuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(r, g, b, a).endVertex()
            bufferBuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(r, g, b, a).endVertex()
        }
        if (sides and 0x4 != 0x0) {
            bufferBuilder.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, a).endVertex()
            bufferBuilder.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, a).endVertex()
            bufferBuilder.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, a).endVertex()
            bufferBuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(r, g, b, a).endVertex()
        }
        if (sides and 0x8 != 0x0) {
            bufferBuilder.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, a).endVertex()
            bufferBuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, a).endVertex()
            bufferBuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(r, g, b, a).endVertex()
            bufferBuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(r, g, b, a).endVertex()
        }
        if (sides and 0x10 != 0x0) {
            bufferBuilder.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, a).endVertex()
            bufferBuilder.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, a).endVertex()
            bufferBuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(r, g, b, a).endVertex()
            bufferBuilder.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, a).endVertex()
        }
        if (sides and 0x20 != 0x0) {
            bufferBuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, a).endVertex()
            bufferBuilder.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, a).endVertex()
            bufferBuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(r, g, b, a).endVertex()
            bufferBuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(r, g, b, a).endVertex()
        }
        tessellator.draw()
    }
}