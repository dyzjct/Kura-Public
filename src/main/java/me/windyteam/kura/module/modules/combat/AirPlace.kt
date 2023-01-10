package me.windyteam.kura.module.modules.combat

import me.windyteam.kura.module.Category
import me.windyteam.kura.module.Module
import net.minecraft.item.ItemBlock
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraftforge.event.entity.player.PlayerInteractEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@Module.Info(name = "AirPlace", category = Category.MISC)
object AirPlace: Module() {

    @SubscribeEvent
    fun onUseItem(event: PlayerInteractEvent.RightClickItem) {
        if (fullNullCheck()) {
            return
        }
        val hitResult = mc.player.rayTrace(4.0, 0F)

        if (mc.player.heldItemMainhand.getItem() !is ItemBlock || hitResult == null) return
        mc.player.connection.sendPacket(CPacketPlayerTryUseItemOnBlock(hitResult.blockPos,EnumFacing.UP, EnumHand.MAIN_HAND,0.5f, 1.0f, 0.5f))
    }

}