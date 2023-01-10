package me.windyteam.kura.module.modules.render


import me.windyteam.kura.event.events.entity.MotionUpdateEvent
import me.windyteam.kura.module.Category
import me.windyteam.kura.module.Module
import me.windyteam.kura.setting.BooleanSetting
import net.minecraft.client.Minecraft
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent


@Module.Info(name = "AntiPlayerSwing", category = Category.RENDER)
class AntiPlayerSwing : Module() {
    private val crouch: BooleanSetting = bsetting("Crouch", false)
    private val noLimbSwing: BooleanSetting = bsetting("NoLimbSwing" , true)
    var mc = Minecraft.getMinecraft()

    override fun onUpdate() {
        for (player in mc.world.playerEntities) {
            if (player == mc.player) continue
            player.isSneaking = crouch.value as Boolean
        }
    }

    @SubscribeEvent
    fun onTick(event: MotionUpdateEvent.Tick?) {
        try {
            for (player in mc.world.playerEntities) {
                if (noLimbSwing.value as Boolean) {
                    player.limbSwing = 0f
                    player.limbSwingAmount = 0f
                    player.prevLimbSwingAmount = 0f
                }
            }
        } catch (ignore: Exception) {
        }
    }
}