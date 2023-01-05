package me.dyzjct.kura.module.modules.xddd

import me.dyzjct.kura.event.events.entity.MotionUpdateEvent
import me.dyzjct.kura.event.events.player.UpdateWalkingPlayerEvent
import me.dyzjct.kura.module.Category
import me.dyzjct.kura.module.Module
import me.dyzjct.kura.utils.NTMiku.TimerUtils
import me.dyzjct.kura.utils.block.BlockUtil
import me.dyzjct.kura.utils.entity.EntityUtil
import me.dyzjct.kura.utils.inventory.InventoryUtil
import net.minecraft.block.BlockObsidian
import net.minecraft.entity.Entity
import net.minecraft.entity.item.EntityEnderCrystal
import net.minecraft.network.play.client.CPacketAnimation
import net.minecraft.network.play.client.CPacketPlayer
import net.minecraft.network.play.client.CPacketUseEntity
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.util.stream.Collectors

@Module.Info(
    name = "Surround",
    category = Category.XDDD,
    description = "Continually places obsidian around your feet"
)
class Surround : Module() {
    var slot = 0
    var oldslot = 0
    private var startPos: BlockPos? = null
    private val placedelay = isetting("PlaceDelay", 50, 0, 250)
    private var smart = bsetting("Smart", true)
    private var center = bsetting("Center", true)
    private var packet = bsetting("Packet", true)
    private var rot = bsetting("Rotate", false)
    private var breakcry = bsetting("BreakCrystal", true)
    private var newPos2: BlockPos? = null
    private var explodeTimerUtils = TimerUtils()
    override fun onEnable() {
        if (fullNullCheck()) {
            return
        }
//        delay.reset()
        delay.tickAndReset(placedelay.value)
        startPos = EntityUtil.getPlayerPos()
        val centerPos = mc.player.position
        val y = centerPos.getY().toDouble()
        var x = centerPos.getX().toDouble()
        var z = centerPos.getZ().toDouble()
        val plusPlus = Vec3d(x + 0.5, y, z + 0.5)
        val plusMinus = Vec3d(x + 0.5, y, z - 0.5)
        val minusMinus = Vec3d(x - 0.5, y, z - 0.5)
        val minusPlus = Vec3d(x - 0.5, y, z + 0.5)
        if (center.value) {
            if (getDst(plusPlus) < getDst(plusMinus) && getDst(plusPlus) < getDst(minusMinus) && getDst(plusPlus) < getDst(
                    minusPlus
                )
            ) {
                x = centerPos.getX().toDouble() + 0.5
                z = centerPos.getZ().toDouble() + 0.5
                centerPlayer(x, y, z)
            }
            if (getDst(plusMinus) < getDst(plusPlus) && getDst(plusMinus) < getDst(minusMinus) && getDst(plusMinus) < getDst(
                    minusPlus
                )
            ) {
                x = centerPos.getX().toDouble() + 0.5
                z = centerPos.getZ().toDouble() - 0.5
                centerPlayer(x, y, z)
            }
            if (getDst(minusMinus) < getDst(plusPlus) && getDst(minusMinus) < getDst(plusMinus) && getDst(minusMinus) < getDst(
                    minusPlus
                )
            ) {
                x = centerPos.getX().toDouble() - 0.5
                z = centerPos.getZ().toDouble() - 0.5
                centerPlayer(x, y, z)
            }
            if (getDst(minusPlus) < getDst(plusPlus) && getDst(minusPlus) < getDst(plusMinus) && getDst(minusPlus) < getDst(
                    minusMinus
                )
            ) {
                x = centerPos.getX().toDouble() - 0.5
                z = centerPos.getZ().toDouble() + 0.5
                centerPlayer(x, y, z)
            }
        }
        mc.playerController.syncCurrentPlayItem()
    }

    override fun onLogout() {
        disable()
    }

//    @SubscribeEvent
//    fun onUpdate(event: UpdateWalkingPlayerEvent?) {
    @SubscribeEvent
    fun onTick(event: MotionUpdateEvent.Tick) {
        if (fullNullCheck()) {
            toggle()
            return
        }
    delay.tickAndReset(placedelay.value)
        if (breakcry.value) {
            breakcrystal()
        }
        slot = InventoryUtil.findHotbarBlock(BlockObsidian::class.java)
        oldslot = mc.player.inventory.currentItem
        if (startPos != null && smart.value && startPos != EntityUtil.getPlayerPos()) {
            toggle()
            return
        }
        if (slot == -1) {
            toggle()
            return
        }
        for (pos in surroundPos) {
            newPos2 = addPos(pos)
            if (BlockUtil.isPositionPlaceable(newPos2, false) < 2) continue
            if (slot == -1) {
                toggle()
            }
            InventoryUtil.switchToHotbarSlot(slot, false)
            BlockUtil.placeBlock(newPos2, EnumHand.MAIN_HAND, rot.value, packet.value)
            InventoryUtil.switchToHotbarSlot(oldslot, false)
        }
    }

    fun addPos(pos: BlockPos): BlockPos {
        val pPos = EntityUtil.getPlayerPos(0.2)
        return BlockPos(pPos.getX() + pos.getX(), pPos.getY() + pos.getY(), pPos.getZ() + pos.getZ())
    }

    fun getDst(vec: Vec3d?): Double {
        return mc.player.positionVector.distanceTo(vec)
    }

    fun centerPlayer(x: Double, y: Double, z: Double) {
        mc.player.connection.sendPacket(CPacketPlayer.Position(x, y, z, true))
        mc.player.setPosition(x, y, z)
    }

    fun breakcrystal() {
        for (crystal in mc.world.loadedEntityList.stream().filter { e: Entity -> e is EntityEnderCrystal && !e.isDead }
            .sorted(Comparator.comparing { e: Entity? -> java.lang.Float.valueOf(mc.player.getDistance(e)) }).collect(
                Collectors.toList()
            )) {
            if (crystal !is EntityEnderCrystal || mc.player.getDistance(crystal) > 4.0f) continue
            if (explodeTimerUtils.passed(50) && mc.connection != null) {
                mc.player.connection.sendPacket(CPacketUseEntity(crystal))
                mc.player.connection.sendPacket(CPacketAnimation(EnumHand.OFF_HAND))
                explodeTimerUtils.reset()
            }
        }
    }

    companion object {
        var delay = TimerUtils()
        var surroundPos = arrayOf(
            BlockPos(0, -1, 0),
            BlockPos(1, -1, 0),
            BlockPos(-1, -1, 0),
            BlockPos(0, -1, 1),
            BlockPos(0, -1, -1),
            BlockPos(1, 0, 0),
            BlockPos(-1, 0, 0),
            BlockPos(0, 0, 1),
            BlockPos(0, 0, -1)
        )
    }
}