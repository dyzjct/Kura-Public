package me.windyteam.kura.module.modules.render


import me.windyteam.kura.event.events.entity.MotionUpdateEvent
import me.windyteam.kura.module.Category
import me.windyteam.kura.module.Module
import net.minecraft.client.Minecraft
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent


@Module.Info(name = "AntiPlayerSwing", category = Category.RENDER)
object AntiPlayerSwing : Module() {
    private val crouch = bsetting("Crouch", false)
    private val noLimbSwing = bsetting("NoLimbSwing" , true)
    private var mc = Minecraft.getMinecraft()

    @SubscribeEvent
    fun onTick(event: MotionUpdateEvent.FastTick) {
        runCatching{
            for (player in mc.world.playerEntities) {
                if (player == mc.player) continue
                player.isSneaking = crouch.value
            }
            for (player in mc.world.playerEntities) {
                if (noLimbSwing.value) {
                    player.limbSwing = 0f
                    player.limbSwingAmount = 0f
                    player.prevLimbSwingAmount = 0f
                }
            }
        }
    }
}