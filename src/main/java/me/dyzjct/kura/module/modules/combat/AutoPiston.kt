package me.dyzjct.kura.module.modules.combat

import me.dyzjct.kura.module.Category
import me.dyzjct.kura.module.Module
import me.dyzjct.kura.module.Module.Info
import me.dyzjct.kura.module.ModuleManager
import me.dyzjct.kura.utils.Timer
import me.dyzjct.kura.utils.player.getTarget
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.util.math.BlockPos

@Info(name = "AutoPiston", category = Category.COMBAT)
class AutoPiston : Module() {
    private val range = isetting("Range", 5, 1, 16)
    private val delay = isetting("PistonDelay", 50, 0, 250)
    var target: EntityPlayer? = null
    private val timer = Timer()
    override fun onUpdate() {
        if (fullNullCheck()) {
            return
        }
        if (!mc.player.onGround){
            return
        }
        target = getTarget(range.value)
        val playerpos = BlockPos(target!!.posX, target!!.posY, target!!.posZ)
        if (this.getBlock(playerpos.add(0, 1, 0))!!.block != Blocks.AIR){
            return
        }
        if (this.getBlock(playerpos.add(0, 2, 0))!!.block != Blocks.AIR){
            return
        }
        if (this.getBlock(playerpos.add(1, 0, 0))!!.block == Blocks.OBSIDIAN || this.getBlock(playerpos.add(1, 0, 0))!!.block == Blocks.BEDROCK &&
            this.getBlock(playerpos.add(-1, 0, 0))!!.block == Blocks.OBSIDIAN || this.getBlock(playerpos.add(-1, 0, 0))!!.block == Blocks.BEDROCK &&
            this.getBlock(playerpos.add(0, 0, 1))!!.block == Blocks.OBSIDIAN || this.getBlock(playerpos.add(0, 0, 1))!!.block == Blocks.BEDROCK &&
            this.getBlock(playerpos.add(0, 0, -1))!!.block == Blocks.OBSIDIAN || this.getBlock(playerpos.add(0, 0, -1))!!.block == Blocks.BEDROCK) {
            if (!ModuleManager.getModuleByClass(HoleKicker::class.java).isEnabled){
                ModuleManager.getModuleByClass(HoleKicker::class.java).enable()
                timer.reset()
            }
        }else if (this.getBlock(playerpos.add(0, 0, 0))!!.block == Blocks.OBSIDIAN || this.getBlock(playerpos.add(0, 0, 0))!!.block == Blocks.BEDROCK){
            if (!ModuleManager.getModuleByClass(HoleKicker::class.java).isEnabled){
                ModuleManager.getModuleByClass(HoleKicker::class.java).enable()
                timer.reset()
            }
        }
        if (!timer.passedMs((delay.value as Int).toLong())){
            return
        }
    }

    private fun getBlock(block: BlockPos): IBlockState? {
        return mc.world.getBlockState(block)
    }

}