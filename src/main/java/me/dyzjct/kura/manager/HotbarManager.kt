package me.dyzjct.kura.manager

import com.sun.istack.internal.NotNull
import me.dyzjct.kura.event.events.entity.MotionUpdateEvent.FastTick
import me.dyzjct.kura.event.events.player.PacketEvent
import net.minecraft.client.Minecraft
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.item.ItemStack
import net.minecraft.network.Packet
import net.minecraft.network.play.client.CPacketHeldItemChange
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

object HotbarManager : net.minecraftforge.fml.common.eventhandler.Event() {
    var mc: Minecraft? = Minecraft.getMinecraft()
    var serverSideHotbar = 0; private set

    @SubscribeEvent
    fun onPacket(it: PacketEvent.Send) {
        if (it.isCanceled || it.getPacket<Packet<*>>() !is CPacketHeldItemChange) return

        synchronized(mc!!.playerController) {
            serverSideHotbar = (it.getPacket<Packet<*>>() as CPacketHeldItemChange).slotId
        }
    }

    @JvmStatic
    inline fun spoofHotbarInvoke(slot: Int, crossinline block: () -> Unit) {
        synchronized(mc!!.playerController) {
            if (serverSideHotbar != slot) {
                mc!!.connection!!.sendPacket(CPacketHeldItemChange(slot))
            }
            block.invoke()
            resetHotbar()
        }
    }

    fun resetHotbar() {
        val slot = mc!!.playerController.currentPlayerItem
        if (serverSideHotbar != slot) {
            mc!!.connection!!.sendPacket(CPacketHeldItemChange(slot))
        }
    }

    @NotNull
    fun getServerSideItem(@NotNull player: EntityPlayerSP): ItemStack {
        return player.inventory.mainInventory[serverSideHotbar]
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
}
