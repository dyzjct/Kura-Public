package me.windyteam.kura.module.hud.huds

import me.windyteam.kura.manager.FontManager
import me.windyteam.kura.manager.GuiManager
import me.windyteam.kura.module.HUDModule
import me.windyteam.kura.utils.math.RotationUtil
import java.awt.Color

/**
 * Created by dyzjct on 29/12/2022
 */

@HUDModule.Info(name = "Direction", x = 150, y = 150, width = 15, height = 10)
class Direction : HUDModule() {
    private var custom = bsetting("CustomFont",true)
    
    override fun onRender() {
        val fontColor = Color(
            GuiManager.getINSTANCE().red / 255f,
            GuiManager.getINSTANCE().green / 255f,
            GuiManager.getINSTANCE().blue / 255f,
            1f
        ).rgb
        if (!custom.value){
            fontRenderer.drawString(RotationUtil.getDirection(false), x + 2, y + 4, fontColor)
        } else{
            FontManager.font2!!.drawString(RotationUtil.getDirection(false), x + 2F, y + 4F, fontColor)
        }
    }
}