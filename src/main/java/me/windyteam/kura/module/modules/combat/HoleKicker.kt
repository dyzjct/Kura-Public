package me.windyteam.kura.module.modules.combat

import me.windyteam.kura.module.Category
import me.windyteam.kura.module.Module
import me.windyteam.kura.utils.block.BlockUtil2
import me.windyteam.kura.utils.entity.EntityUtil
import me.windyteam.kura.utils.inventory.InventoryUtil
import me.windyteam.kura.utils.player.getTarget
import net.minecraft.block.BlockAir
import net.minecraft.block.BlockFire
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.Entity
import net.minecraft.entity.item.EntityEnderCrystal
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.network.Packet
import net.minecraft.network.play.client.CPacketAnimation
import net.minecraft.network.play.client.CPacketHeldItemChange
import net.minecraft.network.play.client.CPacketPlayer
import net.minecraft.network.play.client.CPacketUseEntity
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import java.util.stream.Collectors


/**
 * created by chunfeng666 on 2022-10-05
 * update by dyzjct on 2022-12-17
 */
@Module.Info(name = "HoleKicker", category = Category.COMBAT)
class HoleKicker : Module() {
    private val range = isetting("Range", 5, 1, 16)
    private val holepull = bsetting("HolePull", true)
    private val obihelper = bsetting("ObiHelper", true)
    private val breakcrystal = bsetting("BreakCrystal", false)
    private val placeMod = msetting("PlaceMod", PlaceMods.Piston)
    private val packetPlace = bsetting("PacketPlace", false)
    private var redstonemod: Boolean = false
    private var canpull: String = "null"

    var target: EntityPlayer? = null


    private var obsidian = -1

    private var obix: String = "null"
    private var obiz: String = "null"
    private var obix1: String = "null"
    private var obiz1: String = "null"
    private var filler = false
    private var isSneaking = false
    override fun onEnable() {
        if (placeMod.value!! == PlaceMods.Piston) {
            if (InventoryUtil.findHotbarBlock(Blocks.REDSTONE_BLOCK) == -1) {
                disable()
                return
            }
            if (InventoryUtil.findHotbarBlock(Blocks.PISTON) == -1) {
                disable()
                return
            }
        }
        if (placeMod.value!! == PlaceMods.SPiston) {
            if (InventoryUtil.findHotbarBlock(Blocks.REDSTONE_BLOCK) == -1) {
                disable()
                return
            }
            if (InventoryUtil.findHotbarBlock(Blocks.STICKY_PISTON) == -1) {
                disable()
                return
            }
        }
        if (fullNullCheck()) return
        EntityUtil.getRoundedBlockPos(mc.player as Entity)
        doPiston()
    }

    override fun onDisable() {
        isPlacing = false
        redstonemod = false
        isSneaking = EntityUtil.stopSneaking(isSneaking)
        canpull = "null"
        obix = "null"
        obiz = "null"
        obix1 = "null"
        obiz1 = "null"
    }


    private fun doPiston() {
        if (fullNullCheck()) {
            return
        }
        target = getTarget(range.value)
        if (target == null) return
        if (breakcrystal.value) {
            breakcrystal()
        }
        if (obihelper.value) {
            getobipos()
            placeobi()
        }
        doPistonTrap()
        breakredstone()
    }

    private fun doPistonTrap() {
        if (fullNullCheck()) {
            return
        }
        val b = mc.player.inventory.currentItem
        val c = target!!.positionVector
//            Mode One
//            +x
        if (checkList(c, EntityUtil.getVarOffsets(0, 1, 0)) && checkList(
                c, EntityUtil.getVarOffsets(0, 2, 0)
            )
        ) if (checkList(c, EntityUtil.getVarOffsets(1, 2, 0)) && checkList(
                c, EntityUtil.getVarOffsets(1, 1, 0)
            ) && checkList(c, EntityUtil.getVarOffsets(0, 2, 0))
        ) {
            if (placeMod.value!! == PlaceMods.Piston) {
                mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.PISTON)
            } else {
                mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.STICKY_PISTON)
            }
//            obix="place"
            redstonemod = true
            mc.playerController.updateController()
            mc.player.connection.sendPacket(CPacketPlayer.Rotation(270.0f, 0f, true) as Packet<*>)
            placeList(c, EntityUtil.getVarOffsets(1, 1, 0))
            mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.REDSTONE_BLOCK)
            mc.playerController.updateController() //Summer
            placeList(c, EntityUtil.getVarOffsets(1, 2, 0))
            if (!checkList(c, EntityUtil.getVarOffsets(-1, 1, 0))) {
                canpull = "pull"
            }
            if (!checkList(c, EntityUtil.getVarOffsets(-1, 2, 0))) {
                canpull = "pull"
            }
        }
//            -z
        else if (checkList(c, EntityUtil.getVarOffsets(0, 2, -1)) && checkList(
                c, EntityUtil.getVarOffsets(0, 1, -1)
            ) && checkList(c, EntityUtil.getVarOffsets(0, 2, 0))
        ) {
            if (placeMod.value!! == PlaceMods.Piston) {
                mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.PISTON)
            } else {
                mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.STICKY_PISTON)
            }
//            obiz1="place"
            redstonemod = true
            mc.playerController.updateController()
            mc.player.connection.sendPacket(CPacketPlayer.Rotation(180.0f, 0f, true) as Packet<*>)
            placeList(c, EntityUtil.getVarOffsets(0, 1, -1))
            mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.REDSTONE_BLOCK)
            mc.playerController.updateController() //snowy day
            placeList(c, EntityUtil.getVarOffsets(0, 2, -1))
            if (!checkList(c, EntityUtil.getVarOffsets(0, 1, 1))) {
                canpull = "pull"
            }
            if (!checkList(c, EntityUtil.getVarOffsets(0, 2, 1))) {
                canpull = "pull"
            }
        }
//            +z
        else if (checkList(c, EntityUtil.getVarOffsets(0, 2, 1)) && checkList(
                c, EntityUtil.getVarOffsets(0, 1, 1)
            ) && checkList(c, EntityUtil.getVarOffsets(0, 2, 0))
        ) {
            if (placeMod.value!! == PlaceMods.Piston) {
                mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.PISTON)
            } else {
                mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.STICKY_PISTON)
            }
//            obiz="place"
            redstonemod = true
            mc.playerController.updateController()
            mc.player.connection.sendPacket(CPacketPlayer.Rotation(0.0f, 0f, true) as Packet<*>)
            placeList(c, EntityUtil.getVarOffsets(0, 1, 1))
            mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.REDSTONE_BLOCK)
            mc.playerController.updateController() //snowy day
            placeList(c, EntityUtil.getVarOffsets(0, 2, 1))
            if (!checkList(c, EntityUtil.getVarOffsets(0, 1, -1))) {
                canpull = "pull"
            }
            if (!checkList(c, EntityUtil.getVarOffsets(0, 2, -1))) {
                canpull = "pull"
            }
        }
//            -x
        else if (checkList(c, EntityUtil.getVarOffsets(-1, 2, 0)) && checkList(
                c, EntityUtil.getVarOffsets(-1, 1, 0)
            ) && checkList(c, EntityUtil.getVarOffsets(0, 2, 0))
        ) {
            if (placeMod.value!! == PlaceMods.Piston) {
                mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.PISTON)
            } else {
                mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.STICKY_PISTON)
            }
//            obix1="place"
            redstonemod = true
            mc.playerController.updateController()
            mc.player.connection.sendPacket(CPacketPlayer.Rotation(90.0f, 0f, true) as Packet<*>)
            placeList(c, EntityUtil.getVarOffsets(-1, 1, 0))
            mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.REDSTONE_BLOCK)
            mc.playerController.updateController() //Summer
            placeList(c, EntityUtil.getVarOffsets(-1, 2, 0))
            if (!checkList(c, EntityUtil.getVarOffsets(1, 1, 0))) {
                canpull = "pull"
            }
            if (!checkList(c, EntityUtil.getVarOffsets(1, 2, 0))) {
                canpull = "pull"
            }
        }
//            Mod Two
//            +x
        else if (checkList(c, EntityUtil.getVarOffsets(2, 1, 0)) && checkList(
                c, EntityUtil.getVarOffsets(1, 1, 0)
            ) && checkList(c, EntityUtil.getVarOffsets(0, 2, 0))
        ) {
            if (placeMod.value!! == PlaceMods.Piston) {
                mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.PISTON)
            } else {
                mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.STICKY_PISTON)
            }
//            obix="place"
            redstonemod = true
            mc.playerController.updateController()
            mc.player.connection.sendPacket(CPacketPlayer.Rotation(270.0f, 0f, true) as Packet<*>)
            placeList(c, EntityUtil.getVarOffsets(1, 1, 0))
            mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.REDSTONE_BLOCK)
            mc.playerController.updateController() //Summer
            placeList(c, EntityUtil.getVarOffsets(2, 1, 0))
            if (!checkList(c, EntityUtil.getVarOffsets(-1, 1, 0))) {
                canpull = "pull"
            }
            if (!checkList(c, EntityUtil.getVarOffsets(-1, 2, 0))) {
                canpull = "pull"
            }
        }
//            -z
        else if (checkList(c, EntityUtil.getVarOffsets(0, 1, -2)) && checkList(
                c, EntityUtil.getVarOffsets(0, 1, -1)
            ) && checkList(c, EntityUtil.getVarOffsets(0, 2, 0))
        ) {
            if (placeMod.value!! == PlaceMods.Piston) {
                mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.PISTON)
            } else {
                mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.STICKY_PISTON)
            }
//            obiz1="place"
            redstonemod = true
            mc.playerController.updateController()
            mc.player.connection.sendPacket(CPacketPlayer.Rotation(180.0f, 0f, true) as Packet<*>)
            placeList(c, EntityUtil.getVarOffsets(0, 1, -1))
            mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.REDSTONE_BLOCK)
            mc.playerController.updateController() //snowy day
            placeList(c, EntityUtil.getVarOffsets(0, 1, -2))
            if (!checkList(c, EntityUtil.getVarOffsets(0, 1, 1))) {
                canpull = "pull"
            }
            if (!checkList(c, EntityUtil.getVarOffsets(0, 2, 1))) {
                canpull = "pull"
            }
        }
//            +z
        else if (checkList(c, EntityUtil.getVarOffsets(0, 1, 2)) && checkList(
                c, EntityUtil.getVarOffsets(0, 1, 1)
            ) && checkList(c, EntityUtil.getVarOffsets(0, 2, 0))
        ) {
            if (placeMod.value!! == PlaceMods.Piston) {
                mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.PISTON)
            } else {
                mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.STICKY_PISTON)
            }
//            obiz="place"
            redstonemod = true
            mc.playerController.updateController()
            mc.player.connection.sendPacket(CPacketPlayer.Rotation(0.0f, 0f, true) as Packet<*>)
            placeList(c, EntityUtil.getVarOffsets(0, 1, 1))
            mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.REDSTONE_BLOCK)
            mc.playerController.updateController() //snowy day
            placeList(c, EntityUtil.getVarOffsets(0, 1, 2))
            if (!checkList(c, EntityUtil.getVarOffsets(0, 1, -1))) {
                canpull = "pull"
            }
            if (!checkList(c, EntityUtil.getVarOffsets(0, 2, -1))) {
                canpull = "pull"
            }
        }
//            -x
        else if (checkList(c, EntityUtil.getVarOffsets(-2, 1, 0)) && checkList(
                c, EntityUtil.getVarOffsets(-1, 1, 0)
            ) && checkList(c, EntityUtil.getVarOffsets(0, 2, 0))
        ) {
            if (placeMod.value!! == PlaceMods.Piston) {
                mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.PISTON)
            } else {
                mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.STICKY_PISTON)
            }
//            obix1="place"
            redstonemod = true
            mc.playerController.updateController()
            mc.player.connection.sendPacket(CPacketPlayer.Rotation(90.0f, 0f, true) as Packet<*>)
            placeList(c, EntityUtil.getVarOffsets(-1, 1, 0))
            mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.REDSTONE_BLOCK)
            mc.playerController.updateController() //Summer
            placeList(c, EntityUtil.getVarOffsets(-2, 1, 0))
            if (!checkList(c, EntityUtil.getVarOffsets(1, 1, 0))) {
                canpull = "pull"
            }
            if (!checkList(c, EntityUtil.getVarOffsets(1, 2, 0))) {
                canpull = "pull"
            }
        }
//            Mode Three
//            x
        else if (checkList(c, EntityUtil.getVarOffsets(1, 1, 1)) && checkList(
                c, EntityUtil.getVarOffsets(1, 1, 0)
            ) && checkList(c, EntityUtil.getVarOffsets(0, 2, 0))
        ) {
            if (placeMod.value!! == PlaceMods.Piston) {
                mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.PISTON)
            } else {
                mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.STICKY_PISTON)
            }
//            obix="place"
            redstonemod = true
            mc.playerController.updateController()
            mc.player.connection.sendPacket(CPacketPlayer.Rotation(270.0f, 0f, true) as Packet<*>)
            placeList(c, EntityUtil.getVarOffsets(1, 1, 0))
            mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.REDSTONE_BLOCK)
            mc.playerController.updateController() //Summer
            placeList(c, EntityUtil.getVarOffsets(1, 1, 1))
            if (!checkList(c, EntityUtil.getVarOffsets(-1, 1, 0))) {
                canpull = "pull"
            }
            if (!checkList(c, EntityUtil.getVarOffsets(-1, 2, 0))) {
                canpull = "pull"
            }
        }
//            -z
        else if (checkList(c, EntityUtil.getVarOffsets(1, 1, -1)) && checkList(
                c, EntityUtil.getVarOffsets(0, 1, -1)
            ) && checkList(c, EntityUtil.getVarOffsets(0, 2, 0))
        ) {
            if (placeMod.value!! == PlaceMods.Piston) {
                mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.PISTON)
            } else {
                mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.STICKY_PISTON)
            }
//            obiz1="place"
            redstonemod = true
            mc.playerController.updateController()
            mc.player.connection.sendPacket(CPacketPlayer.Rotation(180.0f, 0f, true) as Packet<*>)
            placeList(c, EntityUtil.getVarOffsets(0, 1, -1))
            mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.REDSTONE_BLOCK)
            mc.playerController.updateController() //snowy day
            placeList(c, EntityUtil.getVarOffsets(1, 1, -1))
            if (!checkList(c, EntityUtil.getVarOffsets(0, 1, 1))) {
                canpull = "pull"
            }
            if (!checkList(c, EntityUtil.getVarOffsets(0, 2, 1))) {
                canpull = "pull"
            }
        }
//            +z
        else if (checkList(c, EntityUtil.getVarOffsets(1, 1, 1)) && checkList(
                c, EntityUtil.getVarOffsets(0, 1, 1)
            ) && checkList(c, EntityUtil.getVarOffsets(0, 2, 0))
        ) {
            if (placeMod.value!! == PlaceMods.Piston) {
                mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.PISTON)
            } else {
                mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.STICKY_PISTON)
            }
//            obiz="place"
            redstonemod = true
            mc.playerController.updateController()
            mc.player.connection.sendPacket(CPacketPlayer.Rotation(0.0f, 0f, true) as Packet<*>)
            placeList(c, EntityUtil.getVarOffsets(0, 1, 1))
            mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.REDSTONE_BLOCK)
            mc.playerController.updateController() //snowy day
            placeList(c, EntityUtil.getVarOffsets(1, 1, 1))
            if (!checkList(c, EntityUtil.getVarOffsets(0, 1, -1))) {
                canpull = "pull"
            }
            if (!checkList(c, EntityUtil.getVarOffsets(0, 2, -1))) {
                canpull = "pull"
            }
        }
//            -x
        else if (checkList(c, EntityUtil.getVarOffsets(-1, 1, 1)) && checkList(
                c, EntityUtil.getVarOffsets(-1, 1, 0)
            ) && checkList(c, EntityUtil.getVarOffsets(0, 2, 0))
        ) {
            if (placeMod.value!! == PlaceMods.Piston) {
                mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.PISTON)
            } else {
                mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.STICKY_PISTON)
            }
//            obix1="place"
            redstonemod = true
            mc.playerController.updateController()
            mc.player.connection.sendPacket(CPacketPlayer.Rotation(90.0f, 0f, true) as Packet<*>)
            placeList(c, EntityUtil.getVarOffsets(-1, 1, 0))
            mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.REDSTONE_BLOCK)
            mc.playerController.updateController() //Summer
            placeList(c, EntityUtil.getVarOffsets(-1, 1, 1))
            if (!checkList(c, EntityUtil.getVarOffsets(1, 1, 0))) {
                canpull = "pull"
            }
            if (!checkList(c, EntityUtil.getVarOffsets(1, 2, 0))) {
                canpull = "pull"
            }
        }
//            Mode Four
        //            x
        else if (checkList(c, EntityUtil.getVarOffsets(1, 1, -1)) && checkList(
                c, EntityUtil.getVarOffsets(1, 1, 0)
            ) && checkList(c, EntityUtil.getVarOffsets(0, 2, 0))
        ) {
            if (placeMod.value!! == PlaceMods.Piston) {
                mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.PISTON)
            } else {
                mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.STICKY_PISTON)
            }
            obix = "place"
            redstonemod = true
            mc.playerController.updateController()
            mc.player.connection.sendPacket(CPacketPlayer.Rotation(270.0f, 0f, true) as Packet<*>)
            placeList(c, EntityUtil.getVarOffsets(1, 1, 0))
            mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.REDSTONE_BLOCK)
            mc.playerController.updateController() //Summer
            placeList(c, EntityUtil.getVarOffsets(1, 1, -1))
            if (!checkList(c, EntityUtil.getVarOffsets(-1, 1, 0))) {
                canpull = "pull"
            }
            if (!checkList(c, EntityUtil.getVarOffsets(-1, 2, 0))) {
                canpull = "pull"
            }
        }
//            -z
        else if (checkList(c, EntityUtil.getVarOffsets(-1, 1, -1)) && checkList(
                c, EntityUtil.getVarOffsets(0, 1, -1)
            ) && checkList(c, EntityUtil.getVarOffsets(0, 2, 0))
        ) {
            if (placeMod.value!! == PlaceMods.Piston) {
                mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.PISTON)
            } else {
                mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.STICKY_PISTON)
            }
            obiz1 = "place"
            redstonemod = true
            mc.playerController.updateController()
            mc.player.connection.sendPacket(CPacketPlayer.Rotation(180.0f, 0f, true) as Packet<*>)
            placeList(c, EntityUtil.getVarOffsets(0, 1, -1))
            mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.REDSTONE_BLOCK)
            mc.playerController.updateController() //snowy day
            placeList(c, EntityUtil.getVarOffsets(-1, 1, -1))
            if (!checkList(c, EntityUtil.getVarOffsets(0, 1, 1))) {
                canpull = "pull"
            }
            if (!checkList(c, EntityUtil.getVarOffsets(0, 2, 1))) {
                canpull = "pull"
            }
        }
//            +z
        else if (checkList(c, EntityUtil.getVarOffsets(-1, 1, 1)) && checkList(
                c, EntityUtil.getVarOffsets(0, 1, 1)
            ) && checkList(c, EntityUtil.getVarOffsets(0, 2, 0))
        ) {
            if (placeMod.value!! == PlaceMods.Piston) {
                mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.PISTON)
            } else {
                mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.STICKY_PISTON)
            }
            obiz = "place"
            redstonemod = true
            mc.playerController.updateController()
            mc.player.connection.sendPacket(CPacketPlayer.Rotation(0.0f, 0f, true) as Packet<*>)
            placeList(c, EntityUtil.getVarOffsets(0, 1, 1))
            mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.REDSTONE_BLOCK)
            mc.playerController.updateController() //snowy day
            placeList(c, EntityUtil.getVarOffsets(-1, 1, 1))
            if (!checkList(c, EntityUtil.getVarOffsets(0, 1, -1))) {
                canpull = "pull"
            }
            if (!checkList(c, EntityUtil.getVarOffsets(0, 2, -1))) {
                canpull = "pull"
            }
        }
//            -x
        else if (checkList(c, EntityUtil.getVarOffsets(-1, 1, -1)) && checkList(
                c, EntityUtil.getVarOffsets(-1, 1, 0)
            ) && checkList(c, EntityUtil.getVarOffsets(0, 2, 0))
        ) {
            if (placeMod.value!! == PlaceMods.Piston) {
                mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.PISTON)
            } else {
                mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.STICKY_PISTON)
            }
            obix1 = "place"
            redstonemod = true
            mc.playerController.updateController()
            mc.player.connection.sendPacket(CPacketPlayer.Rotation(90.0f, 0f, true) as Packet<*>)
            placeList(c, EntityUtil.getVarOffsets(-1, 1, 0))
            mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.REDSTONE_BLOCK)
            mc.playerController.updateController() //Summer
            placeList(c, EntityUtil.getVarOffsets(-1, 1, -1))
            if (!checkList(c, EntityUtil.getVarOffsets(1, 1, 0))) {
                canpull = "pull"
            }
            if (!checkList(c, EntityUtil.getVarOffsets(1, 2, 0))) {
                canpull = "pull"
            }
        }
        mc.player.inventory.currentItem = b
        mc.playerController.updateController()
        filler = true
    }

    private fun breakredstone() {
        if (fullNullCheck()) {
            return
        }
        target = getTarget(range.value)
        if (target == null) {
            return
        }
        val breakpos = BlockPos(target!!.posX, target!!.posY, target!!.posZ)
//        Mode One
        if (canpull == "pull" && holepull.value) {

            if (getBlock(breakpos.add(1, 2, 0))!!.block == Blocks.REDSTONE_BLOCK) {
                mc.playerController.onPlayerDamageBlock(
                    breakpos.add(1, 2, 0), BlockUtil2.getRayTraceFacing(breakpos.add(1, 2, 0))
                )
            }
            if (getBlock(breakpos.add(-1, 2, 0))!!.block == Blocks.REDSTONE_BLOCK) {
                mc.playerController.onPlayerDamageBlock(
                    breakpos.add(-1, 2, 0), BlockUtil2.getRayTraceFacing(breakpos.add(-1, 2, 0))
                )
            }
            if (getBlock(breakpos.add(0, 2, 1))!!.block == Blocks.REDSTONE_BLOCK) {
                mc.playerController.onPlayerDamageBlock(
                    breakpos.add(0, 2, 1), BlockUtil2.getRayTraceFacing(breakpos.add(0, 2, 1))
                )
            }
            if (getBlock(breakpos.add(0, 2, -1))!!.block == Blocks.REDSTONE_BLOCK) {
                mc.playerController.onPlayerDamageBlock(
                    breakpos.add(0, 2, -1), BlockUtil2.getRayTraceFacing(breakpos.add(0, 2, -1))
                )
            }
//        Mode Two
            if (getBlock(breakpos.add(2, 1, 0))!!.block == Blocks.REDSTONE_BLOCK) {
                mc.playerController.onPlayerDamageBlock(
                    breakpos.add(2, 1, 0), BlockUtil2.getRayTraceFacing(breakpos.add(2, 1, 0))
                )
            }
            if (getBlock(breakpos.add(-2, 1, 0))!!.block == Blocks.REDSTONE_BLOCK) {
                mc.playerController.onPlayerDamageBlock(
                    breakpos.add(-2, 1, 0), BlockUtil2.getRayTraceFacing(breakpos.add(-2, 1, 0))
                )
            }
            if (getBlock(breakpos.add(0, 1, 2))!!.block == Blocks.REDSTONE_BLOCK) {
                mc.playerController.onPlayerDamageBlock(
                    breakpos.add(0, 1, 2), BlockUtil2.getRayTraceFacing(breakpos.add(0, 1, 2))
                )
            }
            if (getBlock(breakpos.add(0, 1, -2))!!.block == Blocks.REDSTONE_BLOCK) {
                mc.playerController.onPlayerDamageBlock(
                    breakpos.add(0, 1, -2), BlockUtil2.getRayTraceFacing(breakpos.add(0, 1, -2))
                )
            }
//        Mode Three
            if (getBlock(breakpos.add(1, 1, 1))!!.block == Blocks.REDSTONE_BLOCK) {
                mc.playerController.onPlayerDamageBlock(
                    breakpos.add(1, 1, 1), BlockUtil2.getRayTraceFacing(breakpos.add(1, 1, 1))
                )
            }
            if (getBlock(breakpos.add(-1, 1, 1))!!.block == Blocks.REDSTONE_BLOCK) {
                mc.playerController.onPlayerDamageBlock(
                    breakpos.add(-1, 1, 1), BlockUtil2.getRayTraceFacing(breakpos.add(-1, 1, 1))
                )
            }
            if (getBlock(breakpos.add(1, 1, 1))!!.block == Blocks.REDSTONE_BLOCK) {
                mc.playerController.onPlayerDamageBlock(
                    breakpos.add(1, 1, 1), BlockUtil2.getRayTraceFacing(breakpos.add(1, 1, 1))
                )
            }
            if (getBlock(breakpos.add(1, 1, -1))!!.block == Blocks.REDSTONE_BLOCK) {
                mc.playerController.onPlayerDamageBlock(
                    breakpos.add(1, 1, -1), BlockUtil2.getRayTraceFacing(breakpos.add(1, 1, -1))
                )
            }
//        Mode Four
            if (getBlock(breakpos.add(1, 1, -1))!!.block == Blocks.REDSTONE_BLOCK) {
                mc.playerController.onPlayerDamageBlock(
                    breakpos.add(1, 1, -1), BlockUtil2.getRayTraceFacing(breakpos.add(1, 1, -1))
                )
            }
            if (getBlock(breakpos.add(-1, 1, -1))!!.block == Blocks.REDSTONE_BLOCK) {
                mc.playerController.onPlayerDamageBlock(
                    breakpos.add(-1, 1, -1), BlockUtil2.getRayTraceFacing(breakpos.add(-1, 1, -1))
                )
            }
            if (getBlock(breakpos.add(-1, 1, 1))!!.block == Blocks.REDSTONE_BLOCK) {
                mc.playerController.onPlayerDamageBlock(
                    breakpos.add(-1, 1, 1), BlockUtil2.getRayTraceFacing(breakpos.add(-1, 1, 1))
                )
            }
            if (getBlock(breakpos.add(-1, 1, -1))!!.block == Blocks.REDSTONE_BLOCK) {
                mc.playerController.onPlayerDamageBlock(
                    breakpos.add(-1, 1, -1), BlockUtil2.getRayTraceFacing(breakpos.add(-1, 1, -1))
                )
            }
        }
        toggle()
    }


    private fun placeobi() {
        if (fullNullCheck()) {
            return
        }
        target = getTarget(range.value)
        if (target == null) {
            return
        }
        obsidian = InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN)
        if (obsidian == -1) {
            return
        }
        val placepos = BlockPos(target!!.posX, target!!.posY, target!!.posZ)

//        +x
        if (this.getBlock(placepos.add(1, 0, 0))!!.block == Blocks.AIR && obix == "place") {
            this.perform(placepos.add(1, 0, 0))
        }
//        -z
        if (this.getBlock(placepos.add(0, 0, -1))!!.block == Blocks.AIR && obiz1 == "place") {
            this.perform(placepos.add(0, 0, -1))
        }
//        +z
        if (this.getBlock(placepos.add(0, 0, 1))!!.block == Blocks.AIR && obiz == "place") {
            this.perform(placepos.add(0, 0, 1))
        }
//        -x
        if (this.getBlock(placepos.add(-1, 0, 0))!!.block == Blocks.AIR && obix1 == "place") {
            this.perform(placepos.add(-1, 0, 0))
        }
    }

    private fun placeList(pos: Vec3d, list: Array<Vec3d>) {
        for (vec3d in list) {
            val position = BlockPos(pos).add(vec3d.x, vec3d.y, vec3d.z)
            placeBlock(position)
        }
    }

    private fun checkList(pos: Vec3d, list: Array<Vec3d>): Boolean {
        for (vec3d in list) {
            val position = BlockPos(pos).add(vec3d.x, vec3d.y, vec3d.z)
            val block = mc.world.getBlockState(position).block
            if (block is BlockAir || block is BlockFire) return true
        }
        return false
    }


    private fun placeBlock(pos: BlockPos) {
//        if (placements < blockPerPlace.value && mc.player.getDistanceSq(pos) <= MathUtil.square(6.0)) {
//            isPlacing = true
//            if (redstonemod) {
//                packetmode = false
//            }
//            isSneaking = me.windyteam.kura.utils.block.BlockUtil.placeBlock(
//                pos, EnumHand.MAIN_HAND, false, packetmode, true
//            )
//            placements++
//        }
        BlockUtil2.placeBlock(pos,EnumHand.MAIN_HAND,false,packetPlace.value,true)
    }

    enum class PlaceMods {
        Piston, SPiston
    }

    private fun getBlock(block: BlockPos): IBlockState? {
        return mc.world.getBlockState(block)
    }

    private fun switchToSlot(slot: Int) {
        mc.player.connection.sendPacket(CPacketHeldItemChange(slot) as Packet<*>)
        mc.player.inventory.currentItem = slot
        mc.playerController.updateController()
    }

    private fun perform(pos: BlockPos) {
        val old = mc.player.inventory.currentItem
        this.switchToSlot(this.obsidian)
        BlockUtil2.placeBlock(pos, EnumHand.MAIN_HAND, false, true, false)
        this.switchToSlot(old)
    }

    private fun getobipos() {
        val c = target!!.positionVector
//            Mode One
//            +x
        if (checkList(c, EntityUtil.getVarOffsets(0, 1, 0)) && checkList(
                c, EntityUtil.getVarOffsets(0, 2, 0)
            )
        ) if (checkList(
                c, EntityUtil.getVarOffsets(1, 1, 0)
            ) && checkList(c, EntityUtil.getVarOffsets(0, 2, 0))
        ) {
            obix = "place"
        }
//            -z
        else if (checkList(
                c, EntityUtil.getVarOffsets(0, 1, -1)
            ) && checkList(c, EntityUtil.getVarOffsets(0, 2, 0))
        ) {
            obiz1 = "place"
        }
//            +z
        else if (checkList(
                c, EntityUtil.getVarOffsets(0, 1, 1)
            ) && checkList(c, EntityUtil.getVarOffsets(0, 2, 0))
        ) {
            obiz = "place"
        }
//            -x
        else if (checkList(
                c, EntityUtil.getVarOffsets(-1, 1, 0)
            ) && checkList(c, EntityUtil.getVarOffsets(0, 2, 0))
        ) {
            obix1 = "place"
        }
    }


    companion object {
        var isPlacing = false
        fun breakcrystal() {
            for (crystal in mc.world.loadedEntityList.stream()
                .filter { e: Entity -> e is EntityEnderCrystal && !e.isDead }
                .sorted(Comparator.comparing { e: Entity -> java.lang.Float.valueOf(mc.player.getDistance(e)) })
                .collect(
                    Collectors.toList()
                )) {
                if (crystal !is EntityEnderCrystal || mc.player.getDistance(crystal) > 4.0f) continue
                mc.player.connection.sendPacket(CPacketUseEntity(crystal) as Packet<*>)
                mc.player.connection.sendPacket(CPacketAnimation(EnumHand.OFF_HAND) as Packet<*>)
            }
        }
    }
}