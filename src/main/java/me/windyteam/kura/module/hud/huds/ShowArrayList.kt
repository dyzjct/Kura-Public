package me.windyteam.kura.module.hud.huds

import me.windyteam.kura.manager.GuiManager
import me.windyteam.kura.module.HUDModule
import me.windyteam.kura.module.IModule
import me.windyteam.kura.module.Module
import me.windyteam.kura.module.ModuleManager.getModules
import me.windyteam.kura.utils.color.ColorUtil
import me.windyteam.kura.utils.font.CFont
import me.windyteam.kura.utils.font.CFontRenderer
import me.windyteam.kura.utils.gl.MelonTessellator.drawRect
import me.windyteam.kura.utils.hud.AnimationUtil
import me.windyteam.kura.utils.mc.ChatUtil
import net.minecraft.client.gui.ScaledResolution
import java.awt.Color
import java.awt.Font

@HUDModule.Info(name = "ArrayList", x = 50, y = 50, width = 100, height = 100)
object ShowArrayList : HUDModule() {
    private var count = 0
    private var customFont = bsetting("CustomFont", true)
    private var drawBG = bsetting("BlurBackground", true)
    private var anim = bsetting("StressAnimation", false)
    private var animationSpeed = dsetting("AnimationSpeed", 3.5, 0.0, 5.0)
    private var sideLine = bsetting("SideLine", false)
    private var sideLineWidth = isetting("SideWidth", -1, -1, 5).b(sideLine)
    private var sideColor = csetting("SideLineColor", Color(231, 13, 103)).b(sideLine)
    private var sideAlpha = isetting("SideAlpha", 80, 1, 255).b(sideLine)
    private var mode = msetting("Mode", Mode.Rainbow)
    private var color = csetting("Color", Color(210, 100, 165)).m(mode, Mode.Custom)

    private fun getArrayList(module: IModule): String {
        return module.getName() + if (module.hudInfo == null || module.hudInfo == "") "" else " " + ChatUtil.SECTIONSIGN + "7" + (if (module.hudInfo == "" || module.hudInfo == null) "" else "[") + ChatUtil.SECTIONSIGN + "f" + module.hudInfo + '\u00a7' + "7" + if (module.hudInfo == "") "" else "]"
    }

    override fun onRender() {
        count = 0
        val screenWidth = ScaledResolution(mc).scaledWidth
        getModules().stream().filter { it!!.isEnabled }.sorted(Comparator.comparing {
                if (customFont.value) fonts.getStringWidth(
                    getArrayList(
                        it!!
                    )
                ) * -1 else mc.fontRenderer.getStringWidth(getArrayList(it!!)) * -1

        }).forEach { module: IModule ->
            if ((module as Module).isShownOnArray) {
                val screenWidthScaled = ScaledResolution(mc).scaledWidth
                val screenHightScaled = ScaledResolution(mc).scaledHeight
                val modWidth =
                    (if (customFont.value) fonts.getStringWidth(getArrayList(module)) else mc.fontRenderer.getStringWidth(
                        getArrayList(module)
                    )).toFloat()
                val modText = getArrayList(module)
                if (module.remainingAnimation < modWidth && module.isEnabled()) {
                    module.remainingAnimation = AnimationUtil.moveTowards(
                        module.remainingAnimation,
                        modWidth + 1f,
                        (0.01f + animationSpeed.value / 30).toFloat(),
                        0.1f,
                        anim.value
                    )
                }
                if (module.remainingAnimation >= modWidth && module.isDisabled()) {
                    module.remainingAnimation = -AnimationUtil.moveTowards(
                        module.remainingAnimation,
                        modWidth - 1f,
                        (0.01f + animationSpeed.value / 30).toFloat(),
                        0.1f,
                        anim.value
                    )
                    ChatUtil.sendMessage("1")
                }
                if (module.remainingAnimation > modWidth && module.isEnabled()) {
                    module.remainingAnimation = modWidth
                }
                if (module.remainingAnimation <= modWidth && module.isDisabled()) {
                    module.remainingAnimation = modWidth
                }
                //RenderUtils.drawRect(x - modWidth - 4, this.y + (10 * count), x, y, new Color(255, 197, 237, 80));
                if (x < screenWidthScaled / 2) {
                    if (drawBG.value) {
                        drawRect(
                            (x - 1 - modWidth + module.remainingAnimation).toInt().toFloat(),
                            (y + 10 * count).toFloat(),
                            (x - 2 + module.remainingAnimation).toInt().toFloat(),
                            (y + 10 * count + 10).toFloat(),
                            Color(0, 0, 0, 70).rgb
                        )
                    }
                    if (customFont.value) {
                        fonts.drawString(
                            modText,
                            (x - 2 - modWidth + module.remainingAnimation).toInt().toFloat(),
                            (y + 10 * count).toFloat(),
                            generateColor()
                        )
                    } else {
                        mc.fontRenderer.drawStringWithShadow(
                            modText,
                            (x - 2 - modWidth + module.remainingAnimation).toInt().toFloat(),
                            (y + 10 * count).toFloat(),
                            generateColor()
                        )
                    }
                } else {
                    if (drawBG.value) {
                        drawRect(
                            (x - module.remainingAnimation - 2).toInt().toFloat(),
                            (y + 10 * count).toFloat(),
                            (x - module.remainingAnimation + modWidth).toInt().toFloat(),
                            (y + 10 * count + 10).toFloat(),
                            Color(0, 0, 0, 70).rgb
                        )
                    }
                    if (sideLine.value) {
                        val sColor =
                            Color(sideColor.value.red, sideColor.value.green, sideColor.value.blue, sideAlpha.value)
                        drawRect(
                            (x - module.remainingAnimation - 2).toInt().toFloat(),
                            (y + 10 * count).toFloat(),
                            (x - module.remainingAnimation + sideLineWidth.value).toInt().toFloat(),
                            (y + 10 * count + 10).toFloat(),
                            sColor.rgb
                        )
                    }
                    if (customFont.value) {
                        fonts.drawString(
                            modText,
                            (x - module.remainingAnimation).toInt().toFloat(),
                            (y + 10 * count).toFloat(),
                            generateColor()
                        )
                    } else {
                        mc.fontRenderer.drawStringWithShadow(
                            modText,
                            (x - module.remainingAnimation).toInt().toFloat(),
                            (y + 10 * count).toFloat(),
                            generateColor()
                        )
                    }
                }
                if (y > screenHightScaled/2){
                    count--
                } else {
                    count++
                }
            }
        }
        width = if (x < screenWidth / 2) {
            75
        } else {
            -75
        }
        height = (fonts.height + 1) * count
    }

    private fun generateColor(): Int {
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

    enum class Mode {
        Rainbow, GuiSync, Custom
    }

    private var fonts = CFontRenderer(
        CFont.CustomFont(
            "/assets/fonts/Comfortaa.ttf",
            20f,
            Font.PLAIN
        ), true, false
    )
}