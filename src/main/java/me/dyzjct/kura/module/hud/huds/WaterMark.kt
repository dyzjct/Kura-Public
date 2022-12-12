package me.dyzjct.kura.module.hud.huds

import me.dyzjct.kura.manager.FontManager
import me.dyzjct.kura.manager.GuiManager
import me.dyzjct.kura.module.HUDModule
import me.dyzjct.kura.setting.Setting
import me.dyzjct.kura.utils.color.ColorUtil
import org.lwjgl.opengl.GL11
import java.awt.Color
@HUDModule.Info(name = "WaterMark", x = 20, y = 20, width = 50, height = 20)
class WaterMark : HUDModule() {
    var text = ssetting("ViewText", "kura")
    var colormod = msetting("ColorMod", Welcomer.ColorMode.GuiSync)
    var color = csetting("Color", Color(255, 255, 255))
    var saturation: Setting<Float> = fsetting("Saturation", 1.0f, 0f, 1.0f)
    var brightness: Setting<Float> = fsetting("Brightness", 1.0f, 0f, 1.0f)
    var alpha: Setting<Int> = isetting("Alpha", 90, 1, 255)
    var speed = isetting("RainbowSpeed", 1, 0, 1)
//    var Scala = dsetting("Scala", 1.0, 0.0, 3.0)


    fun generateColor(): Int {
        val fontColor = Color(
            GuiManager.getINSTANCE().red / 255f,
            GuiManager.getINSTANCE().green / 255f,
            GuiManager.getINSTANCE().blue / 255f,
            1f
        ).rgb
        val custom = Color(color.value.red, color.value.green, color.value.blue).rgb
        when (colormod.value) {
            Welcomer.ColorMode.Rainbow -> return ColorUtil.staticRainbow().rgb
            Welcomer.ColorMode.GuiSync -> return fontColor
            Welcomer.ColorMode.Custom -> return custom
        }
        return -1
    }

    override fun onRender() {
        GL11.glPushMatrix()
        GL11.glTranslated(x.toDouble(), y.toFloat().toDouble(), 0.0)
//        GL11.glScaled(Scala.value.toDouble(), Scala.value.toDouble(), 0.0)
        FontManager.font2!!.drawString(text.value, 0.0, 0.0, generateColor(), false)
        GL11.glPopMatrix()
//        width = (FontManager.font2!!.getStringWidth(text.value).toFloat() * Scala.value).toInt()
//        height = (FontManager.font2!!.height.toFloat() * Scala.value).toInt()
    }

    enum class ColorMode {
        Rainbow, Custom, GuiSync
    }
}