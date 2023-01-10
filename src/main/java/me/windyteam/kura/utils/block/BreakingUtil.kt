package me.windyteam.kura.utils.block

import me.windyteam.kura.utils.HotbarSlot
import me.windyteam.kura.utils.animations.fastCeil
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Enchantments
import net.minecraft.init.Items
import net.minecraft.item.*
import net.minecraft.util.math.BlockPos

open class BreakingUtil {

    companion object {
        var maxTime = 0f
        var mc: Minecraft = Minecraft.getMinecraft()

        @JvmStatic
        fun calcBreakTime(player: Int, pos: BlockPos): Float {
            val blockState = mc.world.getBlockState(pos)

            val hardness = blockState.getBlockHardness(mc.world, pos)
            var p: EntityPlayer? = null
            if (mc.world.getEntityByID(player) is EntityPlayer) {
                p = mc.world.getEntityByID(player) as EntityPlayer
            }
            if (p != null) {
                val breakSpeed = if (p != mc.player) getBreakSpeedObi(p, blockState) else getBreakSpeed(p, blockState) //getBreakSpeed(p, blockState)
                if (breakSpeed == -1.0f) {
                    return -1f
                }
                val relativeDamage = breakSpeed / hardness / 30.0f
                val ticks = (0.7f / relativeDamage).fastCeil()
                maxTime = ticks * 50f
                return ticks * 50f
            }
            return 0f
        }

        @JvmStatic
        fun calcBreakTime(player: EntityPlayer, pos: BlockPos): Float {
            val blockState = mc.world.getBlockState(pos)

            val hardness = blockState.getBlockHardness(mc.world, pos)
            val breakSpeed = if (player != mc.player) getBreakSpeedObi(player, blockState) else getBreakSpeed(player, blockState)
            if (breakSpeed == -1.0f) {
                return -1f
            }

            val relativeDamage = breakSpeed / hardness / 30.0f
            val ticks = (0.7f / relativeDamage).fastCeil()
            maxTime = ticks * 50f
            return ticks * 50f
        }

        @JvmStatic
        inline val EntityPlayer.hotbarSlots: List<HotbarSlot>
            get() = ArrayList<HotbarSlot>().apply {
                for (slot in 36..44) {
                    add(HotbarSlot(inventoryContainer.inventorySlots[slot]))
                }
            }

        @JvmStatic
        fun getBreakSpeed(player: EntityPlayer, blockState: IBlockState): Float {
            var maxSpeed = 1.0f
            for (slot in player.hotbarSlots) {
                val stack = slot.stack

                if (stack.isEmpty || !stack.item.isTool) {
                    continue
                } else {
                    var speed = stack.getDestroySpeed(blockState)

                    if (speed <= 1.0f) {
                        continue
                    } else {
                        val efficiency = EnchantmentHelper.getEnchantmentLevel(Enchantments.EFFICIENCY, stack)
                        if (efficiency > 0) {
                            speed += efficiency * efficiency + 1.0f
                        }
                    }

                    if (speed > maxSpeed) {
                        maxSpeed = speed
                    }
                }
            }

            return maxSpeed
        }
        val Item.isTool: Boolean
            get() = this is ItemTool
                    || this is ItemSword
                    || this is ItemHoe
                    || this is ItemShears
        @JvmStatic
        fun getBreakSpeedObi(player: EntityPlayer, blockState: IBlockState): Float {
            var maxSpeed = 1.0f
            var speed = ItemStack(Items.DIAMOND_PICKAXE).getDestroySpeed(blockState)
            speed += 5 * 5 + 1.0f
            if (speed > maxSpeed) {
                maxSpeed = speed
            }
            return maxSpeed
        }
    }
}