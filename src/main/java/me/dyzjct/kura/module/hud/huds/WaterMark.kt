package me.dyzjct.kura.module.hud.huds

import me.dyzjct.kura.manager.FontManager
import me.dyzjct.kura.manager.GuiManager
import me.dyzjct.kura.module.HUDModule
import me.dyzjct.kura.setting.Setting
import me.dyzjct.kura.utils.color.ColorUtil
import org.lwjgl.opengl.GL11
import java.awt.Color

/**
 * Created by dyzjct on 29/12/2022
 */
@HUDModule.Info(name = "WaterMark", x = 20, y = 20, width = 50, height = 20)
class WaterMark : HUDModule() {
    private var text = ssetting("ViewText", "kura")
    private var colormod = msetting("ColorMod", Welcomer.ColorMode.GuiSync)
    private var color = csetting("Color", Color(255, 255, 255))


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