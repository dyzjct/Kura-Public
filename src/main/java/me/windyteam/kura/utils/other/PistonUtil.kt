package me.windyteam.kura.utils.other

import kura.utils.isPlaceable
import me.windyteam.kura.module.Module
import me.windyteam.kura.module.modules.combat.HoleKickerRewrite
import me.windyteam.kura.utils.block.BlockInteractionHelper
import me.windyteam.kura.utils.block.BlockUtil
import me.windyteam.kura.utils.inventory.InventoryUtil
import me.windyteam.kura.utils.math.RotationUtil
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.entity.Entity
import net.minecraft.entity.item.EntityEnderCrystal
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.network.play.client.CPacketAnimation
import net.minecraft.network.play.client.CPacketHeldItemChange
import net.minecraft.network.play.client.CPacketPlayer
import net.minecraft.network.play.client.CPacketUseEntity
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.Vec3i
import java.util.stream.Collectors

val mc = Minecraft.getMinecraft()

fun breakList(list: MutableList<BlockPos>) {
    list.add(BlockPos(1, 2, 0))
    list.add(BlockPos(-1, 2, 0))
    list.add(BlockPos(0, 2, 1))
    list.add(BlockPos(0, 2, -1))
    list.add(BlockPos(1, 0, 0))
    list.add(BlockPos(-1, 0, 0))
    list.add(BlockPos(0, 0, 1))
    list.add(BlockPos(0, 0, -1))
}

fun pistonList(list: MutableList<BlockPos>) {
    list.add(BlockPos(1, 1, 0))
    list.add(BlockPos(-1, 1, 0))
    list.add(BlockPos(0, 1, 1))
    list.add(BlockPos(0, 1, -1))
}

fun checkList(list: MutableList<BlockPos>) {
    list.add(BlockPos(-1, 1, 0))
    list.add(BlockPos(1, 1, 0))
    list.add(BlockPos(0, 1, -1))
    list.add(BlockPos(0, 1, 1))
}

fun loading() {
    pistonList(HoleKickerRewrite.pistonList)
    checkList(HoleKickerRewrite.checkList)
    breakList(HoleKickerRewrite.breakList)
}

fun pistonCount(target: EntityPlayer?) {
    target?.let {
        if (HoleKickerRewrite.breakCrystal!!.value) {
            breakCrystal()
        }
        val playerPos = BlockPos(it.posX, it.posY, it.posZ)
        val b = Module.mc.player.inventory.currentItem

        var doRedStone = false
        var canPlacePiston = true
        for (i in 0..4) {
            if (Module.mc.player.posY < HoleKickerRewrite.target!!.posY) {
                if (HoleKickerRewrite.autoToggle!!.value) HoleKickerRewrite.disable()
                return
            }
            if (getBlock(BlockPos(playerPos.add(0, 2, 0)))!!.block != Blocks.AIR) continue
            if (!HoleKickerRewrite.autoPush!!.value) {
                if (getBlock(playerPos.add(HoleKickerRewrite.checkList[i]))!!.block != Blocks.AIR) continue
                if (getBlock(
                        playerPos.add(
                            HoleKickerRewrite.checkList[i].x, 2, HoleKickerRewrite.checkList[i].z
                        )
                    )!!.block != Blocks.AIR
                ) continue
            }
            if (getBlock(playerPos.add(HoleKickerRewrite.pistonList[i]))!!.block != Blocks.PISTON && getBlock(
                    playerPos.add(
                        HoleKickerRewrite.pistonList[i]
                    )
                )!!.block != Blocks.STICKY_PISTON && getBlock(playerPos.add(HoleKickerRewrite.pistonList[i]))!!.block != Blocks.AIR
            ) continue
            if (getBlock(
                    playerPos.add(
                        HoleKickerRewrite.checkList[i].x, 2, HoleKickerRewrite.checkList[i].z
                    )
                )!!.block != Blocks.REDSTONE_BLOCK && getBlock(
                    playerPos.add(
                        HoleKickerRewrite.checkList[i].x, 2, HoleKickerRewrite.checkList[i].z
                    )
                )!!.block != Blocks.AIR && getBlock(
                    playerPos.add(
                        HoleKickerRewrite.checkList[i].x, 0, HoleKickerRewrite.checkList[i].z
                    )
                )!!.block != Blocks.REDSTONE_BLOCK && getBlock(
                    playerPos.add(
                        HoleKickerRewrite.checkList[i].x, 0, HoleKickerRewrite.checkList[i].z
                    )
                )!!.block != Blocks.AIR
            ) continue
            if (mc.world.isPlaceable(
                    BlockPos(
                        playerPos.add(
                            HoleKickerRewrite.pistonList[i].x, 0, HoleKickerRewrite.pistonList[i].z
                        )
                    )
                ) && HoleKickerRewrite.timer.passedMs(
                    HoleKickerRewrite.delay!!.value.toLong()
                )
            ) {
                switchToSlot(InventoryUtil.findHotbarBlock(Blocks.REDSTONE_BLOCK))
                if (HoleKickerRewrite.xinBypass!!.value) {
                    rotationBlock(
                        playerPos.add(
                            HoleKickerRewrite.pistonList[i].x, 0, HoleKickerRewrite.pistonList[i].z
                        )
                    )
                } else {
                    placeBlock(
                        playerPos.add(
                            HoleKickerRewrite.pistonList[i].x, 0, HoleKickerRewrite.pistonList[i].z
                        )
                    )
                }
                doRedStone = true
            }

            val pistonSide = BlockUtil.getFirstFacing(
                playerPos.add(
                    HoleKickerRewrite.pistonList[i].x.toDouble(),
                    HoleKickerRewrite.pistonList[i].y.toDouble(),
                    HoleKickerRewrite.pistonList[i].z.toDouble()
                )
            )
            val pistonNeighbour: BlockPos = playerPos.add(HoleKickerRewrite.pistonList[i]).offset(pistonSide)
            val pistonOpposite = pistonSide.getOpposite()
            val pistonHitVec = Vec3d(pistonNeighbour as Vec3i).add(0.5, 0.5, 0.5)
                .add(Vec3d(pistonOpposite.getDirectionVec()).scale(0.5))
            if (HoleKickerRewrite.rotate!!.value) {
                RotationUtil.faceVector(pistonHitVec, true)
            }
            switchToSlot(b)
            if (InventoryUtil.findHotbarBlock(Blocks.STICKY_PISTON) != -1) {
                switchToSlot(InventoryUtil.findHotbarBlock(Blocks.STICKY_PISTON))
            } else if (InventoryUtil.findHotbarBlock(Blocks.PISTON) != -1) {
                switchToSlot(InventoryUtil.findHotbarBlock(Blocks.PISTON))
            }
            if (HoleKickerRewrite.timer.passedMs(HoleKickerRewrite.delay!!.value.toLong()) && canPlacePiston && mc.world.isPlaceable(
                    playerPos.add(
                        HoleKickerRewrite.pistonList[i]
                    )
                ) && Module.mc.player.inventory.currentItem == InventoryUtil.findHotbarBlock(
                    Blocks.PISTON
                ) || Module.mc.player.inventory.currentItem == InventoryUtil.findHotbarBlock(Blocks.STICKY_PISTON)
            ) {
                when (HoleKickerRewrite.pistonList[i]) {
                    HoleKickerRewrite.pistonList[0] -> {
                        Module.mc.player.connection.sendPacket(CPacketPlayer.Rotation(270.0f, 0f, true))
                    }

                    HoleKickerRewrite.pistonList[1] -> {
                        Module.mc.player.connection.sendPacket(CPacketPlayer.Rotation(90.0f, 0f, true))
                    }

                    HoleKickerRewrite.pistonList[2] -> {
                        Module.mc.player.connection.sendPacket(CPacketPlayer.Rotation(0.0f, 0f, true))
                    }

                    HoleKickerRewrite.pistonList[3] -> {
                        Module.mc.player.connection.sendPacket(CPacketPlayer.Rotation(180.0f, 0f, true))
                    }
                }
                placeBlock(playerPos.add(HoleKickerRewrite.pistonList[i]))
                canPlacePiston = false
            }

            switchToSlot(b)

            if (HoleKickerRewrite.timer.passedMs(HoleKickerRewrite.delay.value.toLong()) && mc.world.isPlaceable(
                    playerPos.add(
                        HoleKickerRewrite.pistonList[i].x, 2, HoleKickerRewrite.pistonList[i].z
                    )
                ) && !doRedStone
            ) {
                switchToSlot(InventoryUtil.findHotbarBlock(Blocks.REDSTONE_BLOCK))
                if (HoleKickerRewrite.xinBypass!!.value) {
                    rotationBlock(
                        playerPos.add(
                            HoleKickerRewrite.pistonList[i].x, 2, HoleKickerRewrite.pistonList[i].z
                        )
                    )
                } else {
                    placeBlock(
                        playerPos.add(
                            HoleKickerRewrite.pistonList[i].x, 2, HoleKickerRewrite.pistonList[i].z
                        )
                    )
                }
                switchToSlot(b)
            }
            switchToSlot(b)
            Module.mc.player.inventory.currentItem = b
            Module.mc.playerController.updateController()
            if (getBlock(
                    playerPos.add(
                        HoleKickerRewrite.pistonList[i].x,
                        HoleKickerRewrite.pistonList[i].y,
                        HoleKickerRewrite.pistonList[i].z
                    )
                )!!.block != Blocks.AIR
            ) {
                if (getBlock(
                        playerPos.add(
                            HoleKickerRewrite.pistonList[i].x,
                            HoleKickerRewrite.pistonList[i].y + 1,
                            HoleKickerRewrite.pistonList[i].z
                        )
                    )!!.block == Blocks.REDSTONE_BLOCK || getBlock(
                        playerPos.add(
                            HoleKickerRewrite.pistonList[i].x,
                            HoleKickerRewrite.pistonList[i].y - 1,
                            HoleKickerRewrite.pistonList[i].z
                        )
                    )!!.block == Blocks.REDSTONE_BLOCK
                ) {
                    if (HoleKickerRewrite.autoPush.value) {
                        doBreak()
                    }
                    if (HoleKickerRewrite.autoToggle!!.value) {
                        HoleKickerRewrite.disable()
                    }
                }
            }
        }
    }
}

fun placeBlock(pos: BlockPos) {
    BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, false, HoleKickerRewrite.packetPlace!!.value, false)
}

fun rotationBlock(pos: BlockPos) {
    BlockUtil.placeBlock(
        pos, EnumHand.MAIN_HAND, HoleKickerRewrite.rotate!!.value, HoleKickerRewrite.packetPlace!!.value, false
    )
}

fun autoClean() {
    HoleKickerRewrite.pistonList.clear()
    HoleKickerRewrite.checkList.clear()
    HoleKickerRewrite.breakList.clear()
}

fun doBreak() {
    val breakPos =
        BlockPos(HoleKickerRewrite.target!!.posX, HoleKickerRewrite.target!!.posY, HoleKickerRewrite.target!!.posZ)
    for (i in HoleKickerRewrite.breakList) {
        if (getBlock(breakPos.add(i))!!.block == Blocks.REDSTONE_BLOCK) {
            Module.mc.playerController.onPlayerDamageBlock(
                breakPos.add(i), BlockUtil.getRayTraceFacing(breakPos.add(i))
            )
        }
    }
}

fun breakCrystal() {
    for (crystal in Module.mc.world.loadedEntityList.stream()
        .filter { e: Entity -> e is EntityEnderCrystal && !e.isDead }
        .sorted(Comparator.comparing { e: Entity -> java.lang.Float.valueOf(Module.mc.player.getDistance(e)) }).collect(
            Collectors.toList()
        )) {
        if (crystal !is EntityEnderCrystal || Module.mc.player.getDistance(crystal) > 4.0f) continue
        if (HoleKickerRewrite.rotate!!.value) {
            Module.mc.player.connection.sendPacket(
                CPacketPlayer.Rotation(
                    BlockInteractionHelper.getLegitRotations(crystal.positionVector)[0],
                    BlockInteractionHelper.getLegitRotations(crystal.positionVector)[1],
                    true
                )
            )
        }
        Module.mc.player.connection.sendPacket(CPacketUseEntity(crystal))
        Module.mc.player.connection.sendPacket(CPacketAnimation(EnumHand.OFF_HAND))
    }
}

fun switchToSlot(slot: Int) {
    Module.mc.player.connection.sendPacket(CPacketHeldItemChange(slot))
    Module.mc.player.inventory.currentItem = slot
    Module.mc.playerController.updateController()
}

fun getBlock(block: BlockPos): IBlockState? {
    return Module.mc.world.getBlockState(block)
}