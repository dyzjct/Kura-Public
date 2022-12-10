package me.dyzjct.kura.module.hud.huds

import me.dyzjct.kura.manager.GuiManager
import me.dyzjct.kura.module.HUDModule
import me.dyzjct.kura.utils.font.CFont
import me.dyzjct.kura.utils.font.RFontRenderer
import net.minecraft.client.Minecraft
import java.awt.Color
import java.util.concurrent.CopyOnWriteArrayList

@HUDModule.Info(name = "Welcomer", x = 20, y = 20)
class Welcomer : HUDModule() {
    var fonts = CopyOnWriteArrayList<RFontRenderer>()
    var font = RFontRenderer(CFont.CustomFont("/assets/fonts/font.ttf", 47.0f, 0), true, false)
    override fun onRender() {
        if (!fonts.contains(font)) {
            fonts.add(font)
        }
        val fontColor = Color(
            GuiManager.getINSTANCE().red / 255f,
            GuiManager.getINSTANCE().green / 255f,
            GuiManager.getINSTANCE().blue / 255f,
            1f
        ).rgb
        val Final = "Welcome " + Minecraft.getMinecraft().player.name + "!Have a nice day :)"
        fontRenderer.drawString(Final, x + 2, y + 4, fontColor)
        width = fontRenderer.getStringWidth(Final) + 4
    }
}