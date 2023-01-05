package me.dyzjct.kura.module.modules.movement

import me.dyzjct.kura.event.events.client.PacketEvents
import me.dyzjct.kura.event.events.entity.MotionUpdateEvent
import me.dyzjct.kura.module.Category
import me.dyzjct.kura.module.Module
import net.minecraft.init.Blocks
import net.minecraft.network.play.client.CPacketConfirmTeleport
import net.minecraft.network.play.server.SPacketPlayerPosLook
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import kotlin.math.abs
import kotlin.math.floor

@Module.Info(name = "PositionBug", category = Category.MOVEMENT)
class PositionBug : Module() {
    var teleportID = 0
    private val offset = isetting("Offset", 1, 0, 10)
    private val onBurrow = bsetting("OnBurrow", false)
    private val toggle = bsetting("AutoToggle", false)

//    override fun onEnable(){
//        teleportID = 0
//    }
    @SubscribeEvent
    fun onTick(event: MotionUpdateEvent?) {
        val x = abs(mc.player.posX) - floor(abs(mc.player.posX))
        val z = abs(mc.player.posZ) - floor(abs(mc.player.posZ))
        if (x == 0.700 || x == 0.300 || z == 0.700 || z == 0.300 || !onBurrow.value && mc.world.getBlockState(
                BlockPos(
                    mc.player
                )
            ).block != Blocks.AIR
        ) return
        val playerVec = mc.player.positionVector
        if (mc.world.getBlockState(
                BlockPos(
                    playerVec.add(
                        Vec3d(
                            0.3 + offset.value / 100.0, 0.2, 0.0
                        )
                    )
                )
            ).block != Blocks.AIR
        ) {
            mc.player.setPosition(mc.player.posX + offset.value / 100.0, mc.player.posY, mc.player.posZ)
            if (toggle.value) disable()
            return
        }
        if (mc.world.getBlockState(
                BlockPos(
                    playerVec.add(
                        Vec3d(
                            -0.3 - offset.value / 100.0, 0.2, 0.0
                        )
                    )
                )
            ).block != Blocks.AIR
        ) {
            mc.player.setPosition(mc.player.posX - offset.value / 100.0, mc.player.posY, mc.player.posZ)
            if (toggle.value) disable()
            return
        }
        if (mc.world.getBlockState(
                BlockPos(
                    playerVec.add(
                        Vec3d(
                            0.0, 0.2, 0.3 + offset.value / 100.0
                        )
                    )
                )
            ).block != Blocks.AIR
        ) {
            mc.player.setPosition(mc.player.posX, mc.player.posY, mc.player.posZ + offset.value / 100.0)
            if (toggle.value) disable()
            return
        }
        if (mc.world.getBlockState(
                BlockPos(
                    playerVec.add(
                        Vec3d(
                            0.0, 0.2, -0.3 - offset.value / 100.0
                        )
                    )
                )
            ).block != Blocks.AIR
        ) {
            mc.player.setPosition(mc.player.posX, mc.player.posY, mc.player.posZ - offset.value / 100.0)
            if (toggle.value) disable()
        }
//        mc.player.connection.sendPacket(CPacketConfirmTeleport(++teleportID))
    }
//    @SubscribeEvent
//    open fun onPacketReceive(event: PacketEvents.Receive?){
//        val packet: SPacketPlayerPosLook = event!!.getPacket()
//        teleportID = packet.getTeleportId()
//    }
}