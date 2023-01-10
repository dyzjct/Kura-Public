package me.windyteam.kura.module.hud.huds

import me.windyteam.kura.manager.FontManager
import me.windyteam.kura.manager.GuiManager
import me.windyteam.kura.module.HUDModule
import me.windyteam.kura.utils.color.ColorUtil

import net.minecraft.client.Minecraft
import org.lwjgl.opengl.GL11
import java.awt.Color
@HUDModule.Info(name = "Welcomer", x = 50, y = 50, width = 100, height = 15)
class Welcomer : HUDModule() {
    var color = csetting("Color", Color(255, 255, 255))
    var colormod = msetting("ColorMod", ColorMode.GuiSync)

    fun generateColor(): Int {
        val fontColor = Color(
            GuiManager.getINSTANCE().red / 255f,
            GuiManager.getINSTANCE().green / 255f,
            GuiManager.getINSTANCE().blue / 255f,
            1f
        ).rgb
        val custom = Color(color.value.red, color.value.green, color.value.blue).rgb
        when (colormod.value) {
            ColorMode.Rainbow -> return ColorUtil.staticRainbow().rgb
            ColorMode.GuiSync -> return fontColor
            ColorMode.Custom -> return custom
        }
        return -1
    }

    override fun onRender() {
        GL11.glPushMatrix()
        GL11.glTranslated(x.toDouble(), y.toFloat().toDouble(), 0.0)
        val Final = "Welcome " + Minecraft.getMinecraft().player.name + "! Have a nice day :)"
        FontManager.font2!!.drawString(Final, 0.0, 0.0, generateColor(), false)
        GL11.glPopMatrix()
    }

    enum class ColorMode {
        GuiSync, Custom, Rainbow
    }
}