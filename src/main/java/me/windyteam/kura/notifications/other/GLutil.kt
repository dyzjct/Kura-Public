package me.windyteam.kura.notifications.other

import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.opengl.GL11
import me.windyteam.kura.notifications.other.ColourUtil.glColour
import java.awt.Color

@SideOnly(Side.CLIENT)
object GLutil {
    fun drawRect(x: Float, y: Float, width: Float, height: Float, colour: Color) {
        GL11.glPushMatrix()
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)

        colour.glColour()

        GL11.glBegin(GL11.GL_QUADS)

        GL11.glVertex2f(x, y)
        GL11.glVertex2f(x, y + height)
        GL11.glVertex2f(x + width, y + height)
        GL11.glVertex2f(x + width, y)

        GL11.glEnd()

        GL11.glDisable(GL11.GL_BLEND)
        GL11.glEnable(GL11.GL_TEXTURE_2D)
        GL11.glPopMatrix()
    }
    fun drawBorder(x: Float, y: Float, width: Float, height: Float, border: Float, colour: Color) {
        drawRect(x - border, y - border, width + (border * 2f), border, colour)
        drawRect(x - border, y, border, height, colour)
        drawRect(x - border, y + height, width + (border * 2f), border, colour)
        drawRect(x + width, y, border, height, colour)
    }

}