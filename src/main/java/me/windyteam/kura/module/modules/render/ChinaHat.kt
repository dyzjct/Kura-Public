package me.windyteam.kura.module.modules.render

import me.windyteam.kura.event.events.render.RenderEvent
import me.windyteam.kura.module.Category
import me.windyteam.kura.module.Module
import me.windyteam.kura.module.Module.Info
import net.minecraft.client.renderer.GlStateManager
import org.lwjgl.opengl.GL11
import org.lwjgl.util.glu.Cylinder
import org.lwjgl.util.glu.GLU
import java.awt.Color

@Info(name = "ChinaHat", category = Category.RENDER)
object ChinaHat : Module() {
    private val display = settings("Display", false)
    private val mode = settings("Mode", Mode.FullChinaHat)
    private val color = settings("Color", Color(255, 255, 255))
    private val alpha = settings("Alpha", 60, 0, 255)

    override fun onWorldRender(event: RenderEvent) {
        if (fullNullCheck()) return
        if (!display.value) if (mc.gameSettings.thirdPersonView == 0) return
        GL11.glPushMatrix()
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glShadeModel(GL11.GL_SMOOTH)
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glEnable(GL11.GL_LINE_SMOOTH)
        GL11.glDisable(GL11.GL_DEPTH_TEST)
        GL11.glDepthMask(false)
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST)
        GL11.glDisable(GL11.GL_CULL_FACE)
        GL11.glColor4f(color.value.red / 255f, color.value.green / 255f, color.value.blue / 255f, alpha.value / 255f)
        GL11.glTranslatef(0f, mc.player.height + 0.4f, 0f)
        GL11.glRotatef(90f, 1f, 0f, 0f)

        //30 for circle
        val shaft = Cylinder()
        when (mode.value) {
            Mode.Umbrella -> {
                shaft.drawStyle = GLU.GLU_LINE
                shaft.draw(0f, 0.7f, 0.3f, 8, 1)
                shaft.drawStyle = GLU.GLU_FILL
                shaft.draw(0f, 0.7f, 0.3f, 8, 1)
            }

            Mode.FullChinaHat -> {
                shaft.drawStyle = GLU.GLU_LINE
                shaft.draw(0f, 0.7f, 0.3f, 30, 1)
                shaft.drawStyle = GLU.GLU_FILL
                shaft.draw(0f, 0.7f, 0.3f, 30, 1)
            }

            Mode.ChinaHat -> {
                shaft.drawStyle = GLU.GLU_FILL
                shaft.draw(0f, 0.7f, 0.3f, 60, 1)
            }
        }

        GlStateManager.resetColor()
        GL11.glEnable(GL11.GL_TEXTURE_2D)
        GL11.glEnable(GL11.GL_DEPTH_TEST)
        GL11.glDisable(GL11.GL_LINE_SMOOTH)
        GL11.glDepthMask(true)
        GL11.glDisable(GL11.GL_BLEND)
        GL11.glEnable(GL11.GL_CULL_FACE)
        GL11.glPopMatrix()
    }

    enum class Mode {
        Umbrella, ChinaHat, FullChinaHat
    }
}