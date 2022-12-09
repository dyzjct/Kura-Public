package me.dyzjct.kura.module.modules.combat

import me.dyzjct.kura.module.Category
import me.dyzjct.kura.module.Module
import me.dyzjct.kura.setting.Setting
import me.dyzjct.kura.utils.MathUtil
import me.dyzjct.kura.utils.NTMiku.BlockUtil
import me.dyzjct.kura.utils.Timer
import me.dyzjct.kura.utils.entity.EntityUtil
import me.dyzjct.kura.utils.inventory.InventoryUtil
import net.minecraft.block.Block
import net.minecraft.block.BlockAir
import net.minecraft.block.BlockFire
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.Entity
import net.minecraft.entity.item.EntityEnderCrystal
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.network.Packet
import net.minecraft.network.play.client.CPacketAnimation
import net.minecraft.network.play.client.CPacketPlayer
import net.minecraft.network.play.client.CPacketUseEntity
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import java.util.stream.Collectors

/**
 * created by chunfeng666 on 2022-10-05
 * update by dyzjct on 2022-12-9
 */
@Module.Info(name = "HoleKicker", category = Category.COMBAT)
class HoleKicker : Module() {
    private val delay = isetting("PlaceDelay", 50, 0, 250)
    private val range = isetting("Range", 5, 1, 16)
    private val blockPerPlace = isetting("BlocksTick", 8, 1, 30)
    private val BreakCrystal = bsetting("BreakCrystal", false)
    private val PlaceMod = msetting("PlaceMod",PlaceMods.Piston)
    private val timer = Timer()

    var targets: EntityPlayer? = null


    var target: EntityPlayer? = null
    private fun getTarget(range: Double): EntityPlayer? {
        var target: EntityPlayer? = null
        for (player in ArrayList(mc.world.playerEntities)) {
            if (EntityUtil.isntValid(player, range)) continue
            if (mc.player.getDistance(player) > range) continue
            target = player
            if (player != null) {
                break
            }
            return player
        }
        return target
    }

    private var filler = false
    private var didPlace = false
    private var isSneaking = false
    private var placements = 0
    override fun onEnable() {
        if (PlaceMod.value.equals(PlaceMods.Piston)) {
            if (InventoryUtil.findHotbarBlock(Blocks.REDSTONE_BLOCK) == -1) {
                disable()
                return
            }
            if (InventoryUtil.findHotbarBlock(Blocks.PISTON) == -1) {
                disable()
                return
            }
        }
        if (PlaceMod.value.equals(PlaceMods.SPiston)) {
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
    }

    override fun onUpdate() {
        if (BreakCrystal.value) {
            breakcrystal()
        }
        doPiston()
        breakredstone()
        toggle()
    }

    override fun onDisable() {
        isPlacing = false
        isSneaking = EntityUtil.stopSneaking(isSneaking)
    }


    private fun doPiston() {
        if (check()) return
        doPistonTrap()
        if (didPlace) timer.reset()
    }

    private fun doPistonTrap() {
        val b = mc.player.inventory.currentItem
        val c = target!!.positionVector
        if (PlaceMod.value.equals(PlaceMods.Piston)) {
            if (checkList(c, EntityUtil.getVarOffsets(0, 1, 0)) && checkList(
                    c, EntityUtil.getVarOffsets(0, 2, 0)
                )
            ) if (checkList(c, EntityUtil.getVarOffsets(1, 1, 0)) && checkList(c, EntityUtil.getVarOffsets(0, 2, 0))) {
                mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.PISTON)
                mc.playerController.updateController()
                mc.player.connection.sendPacket(CPacketPlayer.Rotation(270.0f, 0f, true) as Packet<*>)
                placeList(c, EntityUtil.getVarOffsets(1, 1, 0))
                mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.REDSTONE_BLOCK)
                mc.playerController.updateController() //Summer
                placeList(c, EntityUtil.getVarOffsets(1, 2, 0))
            } else if (checkList(c, EntityUtil.getVarOffsets(0, 1, -1)) && checkList(c, EntityUtil.getVarOffsets(0, 2, 0))) {
                mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.PISTON)
                mc.playerController.updateController()
                mc.player.connection.sendPacket(CPacketPlayer.Rotation(180.0f, 0f, true) as Packet<*>)
                placeList(c, EntityUtil.getVarOffsets(0, 1, -1))
                mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.REDSTONE_BLOCK)
                mc.playerController.updateController() //snowy day
                placeList(c, EntityUtil.getVarOffsets(0, 2, -1))
            }
            else if (checkList(c, EntityUtil.getVarOffsets(0, 1, 1)) && checkList(c, EntityUtil.getVarOffsets(0, 2, 0))) {
                mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.PISTON)
                mc.playerController.updateController()
                mc.player.connection.sendPacket(CPacketPlayer.Rotation(0.0f, 0f, true) as Packet<*>)
                placeList(c, EntityUtil.getVarOffsets(0, 1, 1))
                mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.REDSTONE_BLOCK)
                mc.playerController.updateController() //snowy day
                placeList(c, EntityUtil.getVarOffsets(0, 2, 1))
            }else if (checkList(c, EntityUtil.getVarOffsets(-1, 1, 0)) && checkList(c, EntityUtil.getVarOffsets(0, 2, 0))) {
                mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.PISTON)
                mc.playerController.updateController()
                mc.player.connection.sendPacket(CPacketPlayer.Rotation(90.0f, 0f, true) as Packet<*>)
                placeList(c, EntityUtil.getVarOffsets(-1, 1, 0))
                mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.REDSTONE_BLOCK)
                mc.playerController.updateController() //Summer
                placeList(c, EntityUtil.getVarOffsets(-1, 2, 0))
            }
        }
        if (PlaceMod.value.equals(PlaceMods.SPiston)) {
            if (checkList(c, EntityUtil.getVarOffsets(0, 1, 0)) && checkList(
                    c, EntityUtil.getVarOffsets(0, 2, 0)
                )
            ) if (checkList(c, EntityUtil.getVarOffsets(1, 1, 0)) && checkList(c, EntityUtil.getVarOffsets(0, 2, 0))) {
                mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.STICKY_PISTON)
                mc.playerController.updateController()
                mc.player.connection.sendPacket(CPacketPlayer.Rotation(270.0f, 0f, true) as Packet<*>)
                placeList(c, EntityUtil.getVarOffsets(1, 1, 0))
                mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.REDSTONE_BLOCK)
                mc.playerController.updateController() //Summer
                placeList(c, EntityUtil.getVarOffsets(1, 2, 0))
            } else if (checkList(c, EntityUtil.getVarOffsets(0, 1, -1)) && checkList(c, EntityUtil.getVarOffsets(0, 2, 0))) {
                mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.STICKY_PISTON)
                mc.playerController.updateController()
                mc.player.connection.sendPacket(CPacketPlayer.Rotation(180.0f, 0f, true) as Packet<*>)
                placeList(c, EntityUtil.getVarOffsets(0, 1, -1))
                mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.REDSTONE_BLOCK)
                mc.playerController.updateController() //snowy day
                placeList(c, EntityUtil.getVarOffsets(0, 2, -1))
            }
            else if (checkList(c, EntityUtil.getVarOffsets(0, 1, 1)) && checkList(c, EntityUtil.getVarOffsets(0, 2, 0))) {
                mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.STICKY_PISTON)
                mc.playerController.updateController()
                mc.player.connection.sendPacket(CPacketPlayer.Rotation(0.0f, 0f, true) as Packet<*>)
                placeList(c, EntityUtil.getVarOffsets(0, 1, 1))
                mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.REDSTONE_BLOCK)
                mc.playerController.updateController() //snowy day
                placeList(c, EntityUtil.getVarOffsets(0, 2, 1))
            }else if (checkList(c, EntityUtil.getVarOffsets(-1, 1, 0)) && checkList(c, EntityUtil.getVarOffsets(0, 2, 0))) {
                mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.STICKY_PISTON)
                mc.playerController.updateController()
                mc.player.connection.sendPacket(CPacketPlayer.Rotation(90.0f, 0f, true) as Packet<*>)
                placeList(c, EntityUtil.getVarOffsets(-1, 1, 0))
                mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.REDSTONE_BLOCK)
                mc.playerController.updateController() //Summer
                placeList(c, EntityUtil.getVarOffsets(-1, 2, 0))
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
        targets = getTarget(range.value.toDouble())
        if (targets == null) {
            return
        }
        val breakpos = BlockPos(targets!!.posX, targets!!.posY, targets!!.posZ)
        if (getBlock(breakpos.add(1, 2, 0))!!.block == Blocks.REDSTONE_BLOCK) {
            mc.playerController.onPlayerDamageBlock(
                breakpos.add(1, 2, 0),
                BlockUtil.getRayTraceFacing(breakpos.add(1, 2, 0))
            )
        }
        if (getBlock(breakpos.add(-1, 2, 0))!!.block == Blocks.REDSTONE_BLOCK) {
            mc.playerController.onPlayerDamageBlock(
                breakpos.add(-1, 2, 0),
                BlockUtil.getRayTraceFacing(breakpos.add(-1, 2, 0))
            )
        }
        if (getBlock(breakpos.add(0, 2, 1))!!.block == Blocks.REDSTONE_BLOCK) {
            mc.playerController.onPlayerDamageBlock(
                breakpos.add(0, 2, 1),
                BlockUtil.getRayTraceFacing(breakpos.add(0, 2, 1))
            )
        }
        if (getBlock(breakpos.add(0, 2, -1))!!.block == Blocks.REDSTONE_BLOCK) {
            mc.playerController.onPlayerDamageBlock(
                breakpos.add(0, 2, -1),
                BlockUtil.getRayTraceFacing(breakpos.add(0, 2, -1))
            )
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

    private fun check(): Boolean {
        isPlacing = false
        didPlace = false
        filler = false
        placements = 0
        isSneaking = EntityUtil.stopSneaking(isSneaking)
        target = getTarget((range.value as Int).toDouble())
        return target == null || !timer.passedMs((delay.value as Int).toLong())
    }

    private fun placeBlock(pos: BlockPos) {
        if (placements < (blockPerPlace.value as Int) && mc.player.getDistanceSq(pos) <= MathUtil.square(6.0)) {
            isPlacing = true
            isSneaking = me.dyzjct.kura.utils.block.BlockUtil.placeBlock(
                pos, EnumHand.MAIN_HAND, false, false, true
            )
            didPlace = true
            placements++
        }
    }

    enum class PlaceMods {
        Piston, SPiston
    }


    private fun getBlock(block: BlockPos): IBlockState? {
        return mc.world.getBlockState(block)
    }

    companion object {
        var isPlacing = false
        fun breakcrystal() {
            for (crystal in mc.world.loadedEntityList.stream()
                .filter { e: Entity -> e is EntityEnderCrystal && !e.isDead }
                .sorted(Comparator.comparing { e: Entity? -> java.lang.Float.valueOf(mc.player.getDistance(e)) })
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