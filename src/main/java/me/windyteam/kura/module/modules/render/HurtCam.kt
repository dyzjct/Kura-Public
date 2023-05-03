package me.windyteam.kura.module.modules.render

import me.windyteam.kura.event.events.client.PacketEvents
import me.windyteam.kura.module.Category
import me.windyteam.kura.module.Module
import me.windyteam.kura.utils.color.ColorUtil
import me.windyteam.kura.utils.render.FadeUtils
import me.windyteam.kura.utils.render.RenderUtil
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.network.Packet
import net.minecraft.network.play.server.SPacketEntityStatus
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@Module.Info(name = "HurtCam", category = Category.RENDER)
object HurtCam : Module() {
    private val red = settings("Red", 255, 0, 255)
    private val green = settings("Green", 255, 0, 255)
    private val blue = settings("Blue", 255, 0, 255)
    private val Height = settings("Height", 100, 10, 350)
    private val doRainbow = settings("Rainbow",false)
    private val hurt = FadeUtils(1000)

    override fun onRender2D(event: RenderGameOverlayEvent.Post) {
        if (hurt.easeOutQuad() != 1.0) {
            val height2 = (Height.value * (1 - hurt.easeOutQuad())).toInt()
            val scaledResolution = ScaledResolution(Minecraft.getMinecraft())
            val rainbow = ColorUtil.rainbow(255)
            RenderUtil.drawGradientRect(
                0f,
                0f,
                scaledResolution.scaledWidth.toFloat(),
                height2.toFloat(),
                ColorUtil.toRGBA(if (doRainbow.value) rainbow.red else red.value,if (doRainbow.value) rainbow.green else green.value,if (doRainbow.value) rainbow.blue else blue.value, 255),
                ColorUtil.toRGBA(red.value, green.value, blue.value, 0)
            )
            RenderUtil.drawGradientRect(
                0f,
                scaledResolution.scaledHeight - height2.toFloat(),
                scaledResolution.scaledWidth.toFloat(),
                height2.toFloat(),
                ColorUtil.toRGBA(red.value, green.value, blue.value, 0),
                ColorUtil.toRGBA(red.value, green.value, blue.value, 255)
            )
        }
    }

    @SubscribeEvent
    fun onPacketSend(event: PacketEvents) {
        runCatching{
            if (event.getPacket<Packet<*>>() is SPacketEntityStatus) {
                if ((event.getPacket<Packet<*>>() as SPacketEntityStatus).opCode.toInt() == 2 && mc.player == (event.getPacket<Packet<*>>() as SPacketEntityStatus).getEntity(
                        mc.world
                    )
                ) {
                    hurt.reset()
                }
            }
        }
    }
}