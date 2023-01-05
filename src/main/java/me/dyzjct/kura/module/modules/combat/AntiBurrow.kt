package me.dyzjct.kura.module.modules.combat

import me.dyzjct.kura.module.Category
import me.dyzjct.kura.module.Module
import me.dyzjct.kura.module.modules.misc.InstantMine
import me.dyzjct.kura.utils.block.BlockUtil
import me.dyzjct.kura.utils.player.getTarget
import net.minecraft.block.BlockLiquid
import net.minecraft.client.gui.GuiHopper
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper

@Module.Info(name = "BurrowMiner", category = Category.COMBAT)
class AntiBurrow : Module() {
    var range = isetting("Range", 4, 0, 10)
    var ticked = 0
    var player: EntityPlayer? = null

    //    private EntityPlayer getTarget(double range) {
    //        EntityPlayer target = null;
    //        double distance = Math.pow(range, 2.0) + 1.0;
    //        for (EntityPlayer player : AntiBurrow2.mc.world.playerEntities) {
    //            if (EntityUtil.isntValid((Entity)player, range) || SpeedManager.getPlayerSpeed(player) > 10.0) continue;
    //            if (target == null) {
    //                target = player;
    //                distance = AntiBurrow2.mc.player.getDistanceSq((Entity)player);
    //                continue;
    //            }
    //            if (AntiBurrow2.mc.player.getDistanceSq((Entity)player) >= distance) continue;
    //            target = player;
    //            distance = AntiBurrow2.mc.player.getDistanceSq((Entity)player);
    //        }
    //        return target;
    //    }
    override fun onUpdate() {
        if (fullNullCheck()) {
            return
        }
        try {
            val player = getTarget(range.value)
        } catch (ignored: Exception) {
        }
        //
        if (mc.currentScreen is GuiHopper) {
            return
        }
        player = getTarget(range.value)
        if (player == null) {
            return
        }
        pos = BlockPos(player!!.posX, player!!.posY + 0.5, player!!.posZ)
        if (ticked >= 0) {
            ++ticked
        }
        if (InstantMine.breakPos != null && InstantMine.breakPos == pos as Any? && ticked >= 60 && mc.world.getBlockState(
                pos
            ).block !== Blocks.BEDROCK && mc.world.getBlockState(pos).block !== Blocks.AIR && mc.world.getBlockState(pos).block !== Blocks.WEB && mc.world.getBlockState(
                pos
            ).block !== Blocks.REDSTONE_WIRE && !isOnLiquid && !isInLiquid && mc.world.getBlockState(pos).block !== Blocks.WATER && mc.world.getBlockState(
                pos
            ).block !== Blocks.LAVA
        ) {
            mc.player.swingArm(EnumHand.MAIN_HAND)
            mc.playerController.onPlayerDamageBlock(pos, BlockUtil.getRayTraceFacing(pos))
            ticked = 1
        }
        if (InstantMine.breakPos2 != null && InstantMine.breakPos2 == pos as Any?) {
            return
        }
        if (InstantMine.breakPos != null) {
            if (InstantMine.breakPos == pos as Any?) {
                return
            }
            if (InstantMine.breakPos == BlockPos(mc.player.posX, mc.player.posY + 2.0, mc.player.posZ) as Any) {
                return
            }
            if (InstantMine.breakPos == BlockPos(mc.player.posX, mc.player.posY - 1.0, mc.player.posZ) as Any) {
                return
            }
            if (mc.world.getBlockState(InstantMine.breakPos).block === Blocks.WEB) {
                return
            }
        }
        if (mc.world.getBlockState(pos).block !== Blocks.AIR && mc.world.getBlockState(pos).block !== Blocks.BEDROCK && mc.world.getBlockState(
                pos
            ).block !== Blocks.WEB && mc.world.getBlockState(pos).block !== Blocks.REDSTONE_WIRE && !isOnLiquid && !isInLiquid && mc.world.getBlockState(
                pos
            ).block !== Blocks.WATER && mc.world.getBlockState(pos).block !== Blocks.LAVA
        ) {
            mc.player.swingArm(EnumHand.MAIN_HAND)
            mc.playerController.onPlayerDamageBlock(pos, BlockUtil.getRayTraceFacing(pos))
            ticked = 1
        }
    }

    val displayInfo: String?
        //    @Override
        get() =//        if (!HUD.getInstance().moduleInfo.getValue().booleanValue()) {
//            return null;
//        }
            if (player != null) {
                player!!.name
            } else null
    private val isOnLiquid: Boolean
        private get() {
            val y = mc.player.posY - 0.03
            for (x in MathHelper.floor(mc.player.posX) until MathHelper.ceil(mc.player.posX)) {
                for (z in MathHelper.floor(mc.player.posZ) until MathHelper.ceil(mc.player.posZ)) {
                    val pos = BlockPos(x, MathHelper.floor(y), z)
                    if (mc.world.getBlockState(pos).block !is BlockLiquid) continue
                    return true
                }
            }
            return false
        }
    private val isInLiquid: Boolean
        private get() {
            val y = mc.player.posY + 0.01
            for (x in MathHelper.floor(mc.player.posX) until MathHelper.ceil(mc.player.posX)) {
                for (z in MathHelper.floor(mc.player.posZ) until MathHelper.ceil(mc.player.posZ)) {
                    val pos = BlockPos(x, y.toInt(), z)
                    if (mc.world.getBlockState(pos).block !is BlockLiquid) continue
                    return true
                }
            }
            return false
        }

    companion object {
        var pos: BlockPos? = null
    }
}