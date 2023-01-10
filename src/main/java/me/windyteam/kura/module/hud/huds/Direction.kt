package me.windyteam.kura.module.hud.huds

import me.windyteam.kura.manager.FontManager
import me.windyteam.kura.manager.GuiManager
import me.windyteam.kura.module.HUDModule
import me.windyteam.kura.setting.Setting
import me.windyteam.kura.utils.color.ColorUtil
import me.windyteam.kura.utils.math.RotationUtil
import org.lwjgl.opengl.GL11
import java.awt.Color

/**
 * Created by dyzjct on 29/12/2022
 */
@HUDModule.Info(name = "Direction", x = 150, y = 150, width = 15, height = 10)
class Direction : HUDModule() {
    private var custom = bsetting("CustomFont",true)
    private var colormod = msetting("ColorMod", Welcomer.ColorMode.GuiSync)
    private var color = csetting("Color", Color(255, 255, 255))


    private fun generateColor(): Int {
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
        if (!custom.value){
            fontRenderer.drawString(RotationUtil.getDirection(false), 0.0F, 0.0F, generateColor(), false)
        } else{
            FontManager.font2!!.drawString(RotationUtil.getDirection(false), 0.0, 0.0, generateColor(), false)
        }
        GL11.glPopMatrix()
    }

    enum class ColorMode {
        Rainbow, Custom, GuiSync
    }
}