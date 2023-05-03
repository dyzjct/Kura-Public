package me.windyteam.kura.module.modules.combat

import me.windyteam.kura.event.events.client.PacketEvents
import me.windyteam.kura.module.Category
import me.windyteam.kura.module.Module
import me.windyteam.kura.utils.mc.ChatUtil
import net.minecraft.entity.Entity
import net.minecraft.item.ItemBow
import net.minecraft.item.ItemStack
import net.minecraft.network.Packet
import net.minecraft.network.play.client.CPacketEntityAction
import net.minecraft.network.play.client.CPacketPlayer.PositionRotation
import net.minecraft.network.play.client.CPacketPlayerDigging
import net.minecraft.util.EnumHand
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@Module.Info(name = "32kBow", category = Category.COMBAT)
object EzBow : Module() {
    private var spoofs = settings("Spoofs", 10, 1, 300)
    private var debug = settings("Debug", false)
    var cancel = false

    @SubscribeEvent
    fun onPacketSend(event: PacketEvents.Send) {
        if (event.stage !== 0) {
            return
        }
        if (event.packet is CPacketPlayerDigging) {
            val packet = event.getPacket() as CPacketPlayerDigging
            if (packet.action == CPacketPlayerDigging.Action.RELEASE_USE_ITEM) {
                val handStack: ItemStack = mc.player.getHeldItem(EnumHand.MAIN_HAND)
                if (!handStack.isEmpty() && handStack.getItem() != null && handStack.getItem() is ItemBow) {
                    cancel = true
                    mc.player.connection.sendPacket(
                        CPacketEntityAction(
                            mc.player as Entity,
                            CPacketEntityAction.Action.START_SPRINTING
                        ) as Packet<*>
                    )
                    for (index in 0 until spoofs.value) {
                        mc.player.connection.sendPacket(
                            PositionRotation(
                                mc.player.posX,
                                mc.player.posY + 1.0E-10,
                                mc.player.posZ,
                                mc.player.rotationYaw,
                                mc.player.rotationPitch,
                                false
                            ) as Packet<*>
                        )
                        mc.player.connection.sendPacket(
                            PositionRotation(
                                mc.player.posX,
                                mc.player.posY - 1.0E-10,
                                mc.player.posZ,
                                mc.player.rotationYaw,
                                mc.player.rotationPitch,
                                true
                            ) as Packet<*>
                        )
                        if (debug.value) ChatUtil.sendMessage("Spoofed")
                    }
                }
            } else {
                cancel = false
            }
        } else {
            cancel = false
        }
    }
}