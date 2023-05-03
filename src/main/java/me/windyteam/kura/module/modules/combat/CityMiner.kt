package me.windyteam.kura.module.modules.combat

import me.windyteam.kura.event.events.entity.MotionUpdateEvent
import me.windyteam.kura.module.Category
import me.windyteam.kura.module.Module
import me.windyteam.kura.module.modules.player.PacketMine
import me.windyteam.kura.utils.block.BlockInteractionHelper
import me.windyteam.kura.utils.block.BlockUtil
import me.windyteam.kura.utils.combat.HoleUtils.isAir
import me.windyteam.kura.utils.inventory.InventoryUtil
import me.windyteam.kura.utils.mc.ChatUtil
import me.windyteam.kura.utils.other.clearCity
import me.windyteam.kura.utils.other.loadCity
import me.windyteam.kura.utils.player.getTarget
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@Module.Info(name = "CityMiner", category = Category.COMBAT)
object CityMiner : Module() {
    private val range = settings("Range",6,1,10)
    private val mineBurrow = settings("MineBurrow",true)
    private val mineFeet = settings("MineFeet",true)
    private val findBest = settings("FindBest",true)
    private val ghostHand = settings("GhostHand",true)
    private val rotate = settings("Rotate",true)
    private val autoToggle = settings("AutoToggle",false)
    private val debug = settings("deBug",false)
    var target:EntityPlayer? = null
    var cityList = mutableListOf<BlockPos>()
    var antiList = mutableListOf<BlockPos>()

    override fun onEnable() {
        if (fullNullCheck()) return
        loadCity()
    }

    @SubscribeEvent
    fun onTick(event: MotionUpdateEvent){
        if (fullNullCheck()) return
        target = getTarget(range.value)
        if (target == null) {
            if (autoToggle.value) {
                disable()
            }
            return
        }
        val targetPos = BlockPos(target!!.posX, target!!.posY, target!!.posZ)
        if (mineBurrow.value){
            if (!mc.world.isAir(targetPos) && getBlock(targetPos)!!.block != Blocks.BEDROCK){
                if (PacketMine.currentPos != targetPos){
                    if (rotate.value) {
                        event.setRotation(
                            BlockInteractionHelper.getLegitRotations(targetPos.add(0.5,0.5,0.5))[0],
                            BlockInteractionHelper.getLegitRotations(targetPos.add(0.5,0.5,0.5))[1]
                        )
                    }
                    mineBlock(targetPos)
                    if (rotate.value) {
                        event.setRotation(
                            BlockInteractionHelper.getLegitRotations(targetPos.add(0.5,0.5,0.5))[0],
                            BlockInteractionHelper.getLegitRotations(targetPos.add(0.5,0.5,0.5))[1]
                        )
                    }
                }
            }
        }
        runCatching {
            if (mineFeet.value){
                if (mc.world.isAir(targetPos) || getBlock(targetPos)!!.block == Blocks.BEDROCK){
                    var haveBest = false
                    var doFeetMiner = false
                    for (x in 0..3){
                        if (!mc.world.isAir(targetPos.add(cityList[x])) && getBlock(targetPos.add(cityList[x]))!!.block != Blocks.BEDROCK){
                            doFeetMiner = true
                        }
                    }
                    if (!doFeetMiner) {
                        if (autoToggle.value){
                            disable()
                        }
                    }
                    if (doFeetMiner){
                        for (i in 0..3){
                            if (mc.world.isAir(targetPos.add(cityList[i])) || getBlock(targetPos.add(cityList[i]))!!.block == Blocks.BEDROCK){
                                continue
                            }
                            if (!haveBest){
                                for (x in 0..3){
                                    if (isPlaceCrystal(targetPos.add(antiList[x]))){
                                        haveBest = true
                                    }
                                }
                            }
                            if (haveBest && findBest.value){
                                if (!isPlaceCrystal(targetPos.add(antiList[i]))){
                                    continue
                                }
                            }
                            if (PacketMine.currentPos != targetPos.add(cityList[i])){
                                if (rotate.value) {
                                    event.setRotation(
                                        BlockInteractionHelper.getLegitRotations(targetPos.add(cityList[i]).add(0.5,0.5,0.5))[0],
                                        BlockInteractionHelper.getLegitRotations(targetPos.add(cityList[i]).add(0.5,0.5,0.5))[1]
                                    )
                                }
                                mineBlock(targetPos.add(cityList[i]))
                                if (rotate.value) {
                                    event.setRotation(
                                        BlockInteractionHelper.getLegitRotations(targetPos.add(cityList[i]).add(0.5,0.5,0.5))[0],
                                        BlockInteractionHelper.getLegitRotations(targetPos.add(cityList[i]).add(0.5,0.5,0.5))[1]
                                    )
                                }
                            }
                            break
                        }
                    }
                }
            }
        }
    }

    private fun mineBlock(block: BlockPos){
        if (debug.value){
            ChatUtil.sendMessage("doMiner")
            ChatUtil.sendMessage(block.toString())
        }
        val old = mc.playerController.currentPlayerItem
        if (ghostHand.value) mc.playerController.currentPlayerItem = InventoryUtil.findHotbarItem(Items.DIAMOND_PICKAXE)
        mc.playerController.onPlayerDamageBlock(
            block, BlockUtil.getRayTraceFacing(block)
        )
        if (ghostHand.value) mc.playerController.currentPlayerItem = old
        if (autoToggle.value) {
            disable()
        }
    }

    fun getBlock(block: BlockPos): IBlockState? {
        return mc.world.getBlockState(block)
    }

    private fun isPlaceCrystal(block: BlockPos): Boolean {
        return getBlock(block)!!.block == Blocks.AIR && getBlock(block.add(0,1,0))!!.block == Blocks.AIR && getBlock(block.add(0,-1,0))!!.block != Blocks.AIR
    }

    override fun onDisable() {
        if (fullNullCheck()) return
        clearCity()
    }
}