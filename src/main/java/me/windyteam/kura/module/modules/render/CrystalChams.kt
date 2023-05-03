package me.windyteam.kura.module.modules.render

import me.windyteam.kura.module.Category
import me.windyteam.kura.module.Module
import me.windyteam.kura.setting.BooleanSetting
import me.windyteam.kura.setting.IntegerSetting
import me.windyteam.kura.setting.ModeSetting
import me.windyteam.kura.setting.Setting

@Module.Info(name = "CrystalChams", category = Category.RENDER)
object CrystalChams : Module() {
    private val page = settings("Settings", Page.GLOBAL)

    @JvmField
    var fill: BooleanSetting = settings("Fill", true).m(page, Page.GLOBAL)

    @JvmField
    var xqz: BooleanSetting = settings("XQZ", true).b(fill).m(page, Page.GLOBAL)

    @JvmField
    var wireframe: BooleanSetting = settings("Wireframe", true).m(page, Page.GLOBAL)

    @JvmField
    var model: ModeSetting<Enum<*>?> = settings("Model", Model.XQZ).m(page, Page.GLOBAL)

    @JvmField
    var glint: BooleanSetting = settings("Glint", false).m(page, Page.GLOBAL)

    @JvmField
    var scale: Setting<Float> = settings("Scale", 1.0f, 0.1f, 1.0f).m(page, Page.GLOBAL)

    @JvmField
    var changeSpeed: BooleanSetting = settings("ChangeSpeed", false).m(page, Page.GLOBAL)

    @JvmField
    var spinSpeed: Setting<Float>

    @JvmField
    var floatFactor: Setting<Float>

    @JvmField
    var lineWidth: Setting<Float>

    @JvmField
    var rainbow: BooleanSetting

    @JvmField
    val red: IntegerSetting = settings("Red", 255, 0, 255).m(page, Page.COLORS)

    @JvmField
    val green: IntegerSetting = settings("Green", 255, 0, 255).m(page, Page.COLORS)

    @JvmField
    val blue: IntegerSetting = settings("Blue", 255, 0, 255).m(page, Page.COLORS)

    @JvmField
    val alpha: IntegerSetting = settings("Alpha", 255, 0, 255).m(page, Page.COLORS)

    @JvmField
    var lineColor: BooleanSetting = settings("LineColor", true).m(page, Page.COLORS)

    @JvmField
    val lineRed: IntegerSetting = settings("lineRed", 255, 0, 255).m(page, Page.COLORS).b(lineColor)

    @JvmField
    val lineGreen: IntegerSetting = settings("lineGreen", 255, 0, 255).m(page, Page.COLORS).b(lineColor)

    @JvmField
    val lineBlue: IntegerSetting = settings("lineBlue", 255, 0, 255).m(page, Page.COLORS).b(lineColor)

    @JvmField
    val lineAlpha: IntegerSetting = settings("lineAlpha", 255, 0, 255).m(page, Page.COLORS).b(lineColor)

    @JvmField
    var modelColor: BooleanSetting = settings("ModelColor", true).m(page, Page.COLORS)

    @JvmField
    val modelRed: IntegerSetting = settings("modelRed", 255, 0, 255).m(page, Page.COLORS).b(modelColor)

    @JvmField
    val modelGreen: IntegerSetting = settings("modelGreen", 255, 0, 255).m(page, Page.COLORS).b(modelColor)

    @JvmField
    val modelBlue: IntegerSetting = settings("modelBlue", 255, 0, 255).m(page, Page.COLORS).b(modelColor)

    @JvmField
    val modelAlpha: IntegerSetting = settings("modelAlpha", 255, 0, 255).m(page, Page.COLORS).b(modelColor)

    init {
        spinSpeed = settings("SpinSpeed", 1.0f, 0.0f, 10.0f).b(changeSpeed).m(page, Page.GLOBAL)
        floatFactor = settings("FloatFactor", 1.0f, 0.0f, 1.0f).b(changeSpeed).m(page, Page.GLOBAL)
        lineWidth = settings("LineWidth", 1.0f, 0.1f, 3.0f).m(page, Page.COLORS)
        rainbow = settings("Rainbow", false).m(page, Page.COLORS)
    }

    override fun getHudInfo(): String {
        var info: String? = null
        if (fill.value) {
            info = "Fill"
        } else if (wireframe.value) {
            info = "Wireframe"
        }
        if (wireframe.value && fill.value) {
            info = "Both"
        }
        return info!!
    }

    enum class Page {
        COLORS, GLOBAL
    }

    enum class Model {
        XQZ, VANILLA, OFF
    }
}