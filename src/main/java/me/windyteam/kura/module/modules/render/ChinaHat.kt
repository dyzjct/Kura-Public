package me.windyteam.kura.module.modules.render

import me.windyteam.kura.event.events.render.Render3DEvent
import me.windyteam.kura.module.Category
import me.windyteam.kura.module.Module
import me.windyteam.kura.setting.Setting
import me.windyteam.kura.utils.color.ColorUtil
import me.windyteam.kura.utils.render.RenderUtil
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.awt.Color
import kotlin.math.abs

@Module.Info(name = "ChinaHat", category = Category.RENDER)
class ChinaHat : Module() {
    private val color = csetting("Color", Color(255, 255, 255))
    private val color2 = csetting("Color", Color(255, 255, 255))
    private var points: Setting<Int> = isetting("Points", 12, 4, 64)
    private var firstPerson: Setting<Boolean> = bsetting("FirstPerson", false)

    @SubscribeEvent
    fun onRender3D(event: Render3DEvent) {
        var f: Float
        if (mc.gameSettings.thirdPersonView != 0 || firstPerson.value) {
            for (i in 0..399) {
                f = ColorUtil.getGradientOffset(
                    Color(color2.value.red, color2.value.green, color2.value.blue, 255),
                    Color(color.value.red, color.value.green, color.value.blue, 255),
                    abs(
                        System.currentTimeMillis() / 7L - i / 2
                    ) / 120.0
                ).rgb.toFloat()
                if (mc.player.isElytraFlying) {
                    RenderUtil.drawHat(
                        mc.player,
                        0.009 + i * 0.0014,
                        event.partialTicks,
                        points.value,
                        2.0f,
                        1.1f - i * 7.85E-4f - if (mc.player.isSneaking) 0.07f else 0.03f,
                        f.toInt()
                    )
                } else if (mc.player.isSneaking) {
                    RenderUtil.drawHat(
                        mc.player,
                        0.009 + i * 0.0014,
                        event.partialTicks,
                        points.value,
                        2.0f,
                        1.1f - i * 7.85E-4f - if (mc.player.isSneaking) 0.07f else 0.03f,
                        f.toInt()
                    )
                } else {
                    RenderUtil.drawHat(
                        mc.player,
                        0.009 + i * 0.0014,
                        event.partialTicks,
                        points.value,
                        2.0f,
                        2.2f - i * 7.85E-4f - if (mc.player.isSneaking) 0.07f else 0.03f,
                        f.toInt()
                    )
                }
            }
        }
    }
}