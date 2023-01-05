package me.dyzjct.kura.module.hud.huds

import me.dyzjct.kura.manager.FontManager
import me.dyzjct.kura.manager.GuiManager
import me.dyzjct.kura.module.HUDModule
import me.dyzjct.kura.utils.mc.ChatUtil
import java.awt.Color

@HUDModule.Info(name = "CoordsHUD", x = 150, y = 150, width = 100, height = 10)
class CoordsHUD : HUDModule() {
    private var custom = bsetting("CustomFont",true)
    override fun onRender() {
        val fontColor = Color(
            GuiManager.getINSTANCE().red / 255f,
            GuiManager.getINSTANCE().green / 255f,
            GuiManager.getINSTANCE().blue / 255f,
            1f
        ).rgb
        val inHell = mc.player.dimension == -1
        val f = if (!inHell) 0.125f else 8.0f
        val posX = String.format("%.1f", mc.player.posX)
        val posY = String.format("%.1f", mc.player.posY)
        val posZ = String.format("%.1f", mc.player.posZ)
        val hposX = String.format("%.1f", mc.player.posX * f.toDouble())
        val hposZ = String.format("%.1f", mc.player.posZ * f.toDouble())
        val ow = "$posX, $posY, $posZ"
        val nether = "$hposX, $posY, $hposZ"
        val final =
            ChatUtil.SECTIONSIGN.toString() + "rXYZ " + ChatUtil.SECTIONSIGN + "f" + ow + ChatUtil.SECTIONSIGN + "r [" + ChatUtil.SECTIONSIGN + "f" + nether + ChatUtil.SECTIONSIGN + "r]"
        if (!custom.value) {
            fontRenderer.drawString(final, x + 2, y + 4, fontColor)
        } else{
            FontManager.font2!!.drawString(final,x + 2F,  y + 4F,fontColor)
        }
        width = fontRenderer.getStringWidth(final) + 4
    }
}