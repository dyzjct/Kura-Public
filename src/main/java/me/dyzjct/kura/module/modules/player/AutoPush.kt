package me.dyzjct.kura.module.modules.player

import me.dyzjct.kura.event.events.entity.MotionUpdateEvent
import me.dyzjct.kura.module.Category
import me.dyzjct.kura.module.Module
import me.dyzjct.kura.setting.Setting
import me.dyzjct.kura.utils.MathUtil
import me.dyzjct.kura.utils.NTMiku.Timerss
import me.dyzjct.kura.utils.block.BlockUtilkt
import me.dyzjct.kura.utils.entity.EntityUtil
import me.dyzjct.kura.utils.inventory.InventoryUtil
import me.dyzjct.kura.utils.mc.ChatUtil
import me.dyzjct.kura.utils.player.PlayerUtils
import net.minecraft.block.Block
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.network.Packet
import net.minecraft.network.play.client.CPacketHeldItemChange
import net.minecraft.network.play.client.CPacketPlayer
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.Vec3i
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@Module.Info(name = "AutoPush", category = Category.PLAYER)
class AutoPush : Module() {
    var preDelay: Setting<Float>
    var placeDelay: Setting<Float>
    var packetPlace: Setting<Boolean>
    var silentSwitch: Setting<Boolean>
    var range: Setting<Float>
    var targetRange: Setting<Float>
    var target: EntityPlayer?
    var pistonSlot = 0
    var render: Setting<Boolean>? = null
    var redstoneSlot = 0
    var obbySlot: Int
    var pistonPos: BlockPos? = null
    var redstonePos: BlockPos?
    val redStonePos: BlockPos? = null
    var stage: Int
    var preTimer: Timerss? = null
    var timer: Timerss?
    var oldslot: Int
    var oldhand: EnumHand?
    var isTorch: Boolean

    private val Helper = bsetting("Helper",false)
    private val redStoneType = msetting("Blocks", RedStone.Both)
    private val targetType = msetting("Mode", Target.None)
    init {
        preDelay = fsetting("Block Delay", 0.0f, 0.0f, 25.0f)
        placeDelay = fsetting("Delay", 0.0f, 0.0f, 25.0f)
        packetPlace = bsetting("Packet Place", false)

        silentSwitch = bsetting("Silent Switch", false)

        range = fsetting("Range", 10.0f, 0.0f,20.0f )
        render = bsetting("Render is bad", false)
        targetRange = fsetting("Target Range", 10.0f, 0.0f, 20.0f)
        target = null
        obbySlot = -1
        redstonePos = null
        stage = 0
        timer = null
        oldslot = -1
        oldhand = null
        isTorch = false
    }

    fun reset() {
        target = null
        pistonSlot = -1
        redstoneSlot = -1
        obbySlot = -1
        pistonPos = null
        redstonePos = null
        stage = 0
        preTimer = null
        timer = null
        oldslot = -1
        oldhand = null
        isTorch = false
    }

    override fun onEnable() {
        reset()
    }

//    fun onRender3D() {
//        try {
//            if (this.redStonePos == null) {
//                return
//            }
//            if (render as Boolean) {
//                RenderUtil3D.drawBox(pistonPos, 1.0, Color.WHITE, 63)
//                RenderUtil3D.drawBox(this.redStonePos, 1.0, Color.red, 63)
//            }
//        } catch (ex: Exception) {
//        }
//    }
    
    @SubscribeEvent
    fun onTick(event: MotionUpdateEvent.Tick?) {
        if (Helper.value){
            if (!findMaterials()) {
                ChatUtil.sendMessage("Cannot find Bloks disabling...")
                disable()
                return
            }
        }
        target = findTarget()
        if (target == null) {
            ChatUtil.sendMessage("Cannot find Players! disabling...")
            disable()
            return
        }
        if ((isNull(pistonPos) || isNull(redstonePos)) && !findSpace(target)) {
            ChatUtil.sendMessage("Cannot find space! disabling...")
            disable()
            return
        }
        if (preTimer == null) {
            preTimer = Timerss()
        }
        if (preTimer!!.passedX(preDelay.value.toDouble()) && !prepareBlock()) {
            restoreItem()
            return
        }
        if (timer == null) {
            timer = Timerss()
        }
        if (stage == 0 && timer!!.passedX(placeDelay.value.toDouble())) {
            setItem(pistonSlot)
            val targetPos = BlockPos(target!!.posX, target!!.posY, target!!.posZ)
            val angle = MathUtil.calcAngle(Vec3d(pistonPos as Vec3i?), Vec3d(targetPos as Vec3i))
            mc.player.connection.sendPacket(CPacketPlayer.Rotation(angle[0] + 180.0f, angle[1], true) as Packet<*>)
            BlockUtilkt.placeBlock(pistonPos, packetPlace.value)
            stage = 1
            timer!!.reset()
        }
        if (stage == 1 && timer!!.passedX(placeDelay.value.toDouble())) {
            setItem(redstoneSlot)
            BlockUtilkt.placeBlock(redstonePos, packetPlace.value)
            stage = 2
            disable()
            reset()
        }
        restoreItem()
    }

    fun setItem(slot: Int) {
        if (silentSwitch.value) {
            oldhand = null
            if (mc.player.isHandActive) {
                oldhand = mc.player.activeHand
            }
            oldslot = mc.player.inventory.currentItem
            mc.player.connection.sendPacket(CPacketHeldItemChange(slot) as Packet<*>)
        } else {
            mc.player.inventory.currentItem = slot
            mc.playerController.updateController()
        }
    }

    fun restoreItem() {
        if (oldslot != -1 && silentSwitch.value) {
            if (oldhand != null) {
               mc.player.setActiveHand(oldhand)
            }
          mc.player.connection.sendPacket(CPacketHeldItemChange(oldslot) as Packet<*>)
            oldslot = -1
            oldhand = null
        }
    }

    fun isNull(`object`: Any?): Boolean {
        return `object` == null
    }

    fun findSpace(target: EntityPlayer?): Boolean {
        val targetPos = BlockPos(target!!.posX, target.posY, target.posZ)
        val mypos = BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ)
        val offsets = arrayOf(BlockPos(1, 0, 0), BlockPos(-1, 0, 0), BlockPos(0, 0, 1), BlockPos(0, 0, -1))
        val poses: MutableList<AutoPushPos?> = ArrayList()
        for (offset in offsets) {
            val pos = AutoPushPos()
            val base = targetPos.add(offset as Vec3i)
            if (BlockUtilkt.getBlock(base) !== Blocks.AIR) {
                val pistonPos = base.add(0, 1, 0)
                if (BlockUtilkt.getBlock(pistonPos) === Blocks.AIR) {
                    if (!checkPos(pistonPos)) {
                        if (PlayerUtils.getDistance(pistonPos) >= 3.6 || pistonPos.y <= mypos.y + 1) {
                            if (BlockUtilkt.getBlock(targetPos.add(offset.x * -1, 1, offset.z * -1)) === Blocks.AIR) {
                                val redstonePoses: MutableList<BlockPos?> = ArrayList()
                                val roffsets: MutableList<BlockPos> = ArrayList()
                                roffsets.add(BlockPos(1, 0, 0))
                                roffsets.add(BlockPos(-1, 0, 0))
                                roffsets.add(BlockPos(0, 0, 1))
                                roffsets.add(BlockPos(0, 0, -1))
                                if (redStoneType.value == RedStone.Block) {
                                    roffsets.add(BlockPos(0, 1, 0))
                                }
                                for (roffset in roffsets) {
                                    val redstonePos = pistonPos.add(roffset as Vec3i)
                                    if (redstonePos.x == targetPos.x && redstonePos.z == targetPos.z) {
                                        continue
                                    }
                                    if (checkPos(redstonePos)) {
                                        continue
                                    }
                                    if (BlockUtilkt.getBlock(redstonePos) !== Blocks.AIR) {
                                        continue
                                    }
                                    redstonePoses.add(redstonePos)
                                }
                                val redstonePos2 = redstonePoses.stream().min(Comparator.comparing { b: BlockPos? ->
                                    mc.player.getDistance(
                                        b!!.x.toDouble(), b.y.toDouble(), b.z.toDouble()
                                    )
                                }).orElse(null)
                                if (redstonePos2 != null) {
                                    pos.setPiston(pistonPos)
                                    pos.setRedStone(redstonePos2)
                                    poses.add(pos)
                                }
                            }
                        }
                    }
                }
            }
        }
        val bestPos = poses.stream().filter { p: AutoPushPos? -> p!!.maxRange <= range.value }
            .min(Comparator.comparing { p: AutoPushPos? -> p!!.maxRange }).orElse(null)
        if (bestPos != null) {
            pistonPos = bestPos.piston
            redstonePos = bestPos.redstone
            return true
        }
        return false
    }

    fun findTarget(): EntityPlayer? {
        var target: EntityPlayer? = null
        mc.world.playerEntities as List<EntityPlayer>
        if (targetType.value == Target.None) {
            target = PlayerUtils.getNearestPlayer(targetRange.value.toDouble())
        }
        return target
    }

    private fun findTarget(range: Double): EntityPlayer? {
        var target: EntityPlayer? = null
        for (player in java.util.ArrayList(mc.world.playerEntities)) {
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

    fun findMaterials(): Boolean {
        if (Helper.value){
            pistonSlot = InventoryUtil.findHotbarBlock(Blocks.PISTON as Block)
            val redstoneBlock = InventoryUtil.findHotbarBlock(Blocks.REDSTONE_BLOCK)
            val redstoneTorch = InventoryUtil.findHotbarBlock(Blocks.REDSTONE_TORCH)
            obbySlot = InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN)
            if (itemCheck(pistonSlot)) {
                pistonSlot = InventoryUtil.findHotbarBlock(Blocks.STICKY_PISTON as Block)
            }
            if (redStoneType.value == RedStone.Block) {
                isTorch = false
                redstoneSlot = redstoneBlock
            }
            if (redStoneType.value == RedStone.Torch) {
                isTorch = true
                redstoneSlot = redstoneTorch
            }
            if (redStoneType.value == RedStone.Both) {
                isTorch = true
                redstoneSlot = redstoneTorch
                if (itemCheck(redstoneSlot)) {
                    isTorch = false
                    redstoneSlot = redstoneBlock
                }
            }
        }
        return !itemCheck(redstoneSlot) && !itemCheck(pistonSlot) && !itemCheck(obbySlot)
    }

    fun checkPos(pos: BlockPos): Boolean {
        val mypos = BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ)
        return pos.x == mypos.x && pos.z == mypos.z
    }

    fun itemCheck(slot: Int): Boolean {
        return slot == -1
    }

    fun prepareBlock(): Boolean {
        BlockPos(target!!.posX, target!!.posY, target!!.posZ)
        val piston = pistonPos!!.add(0, -1, 0)
        val redstone = redstonePos!!.add(0, -1, 0)
        if (BlockUtilkt.getBlock(piston) === Blocks.AIR) {
            setItem(obbySlot)
            BlockUtilkt.placeBlock(piston, packetPlace.value)
            if (delayCheck()) {
                return false
            }
        }
        if (BlockUtilkt.getBlock(redstone) === Blocks.AIR) {
            setItem(obbySlot)
            BlockUtilkt.placeBlock(redstone, packetPlace.value)
            if (delayCheck()) {
                return false
            }
        }
        return true
    }

    fun delayCheck(): Boolean {
        return preDelay.value != 0.0f
    }

    enum class Target {
        None
    }

    enum class RedStone {
        Block, Torch, Both
    }

    inner class AutoPushPos {
        var piston: BlockPos? = null
        var redstone: BlockPos? = null
        val maxRange: Double
            get() = if (piston == null || redstone == null) {
                999999.0
            } else Math.max(
                PlayerUtils.getDistance(
                    piston
                ), PlayerUtils.getDistance(redstone)
            )

        @JvmName("setPiston1")
        fun setPiston(piston: BlockPos?) {
            this.piston = piston
        }

        fun setRedStone(redstone: BlockPos?) {
            this.redstone = redstone
        }
    }
}