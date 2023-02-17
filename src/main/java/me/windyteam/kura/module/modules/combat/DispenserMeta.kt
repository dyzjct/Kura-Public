package me.windyteam.kura.module.modules.combat

import kura.utils.Wrapper
import me.windyteam.kura.event.events.entity.MotionUpdateEvent
import me.windyteam.kura.module.Category
import me.windyteam.kura.module.Module
import me.windyteam.kura.module.modules.crystalaura.AutoCrystal
import me.windyteam.kura.utils.block.BlockInteractionHelper
import me.windyteam.kura.utils.fuck.vector.VectorUtils.toVec3d
import me.windyteam.kura.utils.inventory.InventoryUtil
import me.windyteam.kura.utils.math.RotationUtil
import me.windyteam.kura.utils.mc.ChatUtil
import net.minecraft.block.BlockAir
import net.minecraft.block.BlockLiquid
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.init.Blocks
import net.minecraft.inventory.ClickType
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.network.play.client.CPacketAnimation
import net.minecraft.network.play.client.CPacketEntityAction
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

/**
 * Created by hub on 10/12/2019
 * Updated by zenhao on 2/2/2023
 */
@Module.Info(
    name = "DispenserMeta",
    category = Category.COMBAT,
    description = "Do not use with any AntiGhostBlock Mod!"
)
object DispenserMeta : Module() {
    private val rotate = bsetting("Rotate", false)
    private val placeStage = isetting("PlaceStage", 0, 0, 1)
    private val faceStage = isetting("FaceStage", 0, 0, 1)
    private val offsetFacing =
        arrayOf(EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.WEST, EnumFacing.EAST, EnumFacing.UP)
    private var stage = 0
    private var placeTarget: BlockPos? = null
    private var shulkerSlot = -1
    override fun onDisable() {
        if (fullNullCheck()) {
            return
        }
        mc.player.isSneaking = false
    }

    override fun onEnable() {
        if (fullNullCheck()) {
            disable()
            return
        }
        stage = 0
        placeTarget = null
        shulkerSlot = -1
        for (i in 0..8) {
            val stack = mc.player.inventory.getStackInSlot(i)
            if (stack == ItemStack.EMPTY || stack.getItem() !is ItemBlock) continue
            val block = (stack.getItem() as ItemBlock).block
            if (BlockInteractionHelper.shulkerList.contains(block)) {
                shulkerSlot = i
                break
            }
        }
        if (shulkerSlot < 0) {
            ChatUtil.sendMessage("No Shulker Found!")
            disable()
        }
        if (mc.objectMouseOver == null) {
            ChatUtil.NoSpam.sendMessage("[Dispenser32k] Not a valid place target, disabling.")
            disable()
            return
        }
        placeTarget = mc.objectMouseOver.blockPos.up()
        stage = 0
    }

    @SubscribeEvent
    fun onTick(event : MotionUpdateEvent.FastTick) {
        val obiSlot = InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN)
        val dispenserSlot = InventoryUtil.findHotbarBlock(Blocks.DISPENSER)
        val redstoneSlot = InventoryUtil.findHotbarBlock(Blocks.REDSTONE_BLOCK)
        val hopperSlot = InventoryUtil.findHotbarBlock(Blocks.HOPPER)
        if (obiSlot == -1 || dispenserSlot == -1 || redstoneSlot == -1 || hopperSlot == -1) {
            disable()
            return
        }
        if (placeTarget == null) {
            ChatUtil.sendMessage("PlacePos Not Found!")
            disable()
            return
        }
        when (stage) {
            0 -> {
                placeBlock(obiSlot, placeTarget!!)
                placeBlock(dispenserSlot, placeTarget!!.add(0, 1, 0))
                mc.player.connection.sendPacket(CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING))
                mc.player.connection.sendPacket(
                    fastPosDirectionDown(
                        placeTarget!!.add(0, 1, 0),
                        EnumHand.MAIN_HAND,
                        0f,
                        0f,
                        0f
                    )
                )
                stage = 1
                return
            }

            1 -> {
                if (mc.currentScreen !is GuiContainer) {
                    return
                }
                mc.playerController.windowClick(mc.player.openContainer.windowId, 1, shulkerSlot, ClickType.SWAP, mc.player)
                mc.player.closeScreen()
                stage = 2
                return
            }

            2 -> {
                for (facing in offsetFacing) {
                    val placePos = placeTarget!!.up().offset(facing)
                    if (!mc.world.noCollision(placePos)
                        || !mc.world.isAirBlock(placePos)
                        || placePos == placeTarget!!.up().offset(mc.player.horizontalFacing.getOpposite())
                        || placePos.getY() < placeTarget!!.getY()
                    ) continue
                    placeBlock(
                        redstoneSlot,
                        placeTarget!!.up(),
                        if (facing == EnumFacing.UP) EnumFacing.DOWN else facing
                    )
                    break
                }
                stage = 3
                return
            }

            3 -> {
                val block =
                    mc.world.getBlockState(placeTarget!!.offset(mc.player.horizontalFacing.getOpposite()).up()).block
                if (block is BlockAir || block is BlockLiquid) {
                    return
                }
                placeBlock(
                    hopperSlot,
                    placeTarget!!,
                    mc.player.horizontalFacing.getOpposite()
                )
                mc.connection!!.sendPacket(
                    CPacketEntityAction(
                        mc.player,
                        CPacketEntityAction.Action.STOP_SNEAKING
                    )
                )
                mc.player.connection.sendPacket(
                    fastPosDirectionDown(
                        placeTarget!!.offset(mc.player.horizontalFacing.getOpposite()),
                        EnumHand.MAIN_HAND,
                        0f,
                        0f,
                        0f
                    )
                )
                stage = 4
                return
            }

            4 -> {
                if (mc.currentScreen !is GuiContainer) {
                    return
                }
                if ((mc.currentScreen as GuiContainer?)!!.inventorySlots.getSlot(0).stack.isEmpty) {
                    return
                }
                mc.playerController.windowClick(
                    mc.player.openContainer.windowId,
                    0,
                    shulkerSlot,
                    ClickType.SWAP,
                    mc.player
                )
                disable()
            }
        }
    }

    private fun placeBlock(slot: Int, pos: BlockPos, facing: EnumFacing = EnumFacing.DOWN) {
        val neighbour = when (placeStage.value) {
            0 -> pos.offset(facing)
            1 -> if (facing == EnumFacing.DOWN) pos.offset(facing) else pos
            else -> pos.offset(facing)
        }
        val opposite = when (faceStage.value) {
            0 -> facing.getOpposite()
            1 -> if (facing == EnumFacing.DOWN) facing.getOpposite() else facing
            else -> facing.getOpposite()
        }
        if (rotate.value) {
            val rotations = BlockInteractionHelper.getLegitRotations(
                BlockPos(neighbour.down()).add(
                    0.0, 0.0, 0.0
                )
            )
            RotationUtil.setPlayerRotations(rotations[0],rotations[1])
        }


        if (!mc.world.noCollision(neighbour)) {
            disable()
            return
        }

        val sneak = !Wrapper.player!!.isSneaking
        if (sneak) mc.connection!!.sendPacket(CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING))

        mc.player.inventory.currentItem = slot
        mc.playerController.processRightClickBlock(
            mc.player,
            mc.world,
            neighbour,
            opposite,
            Vec3d(neighbour),
            EnumHand.MAIN_HAND
        )

        mc.connection!!.sendPacket(CPacketAnimation(EnumHand.MAIN_HAND))

        if (sneak) mc.connection!!.sendPacket(CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING))
    }

    fun fastPosDirectionDown(
        pos: BlockPos,
        hand: EnumHand = EnumHand.MAIN_HAND,
        offsetX: Float = 0.5f,
        offsetY: Float = 1f,
        offsetZ: Float = 0.5f,
    ): CPacketPlayerTryUseItemOnBlock {
        return CPacketPlayerTryUseItemOnBlock(pos, EnumFacing.DOWN, hand, offsetX, offsetY, offsetZ)
    }

    fun World.noCollision(pos: BlockPos) = this.checkNoEntityCollision(AxisAlignedBB(pos), mc.player)

    inline fun EntityPlayerSP.spoofSneak(block: () -> Unit) {
//        contract {
//            callsInPlace(block, InvocationKind.EXACTLY_ONCE)
//        }

        if (!this.isSneaking) {
            connection.sendPacket(CPacketEntityAction(this, CPacketEntityAction.Action.START_SNEAKING))
            block.invoke()
            connection.sendPacket(CPacketEntityAction(this, CPacketEntityAction.Action.STOP_SNEAKING))
        } else {
            block.invoke()
        }
    }
}