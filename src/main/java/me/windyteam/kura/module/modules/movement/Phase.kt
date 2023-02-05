package me.windyteam.kura.module.modules.movement

import me.windyteam.kura.module.Category
import me.windyteam.kura.module.Module
import me.windyteam.kura.module.ModuleManager
import me.windyteam.kura.module.modules.combat.HoleSnap
import net.minecraft.network.play.client.CPacketPlayer
import net.minecraft.util.math.MathHelper
import kotlin.math.floor

//Thanks For asphyxia1337(Alpha432)
@Module.Info(
    name = "Phase", category = Category.MOVEMENT, description = "Phases into blocks nearby to prevent crystal damage."
)
class Phase : Module() {
    private val timeout = isetting("Timeout", 5, 1, 10)
    private val collision = isetting("CollisionSize",2,-20,2)
    private var packets = 0
    override fun onDisable() {
        packets = 0
    }

    override fun getHudInfo(): String {
        return packets.toString()
    }

    override fun onUpdate() {
        if (ModuleManager.getModuleByClass(Speed::class.java).isEnabled) {
            disable()
        }
        if (ModuleManager.getModuleByClass(Step::class.java).isEnabled) {
            disable()
        }
        if (ModuleManager.getModuleByClass(HoleSnap::class.java).isEnabled) {
            disable()
        }
        if (mc.world.getCollisionBoxes(mc.player, mc.player.entityBoundingBox.grow(0.01, 0.0, 0.01)).size < collision.value) {
            mc.player.setPosition(
                roundToClosest(mc.player.posX, floor(mc.player.posX) + 0.301, floor(mc.player.posX) + 0.699),
                mc.player.posY,
                roundToClosest(
                    mc.player.posZ, floor(mc.player.posZ) + 0.301, floor(mc.player.posZ) + 0.699
                )
            )
            packets = 0
        } else if (mc.player.ticksExisted % timeout.value == 0) {
            mc.player.setPosition(
                mc.player.posX + MathHelper.clamp(
                    roundToClosest(
                        mc.player.posX, floor(mc.player.posX) + 0.241, floor(
                            mc.player.posX
                        ) + 0.759
                    ) - mc.player.posX, -0.03, 0.03
                ), mc.player.posY, mc.player.posZ + MathHelper.clamp(
                    roundToClosest(
                        mc.player.posZ, floor(mc.player.posZ) + 0.241, floor(mc.player.posZ) + 0.759
                    ) - mc.player.posZ, -0.03, 0.03
                )
            )
            mc.player.connection.sendPacket(
                CPacketPlayer.Position(
                    mc.player.posX, mc.player.posY, mc.player.posZ, true
                )
            )
            mc.player.connection.sendPacket(
                CPacketPlayer.Position(
                    roundToClosest(
                        mc.player.posX, floor(mc.player.posX) + 0.23, floor(
                            mc.player.posX
                        ) + 0.77
                    ), mc.player.posY, roundToClosest(
                        mc.player.posZ, floor(mc.player.posZ) + 0.23, floor(
                            mc.player.posZ
                        ) + 0.77
                    ), true
                )
            )
            packets++
        }
    }

    private fun roundToClosest(num: Double, low: Double, high: Double): Double {
        val d1 = num - low
        val d2 = high - num
        return if (d2 > d1) {
            low
        } else {
            high
        }
    }

    companion object {
        var INSTANCE: Phase? = null
    }
}