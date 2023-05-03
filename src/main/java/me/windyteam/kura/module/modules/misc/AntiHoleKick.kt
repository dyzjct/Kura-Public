package me.windyteam.kura.module.modules.misc

import me.windyteam.kura.event.events.entity.MotionUpdateEvent
import me.windyteam.kura.module.Category
import me.windyteam.kura.module.Module
import me.windyteam.kura.module.Module.Info
import me.windyteam.kura.module.modules.crystalaura.cystalHelper.FastRayTrace
import me.windyteam.kura.utils.TimerUtils
import me.windyteam.kura.utils.block.BlockInteractionHelper
import me.windyteam.kura.utils.block.BlockUtil
import me.windyteam.kura.utils.inventory.InventoryUtil
import me.windyteam.kura.utils.math.DamageUtil
import me.windyteam.kura.utils.math.MathUtil
import net.minecraft.entity.Entity
import net.minecraft.entity.item.EntityEnderCrystal
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.network.play.client.CPacketPlayer
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@Info(name = "AntiHoleKick" , category = Category.MISC)
object AntiHoleKick :Module(){
    private val crystal = settings("Crystal",true)
    private val delay = settings("Delay",50,0,500).b(crystal)
    private val maxSelf = settings("MaxSelfDamage",2,2,24).b(crystal)
    private val packet = settings("Packet",false)
    val timer = TimerUtils()

    @SubscribeEvent
    fun onTick(event: MotionUpdateEvent) {
        if (fullNullCheck()) return
        runCatching {
            if (getCrystal()) return
            if (!check()) return
            if (crystal.value){
                placeCrystal(getPlaceTarget()!!)
            }
            if (packet.value){
                mc.player.connection.sendPacket(CPacketPlayer.Position(mc.player.posX+114514,mc.player.posY+114514,mc.player.posZ+114514,false))
                mc.player.connection.sendPacket(CPacketPlayer.Position(mc.player.posX-114514,mc.player.posY-114514,mc.player.posZ-114514,false))
            }
        }
    }

    private fun check():Boolean{
        val playerPos = BlockPos(mc.player.posX,mc.player.posY,mc.player.posZ)
        return if (mc.world.getBlockState(playerPos.add(1,1,0)).block == Blocks.PISTON || mc.world.getBlockState(playerPos.add(1,1,0)).block == Blocks.STICKY_PISTON) true
        else if (mc.world.getBlockState(playerPos.add(-1,1,0)).block == Blocks.PISTON || mc.world.getBlockState(playerPos.add(-1,1,0)).block == Blocks.STICKY_PISTON) true
        else if (mc.world.getBlockState(playerPos.add(0,1,1)).block == Blocks.PISTON || mc.world.getBlockState(playerPos.add(0,1,1)).block == Blocks.STICKY_PISTON) true
        else mc.world.getBlockState(playerPos.add(0,1,-1)).block == Blocks.PISTON || mc.world.getBlockState(playerPos.add(0,1,-1)).block == Blocks.STICKY_PISTON
    }

    private fun placeCrystal(pos: BlockPos){
        if (InventoryUtil.findHotbarItem(Items.END_CRYSTAL) == -1) return
        if (!timer.passed(delay.value)) return
        mc.player.connection.sendPacket(CPacketPlayer.Rotation(
            BlockInteractionHelper.getLegitRotations(pos.add(0.5,0.5,0.5))[0],
            BlockInteractionHelper.getLegitRotations(pos.add(0.5,0.5,0.5))[1],
            true
        ))
        val old = mc.player.inventory.currentItem
        InventoryUtil.switchToHotbarSlot(InventoryUtil.findHotbarItem(Items.END_CRYSTAL),true)
        BlockUtil.placeCrystalOnBlock(pos,EnumHand.MAIN_HAND)
        InventoryUtil.switchToHotbarSlot(old,true)
    }

    private fun getCrystal():Boolean {
        var haveCrystal = false
        for (entity:Entity in mc.world.loadedEntityList) {
            if ((entity !is EntityEnderCrystal)) continue
            if (mc.player.getDistanceSq(entity) > 6) continue
            haveCrystal = true
        }
        return haveCrystal
    }

    private fun getPlaceTarget(): BlockPos? {
        var closestPos: BlockPos? = null
        var smallestDamage = 10.0f
        for (pos in BlockUtil.possiblePlacePositions(6.0f)) {
            val damage: Float =
                DamageUtil.calculateDamage(pos, mc.player)
            if (damage > maxSelf.value || mc.player.getDistanceSq(
                    pos
                ) >= MathUtil.square(6.0) && BlockUtil.rayTracePlaceCheck(
                    pos,
                    true,
                    1.0f
                )
            ) continue

            if (mc.player.positionVector.squareDistanceTo(
                    Vec3d(
                        pos.add(
                            0.5, 1.0, 0.5
                        )
                    )
                ) > 3.0 * 3.0 && !FastRayTrace.rayTraceVisible(
                    mc.player.positionVector.add(0.0, mc.player.getEyeHeight().toDouble(), 0.0),
                    pos.getX() + 0.5,
                    (pos.getY() + 1f + 1.7f).toDouble(),
                    pos.getZ() + 0.5,
                    20,
                    BlockPos.MutableBlockPos()
                )
            ) continue

            if (closestPos == null) {
                smallestDamage = damage
                closestPos = pos
                continue
            }

            if (!(damage < smallestDamage) && (damage != smallestDamage || mc.player.getDistanceSq(
                    pos
                ) >= mc.player.getDistanceSq(closestPos))
            ) continue

            smallestDamage = damage
            closestPos = pos
        }
        return closestPos
    }
}