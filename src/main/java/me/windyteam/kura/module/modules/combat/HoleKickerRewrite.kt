package me.windyteam.kura.module.modules.combat

import kura.utils.Wrapper
import kura.utils.isReplaceable
import me.windyteam.kura.event.events.block.BlockBreakEvent
import me.windyteam.kura.event.events.entity.MotionUpdateEvent
import me.windyteam.kura.module.Category
import me.windyteam.kura.module.Module
import me.windyteam.kura.module.Module.Info
import me.windyteam.kura.utils.Timer
import me.windyteam.kura.utils.block.BlockUtil
import me.windyteam.kura.utils.block.BlockUtil2
import me.windyteam.kura.utils.getTarget
import me.windyteam.kura.utils.inventory.InventoryUtil
import net.minecraft.block.BlockPistonBase
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.network.Packet
import net.minecraft.network.play.client.CPacketPlayer
import net.minecraft.util.EnumHand
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@Info(name = "HoleKickerRewrite", category = Category.COMBAT)
class HoleKickerRewrite : Module(){
    private val range = isetting("Range",6,0,12)
    private val delay = isetting("Delay", 0, 0, 300)
    private val autoToggle = bsetting("AutoToggle",false)

    private var target :EntityPlayer? = null
    private var pistonList = mutableListOf<BlockPos>()
    private var redStoneList = mutableListOf<BlockPos>()
    private var redStoneList2 = mutableListOf<BlockPos>()
    private var redStoneList3 = mutableListOf<BlockPos>()
    private var redStoneList4 = mutableListOf<BlockPos>()
    private var breakPos:BlockPos? = null
    private var rotateList = mutableListOf<Float>()
    private var isPlace = 0
    private var pushList = mutableListOf<BlockPos>()
    private var doRedStone = false

    private val timer = Timer()
    @SubscribeEvent
    fun onTick(event: MotionUpdateEvent){
        if (fullNullCheck()) return
        if (mc.player == null || mc.world == null) return
        target = getTarget(range.value)
        if (target == null) return
        val playerPos = BlockPos(target!!.posX,target!!.posY,target!!.posZ)
        if (InventoryUtil.findHotbarBlock(Blocks.REDSTONE_BLOCK) == -1) {
            if (autoToggle.value) disable()
            return
        }
        if (InventoryUtil.findHotbarBlock(Blocks.PISTON) == -1) {
            if (autoToggle.value) disable()
            return
        }
        if (!mc.player.onGround) return
        if (getBlock(playerPos.add(0,2,0)).block != Blocks.AIR) return
        if (!this.timer.passedMs(this.delay.value.toLong())) return
        loadList()
        for (i in 1..4){
            if (pistonList[i] == breakPos || getBlock(pistonList[i]).block != Blocks.AIR && getBlock(pistonList[i]).block != Blocks.PISTON) continue
            doRedStone = getBlock(redStoneList[i]) != Blocks.REDSTONE_BLOCK && getBlock(redStoneList2[i]) != Blocks.REDSTONE_BLOCK && getBlock(redStoneList3[i]) != Blocks.REDSTONE_BLOCK && getBlock(redStoneList4[i]) != Blocks.REDSTONE_BLOCK
            if (getBlock(redStoneList2[i]).block == Blocks.AIR && mc.world.isPlaceable(redStoneList2[i])){
                if (doRedStone) blockRedStone(redStoneList2[i])
                if (mc.world.isPlaceable(pistonList[i])) doPistonPlace(pistonList[i],rotateList[i])
            } else if (mc.world.isPlaceable(redStoneList[i])){
                if (mc.world.isPlaceable(pistonList[i])){
                    if (pistonList[i] == breakPos) continue
                    mc.player.connection.sendPacket(CPacketPlayer.Rotation(rotateList[i], 0f, true) as Packet<*>)
                    doPistonPlace(pistonList[i],rotateList[i])
                }
                if (doRedStone) blockRedStone(redStoneList[i])
            } else if (mc.world.isPlaceable(redStoneList3[i])){
                if (mc.world.isPlaceable(pistonList[i])){
                    if (pistonList[i] == breakPos) continue
                    mc.player.connection.sendPacket(CPacketPlayer.Rotation(rotateList[i], 0f, true) as Packet<*>)
                    doPistonPlace(pistonList[i],rotateList[i])
                }
                if (doRedStone) blockRedStone(redStoneList3[i])
            } else if (mc.world.isPlaceable(redStoneList4[i])){
                if (mc.world.isPlaceable(pistonList[i])){
                    if (pistonList[i] == breakPos) continue
                    mc.player.connection.sendPacket(CPacketPlayer.Rotation(rotateList[i], 0f, true) as Packet<*>)
                    doPistonPlace(pistonList[i],rotateList[i])
                }
                if (doRedStone) blockRedStone(redStoneList4[i])
            }
            if (getBlock(pushList[i]).block != Blocks.AIR){
                canBreakRedStone(redStoneList[i])
                canBreakRedStone(redStoneList2[i])
                canBreakRedStone(redStoneList3[i])
                canBreakRedStone(redStoneList4[i])
            }
            break
        }
        clearList()
        if (autoToggle.value && isPlace==4) toggle()
    }

    private fun canBreakRedStone(pos: BlockPos){
        if (getBlock(pos).block == Blocks.REDSTONE_BLOCK) mc.playerController.onPlayerDamageBlock(pos, BlockUtil2.getRayTraceFacing(pos))
    }

    private fun doPistonPlace(pos: BlockPos,rotate:Float){
        if (mc.world.isPlaceable(pos)){
            mc.player.connection.sendPacket(CPacketPlayer.Rotation(rotate, 0f, true) as Packet<*>)
            blockPiston(pos)
        }
    }

    private fun clearList(){
        pistonList.clear()
        rotateList.clear()
        redStoneList.clear()
        redStoneList2.clear()
        redStoneList3.clear()
        pushList.clear()
    }
    private fun loadList(){
        target = getTarget(range.value)
        if (target == null) return
        val playerPos = BlockPos(target!!.posX,target!!.posY,target!!.posZ)
        pistonList.add(playerPos.add(1,1,0))
        pistonList.add(playerPos.add(-1,1,0))
        pistonList.add(playerPos.add(0,1,1))
        pistonList.add(playerPos.add(0,1,-1))
        redStoneList.add(playerPos.add(1,2,0))
        redStoneList.add(playerPos.add(-1,2,0))
        redStoneList.add(playerPos.add(0,2,1))
        redStoneList.add(playerPos.add(0,2,-1))
        redStoneList2.add(playerPos.add(1,0,0))
        redStoneList2.add(playerPos.add(-1,0,0))
        redStoneList2.add(playerPos.add(0,0,1))
        redStoneList2.add(playerPos.add(0,0,-1))
        redStoneList3.add(playerPos.add(1,1,1))
        redStoneList3.add(playerPos.add(-1,1,1))
        redStoneList3.add(playerPos.add(1,1,1))
        redStoneList3.add(playerPos.add(1,1,-1))
        redStoneList4.add(playerPos.add(1,1,-1))
        redStoneList4.add(playerPos.add(-1,1,-1))
        redStoneList4.add(playerPos.add(-1,1,1))
        redStoneList4.add(playerPos.add(-1,1,-1))
        pushList.add(playerPos.add(-1,1,0))
        pushList.add(playerPos.add(1,1,0))
        pushList.add(playerPos.add(0,1,-1))
        pushList.add(playerPos.add(0,1,1))
        rotateList.add(270.0f)
        rotateList.add(90.0f)
        rotateList.add(0.0f)
        rotateList.add(180.0f)
    }

    private fun blockPiston(pos: BlockPos) {
        val old: Int = mc.player.inventory.currentItem
        if (mc.world.getBlockState(pos).block === Blocks.AIR) {
            mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(BlockPistonBase::class.java)
            mc.playerController.updateController()
            BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, false, true, false)
            mc.player.inventory.currentItem = old
            mc.playerController.updateController()
            isPlace++
        }
    }

    private fun blockRedStone(pos: BlockPos) {
        val old: Int = mc.player.inventory.currentItem
        if (mc.world.getBlockState(pos).block === Blocks.AIR) {
            mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.REDSTONE_BLOCK)
            mc.playerController.updateController()
            BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, false, true, false)
            mc.player.inventory.currentItem = old
            mc.playerController.updateController()
            isPlace++
        }
    }

    fun World.isPlaceable(pos: BlockPos, ignoreSelfCollide: Boolean = false) =
        this.getBlockState(pos).isReplaceable && this.checkNoEntityCollision(
            AxisAlignedBB(pos),
        if (ignoreSelfCollide) Wrapper.player else null
    )

    private fun getBlock(block: BlockPos): IBlockState {
        return mc.world.getBlockState(block)
    }

    override fun onDisable() {
        isPlace = 0
    }

    @SubscribeEvent
    fun onBreak(event: BlockBreakEvent) {
        if (fullNullCheck()) {
            return
        }
        if (event.position != null) {
            breakPos = event.position
        }
    }
}