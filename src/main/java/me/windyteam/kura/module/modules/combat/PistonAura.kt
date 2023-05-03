package me.windyteam.kura.module.modules.combat

import kura.utils.isPlaceable
import me.windyteam.kura.event.events.entity.MotionUpdateEvent
import me.windyteam.kura.module.Category
import me.windyteam.kura.module.Module
import me.windyteam.kura.module.modules.player.PacketMine
import me.windyteam.kura.utils.block.BlockUtil
import me.windyteam.kura.utils.entity.EntityUtil
import me.windyteam.kura.utils.inventory.InventoryUtil
import me.windyteam.kura.utils.math.RotationUtil
import me.windyteam.kura.utils.mc.ChatUtil
import me.windyteam.kura.utils.other.*
import me.windyteam.kura.utils.player.Timer
import me.windyteam.kura.utils.player.getTarget
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.network.play.client.CPacketPlayer
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.Vec3i
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@Module.Info(name = "PistonAura", category = Category.COMBAT)
object PistonAura : Module() {
    private val range = settings("Range", 5, 1, 16)
    private val delay = settings("Delay", 100, 0, 500)
    private val debug = settings("Debug", false)
    var pistonList = mutableListOf<BlockPos>()
    var timer = Timer()
    private var auraTime = Timer()
    var target: EntityPlayer? = null

    override fun onEnable() {
        if (fullNullCheck()) return
        if (InventoryUtil.findHotbarBlock(Blocks.REDSTONE_BLOCK) == -1 || InventoryUtil.findHotbarBlock(Blocks.STICKY_PISTON) == -1 && InventoryUtil.findHotbarBlock(
                Blocks.PISTON
            ) == -1
        ) {
            return
        }
        auraLoad()
        target = getTarget(range.value)
    }

    @SubscribeEvent
    fun onTick(event: MotionUpdateEvent) {
        if (fullNullCheck()) return
        if (!mc.player.onGround) {
            return
        }
        runCatching {
            EntityUtil.getRoundedBlockPos(mc.player)
            target = getTarget(range.value)
            if (target == null) {
                return
            }
            if (auraTime.passedMs( 50L)){
                doPistonAura()
                auraTime.reset()
            }
        }
    }

    private fun doPistonAura(){
        target = getTarget(range.value)
        target?.let {
            val playerPos = BlockPos(it.posX, it.posY, it.posZ)
            val b = mc.player.inventory.currentItem
            for (i in 0..3){
                if (pistonCheck(BlockPos(playerPos.add(pistonList[i])))) {
                    continue
                }
                if (redStoneCheck(BlockPos(playerPos.add(pistonList[i]).x,playerPos.y,playerPos.add(pistonList[i]).z)) && redStoneCheck(BlockPos(playerPos.add(pistonList[i]).x,playerPos.y+2,playerPos.add(pistonList[i]).z))){
                    continue
                }
                val targetVec: Vec3d = target!!.positionVector
                when(pistonList[i]){
                    pistonList[0] -> {
                        if (!mc.world.isAirBlock(BlockPos(playerPos.x.toDouble(),playerPos.y.toDouble()+1,playerPos.y.toDouble()+1)) && !mc.world.isAirBlock(BlockPos(playerPos.x.toDouble(),playerPos.y.toDouble()+2,playerPos.y.toDouble()+1))){
                            continue
                        }
                    }
                    pistonList[1] -> {
                        if (!mc.world.isAirBlock(BlockPos(playerPos.x.toDouble(),playerPos.y.toDouble()+1,playerPos.y.toDouble()-1)) && !mc.world.isAirBlock(BlockPos(playerPos.x.toDouble(),playerPos.y.toDouble()+2,playerPos.y.toDouble()-1))){
                            continue
                        }
                    }
                    pistonList[2] -> {
                        if (!mc.world.isAirBlock(BlockPos(playerPos.x.toDouble()-1,playerPos.y.toDouble()+1,playerPos.y.toDouble())) && !mc.world.isAirBlock(BlockPos(playerPos.x.toDouble()-1,playerPos.y.toDouble()+2,playerPos.y.toDouble()))){
                            continue
                        }
                    }pistonList[3] -> {
                        if (!mc.world.isAirBlock(BlockPos(playerPos.x.toDouble()+1,playerPos.y.toDouble()+1,playerPos.y.toDouble())) && !mc.world.isAirBlock(BlockPos(playerPos.x.toDouble()+1,playerPos.y.toDouble()+2,playerPos.y.toDouble()))){
                            continue
                        }
                    }
                }

                when (pistonList[i]) {
                    pistonList[0] -> {
                        if (!mc.world.isAirBlock(BlockPos(playerPos.add(-1,1,0)))){
                            continue
                        }
                    }
                    pistonList[1] -> {
                        if (!mc.world.isAirBlock(BlockPos(playerPos.add(0,1,-1)))){
                            continue
                        }
                    }
                    pistonList[2] -> {
                        if (!mc.world.isAirBlock(BlockPos(playerPos.add(0,1,1)))){
                            continue
                        }
                    }
                    pistonList[3] -> {
                        if (!mc.world.isAirBlock(BlockPos(playerPos.add(1,1,0)))){
                            continue
                        }
                    }
                }

                if (timer.passedMs(delay!!.value.toLong()) && checkCrystal(targetVec,EntityUtil.getVarOffsets(0,1,0)) == null && mc.world.isPlaceable(playerPos.add(pistonList[i]))){
                    val pistonSide = BlockUtil.getFirstFacing(
                        playerPos.add(
                            pistonList[i].x.toDouble(), pistonList[i].y.toDouble(), pistonList[i].z.toDouble()
                        )
                    )
                    val pistonNeighbour: BlockPos = playerPos.add(pistonList[i]).offset(pistonSide)
                    val pistonOpposite = pistonSide.getOpposite()
                    val pistonHitVec = Vec3d(pistonNeighbour as Vec3i).add(0.5, 0.5, 0.5)
                        .add(Vec3d(pistonOpposite.getDirectionVec()).scale(0.5))
                    RotationUtil.faceVector(pistonHitVec, true)
                    when (pistonList[i]) {
                        pistonList[0] -> {
                            mc.player.connection.sendPacket(CPacketPlayer.Rotation(270.0f, 0f, true))
                        }

                        pistonList[2] -> {
                            mc.player.connection.sendPacket(CPacketPlayer.Rotation(0.0f, 0f, true))
                        }

                        pistonList[3] -> {
                            mc.player.connection.sendPacket(CPacketPlayer.Rotation(180.0f, 0f, true))
                        }

                        pistonList[4] -> {
                            mc.player.connection.sendPacket(CPacketPlayer.Rotation(90.0f, 0f, true))
                        }
                    }
                    if (InventoryUtil.findHotbarBlock(Blocks.STICKY_PISTON) != -1) {
                        switchToSlot(InventoryUtil.findHotbarBlock(Blocks.STICKY_PISTON))
                    } else if (InventoryUtil.findHotbarBlock(Blocks.PISTON) != -1) {
                        switchToSlot(InventoryUtil.findHotbarBlock(Blocks.PISTON))
                    }
                    placeBlock(playerPos.add(pistonList[i]))
                    switchToSlot(b)
                    if (debug.value)
                    timer.reset()
                }

                if (timer.passedMs(delay.value.toLong()) && checkCrystal(targetVec,EntityUtil.getVarOffsets(0,1,0)) == null && mc.world.isPlaceable(BlockPos(playerPos.add(pistonList[i]).x,playerPos.y+2,playerPos.add(
                        pistonList[i]).z))){
                    switchToSlot(InventoryUtil.findHotbarBlock(Blocks.REDSTONE_BLOCK))
                    rotationBlock(BlockPos(playerPos.add(pistonList[i]).x,playerPos.y+2,playerPos.add(
                        pistonList[i]).z))
                    switchToSlot(b)
                    if (debug.value) ChatUtil.sendMessage("PLACE RED STONE")
                    timer.reset()
                }
                switchToSlot(b)

                if (isRedStone(BlockPos(playerPos.add(pistonList[i]).x,playerPos.y+2,playerPos.add(
                        pistonList[i]).z))
                ){
                    when (pistonList[i]) {
                        pistonList[0] -> {
                            if (checkCrystal(targetVec,EntityUtil.getVarOffsets(-1,0,0)) == null) placeCrystal(BlockPos(playerPos.add(-1,0,0)))
                        }
                        pistonList[1] -> {
                            if (checkCrystal(targetVec,EntityUtil.getVarOffsets(0,0,-1)) == null) placeCrystal(BlockPos(playerPos.add(0,0,-1)))
                        }
                        pistonList[2] -> {
                            if (checkCrystal(targetVec,EntityUtil.getVarOffsets(0,0,1)) == null) placeCrystal(BlockPos(playerPos.add(0,0,1)))
                        }
                        pistonList[3] -> {
                            if (checkCrystal(targetVec,EntityUtil.getVarOffsets(1,0,0)) == null) placeCrystal(BlockPos(playerPos.add(1,0,0)))
                        }
                    }
                }

                if (timer.passedMs(delay.value.toLong()) && isRedStone(BlockPos(playerPos.add(pistonList[i]).x,playerPos.y+2,playerPos.add(
                        pistonList[i]).z)) && PacketMine.currentPos != BlockPos(playerPos.add(pistonList[i]).x,playerPos.y+2,playerPos.add(
                        pistonList[i]).z)){
                    breakRedStone(BlockPos(playerPos.add(pistonList[i]).x,playerPos.y+2,playerPos.add(
                        pistonList[i]).z))
                    if (debug.value) ChatUtil.sendMessage("BREAK RED STONE")
                    timer.reset()
                }

                if (timer.passedMs(delay.value.toLong()) && mc.world.isAirBlock(BlockPos(playerPos.add(pistonList[i]).x,playerPos.y+2,playerPos.add(
                        pistonList[i]).z))) {
                    hitCrystal(playerPos.add(0,1,0))
                    if (debug.value) ChatUtil.sendMessage("BREAK CRYSTAL")
                }
                break
            }
        }
    }
    override fun onDisable() {
        auraClear()
    }
}