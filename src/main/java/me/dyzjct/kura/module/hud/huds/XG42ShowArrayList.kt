package me.dyzjct.kura.module.hud.huds

import me.dyzjct.kura.module.HUDModule
import me.dyzjct.kura.module.IModule
import me.dyzjct.kura.module.Module
import me.dyzjct.kura.module.ModuleManager
import me.dyzjct.kura.utils.MathUtil
import me.dyzjct.kura.utils.Timer
import me.dyzjct.kura.utils.Wrapper
import me.dyzjct.kura.utils.color.ColorUtils
import me.dyzjct.kura.utils.color.ColourTextFormatting
import me.dyzjct.kura.utils.font.CFontRenderer
import me.dyzjct.kura.utils.gl.RenderUtils
import me.dyzjct.kura.utils.mc.ChatUtil
import net.minecraft.util.text.TextFormatting
import org.lwjgl.opengl.Display
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.awt.Font
import java.util.function.Function
import java.util.stream.Collectors

@HUDModule.Info(name = "XG42ArrayList", x = 50, y = 50, width = 100, height = 100)
class XG42ShowArrayList : HUDModule() {
    var mode = msetting("Mode", Mode.RAINBOW)
    var listPos = msetting("ListPos", Position.CENTER)
    var move = bsetting("Move", true)
    var maxOffset = isetting("MoveLong", 25, 0, 50).b(move)
    var moveDelay = isetting("MoveDelay", 1, 0.1.toInt(), 50).b(move)
    var rainbowSpeed = isetting("SpeedR", 30, 0, 100).m(mode, Mode.RAINBOW)
    var saturationR = isetting("SaturationR", 117, 0, 255).m(mode, Mode.RAINBOW)
    var brightnessR = isetting("BrightnessR", 255, 0, 255).m(mode, Mode.RAINBOW)
    var hueC = isetting("HueC", 178, 0, 255).m(mode, Mode.CUSTOM)
    var saturationC = isetting("SaturationC", 156, 0, 255).m(mode, Mode.CUSTOM)
    var brightnessC = isetting("BrightnessC", 255, 0, 255).m(mode, Mode.CUSTOM)
    var alternate = bsetting("Alternate", true).m(mode, Mode.INFO_OVERLAY)
    var potion = bsetting("PotionsMove", false)
    var forgeHax = bsetting("ForgeHax", false)
    var cFontRenderer = CFontRenderer(Font("Comic Sans MS", Font.PLAIN, 18), true, false)
    private val timer = Timer().reset()
    override fun onInit() {
        if (INSTANCE == null) {
            INSTANCE = this
        }
    }

    override fun onRender() {
        val allModule = ModuleManager.getModules().stream()
            .filter { module: IModule? -> module is Module }
            .sorted(Comparator.comparing { module: IModule -> cFontRenderer.getStringWidth(module.getName() + if (module.hudInfo == null) "" else module.hudInfo + " ") * if (sort_up()) -1 else 1 })
            .collect(Collectors.toList())
        val xFunc: Function<Int, Int>
        xFunc = when (listPos.value) {
            Position.RIGHT -> Function { i: Int -> width - i }
            Position.CENTER -> Function { i: Int -> width / 2 - i / 2 }
            Position.LEFT -> Function { i: Int? -> 0 }
            else -> Function { i: Int? -> 0 }
        }
        var y = 2
        var hue = System.currentTimeMillis() % (360 * getRainbowSpeed()) / (360f * getRainbowSpeed())
        if (potion.value && this.y < 26 && Wrapper.getPlayer().activePotionEffects.size > 0) y =
            Math.max(this.y, 26 - this.y)
        GL11.glDisable(GL11.GL_CULL_FACE)
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glEnable(GL11.GL_TEXTURE_2D)
        GL11.glPushMatrix()
        GL11.glTranslated(x.toDouble(), this.y.toDouble(), 0.0)
        for (i in allModule.indices) {
            if (allModule[i] is Module) {
                val module = allModule[i] as Module
                var rgb: Int
                rgb = when (mode.value) {
                    Mode.RAINBOW -> Color.HSBtoRGB(
                        hue,
                        ColorUtils.toF(saturationR.value),
                        ColorUtils.toF(brightnessR.value)
                    )

                    Mode.CUSTOM -> Color.HSBtoRGB(
                        ColorUtils.toF(hueC.value),
                        ColorUtils.toF(saturationC.value),
                        ColorUtils.toF(brightnessC.value)
                    )

                    Mode.INFO_OVERLAY -> getInfoColour(i)
                    else -> 0
                }


                // field
                val hudInfo = module.hudInfo
                val text = getAlignedText(
                    module.getName(),
                    if (hudInfo == null) "" else ChatUtil.SECTIONSIGN.toString() + "7" + hudInfo + ChatUtil.SECTIONSIGN + "r",
                    listPos.value == Position.LEFT
                )
                val textHeight = cFontRenderer.height + 1
                val textWidth = cFontRenderer.getStringWidth(text)
                val nameWidth = cFontRenderer.getStringWidth(module.getName())
                val hudWidth = cFontRenderer.getStringWidth(hudInfo)
                val offset = module.offset

                //check view
                if (module.isShownOnArray && (if (move.value) module.offset <= maxOffset.value || module.isEnabled else module.isEnabled)) {

                    //move
                    if (timer.passed(moveDelay.value.toLong())) {
                        if (module.isEnabled) {
                            when (listPos.value) {
                                Position.RIGHT -> if (offset > -hudWidth) {
                                    module.offset = offset - 1
                                } else if (offset < -hudWidth) {
                                    module.offset = offset + 1
                                }

                                Position.CENTER -> module.offset = 0
                                Position.LEFT -> if (offset < hudWidth) {
                                    module.offset = offset + 1
                                } else if (offset > hudWidth) {
                                    module.offset = offset - 1
                                }
                            }
                        } else {
                            if (offset <= maxOffset.value && offset != maxOffset.value + 1) {
                                module.offset = offset + 1
                            }
                        }
                    }
                    var target = Math.floor(xFunc.apply(Math.floor(textWidth.toDouble()).toInt()).toDouble()).toInt()
                    if (move.value) {
                        when (listPos.value) {
                            Position.RIGHT -> target =
                                Math.floor(xFunc.apply(Math.floor(nameWidth.toDouble()).toInt()).toDouble()).toInt()

                            Position.CENTER -> target =
                                Math.floor(xFunc.apply(Math.floor(textWidth.toDouble()).toInt()).toDouble()).toInt()

                            Position.LEFT -> target =
                                Math.floor((xFunc.apply(0) - Math.floor(hudWidth.toDouble()).toInt()).toDouble())
                                    .toInt()
                        }
                    }
                    RenderUtils.drawRect(
                        (target + (if (move.value) offset else 0) - 2).toDouble(),
                        y.toDouble(),
                        (textWidth + 4).toDouble(),
                        textHeight.toDouble(),
                        Color(10, 10, 10, 127)
                    )
                    when (listPos.value) {
                        Position.RIGHT -> RenderUtils.drawRect(
                            (target + if (move.value) offset else 0 - 1 - 1).toDouble(),
                            y.toDouble(),
                            1.0,
                            textHeight.toDouble(),
                            Color(rgb)
                        )

                        Position.CENTER -> {
                            RenderUtils.drawRect(
                                (target + if (move.value) offset else 0 - 1 - 1).toDouble(),
                                y.toDouble(),
                                1.0,
                                textHeight.toDouble(),
                                Color(rgb)
                            )
                            RenderUtils.drawRect(
                                (target + (if (move.value) offset else 0) + textWidth + 1).toDouble(),
                                y.toDouble(),
                                1.0,
                                textHeight.toDouble(),
                                Color(rgb)
                            )
                        }

                        else -> RenderUtils.drawRect(
                            (target + (if (move.value) offset else 0) + textWidth + 1).toDouble(),
                            y.toDouble(),
                            1.0,
                            textHeight.toDouble(),
                            Color(rgb)
                        )
                    }
                    cFontRenderer.drawStringWithShadow(
                        text,
                        (target + if (move.value) offset else 0).toDouble(),
                        (y + 1).toDouble(),
                        rgb
                    )
                    hue = hue + 0.02f
                    y = y + textHeight
                }
            }
        }
        GL11.glPopMatrix()
        GL11.glEnable(GL11.GL_CULL_FACE)
        GL11.glDisable(GL11.GL_BLEND)
        height = y
        if (timer.passed(moveDelay.value.toLong())) timer.reset()
    }

    private fun sort_up(): Boolean {
        return y < Display.getWidth() / 2
    }

    fun getRainbowSpeed(): Int {
        val reverseNumber = MathUtil.reverseNumber(rainbowSpeed.value, 1, 100)
        return if (reverseNumber == 0) {
            1
        } else reverseNumber
    }

    fun getInfoColour(n: Int): Int {
        if (!alternate.value) {
            return settingsToColour(false)
        }
        return if (MathUtil.isNumberEven(n)) {
            settingsToColour(true)
        } else settingsToColour(false)
    }

    private fun infoGetSetting(b: Boolean): TextFormatting? {
        return if (b) {
            setToText(ColourTextFormatting.ColourCode.WHITE)
        } else setToText(ColourTextFormatting.ColourCode.BLUE)
    }

    private fun settingsToColour(b: Boolean): Int {
        val color: Color
        color = when (infoGetSetting(b)) {
            TextFormatting.UNDERLINE, TextFormatting.ITALIC, TextFormatting.RESET, TextFormatting.STRIKETHROUGH, TextFormatting.OBFUSCATED, TextFormatting.BOLD -> {
                ColourTextFormatting.colourEnumMap[TextFormatting.WHITE]!!.colorLocal
            }

            else -> {
                ColourTextFormatting.colourEnumMap[infoGetSetting(b)]!!.colorLocal
            }
        }
        return Color(color.getRed(), color.getGreen(), color.getBlue()).rgb
    }

    private fun setToText(colourCode: ColourTextFormatting.ColourCode): TextFormatting? {
        return ColourTextFormatting.toTextMap[colourCode]
    }

    fun getAlignedText(name: String, hud: String, left: Boolean): String {
        val value: String
        value = if (left) {
            "$hud $name"
        } else {
            "$name $hud"
        }
        if (!forgeHax.value) {
            return value
        }
        return if (left) {
            "$value<"
        } else ">$value"
    }

    enum class Mode {
        INFO_OVERLAY, RAINBOW, CUSTOM
    }

    enum class Position {
        RIGHT, CENTER, LEFT
    }

    companion object {
        var INSTANCE: XG42ShowArrayList? = null
        fun rainbow(delay: Int): Int {
            val rainbowState = Math.ceil((System.currentTimeMillis() + delay.toLong()).toDouble() / 20.0)
            return Color.getHSBColor((rainbowState % 360.0 / 360.0).toFloat(), 1.0f, 1.0f).rgb
        }
    }
}