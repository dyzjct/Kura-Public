package me.windyteam.kura.module.modules.combat

import me.windyteam.kura.module.Category
import me.windyteam.kura.module.Module
import me.windyteam.kura.setting.BooleanSetting
import me.windyteam.kura.setting.Setting
import net.minecraft.item.*
import net.minecraft.network.play.client.CPacketPlayerDigging
import net.minecraft.network.play.client.CPacketPlayerTryUseItem
import net.minecraft.util.math.BlockPos

@Module.Info(category = Category.COMBAT, description = "Use items faster", name = "FastUse")
class Fastuse : Module() {
    private val delay: Setting<Int> = isetting("Delay", 0, 0, 10)
    private val all: Setting<Boolean> = bsetting("All", false)
    private val bow: Setting<Boolean> = bsetting("Bow", false).bn(all as BooleanSetting)
    private val expBottles: Setting<Boolean> = bsetting("XP", true).bn(all as BooleanSetting)
    private val endCrystals: Setting<Boolean> = bsetting("Crystal", false).bn(all as BooleanSetting)
    private val fireworks: Setting<Boolean> = bsetting("FireWorks", false).bn(all as BooleanSetting)
    override fun onDisable() {
        mc.rightClickDelayTimer = 2
    }

    override fun onUpdate() {
        if (mc.player == null) return
        if (all.value || bow.value && mc.player.heldItemMainhand.getItem() is ItemBow && mc.player.isHandActive && mc.player.itemInUseMaxCount >= 3) {
            mc.player.connection.sendPacket(
                CPacketPlayerDigging(
                    CPacketPlayerDigging.Action.RELEASE_USE_ITEM,
                    BlockPos.ORIGIN,
                    mc.player.horizontalFacing
                )
            )
            mc.player.connection.sendPacket(CPacketPlayerTryUseItem(mc.player.getActiveHand()))
            mc.player.stopActiveHand()
        }
        if (all.value || bow.value && mc.player.heldItemOffhand.getItem() is ItemBow && mc.player.isHandActive && mc.player.itemInUseMaxCount >= 3) {
            mc.player.connection.sendPacket(
                CPacketPlayerDigging(
                    CPacketPlayerDigging.Action.RELEASE_USE_ITEM,
                    BlockPos.ORIGIN,
                    mc.player.horizontalFacing
                )
            )
            mc.player.connection.sendPacket(CPacketPlayerTryUseItem(mc.player.getActiveHand()))
            mc.player.stopActiveHand()
        }
        if (all.value || expBottles.value && mc.player.heldItemOffhand.getItem() is ItemExpBottle && mc.player.isHandActive && mc.player.itemInUseMaxCount >= 3) {
            mc.player.connection.sendPacket(
                CPacketPlayerDigging(
                    CPacketPlayerDigging.Action.RELEASE_USE_ITEM,
                    BlockPos.ORIGIN,
                    mc.player.horizontalFacing
                )
            )
            mc.player.connection.sendPacket(CPacketPlayerTryUseItem(mc.player.getActiveHand()))
            mc.player.stopActiveHand()
        }
        if (delay.value > 0) {
            if (time <= 0) time = Math.round((2 * Math.round(delay.value.toFloat() / 2)).toFloat()).toLong() else {
                time--
                mc.rightClickDelayTimer = 1
                return
            }
        }
        if (passItemCheck(mc.player.heldItemMainhand.getItem()) || passItemCheck(mc.player.heldItemOffhand.getItem())) {
            mc.rightClickDelayTimer = 0
        }
        if (passItemCheck(mc.player.heldItemOffhand.getItem())) {
            mc.rightClickDelayTimer = 0
        }
    }

    private fun passItemCheck(item: Item): Boolean {
        if (all.value) return true
        if (expBottles.value && item is ItemExpBottle) return true
        return if (endCrystals.value && item is ItemEndCrystal) true else fireworks.value && item is ItemFirework
    }

    companion object {
        private var time: Long = 0
    }
}