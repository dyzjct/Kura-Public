package me.windyteam.kura.module.modules.misc

import me.windyteam.kura.module.Category
import me.windyteam.kura.module.Module
import me.windyteam.kura.utils.Timer
import net.minecraft.item.ItemBucketMilk
import net.minecraft.item.ItemFood
import net.minecraft.item.ItemPotion
import net.minecraft.network.play.client.CPacketHeldItemChange

@Module.Info(name = "PacketEat", category = Category.MISC, description = "PacketEat")
class PacketEat : Module() {
    private val fastEat = bsetting("FastEat", false)
    private val timer = Timer()
    override fun onUpdate() {
        if (fullNullCheck()) {
            return
        }
        if (fastEat.value) {
            if (mc.player.isHandActive) {
                val usingItem = mc.player.getActiveItemStack().getItem()
                if (usingItem is ItemFood || usingItem is ItemBucketMilk
                    || usingItem is ItemPotion
                ) {
                    if (mc.player.itemInUseMaxCount >= 1) {
                        mc.connection!!.sendPacket(CPacketHeldItemChange(mc.player.inventory.currentItem))
                    }
                }
                if (!mc.player.isHandActive) {
                    timer.reset()
                }
            }
        }
    }
}