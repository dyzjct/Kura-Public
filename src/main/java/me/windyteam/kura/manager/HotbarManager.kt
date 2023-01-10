package me.windyteam.kura.manager

import me.windyteam.kura.event.events.entity.MotionUpdateEvent
import me.windyteam.kura.event.events.entity.MotionUpdateEvent.FastTick
import me.windyteam.kura.event.events.player.PacketEvent
import me.windyteam.kura.utils.inventory.HotbarSlot
import net.minecraft.client.Minecraft
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.item.ItemStack
import net.minecraft.network.Packet
import net.minecraft.network.play.client.CPacketHeldItemChange
import net.minecraftforge.fml.common.eventhandler.Event
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

object HotbarManager : Event() {
    var mc: Minecraft? = Minecraft.getMinecraft()
    var serverSideHotbar = 0; private set

    @SubscribeEvent
    fun FastTick(event: MotionUpdateEvent.FastTick){
        if (mc!!.world==null||mc!!.player==null){
            return
        }
        mc!!.playerController.updateController()
    }
    @SubscribeEvent
    fun onPacket(it: PacketEvent.Send) {
        if (it.isCanceled || it.getPacket<Packet<*>>() !is CPacketHeldItemChange) return

        synchronized(mc!!.playerController) {
            serverSideHotbar = (it.getPacket<Packet<*>>() as CPacketHeldItemChange).slotId
        }
    }

    @JvmStatic
    fun spoofHotbar(slot: Int) {
        spoofHotbar(slot, null, false)
    }

    @JvmStatic
    fun spoofHotbar(slot: Int, packet: Boolean) {
        spoofHotbar(slot, null, packet)
    }

    @JvmStatic
    fun spoofHotbar(slot: Int, event: FastTick?) {
        spoofHotbar(slot, event, false)
    }

    @JvmStatic
    fun spoofHotbar(slot: Int, event: FastTick?, packet: Boolean) {
        if (mc!!.player.inventory.currentItem == slot || slot < 0) {
            //if (slot < 0) {
            return
        }
        try {
            //Minecraft.getMinecraft().player.connection.sendPacket(CPacketHeldItemChange(slot))
            synchronized(mc!!.playerController) {
                if (event != null || packet) {
                    //mc.player.inventory.currentItem = slot
                    //mc.playerController.updateController()
                    mc!!.player.inventory.currentItem = slot
                    mc!!.connection!!.sendPacket(CPacketHeldItemChange(slot))
                    //mc.playerController.updateController()
                } else {
                    Minecraft.getMinecraft().player.inventory.currentItem = slot
                    Minecraft.getMinecraft().playerController.updateController()
                }
            }
        } catch (_: Exception) {
        }
    }

    @JvmStatic
    inline fun spoofHotbarBypass(slot: Int, crossinline block: () -> Unit) {
        synchronized(mc!!.playerController) {
            val swap = slot != serverSideHotbar
            if (swap) mc!!.playerController.pickItem(slot)
            block.invoke()
            if (swap) mc!!.playerController.pickItem(slot)
        }
    }

    val EntityPlayerSP.serverSideItem: ItemStack
        get() = inventory.mainInventory[serverSideHotbar]

    inline fun spoofHotbarNew(slot: HotbarSlot) {
        return spoofHotbarNew(slot.hotbarSlot)
    }

    inline fun spoofHotbarNew(slot: Int) {
        if (serverSideHotbar != slot) {
            mc!!.player.connection.sendPacket(CPacketHeldItemChange(slot))
        }
    }

    inline fun spoofHotbarNew(slot: HotbarSlot, crossinline block: () -> Unit) {
        synchronized(mc!!.playerController) {
            spoofHotbarNew(slot)
            block.invoke()
            resetHotbar()
        }
    }

    inline fun spoofHotbarNew(slot: Int, crossinline block: () -> Unit) {
        synchronized(mc!!.playerController) {
            spoofHotbarNew(slot)
            block.invoke()
            resetHotbar()
        }
    }

    inline fun resetHotbar() {
        val slot = mc!!.playerController.currentPlayerItem
        if (serverSideHotbar != slot) {
            spoofHotbarNew(slot)
        }
    }

}
