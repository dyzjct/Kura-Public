package me.windyteam.kura.module.modules.render

import me.windyteam.kura.event.events.render.RenderEvent
import me.windyteam.kura.module.Category
import me.windyteam.kura.module.Module
import me.windyteam.kura.module.Module.Info
import me.windyteam.kura.utils.color.GSColor
import me.windyteam.kura.utils.getTarget
import me.windyteam.kura.utils.gl.MelonTessellator
import me.windyteam.kura.utils.render.RenderUtil
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.util.math.BlockPos
import java.awt.Color

@Info(name = "CityESP", category = Category.RENDER)
class CityESP :Module() {
    private val range = isetting("Range",10,0,30)
    private val color = csetting("Color",Color(255,255,255))
    private val alpha = isetting("Alpha",60,0,255)
    private val lineWidth = fsetting("LineWidth",2f,0f,3f)
    private var target:EntityPlayer? = null
    private var city = mutableListOf<BlockPos>()

    override fun onWorldRender(event: RenderEvent?) {
        if (fullNullCheck()) return
        val colors = GSColor(color.value.red,color.value.green,color.value.blue)
        target = getTarget(range.value)
        if (target == null) return
        val playerpos = BlockPos(target!!.posX,target!!.posY,target!!.posZ)
        if (getBlock(playerpos.add(1,0,0))!!.block == Blocks.OBSIDIAN){
            city.add(playerpos.add(1,0,0))
            city.add(playerpos.add(1,1,0))
            city.add(playerpos.add(2,0,0))
        }
        if (getBlock(playerpos.add(-1,0,0))!!.block == Blocks.OBSIDIAN){
            city.add(playerpos.add(-1,0,0))
            city.add(playerpos.add(-1,1,0))
            city.add(playerpos.add(-2,0,0))
        }
        if (getBlock(playerpos.add(0,0,1))!!.block == Blocks.OBSIDIAN){
            city.add(playerpos.add(0,0,1))
            city.add(playerpos.add(0,1,1))
            city.add(playerpos.add(0,0,2))
        }
        if (getBlock(playerpos.add(0,0,-1))!!.block == Blocks.OBSIDIAN){
            city.add(playerpos.add(0,0,-1))
            city.add(playerpos.add(0,1,-1))
            city.add(playerpos.add(0,0,-2))
        }
        if (getBlock(playerpos.add(1,0,0))!!.block == Blocks.OBSIDIAN || getBlock(playerpos.add(0,0,1))!!.block == Blocks.OBSIDIAN){
            city.add(playerpos.add(1,0,1))
        }
        if (getBlock(playerpos.add(1,0,0))!!.block == Blocks.OBSIDIAN || getBlock(playerpos.add(0,0,-1))!!.block == Blocks.OBSIDIAN){
            city.add(playerpos.add(1,0,-1))
        }
        if (getBlock(playerpos.add(-1,0,0))!!.block == Blocks.OBSIDIAN || getBlock(playerpos.add(0,0,1))!!.block == Blocks.OBSIDIAN){
            city.add(playerpos.add(-1,0,1))
        }
        if (getBlock(playerpos.add(-1,0,0))!!.block == Blocks.OBSIDIAN || getBlock(playerpos.add(0,0,-1))!!.block == Blocks.OBSIDIAN){
            city.add(playerpos.add(-1,0,-1))
        }
        for (i in city){
            if (getBlock(i)!!.block == Blocks.OBSIDIAN){
                drawBox(i,colors)
            }
        }
        city.clear()
    }

    private fun drawBox(drawpos: BlockPos , color:GSColor){
        val fillColor = GSColor(color, alpha.value)
        RenderUtil.drawBoxESP(drawpos, fillColor, color, lineWidth.value, true, true, true, 0.0, false, false, false, false, 0)
    }

    private fun getBlock(block: BlockPos): IBlockState?{
        return mc.world.getBlockState(block)
    }
}