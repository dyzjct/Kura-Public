package me.dyzjct.kura.module.modules.combat

import me.dyzjct.kura.manager.HotbarManager
import me.dyzjct.kura.module.Category
import me.dyzjct.kura.module.Module
import me.dyzjct.kura.module.Module.Info
import me.dyzjct.kura.utils.NTMiku.BlockUtil
import me.dyzjct.kura.utils.Timer
import me.dyzjct.kura.utils.inventory.InventoryUtil
import me.dyzjct.kura.utils.player.getTarget
import kura.utils.isPlaceable
import kura.utils.world
import me.dyzjct.kura.event.events.block.BlockBreakEvent
import me.dyzjct.kura.friend.FriendManager
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent


@Info(name = "TNTHead", category = Category.COMBAT)
class TNTHead : Module(){

    private var range = isetting("Range",6,0,8)
    private var rotate = bsetting("Rotate",false)
    private var target: EntityPlayer? = null
    private var breakpos: BlockPos? = null
    private var delay = 2000
    private var redstone = -1
    private var tnt = -1
    private val timer = Timer()

    override fun onUpdate(){
        if (fullNullCheck()){
            return
        }
        target = getTarget(range.value)
        if (target == null){
            return
        }
        if (mc.player == null || mc.world == null) {
            return
        }
        if (!mc.player.onGround){
            return
        }
        redstone = InventoryUtil.findHotbarBlock(Blocks.REDSTONE_BLOCK)
        if (redstone == -1) {
            return
        }
        tnt = InventoryUtil.findHotbarBlock(Blocks.TNT)
        if (tnt == -1) {
            return
        }
        if (FriendManager.isFriend(target!!.name)){
            return
        }
        val playerpos = BlockPos(target!!.posX, target!!.posY, target!!.posZ)
        if (getBlock(playerpos.add(0,2,0)).block==Blocks.AIR){
            if (getBlock(playerpos.add(0,2,1)).block==Blocks.AIR&&getBlock(playerpos.add(0,2,-1)).block==Blocks.AIR&&getBlock(playerpos.add(1,2,0)).block==Blocks.AIR&&getBlock(playerpos.add(-1,2,0)).block==Blocks.AIR){
                if (getBlock(playerpos.add(0,2,1)).block==Blocks.AIR&&playerpos.add(0,2,1)!=breakpos){
                    placeobi(playerpos.add(0,2,1))
                    if (getBlock(playerpos.add(0,1,1)).block==Blocks.AIR&&playerpos.add(0,1,1)!=breakpos){
                        placeobi(playerpos.add(0,1,1))
                        if (getBlock(playerpos.add(0,0,1)).block==Blocks.AIR&&playerpos.add(0,0,1)!=breakpos){
                            placeobi(playerpos.add(0,0,1))
                        }
                    }
                } else if (getBlock(playerpos.add(0,2,-1)).block==Blocks.AIR&&playerpos.add(0,2,-1)!=breakpos){
                    placeobi(playerpos.add(0,2,-1))
                    if (getBlock(playerpos.add(0,1,-1)).block==Blocks.AIR&&playerpos.add(0,1,-1)!=breakpos){
                        placeobi(playerpos.add(0,1,-1))
                        if (getBlock(playerpos.add(0,0,-1)).block==Blocks.AIR&&playerpos.add(0,0,-1)!=breakpos){
                            placeobi(playerpos.add(0,0,-1))
                        }
                    }
                } else if (getBlock(playerpos.add(1,2,0)).block==Blocks.AIR&&playerpos.add(1,2,0)!=breakpos){
                    placeobi(playerpos.add(1,2,0))
                    if (getBlock(playerpos.add(1,1,0)).block==Blocks.AIR&&playerpos.add(1,1,0)!=breakpos){
                        placeobi(playerpos.add(1,1,0))
                        if (getBlock(playerpos.add(1,0,0)).block==Blocks.AIR&&playerpos.add(1,0,0)!=breakpos){
                            placeobi(playerpos.add(1,0,0))
                        }
                    }
                } else if (getBlock(playerpos.add(-1,2,0)).block==Blocks.AIR&&playerpos.add(-1,2,0)!=breakpos){
                    placeobi(playerpos.add(-1,2,0))
                    if (getBlock(playerpos.add(-1,1,0)).block==Blocks.AIR&&playerpos.add(-1,1,0)!=breakpos){
                        placeobi(playerpos.add(-1,1,0))
                        if (getBlock(playerpos.add(-1,0,0)).block==Blocks.AIR&&playerpos.add(-1,0,0)!=breakpos){
                            placeobi(playerpos.add(-1,0,0))
                        }
                    }
                }
            }
            placetnt(playerpos.add(0,2,0))
            if (getBlock(playerpos.add(0,3,1)).block==Blocks.AIR&&playerpos.add(0,3,1)!=breakpos){
                placeobi(playerpos.add(0,3,1))
            } else if (getBlock(playerpos.add(0,3,-1)).block==Blocks.AIR&&playerpos.add(0,3,-1)!=breakpos){
                placeobi(playerpos.add(0,3,-1))
            } else if (getBlock(playerpos.add(1,3,0)).block==Blocks.AIR&&playerpos.add(1,3,0)!=breakpos){
                placeobi(playerpos.add(1,3,0))
            } else if (getBlock(playerpos.add(-1,3,0)).block==Blocks.AIR&&playerpos.add(-1,3,0)!=breakpos){
                placeobi(playerpos.add(-1,3,0))
            }
            if (getBlock(playerpos.add(0,3,0)).block==Blocks.AIR){
                placeobi(playerpos.add(0,3,0))
            }
        }
        if (getBlock(playerpos.add(0,2,1)).block==Blocks.AIR&&playerpos.add(0,2,1)!=breakpos){
            placeredsotne(playerpos.add(0,2,1))
        } else if (getBlock(playerpos.add(0,2,-1)).block==Blocks.AIR&&playerpos.add(0,2,-1)!=breakpos){
            placeredsotne(playerpos.add(0,2,-1))
        } else if (getBlock(playerpos.add(1,2,0)).block==Blocks.AIR&&playerpos.add(1,2,0)!=breakpos){
            placeredsotne(playerpos.add(1,2,0))
        } else if (getBlock(playerpos.add(-1,2,0)).block==Blocks.AIR&&playerpos.add(-1,2,0)!=breakpos){
            placeredsotne(playerpos.add(-1,2,0))
        }
    }

    @SubscribeEvent
    fun onBreak(event: BlockBreakEvent) {
        if (fullNullCheck()) {
            return
        }
        if (event.position != null) {
            breakpos = event.position
        }
    }

    private fun placeobi(pos: BlockPos) {
        if (fullNullCheck()) return
        if (pos==breakpos) return
        if (!timer.passedMs(this.delay.toLong())){
            return
        }
        if (!world.isPlaceable(pos)){
            return
        }
        val obi = InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN)
        if (obi == -1) {
            return
        }
        val old = mc.player.inventory.currentItem
        HotbarManager.spoofHotbar(obi)
        BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, rotate.value, true, false)
        HotbarManager.spoofHotbar(old)
    }

    private fun placeredsotne(pos: BlockPos) {
        if (fullNullCheck()) return
        if (pos==breakpos) return
        if (!timer.passedMs(this.delay.toLong())){
            return
        }
        if (!world.isPlaceable(pos)){
            return
        }
        val old = mc.player.inventory.currentItem
        HotbarManager.spoofHotbar(redstone)
        BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, rotate.value, true, false)
        HotbarManager.spoofHotbar(old)
    }

    private fun placetnt(pos: BlockPos) {
        if (fullNullCheck()) return
        if (pos==breakpos) return
        if (!timer.passedMs(this.delay.toLong())){
            return
        }
        if (!world.isPlaceable(pos)){
            return
        }
        val old = mc.player.inventory.currentItem
        HotbarManager.spoofHotbar(tnt)
        BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, rotate.value, true, false)
        HotbarManager.spoofHotbar(old)
    }

    private fun getBlock(block: BlockPos): IBlockState {
        return mc.world.getBlockState(block)
    }
}