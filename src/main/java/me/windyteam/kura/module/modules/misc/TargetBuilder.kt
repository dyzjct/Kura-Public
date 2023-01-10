package me.windyteam.kura.module.modules.misc

import kura.utils.Wrapper
import kura.utils.isReplaceable
import kura.utils.world
import me.windyteam.kura.event.events.block.BlockBreakEvent
import me.windyteam.kura.event.events.entity.MotionUpdateEvent
import me.windyteam.kura.friend.FriendManager
import me.windyteam.kura.manager.HotbarManager
import me.windyteam.kura.manager.SpeedManager
import me.windyteam.kura.module.Category
import me.windyteam.kura.module.Module
import me.windyteam.kura.module.Module.Info
import me.windyteam.kura.utils.Timer
import me.windyteam.kura.utils.block.BlockUtil
import me.windyteam.kura.utils.inventory.InventoryUtil
import me.windyteam.kura.utils.player.getTarget
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.util.EnumHand
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@Info(name = "TargetBuilder", category = Category.MISC)
class TargetBuilder : Module() {
    private var range = isetting("Range", 6, 0, 8)
    private var delay = isetting("Delay", 50, 0, 250)
    private var down = bsetting("Down", true)
    private var webdown = bsetting("WebDown", true).b(down)
    private var head = bsetting("Head", false)
    private var webhead = bsetting("webHead", false).b(head)
    private var movespeed = isetting("MaxTargetSpeed",10,0,20)
    private var webfoot = bsetting("WebFoot", false)
    private var webface = bsetting("webFace", false)
    private var rotate = bsetting("Rotate", false)
    private var packet = bsetting("PacketPlace", true)
    private val timer = Timer()
    var target: EntityPlayer? = null
    private var breakpos: BlockPos? = null
    private var web = -1
    private var obi = -1

    @SubscribeEvent
    fun onTick(event: MotionUpdateEvent.Tick) {
        if (fullNullCheck()) return
        target = getTarget(range.value)
        if (mc.player == null || mc.world == null) return
        if (target == null) return
        if (FriendManager.isFriend(target!!.name))return
        web = InventoryUtil.findHotbarBlock(Blocks.WEB)
        obi = InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN)
        if (down.value||head.value){
            if (obi == -1) {
                return
            }
        }
        val playerpos = BlockPos(target!!.posX, target!!.posY, target!!.posZ)
        if (down.value) {
            if (getBlock(playerpos.add(0, -1, 0)).block == Blocks.AIR) {
                if (webdown.value && web != -1){
                    placeweb(playerpos.add(0, -1, 0))
                } else{
                    placeobi(playerpos.add(0, -1, 0))
                }
            }
        }
        if (head.value && playerpos.add(0,2,0) != breakpos && SpeedManager.getPlayerSpeed(target) <= movespeed.value) {
            if (getBlock(playerpos.add(0, 2, 0)).block == Blocks.AIR) {
                if (getBlock(playerpos.add(1,2,0)).block == Blocks.AIR && getBlock(playerpos.add(-1,2,0)).block == Blocks.AIR && getBlock(playerpos.add(0,2,1)).block == Blocks.AIR && getBlock(playerpos.add(0,2,-1)).block == Blocks.AIR) {
                    if (world.isPlaceable(playerpos.add(0, 2, 1)) && world.isPlaceable(playerpos.add(0, 2, 1)) && getBlock(playerpos.add(0, 2, 1)).block == Blocks.AIR && playerpos.add(
                            0,
                            2,
                            1
                        ) != breakpos && getBlock(playerpos.add(0, 1, 1)).block != Blocks.AIR
                    ) {
                        placeobi(playerpos.add(0, 2, 1))
                    } else if (world.isPlaceable(playerpos.add(0, 2, -1)) && world.isPlaceable(playerpos.add(0, 2, -1)) && getBlock(playerpos.add(0, 2, -1)).block == Blocks.AIR && playerpos.add(
                            0,
                            2,
                            -1
                        ) != breakpos && getBlock(playerpos.add(0, 1, -1)).block != Blocks.AIR
                    ) {
                        placeobi(playerpos.add(0, 2, -1))
                    } else if (world.isPlaceable(playerpos.add(1, 2, 0)) && world.isPlaceable(playerpos.add(1, 2, 0)) && getBlock(playerpos.add(1, 2, 0)).block == Blocks.AIR && playerpos.add(
                            1,
                            2,
                            0
                        ) != breakpos && getBlock(playerpos.add(1, 1, 0)).block != Blocks.AIR
                    ) {
                        placeobi(playerpos.add(1, 2, 0))
                    } else if (world.isPlaceable(playerpos.add(-1, 1, 0)) && world.isPlaceable(playerpos.add(-1, 2, 0)) && getBlock(playerpos.add(-1, 2, 0)).block == Blocks.AIR && playerpos.add(
                            -1,
                            2,
                            0
                        ) != breakpos && getBlock(playerpos.add(-1, 1, 0)).block != Blocks.AIR
                    ) {
                        placeobi(playerpos.add(-1, 2, 0))
                    } else if (world.isPlaceable(playerpos.add(0, 1, 1)) && world.isPlaceable(playerpos.add(0, 2, 1)) && getBlock(playerpos.add(0, 2, 1)).block == Blocks.AIR && playerpos.add(
                            0,
                            2,
                            1
                        ) != breakpos
                    ) {
                        if (getBlock(playerpos.add(0, -1, 1)).block == Blocks.AIR) {
                            placeobi(playerpos.add(0, -1, 1))
                        }
                        if (getBlock(playerpos.add(0, 0, 1)).block == Blocks.AIR) {
                            placeobi(playerpos.add(0, 0, 1))
                        }
                        placeobi(playerpos.add(0, 1, 1))
                        placeobi(playerpos.add(0, 2, 1))
                    } else if (world.isPlaceable(playerpos.add(0, 1, -1)) && world.isPlaceable(playerpos.add(0, 2, -1)) && getBlock(playerpos.add(0, 2, -1)).block == Blocks.AIR && playerpos.add(
                            0,
                            2,
                            -1
                        ) != breakpos
                    ) {
                        if (getBlock(playerpos.add(0, -1, -1)).block == Blocks.AIR) {
                            placeobi(playerpos.add(0, -1, -1))
                        }
                        if (getBlock(playerpos.add(0, 0, -1)).block == Blocks.AIR) {
                            placeobi(playerpos.add(0, 0, -1))
                        }
                        placeobi(playerpos.add(0, 1, -1))
                        placeobi(playerpos.add(0, 2, -1))
                    } else if (world.isPlaceable(playerpos.add(1, 1, 0)) && world.isPlaceable(playerpos.add(1, 2, 0)) && getBlock(playerpos.add(1, 2, 0)).block == Blocks.AIR && playerpos.add(
                            1,
                            2,
                            0
                        ) != breakpos
                    ) {
                        if (getBlock(playerpos.add(1, -1, 0)).block == Blocks.AIR) {
                            placeobi(playerpos.add(1, -1, 0))
                        }
                        if (getBlock(playerpos.add(1, 0, 0)).block == Blocks.AIR) {
                            placeobi(playerpos.add(1, 0, 0))
                        }
                        placeobi(playerpos.add(1, 1, 0))
                        placeobi(playerpos.add(1, 2, 0))
                    } else if (world.isPlaceable(playerpos.add(-1,  1, 0)) && world.isPlaceable(playerpos.add(-1, 2, 0)) && getBlock(playerpos.add(-1, 2, 0)).block == Blocks.AIR && playerpos.add(
                            -1,
                            2,
                            0
                        ) != breakpos
                    ) {
                        if (getBlock(playerpos.add(-1, -1, 0)).block == Blocks.AIR) {
                            placeobi(playerpos.add(-1, -1, 0))
                        }
                        if (getBlock(playerpos.add(-1, 0, 0)).block == Blocks.AIR) {
                            placeobi(playerpos.add(-1, 0, 0))
                        }
                        placeobi(playerpos.add(-1, 2, 0))
                        placeobi(playerpos.add(-1, 2, 0))
                    }
                }
                if (webhead.value && web != -1){
                    placeweb(playerpos.add(0, 2, 0))
                } else{
                    placeobi(playerpos.add(0, 2, 0))
                }
            }
        }
        if (webfoot.value && web != -1) {
            if (getBlock(playerpos.add(0, 0, 0)).block == Blocks.AIR) {
                placeweb(playerpos.add(0, 0, 0))
            }
        }
        if (webface.value && web != -1) {
            if (getBlock(playerpos.add(0, 1, 0)).block == Blocks.AIR) {
                placeweb(playerpos.add(0, 1, 0))
            }
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

    private fun placeweb(pos: BlockPos) {
        if (pos == breakpos)return
        if (!timer.passedMs(delay.value.toLong())) return
        val old = mc.player.inventory.currentItem
        HotbarManager.spoofHotbar(web)
        BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, rotate.value, packet.value)
        HotbarManager.spoofHotbar(old)
    }

    private fun placeobi(pos: BlockPos) {
        if (!world.isPlaceable(pos))return
        if (pos == breakpos)return
        if (!timer.passedMs(delay.value.toLong())) return
        val old = mc.player.inventory.currentItem
        HotbarManager.spoofHotbar(obi)
        BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, rotate.value, packet.value)
        HotbarManager.spoofHotbar(old)
    }

    fun World.isPlaceable(pos: BlockPos, ignoreSelfCollide: Boolean = false) =
        this.getBlockState(pos).isReplaceable && this.checkNoEntityCollision(
            AxisAlignedBB(pos),
            if (ignoreSelfCollide) Wrapper.player else null
    )

    private fun getBlock(block: BlockPos): IBlockState {
        return mc.world.getBlockState(block)
    }

    override fun getHudInfo(): String {
        if (target == null){
            return "null"
        } else{
            return target!!.name
        }
    }
}