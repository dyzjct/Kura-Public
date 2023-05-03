package me.windyteam.kura.module.modules.combat

import kura.utils.isPlaceable
import me.windyteam.kura.event.events.entity.MotionUpdateEvent
import me.windyteam.kura.module.Category
import me.windyteam.kura.module.Module
import me.windyteam.kura.setting.BooleanSetting
import me.windyteam.kura.setting.IntegerSetting
import me.windyteam.kura.utils.block.BlockUtil
import me.windyteam.kura.utils.combat.HoleUtils.isAir
import me.windyteam.kura.utils.entity.EntityUtil
import me.windyteam.kura.utils.inventory.InventoryUtil
import me.windyteam.kura.utils.math.RotationUtil
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

@Module.Info(name = "HoleKicker", category = Category.COMBAT)
object HoleKicker : Module() {
    private val range = settings("Range", 5, 1, 16)
    private val delay = settings("Delay", 100, 0, 500)
    val crystalDelay: IntegerSetting? = settings("CrystalDelay",202,1,500)
    val breakCrystal: BooleanSetting? = settings("BreakCrystal", false)
    val packetPlace: BooleanSetting? = settings("PacketPlace", false)
    private val autoPush = settings("Push", false)
    val rotate: BooleanSetting? = settings("Rotate", false)
    private val antiTop = settings("AntiTopKick",true)
    private val xinBypass: BooleanSetting? = settings("XinBypass", false)
    var pistonList = mutableListOf<BlockPos>()
    var checkList = mutableListOf<BlockPos>()
    var breakList = mutableListOf<BlockPos>()
    var timer = Timer()
    private var firstTimer = Timer()
    var crystalTimer = Timer()
    var target: EntityPlayer? = null
    private var isSneaking = false
    private var doPushing = false
    private var piston = false
    private var redstone = false

    override fun onEnable() {
        if (fullNullCheck()) return
        if (InventoryUtil.findHotbarBlock(Blocks.REDSTONE_BLOCK) == -1 || InventoryUtil.findHotbarBlock(Blocks.STICKY_PISTON) == -1 && InventoryUtil.findHotbarBlock(
                Blocks.PISTON
            ) == -1
        ) {
            disable()
            return
        }
        loading()
        target = getTarget(range.value)
        doPushing = canPush(target!!)
        redstone = false
        piston = false
        firstTimer.reset()
    }

    @SubscribeEvent
    fun onTick(event: MotionUpdateEvent) {
        if (fullNullCheck()) return
        if (!mc.player.onGround) {
            disable()
            return
        }
        runCatching {
            EntityUtil.getRoundedBlockPos(mc.player)
            target = getTarget(range.value)
            if (target == null) {
                disable()
                return
            }
            if (firstTimer.passedMs( 50L)){
                doPistonNew()
            }
        }
    }

    private fun doPistonNew(){
        target = getTarget(range.value)
        target?.let {
            val playerPos = BlockPos(it.posX, it.posY, it.posZ)
            val b = mc.player.inventory.currentItem
            for (i in 0..3){
                if (antiTop.value && getBlock(BlockPos(playerPos.add(0, 2, 0)))!!.block != Blocks.AIR) {
                    toggle()
                    return
                }
                if (breakCrystal!!.value){
                    breakCrystal(pistonList[i])
                    breakCrystal(checkList[i])
                }
                if (pistonCheck(BlockPos(playerPos.add(pistonList[i])))) {
                    continue
                }
                if (redStoneCheck(BlockPos(playerPos.add(pistonList[i]).x,playerPos.y,playerPos.add(pistonList[i]).z)) && redStoneCheck(BlockPos(playerPos.add(pistonList[i]).x,playerPos.y+2,playerPos.add(pistonList[i]).z))){
                    continue
                }
                if (!mc.world.isAir(playerPos.add(checkList[i])) || !mc.world.isAir(BlockPos(playerPos.add(checkList[i]).x,playerPos.y+2,playerPos.add(checkList[i]).z))){
                    if (autoPush.value){
                        if (!doPushing)
                            continue
                    } else {
                        continue
                    }
                }

                if (timer.passedMs(delay!!.value.toLong()) && !isRedStone(BlockPos(playerPos.add(pistonList[x]).x,playerPos.y+2,playerPos.add(
                        pistonList[x]).z)) && mc.world.isPlaceable(BlockPos(playerPos.add(pistonList[i]).x,playerPos.y,playerPos.add(
                        pistonList[i]).z))){
                    switchToSlot(InventoryUtil.findHotbarBlock(Blocks.REDSTONE_BLOCK))
                    if (xinBypass!!.value){
                        rotationBlock(BlockPos(playerPos.add(pistonList[i]).x,playerPos.y,playerPos.add(
                            pistonList[i]).z))
                    } else {
                        placeBlock(BlockPos(playerPos.add(pistonList[i]).x,playerPos.y,playerPos.add(
                            pistonList[i]).z))
                    }
                    switchToSlot(b)
                    redstone = true
                } else if(isRedStone(BlockPos(playerPos.add(pistonList[i]).x,playerPos.y,playerPos.add(
                        pistonList[i]).z))) {
                    redstone = true
                }

                if (timer.passedMs(delay.value.toLong()) && mc.world.isPlaceable(playerPos.add(pistonList[i]))){
                    val pistonSide = BlockUtil.getFirstFacing(
                        playerPos.add(
                            pistonList[i].x.toDouble(), pistonList[i].y.toDouble(), pistonList[i].z.toDouble()
                        )
                    )
                    val pistonNeighbour: BlockPos = playerPos.add(pistonList[i]).offset(pistonSide)
                    val pistonOpposite = pistonSide.getOpposite()
                    val pistonHitVec = Vec3d(pistonNeighbour as Vec3i).add(0.5, 0.5, 0.5)
                        .add(Vec3d(pistonOpposite.getDirectionVec()).scale(0.5))
                    if (rotate!!.value) {
                        RotationUtil.faceVector(pistonHitVec, true)
                    }
                    when (pistonList[i]) {
                        pistonList[0] -> {
                            mc.player.connection.sendPacket(CPacketPlayer.Rotation(270.0f, 0f, true))
                        }

                        pistonList[1] -> {
                            mc.player.connection.sendPacket(CPacketPlayer.Rotation(90.0f, 0f, true))
                        }

                        pistonList[2] -> {
                            mc.player.connection.sendPacket(CPacketPlayer.Rotation(0.0f, 0f, true))
                        }

                        pistonList[3] -> {
                            mc.player.connection.sendPacket(CPacketPlayer.Rotation(180.0f, 0f, true))
                        }
                    }
                    if (InventoryUtil.findHotbarBlock(Blocks.STICKY_PISTON) != -1) {
                        switchToSlot(InventoryUtil.findHotbarBlock(Blocks.STICKY_PISTON))
                    } else if (InventoryUtil.findHotbarBlock(Blocks.PISTON) != -1) {
                        switchToSlot(InventoryUtil.findHotbarBlock(Blocks.PISTON))
                    }
                    placeBlock(playerPos.add(pistonList[i]))
                    switchToSlot(b)
                    piston = true
                } else if (isPiston(pistonList[i])) {
                    piston = true
                }

                if (piston && redstone) {
                    if (autoPush.value){
                        if (doPushing){
                            doPull()
                            disable()
                        }
                    } else {
                        disable()
                    }
                }

                if (!isRedStone(BlockPos(playerPos.add(pistonList[x]).x,playerPos.y,playerPos.add(
                        pistonList[x]).z)) && timer.passedMs(delay.value.toLong()) && mc.world.isPlaceable(BlockPos(playerPos.add(pistonList[i]).x,playerPos.y+2,playerPos.add(
                        pistonList[i]).z))){
                    switchToSlot(InventoryUtil.findHotbarBlock(Blocks.REDSTONE_BLOCK))
                    if (xinBypass!!.value){
                        rotationBlock(BlockPos(playerPos.add(pistonList[i]).x,playerPos.y+2,playerPos.add(
                            pistonList[i]).z))
                    } else {
                        placeBlock(BlockPos(playerPos.add(pistonList[i]).x,playerPos.y+2,playerPos.add(
                            pistonList[i]).z))
                    }
                    switchToSlot(b)
                    redstone = true
                } else if (isRedStone(BlockPos(playerPos.add(pistonList[i]).x,playerPos.y,playerPos.add(
                        pistonList[i]).z))) {
                    redstone = true
                }

                if (piston && redstone) {
                    if (doPushing){
                        if (autoPush.value){
                            doPull()
                            disable()
                        }
                    } else {
                        disable()
                    }
                }
            }
        }
    }

    override fun onDisable() {
        isSneaking = EntityUtil.stopSneaking(isSneaking)
        piston = true
        redstone = true
        firstTimer.reset()
        autoClean()
    }
}