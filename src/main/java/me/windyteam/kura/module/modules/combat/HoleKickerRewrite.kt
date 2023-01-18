package me.windyteam.kura.module.modules.combat

import kura.utils.Wrapper
import kura.utils.isReplaceable
import me.windyteam.kura.event.events.block.BlockBreakEvent
import me.windyteam.kura.event.events.entity.MotionUpdateEvent
import me.windyteam.kura.module.Category
import me.windyteam.kura.module.Module
import me.windyteam.kura.module.Module.Info
import me.windyteam.kura.utils.block.BlockUtil
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
    private val autotoggle = bsetting("AutoToggle",true)

    private var target :EntityPlayer? = null
    private var pistonList = mutableListOf<BlockPos>()
    private var redStoneList = mutableListOf<BlockPos>()
    private var redStoneList2 = mutableListOf<BlockPos>()
    private var breakPos:BlockPos? = null
    private var rotateList = mutableListOf<Float>()

    @SubscribeEvent
    fun onTick(event: MotionUpdateEvent){
        if (fullNullCheck()) return
        if (mc.player == null || mc.world == null) return
        target = getTarget(range.value)
        if (target == null) return
        if (InventoryUtil.findHotbarBlock(Blocks.REDSTONE_BLOCK) == -1) {
            disable()
            return
        }
        if (InventoryUtil.findHotbarBlock(Blocks.PISTON) == -1) {
            disable()
            return
        }
        loadList()
        for (i in 1..4){
            if (pistonList[i] == breakPos || getBlock(pistonList[i]).block != Blocks.AIR && getBlock(pistonList[i]).block != Blocks.PISTON) continue
            if (getBlock(redStoneList2[i]).block == Blocks.AIR && mc.world.isPlaceable(redStoneList2[i])){
                blockRedStone(redStoneList2[i])
                doPistonPlace(pistonList[i],rotateList[i])
            } else{
                if (mc.world.isPlaceable(pistonList[i])){
                    if (pistonList[i] == breakPos) continue
                    mc.player.connection.sendPacket(CPacketPlayer.Rotation(rotateList[i], 0f, true) as Packet<*>)
                    doPistonPlace(pistonList[i],rotateList[i])
                }
                blockRedStone(redStoneList[i])
            }
            break
        }
        if (autotoggle.value) toggle()
    }

    private fun doPistonPlace(pos: BlockPos,rotate:Float){
        if (mc.world.isPlaceable(pos)){
            mc.player.connection.sendPacket(CPacketPlayer.Rotation(rotate, 0f, true) as Packet<*>)
            blockPiston(pos)
        }
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