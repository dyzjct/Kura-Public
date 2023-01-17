package me.windyteam.kura.module.modules.combat

import com.mojang.realmsclient.gui.ChatFormatting
import me.windyteam.kura.event.events.entity.MotionUpdateEvent
import me.windyteam.kura.module.Category
import me.windyteam.kura.module.Module
import me.windyteam.kura.utils.entity.EntityUtil
import me.windyteam.kura.utils.inventory.InventoryUtil
import me.windyteam.kura.utils.mc.BlockUtil
import me.windyteam.kura.utils.mc.ChatUtil
import net.minecraft.entity.Entity
import net.minecraft.entity.item.EntityEnderCrystal
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.item.Item
import net.minecraft.item.ItemTool
import net.minecraft.network.Packet
import net.minecraft.network.play.client.CPacketAnimation
import net.minecraft.network.play.client.CPacketHeldItemChange
import net.minecraft.network.play.client.CPacketPlayer
import net.minecraft.network.play.client.CPacketUseEntity
import net.minecraft.potion.Potion
import net.minecraft.util.EnumHand
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.Vec3i
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@Module.Info(name = "Burrow2", category = Category.COMBAT, description = "Block lag lol")
class Burrow2 : Module() {
    private var isSneaking = false
    private val rotate = bsetting("Rotate", true)
    private val smartOffset = bsetting("SmartOffset", true)
    private val offsetX = dsetting("OffsetX", -7.0, -10.0, 10.0)
    private val offsetY = dsetting("OffsetY", -7.0, -10.0, 10.0)
    private val offsetZ = dsetting("OffsetZ", -7.0, -10.0, 10.0)
    private val breakCrystal = bsetting("BreakCrystal", true)
    private val antiWk = bsetting("AntiWeak", true).b(breakCrystal)
    private val multiPlace = bsetting("MultiPlace", false)
    private val mode = msetting("Mode", BlockMode.Obsidian)
    override fun onDisable() {
        isSneaking = EntityUtil.stopSneaking(isSneaking)
    }

    private fun breakcrystal() {
        val axisAlignedBB = AxisAlignedBB(BlockUtil.getFlooredPosition(mc.player as Entity))
        val l = mc.world.getEntitiesWithinAABBExcludingEntity(null as Entity?, axisAlignedBB) as List<Entity>
        for (entity in l) {
            if (entity is EntityEnderCrystal) {
                if (mc.player.isPotionActive(Potion.getPotionById(18)!!) && antiWk.value) {
                    val toolSlot = getSlotByDmg(4.0)
                    if (toolSlot != -1) {
                        val oldSlot = mc.player.inventory.currentItem
                        InventoryUtil.switchToHotbarSlot(toolSlot, false)
                        InventoryUtil.switchToHotbarSlot(oldSlot, false)
                    }
                }
                mc.player.connection.sendPacket(CPacketUseEntity(entity) as Packet<*>)
                mc.player.connection.sendPacket(CPacketAnimation(EnumHand.OFF_HAND) as Packet<*>)
            }
        }
    }

    @SubscribeEvent
    fun onTick(event: MotionUpdateEvent?) {
        isSneaking = EntityUtil.stopSneaking(isSneaking)
        if (breakCrystal.value) {
            this
            breakcrystal()
        }
        if (!mc.world.isBlockLoaded(mc.player.position)) {
            return
        }
        if (!mc.player.onGround || mc.world.getBlockState(
                BlockPos(
                    mc.player.posX,
                    mc.player.posY + 2.0,
                    mc.player.posZ
                )
            ).block !== Blocks.AIR
        ) {
            disable()
            return
        }
        if (mc.world.getBlockState(
                BlockPos(
                    mc.player.posX,
                    Math.round(mc.player.posY).toDouble(),
                    mc.player.posZ
                )
            ).block !== Blocks.AIR
        ) {
            disable()
            return
        }
        if (mode.value === BlockMode.Obsidian && InventoryUtil.getItemHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN)) == -1) {
            ChatUtil.sendMessage(ChatFormatting.RED.toString() + "Obsidian ?")
            disable()
            return
        }
        if (mode.value === BlockMode.Chest && InventoryUtil.getItemHotbar(Item.getItemFromBlock(Blocks.ENDER_CHEST)) == -1) {
            ChatUtil.sendMessage(ChatFormatting.RED.toString() + "Ender Chest ?")
            disable()
            return
        }
        if (mode.value === BlockMode.Smart && InventoryUtil.getItemHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN)) == -1 && InventoryUtil.getItemHotbar(
                Item.getItemFromBlock(Blocks.ENDER_CHEST)
            ) == -1
        ) {
            ChatUtil.sendMessage(ChatFormatting.RED.toString() + "Obsidian/Ender Chest ?")
            disable()
            return
        }
        mc.player.connection.sendPacket(
            CPacketPlayer.Position(
                Math.floor(mc.player.posX) + 0.5, mc.player.posY + 0.419999986886978, Math.floor(
                    mc.player.posZ
                ) + 0.5, false
            ) as Packet<*>
        )
        mc.player.connection.sendPacket(
            CPacketPlayer.Position(
                Math.floor(mc.player.posX) + 0.5, mc.player.posY + 0.7531999805212015, Math.floor(
                    mc.player.posZ
                ) + 0.5, false
            ) as Packet<*>
        )
        mc.player.connection.sendPacket(
            CPacketPlayer.Position(
                Math.floor(mc.player.posX) + 0.5, mc.player.posY + 1.001335979112147, Math.floor(
                    mc.player.posZ
                ) + 0.5, false
            ) as Packet<*>
        )
        mc.player.connection.sendPacket(
            CPacketPlayer.Position(
                Math.floor(mc.player.posX) + 0.5, mc.player.posY + 1.166109260938214, Math.floor(
                    mc.player.posZ
                ) + 0.5, false
            ) as Packet<*>
        )
        val a = mc.player.inventory.currentItem
        if (mode.value === BlockMode.Obsidian) {
            InventoryUtil.switchToHotbarSlot(InventoryUtil.getItemHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN)), false)
        }
        if (mode.value === BlockMode.Chest) {
            InventoryUtil.switchToHotbarSlot(
                InventoryUtil.getItemHotbar(Item.getItemFromBlock(Blocks.ENDER_CHEST)),
                false
            )
        }
        if (mode.value === BlockMode.Smart) {
            if (InventoryUtil.getItemHotbar(Item.getItemFromBlock(Blocks.ENDER_CHEST)) != -1) {
                InventoryUtil.switchToHotbarSlot(
                    InventoryUtil.getItemHotbar(Item.getItemFromBlock(Blocks.ENDER_CHEST)),
                    false
                )
            } else if (InventoryUtil.getItemHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN)) != -1) {
                InventoryUtil.switchToHotbarSlot(
                    InventoryUtil.getItemHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN)),
                    false
                )
            }
        }
        if (!multiPlace.value) {
            isSneaking = BlockUtil.placeBlock(
                BlockPos(getPlayerPosFixY(mc.player as EntityPlayer) as Vec3i),
                EnumHand.MAIN_HAND,
                rotate.value,
                true,
                isSneaking
            )
        } else {
            val baseVec = Vec3d(mc.player.posX, mc.player.posY, mc.player.posZ)
            BlockUtil.placeBlock(baseVec.add(0.3, 0.0, 0.3), EnumHand.MAIN_HAND, false, true)
            BlockUtil.placeBlock(baseVec.add(-0.3, 0.0, 0.3), EnumHand.MAIN_HAND, false, true)
            BlockUtil.placeBlock(baseVec.add(0.3, 0.0, -0.3), EnumHand.MAIN_HAND, false, true)
            BlockUtil.placeBlock(baseVec.add(-0.3, 0.0, -0.3), EnumHand.MAIN_HAND, false, true)
        }
        mc.playerController.updateController()
        mc.player.connection.sendPacket(CPacketHeldItemChange(a) as Packet<*>)
        mc.player.inventory.currentItem = a
        mc.playerController.updateController()
        if (smartOffset.value) {
            var defaultOffset = true
            if (mc.player.posY >= 3.0) {
                var i = -10
                while (i < 10) {
                    if (i == -1) {
                        i = 3
                    }
                    if (mc.world.getBlockState(
                            BlockUtil.getFlooredPosition(mc.player as Entity).add(0, i, 0)
                        ).block == Blocks.AIR && mc.world.getBlockState(
                            BlockUtil.getFlooredPosition(mc.player as Entity).add(0, i + 1, 0)
                        ).block == Blocks.AIR
                    ) {
                        val pos = BlockUtil.getFlooredPosition(mc.player as Entity).add(0, i, 0)
                        mc.player.connection.sendPacket(
                            CPacketPlayer.Position(
                                pos.getX() + 0.3,
                                pos.getY().toDouble(),
                                pos.getZ() + 0.3,
                                true
                            ) as Packet<*>
                        )
                        defaultOffset = false
                        break
                    }
                    ++i
                }
            }
            if (defaultOffset) {
                mc.player.connection.sendPacket(
                    CPacketPlayer.Position(
                        mc.player.posX + offsetX.value,
                        mc.player.posY + offsetY.value,
                        mc.player.posZ + offsetZ.value,
                        true
                    ) as Packet<*>
                )
            }
        } else {
            mc.player.connection.sendPacket(
                CPacketPlayer.Position(
                    mc.player.posX + offsetX.value,
                    mc.player.posY + offsetY.value,
                    mc.player.posZ + offsetZ.value,
                    true
                ) as Packet<*>
            )
        }
        disable()
    }

    internal enum class BlockMode {
        Obsidian, Chest, Smart
    }

    companion object {
        fun getSlotByDmg(minDmg: Double): Int {
            for (i in 0..8) {
                if (mc.player.inventory.getStackInSlot(i).getItem() is ItemTool) {
                    val currItemTool = mc.player.inventory.getStackInSlot(i).getItem() as ItemTool
                    if (currItemTool.attackDamage >= minDmg) {
                        return i
                    }
                }
            }
            return -1
        }

        fun getPlayerPosFixY(player: EntityPlayer): BlockPos {
            return BlockPos(Math.floor(player.posX), Math.round(player.posY).toDouble(), Math.floor(player.posZ))
        }
    }
}