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
import net.minecraft.util.EnumHand
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@Info(name = "HoleKickerRewrite", category = Category.COMBAT)
class HoleKickerRewrite : Module(){
    private val range = isetting("Range",6,0,12)

    private var target :EntityPlayer? = null
    private var pistonList = mutableListOf<BlockPos>()
    private var breakPos:BlockPos? = null

    @SubscribeEvent
    fun onTick(event: MotionUpdateEvent){
        if (fullNullCheck()) return
        target = getTarget(range.value)
        if (target == null) return
        loadPistonList()
        for (i in pistonList){
            if (getBlock(i).block == Blocks.PISTON) break
            if (!mc.world.isPlaceable(i)) continue
            perform(i)
            break
        }
    }

    private fun loadPistonList(){
        target = getTarget(range.value)
        if (target == null) return
        val playerPos = BlockPos(target!!.posX,target!!.posY,target!!.posZ)
        pistonList.add(playerPos.add(1,1,0))
        pistonList.add(playerPos.add(-1,1,0))
        pistonList.add(playerPos.add(0,1,1))
        pistonList.add(playerPos.add(0,1,-1))
    }

    private fun perform(pos: BlockPos) {
        val old: Int = mc.player.inventory.currentItem
        if (mc.world.getBlockState(pos).block === Blocks.AIR) {
            mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock( BlockPistonBase::class.java)
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