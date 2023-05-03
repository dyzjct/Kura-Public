package me.windyteam.kura.utils.other

import kura.utils.isPlaceable
import me.windyteam.kura.module.modules.combat.HoleKicker
import me.windyteam.kura.module.modules.combat.PistonAura
import me.windyteam.kura.utils.block.BlockInteractionHelper
import me.windyteam.kura.utils.block.BlockUtil
import me.windyteam.kura.utils.entity.EntityUtil
import me.windyteam.kura.utils.inventory.InventoryUtil
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.entity.Entity
import net.minecraft.entity.item.EntityEnderCrystal
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.network.play.client.CPacketHeldItemChange
import net.minecraft.network.play.client.CPacketPlayer
import net.minecraft.util.EnumHand
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d

private val mc = Minecraft.getMinecraft()

fun canPush(player: EntityPlayer): Boolean {
    var progress = 0
    if (!mc.world.isAirBlock(BlockPos(player.posX, player.posY + 2.5, player.posZ))) {
        return false
    }
    if (!mc.world.isAirBlock(BlockPos(player.posX, player.posY + 0.5, player.posZ))) {
        return true
    }
    if (!mc.world.isAirBlock(BlockPos(player.posX + 1.0, player.posY+1, player.posZ)) || !mc.world.isAirBlock(BlockPos(player.posX + 1.0, player.posY+2, player.posZ))) {
        progress++
    }

    if (!mc.world.isAirBlock(BlockPos(player.posX - 1.0, player.posY+1, player.posZ)) || !mc.world.isAirBlock(BlockPos(player.posX - 1.0, player.posY+2, player.posZ))) {
        progress++
    }

    if (!mc.world.isAirBlock(BlockPos(player.posX, player.posY+1, player.posZ + 1.0)) || !mc.world.isAirBlock(BlockPos(player.posX, player.posY+2, player.posZ + 1.0))) {
        progress++
    }

    if (!mc.world.isAirBlock(BlockPos(player.posX, player.posY+1, player.posZ - 1.0)) || !mc.world.isAirBlock(BlockPos(player.posX, player.posY+2, player.posZ - 1.0))) {
        progress++
    }

    return progress > 1
}

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

fun auraList(list: MutableList<BlockPos>) {
    list.add(BlockPos(1, 1, 1))
    list.add(BlockPos(-1, 1, 1))
    list.add(BlockPos(-1, 1, -1))
    list.add(BlockPos(1, 1, -1))
}

fun checkList(list: MutableList<BlockPos>) {
    list.add(BlockPos(-1, 1, 0))
    list.add(BlockPos(1, 1, 0))
    list.add(BlockPos(0, 1, -1))
    list.add(BlockPos(0, 1, 1))
}

fun placeBlock(pos: BlockPos) {
    if (HoleKicker.breakCrystal!!.value) {
        breakCrystal(pos)
    }
    BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, false, HoleKicker.packetPlace!!.value, false)
    HoleKicker.timer.reset()
}

fun rotationBlock(pos: BlockPos) {
    if (HoleKicker.breakCrystal!!.value) {
        breakCrystal(pos)
    }
    BlockUtil.placeBlock(
        pos, EnumHand.MAIN_HAND, HoleKicker.rotate!!.value, HoleKicker.packetPlace!!.value, false
    )
    HoleKicker.timer.reset()
}

fun redStoneCheck(pos: BlockPos): Boolean {
    if (HoleKicker.breakCrystal!!.value) {
        breakCrystal(pos)
    }
    return !mc.world.isPlaceable(pos) && getBlock(pos)!!.block != Blocks.REDSTONE_BLOCK
}

fun pistonCheck(pos: BlockPos): Boolean {
    if (HoleKicker.breakCrystal!!.value) {
        breakCrystal(pos)
    }
    return !mc.world.isPlaceable(pos) && getBlock(pos)!!.block != Blocks.PISTON && getBlock(pos)!!.block != Blocks.STICKY_PISTON
}

fun isPiston(pos: BlockPos): Boolean {
    return getBlock(pos)!!.block == Blocks.STICKY_PISTON || getBlock(pos)!!.block == Blocks.PISTON
}

fun isRedStone(pos: BlockPos): Boolean {
    return getBlock(pos)!!.block == Blocks.REDSTONE_BLOCK
}

fun loading() {
    pistonList(HoleKicker.pistonList)
    checkList(HoleKicker.checkList)
    breakList(HoleKicker.breakList)
}

fun autoClean() {
    HoleKicker.pistonList.clear()
    HoleKicker.checkList.clear()
    HoleKicker.breakList.clear()
}

fun placeCrystal(pos: BlockPos){
    if (InventoryUtil.findHotbarItem(Items.END_CRYSTAL) == -1) return
    mc.player.connection.sendPacket(CPacketPlayer.Rotation(
        BlockInteractionHelper.getLegitRotations(pos.add(0.5,0.5,0.5))[0],
        BlockInteractionHelper.getLegitRotations(pos.add(0.5,0.5,0.5))[1],
        true
    ))
    val old = mc.player.inventory.currentItem
    InventoryUtil.switchToHotbarSlot(InventoryUtil.findHotbarItem(Items.END_CRYSTAL),true)
    BlockUtil.placeCrystalOnBlock(pos,EnumHand.MAIN_HAND)
    InventoryUtil.switchToHotbarSlot(old,true)
}

fun auraLoad() {
    auraList(PistonAura.pistonList)
}

fun auraClear() {
    PistonAura.pistonList.clear()
}

fun doPull() {
    val breakPos =
        BlockPos(HoleKicker.target!!.posX, HoleKicker.target!!.posY, HoleKicker.target!!.posZ)
    for (i in HoleKicker.breakList) {
        if (getBlock(breakPos.add(i))!!.block == Blocks.REDSTONE_BLOCK) {
            mc.connection!!.sendPacket(CPacketPlayer.Rotation( BlockInteractionHelper.getLegitRotations(breakPos.add(i).add(0.5,0.5,0.5))[0],
                BlockInteractionHelper.getLegitRotations(breakPos.add(i).add(0.5,0.5,0.5))[1],true))
            mc.playerController.onPlayerDamageBlock(
                breakPos.add(i), BlockUtil.getRayTraceFacing(breakPos.add(i))
            )
        }
    }
}

fun breakRedStone(pos: BlockPos) {
    mc.connection!!.sendPacket(CPacketPlayer.Rotation(BlockInteractionHelper.getLegitRotations(pos.add(0.5,0.5,0.5))[0],
        BlockInteractionHelper.getLegitRotations(pos.add(0.5,0.5,0.5))[1],true))
    mc.playerController.onPlayerDamageBlock(
        pos , BlockUtil.getRayTraceFacing(pos)
    )
}

fun breakCrystal(pos: BlockPos) {
    val a: Vec3d = mc.player.positionVector
    if (checkCrystal(a, EntityUtil.getVarOffsets(pos.x, pos.y, pos.z)) != null && HoleKicker.crystalTimer.passedMs(
            HoleKicker.crystalDelay!!.value.toLong()
        )
    ) {
        mc.player.connection.sendPacket(
            CPacketPlayer.Rotation(
                BlockInteractionHelper.getLegitRotations(pos.add(0.5, 0.5, 0.5))[0],
                BlockInteractionHelper.getLegitRotations(pos.add(0.5, 0.5, 0.5))[1], true
            )
        )
        EntityUtil.attackEntity(checkCrystal(a, EntityUtil.getVarOffsets(pos.x, pos.y, pos.z)), true)
        HoleKicker.crystalTimer.reset()
    }
}

fun hitCrystal(pos: BlockPos) {
    val a: Vec3d = mc.player.positionVector
    mc.player.connection.sendPacket(
        CPacketPlayer.Rotation(
            BlockInteractionHelper.getLegitRotations(pos.add(0.5, 0.5, 0.5))[0],
            BlockInteractionHelper.getLegitRotations(pos.add(0.5, 0.5, 0.5))[1], true
        )
    )
    EntityUtil.attackEntity(checkCrystal(a, EntityUtil.getVarOffsets(pos.x, pos.y, pos.z)), true)
}

fun checkCrystal(pos: Vec3d, list: Array<Vec3d>): Entity? {
    var crystal: Entity? = null
    val var5 = list.size
    for (var6 in 0 until var5) {
        val vec3d = list[var6]
        val position = BlockPos(pos).add(vec3d.x, vec3d.y, vec3d.z)
        for (entity in mc.world.getEntitiesWithinAABB(
            Entity::class.java, AxisAlignedBB(position)
        )) {
            if (entity !is EntityEnderCrystal || crystal != null) continue
            crystal = entity
        }
    }
    return crystal
}

fun switchToSlot(slot: Int) {
    mc.player.connection.sendPacket(CPacketHeldItemChange(slot))
    mc.player.inventory.currentItem = slot
    mc.playerController.updateController()
}

fun getBlock(block: BlockPos): IBlockState? {
    return mc.world.getBlockState(block)
}