package me.windyteam.kura.module.modules.misc

import me.windyteam.kura.event.events.client.PacketEvents
import me.windyteam.kura.module.Category
import me.windyteam.kura.module.Module
import me.windyteam.kura.module.Module.Info
import me.windyteam.kura.module.ModuleManager
import me.windyteam.kura.module.modules.movement.Flight
import me.windyteam.kura.utils.mc.EntityUtil
import net.minecraft.block.state.IBlockState
import net.minecraft.init.Blocks
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@Info(name = "AntiHoleKicker", category = Category.MISC)
class AntiHoleKicker :Module(){
    private var pistonList = mutableListOf<BlockPos>()

    @SubscribeEvent
    fun onPacketReceive(event: PacketEvents.Send){
        if (fullNullCheck()) return
        if (mc.player == null || mc.world == null) return
        val playerPos = BlockPos(mc.player.posX,mc.player.posY,mc.player.posZ)
        loadPistonList()
        if (EntityUtil.isInHole(mc.player) || getBlock(playerPos.add(0,0,0)).block != Blocks.AIR){
            for (i in pistonList){
                if (getBlock(i).block == Blocks.PISTON || getBlock(i).block == Blocks.STICKY_PISTON){
//                    if (event.packet is CPacketPlayer) {
//                        event.isCanceled = true
//                    }
                    if (ModuleManager.getModuleByClass(Flight::class.java).isDisabled){
                        ModuleManager.getModuleByClass(Flight::class.java).enable()
                    }
                }
            }
        }
        pistonList.clear()
    }

    private fun loadPistonList(){
        if (mc.player == null || mc.world == null) return
        val playerPos = BlockPos(mc.player.posX,mc.player.posY,mc.player.posZ)
        pistonList.add(playerPos.add(1,1,0))
        pistonList.add(playerPos.add(-1,1,0))
        pistonList.add(playerPos.add(0,1,1))
        pistonList.add(playerPos.add(0,1,-1))
    }

    private fun getBlock(block: BlockPos): IBlockState {
        return mc.world.getBlockState(block)
    }

}