package me.windyteam.kura.module.hud.huds

import me.windyteam.kura.Kura
import me.windyteam.kura.manager.FontManager
import me.windyteam.kura.manager.GuiManager
import me.windyteam.kura.module.HUDModule
import me.windyteam.kura.setting.Setting
import me.windyteam.kura.utils.color.ColorUtil
import org.lwjgl.opengl.GL11
import java.awt.Color

/**
 * Created by dyzjct on 29/12/2022
 */
@HUDModule.Info(name = "WaterMark", x = 20, y = 20, width = 50, height = 20)
class WaterMark : HUDModule() {
    private var text = ssetting("ViewText", "kura")
    private var version = bsetting("Version",true)
    private var colormod = msetting("ColorMod", Welcomer.ColorMode.GuiSync)
    private var color = csetting("Color", Color(255, 255, 255))

    var txt:String? = null

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
        txt = if (version.value){
            text.value + " beta" + Kura.VERSION
        } else text.value
        GL11.glTranslated(x.toDouble(), y.toFloat().toDouble(), 0.0)
        FontManager.font3!!.drawString(txt, 0.0, 0.0, generateColor(), false)
        GL11.glPopMatrix()
    }

    enum class ColorMode {
        Rainbow, Custom, GuiSync
    }
}