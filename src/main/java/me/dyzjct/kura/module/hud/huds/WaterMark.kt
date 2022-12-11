package me.dyzjct.kura.module.hud.huds

import me.dyzjct.kura.module.HUDModule
import me.dyzjct.kura.setting.Setting
import me.dyzjct.kura.utils.font.CFont
import me.dyzjct.kura.utils.font.RFontRenderer
import org.lwjgl.opengl.GL11
import java.util.concurrent.CopyOnWriteArrayList
import java.util.function.Consumer

@HUDModule.Info(name = "WaterMark", x = 20, y = 20)
class WaterMark : HUDModule() {
    var fonts = CopyOnWriteArrayList<RFontRenderer>()
    var text = ssetting("ViewText", "Kura")

    //    public ColorSetting color = csetting("Color", new Color(255,255,255));
    var saturation: Setting<Float> = fsetting("Saturation", 1.0f, 0f, 1.0f)
    var brightness: Setting<Float> = fsetting("Brightness", 1.0f, 0f, 1.0f)
    var alpha: Setting<Int> = isetting("Alpha", 90, 1, 255)
    var speed = fsetting("RainbowSpeed", 1.0f, 0.0f, 1.0f)
    var Scala = fsetting("Scala", 1.0f, 0.0f, 3.0f)
    var font = RFontRenderer(CFont.CustomFont("/assets/fonts/font.ttf", 47.0f, 0), true, false)
    override fun onRender() {
        if (!fonts.contains(font)) {
            fonts.add(font)
        }
        //        int fontColor = new Color(GuiManager.getINSTANCE().getRed() / 255f, GuiManager.getINSTANCE().getGreen() / 255f, GuiManager.getINSTANCE().getBlue() / 255f, 1F).getRGB();
        fonts.forEach(Consumer { f: RFontRenderer ->
            GL11.glPushMatrix()
            GL11.glTranslated(x.toDouble(), y.toFloat().toDouble(), 0.0)
            GL11.glScaled(Scala.value.toDouble(), Scala.value.toDouble(), 0.0)
            f.drawString(text.value, 0.0, 0.0, speed.value, saturation.value, brightness.value, 50, alpha.value, false)
            GL11.glPopMatrix()
            width = (f.getStringWidth(text.value).toFloat() * Scala.value).toInt()
            height = (f.height.toFloat() * Scala.value).toInt()
        })
    }

    enum class Mode {
        Rainbow, Custom
    }
}