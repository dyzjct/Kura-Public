package me.windyteam.kura.module.modules.xddd

import me.windyteam.kura.event.events.entity.MotionUpdateEvent
import me.windyteam.kura.module.Category
import me.windyteam.kura.module.Module
import me.windyteam.kura.module.modules.combat.HoleKicker
import me.windyteam.kura.utils.TimerUtils
import me.windyteam.kura.utils.block.BlockInteractionHelper
import me.windyteam.kura.utils.block.BlockUtil
import me.windyteam.kura.utils.entity.EntityUtil
import me.windyteam.kura.utils.inventory.InventoryUtil
import net.minecraft.block.BlockObsidian
import net.minecraft.entity.Entity
import net.minecraft.entity.item.EntityEnderCrystal
import net.minecraft.network.play.client.CPacketPlayer
import net.minecraft.util.EnumHand
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@Module.Info(
    name = "Surround",
    category = Category.XDDD,
    description = "Continually places obsidian around your feet"
)
object Surround : Module() {
    var slot = 0
    var oldslot = 0
    private var startPos: BlockPos? = null
    private val placedelay = isetting("PlaceDelay", 50, 0, 250)
    private var smart = bsetting("Smart", true)
    private var center = bsetting("Center", true)
    private var packet = bsetting("Packet", true)
    private var rot = bsetting("Rotate", false)
    private var breakcry = bsetting("BreakCrystal", true)
    private var timerUtils = TimerUtils()
    override fun onEnable() {
        if (fullNullCheck()) {
            return
        }
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

    @SubscribeEvent
    fun onTick(event: MotionUpdateEvent.FastTick) {
        if (fullNullCheck()) {
            toggle()
            return
        }
        if (breakcry.value) {
            breakCrystal(BlockPos(0,0,0))
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
            val surpos = addPos(pos)
            if (BlockUtil.isPositionPlaceable(surpos, false) < 2) continue
            if (slot == -1) {
                toggle()
                return
            }
            if (timerUtils.passedMs(placedelay.value.toLong())){
                InventoryUtil.switchToHotbarSlot(slot, false)
                BlockUtil.placeBlock(surpos, EnumHand.MAIN_HAND, rot.value, packet.value)
                InventoryUtil.switchToHotbarSlot(oldslot, false)
                timerUtils.reset()
            }

        }
    }

    private fun addPos(pos: BlockPos): BlockPos {
        val pPos = EntityUtil.getPlayerPos(0.2)
        return BlockPos(pPos.getX() + pos.getX(), pPos.getY() + pos.getY(), pPos.getZ() + pos.getZ())
    }

    private fun getDst(vec: Vec3d?): Double {
        return mc.player.positionVector.distanceTo(vec)
    }

    private fun centerPlayer(x: Double, y: Double, z: Double) {
        mc.player.connection.sendPacket(CPacketPlayer.Position(x, y, z, true))
        mc.player.setPosition(x, y, z)
    }

    private fun breakCrystal(pos: BlockPos) {
        val a: Vec3d = mc.player.positionVector
        if (checkCrystal(a, EntityUtil.getVarOffsets(pos.x,pos.y,pos.z)) != null && timerUtils.passedMs(202L)){
            mc.player.connection.sendPacket(CPacketPlayer.Rotation(
                BlockInteractionHelper.getLegitRotations(pos.add(0.5,0.5,0.5))[0],
                BlockInteractionHelper.getLegitRotations(pos.add(0.5,0.5,0.5))[1],true))
            EntityUtil.attackEntity(checkCrystal(a, EntityUtil.getVarOffsets(pos.x,pos.y,pos.z)), true)
            HoleKicker.crystalTimer.reset()
            timerUtils.reset()
        }
    }

    private fun checkCrystal(pos: Vec3d?, list: Array<Vec3d>): Entity? {
        var crystal: Entity? = null
        val var5 = list.size
        for (var6 in 0 until var5) {
            val vec3d = list[var6]
            val position = BlockPos(pos!!).add(vec3d.x, vec3d.y, vec3d.z)
            for (entity in mc.world.getEntitiesWithinAABB(
                Entity::class.java, AxisAlignedBB(position)
            )) {
                if (entity !is EntityEnderCrystal || crystal != null) continue
                crystal = entity
            }
        }
        return crystal
    }

    private var surroundPos = arrayOf(
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