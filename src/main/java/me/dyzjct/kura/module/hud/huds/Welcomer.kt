package me.dyzjct.kura.module.hud.huds

import me.dyzjct.kura.manager.GuiManager
import me.dyzjct.kura.module.Category
import me.dyzjct.kura.module.HUDModule
import me.dyzjct.kura.utils.color.ColorUtil
import me.dyzjct.kura.utils.font.CFont
import me.dyzjct.kura.utils.font.RFontRenderer
import net.minecraft.client.Minecraft
import java.awt.Color
import java.util.concurrent.CopyOnWriteArrayList
/**
 * Created by chunfeng666 22/12/10
 */
@HUDModule.Info(name = "Welcomer", x = 160, y = 160, width = 100, height = 10, category = Category.HUD)
class Welcomer : HUDModule() {
    private var mode = msetting("Mode", Mode.Rainbow)
    private var color = csetting("Color", Color(210, 100, 165)).m(mode, Mode.Custom)
    var fonts = CopyOnWriteArrayList<RFontRenderer>()
    var font = RFontRenderer(CFont.CustomFont("/assets/fonts/font.ttf", 47.0f, 0), true, false)
    override fun onRender() {
        if (!fonts.contains(font)) {
            fonts.add(font)
        }

        fun fontColor(): Int {
            val fontColor = Color(
                GuiManager.getINSTANCE().red / 255f,
                GuiManager.getINSTANCE().green / 255f,
                GuiManager.getINSTANCE().blue / 255f,
                1f
            ).rgb
            val custom = Color(color.value.red, color.value.green, color.value.blue).rgb
            when (mode.value) {
                Mode.Rainbow -> return ColorUtil.staticRainbow().rgb
                Mode.GuiSync -> return fontColor
                Mode.Custom -> return custom
            }
            return -1
        }
        val Final = "Hello " + Minecraft.getMinecraft().player.name + "! Thanks for use:)"
        fontRenderer.drawString(Final, x + 2, y + 4, fontColor())
        width = fontRenderer.getStringWidth(Final) + 4
    }
    enum class Mode {
        Rainbow, GuiSync, Custom
    }
}