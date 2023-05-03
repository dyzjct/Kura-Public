package me.windyteam.kura.module.modules.movement

import me.windyteam.kura.event.events.client.PacketEvents
import me.windyteam.kura.event.events.entity.MoveEvent
import me.windyteam.kura.gui.Notification
import me.windyteam.kura.module.Category
import me.windyteam.kura.module.Module
import me.windyteam.kura.setting.Setting
import me.windyteam.kura.utils.Timer
import me.windyteam.kura.utils.entity.EntityUtil
import me.windyteam.kura.utils.math.RandomUtil
import me.windyteam.kura.utils.mc.ChatUtil.sendClientMessage
import net.minecraft.client.Minecraft
import net.minecraft.network.Packet
import net.minecraft.network.play.client.CPacketPlayer
import net.minecraft.network.play.client.CPacketPlayer.PositionRotation
import net.minecraft.network.play.server.SPacketPlayerPosLook
import net.minecraft.util.EnumHand
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.MathHelper
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

/**
 * Created by 086 on 25/08/2017.
 */
@Module.Info(category = Category.MOVEMENT, description = "Makes the player fly", name = "Flight")
class Flight : Module() {
    var mode = msetting("Mode", FlightMode.CREATIVE)
    var speed: Setting<Float> = fsetting("Speed", 2f, 0.1f, 10f)
    var motion = fsetting("Motion", 0f, 0f, 1f)
    var glide = bsetting("Glide",true)
    var glideSpeed = dsetting("GlideSpeed",0.0,0.1,10.0).b(glide)
    var antiKick = bsetting("AntiKick",true)
    var DamageBoost = bsetting("DamageBoost", false)
    var timer = Timer()
    var delay = Timer()
    var warn = false
    var launchY = 0f
    private val groundTimer = Timer()
    fun damagePlayer(d: Double) {
        var d = d
        if (d < 1) d = 1.0
        if (d > MathHelper.floor(mc.player.maxHealth)) d = MathHelper.floor(mc.player.maxHealth).toDouble()
        val offset = 0.0625
        if (mc.player != null && mc.connection != null && mc.player.onGround) {
            var i = 0
            while (i <= (3 + d) / offset) {
                mc.player.connection.sendPacket(
                    CPacketPlayer.Position(
                        mc.player.posX,
                        mc.player.posY + offset, mc.player.posZ, false
                    )
                )
                mc.player.connection.sendPacket(
                    CPacketPlayer.Position(
                        mc.player.posX,
                        mc.player.posY, mc.player.posZ, i.toDouble() == (3 + d) / offset
                    )
                )
                i++
            }
        }
    }

    @SubscribeEvent
    fun onPacket(event: PacketEvents.Receive) {
        if (fullNullCheck()) {
            return
        }
        if (event.getPacket<Packet<*>>() is SPacketPlayerPosLook) {
            if (!warn) {
                sendClientMessage("(LagBackCheck) Fly Disabled", Notification.Type.ERROR)
                warn = true
            }
            toggle()
        }
    }

    override fun onEnable() {
        if (mc.player == null) return
        warn = false
        delay.reset()
        launchY = mc.player.posY.toFloat()
        if (mode.value == FlightMode.VANILLA) {
            mc.player.capabilities.isFlying = true
            if (mc.player.capabilities.isCreativeMode) return
            mc.player.capabilities.allowFlying = true
        }
        if (mode.value == FlightMode.OLDNCP) {
            if (!mc.player.onGround) {
                return
            }
            for (i in 0..2) {
                mc.connection!!.sendPacket(
                    CPacketPlayer.Position(
                        mc.player.posX,
                        mc.player.posY + 1.01,
                        mc.player.posZ,
                        false
                    )
                )
                mc.connection!!.sendPacket(
                    CPacketPlayer.Position(
                        mc.player.posX,
                        mc.player.posY,
                        mc.player.posZ,
                        false
                    )
                )
            }
            mc.player.jump()
            mc.player.swingArm(EnumHand.MAIN_HAND)
        }
        if (mode.value == FlightMode.NEWNCP) {
            if (!mc.player.onGround) {
                return
            }
            for (i in 0..64) {
                mc.connection!!.sendPacket(
                    CPacketPlayer.Position(
                        mc.player.posX,
                        mc.player.posY + 0.049,
                        mc.player.posZ,
                        false
                    )
                )
                mc.connection!!.sendPacket(
                    CPacketPlayer.Position(
                        mc.player.posX,
                        mc.player.posY,
                        mc.player.posZ,
                        false
                    )
                )
            }
            mc.connection!!.sendPacket(
                CPacketPlayer.Position(
                    mc.player.posX,
                    mc.player.posY + 0.1,
                    mc.player.posZ,
                    true
                )
            )
            mc.player.motionX *= 0.1
            mc.player.motionZ *= 0.1
            mc.player.swingArm(EnumHand.MAIN_HAND)
        }
    }

    @SubscribeEvent
    fun moveEvent(event: MoveEvent) {
        if (fullNullCheck()) {
            return
        }
        if (delay.passed(1500)) {
            warn = false
            delay.reset()
        }
        if (DamageBoost.value) {
            if (timer.passed(500)) {
                damagePlayer(RandomUtil.nextInt(1, 3).toDouble())
                timer.reset()
            }
        }
        when (mode.value) {
            FlightMode.STATIC -> {
                mc.player.capabilities.isFlying = false
                mc.player.motionX = 0.0
                mc.player.motionY = 0.0
                mc.player.motionZ = 0.0
                mc.player.jumpMovementFactor = speed.value
                if (mc.gameSettings.keyBindJump.isKeyDown) mc.player.motionY += speed.value.toDouble()
                if (mc.gameSettings.keyBindSneak.isKeyDown) mc.player.motionY -= speed.value.toDouble()
            }
            
            FlightMode.TEST -> {
                mc.player.capabilities.isFlying = false
                mc.player.setVelocity(0.0, 0.0, 0.0)
                if (mc.gameSettings.keyBindJump.isKeyDown) mc.player.motionY += speed.value
                if (mc.gameSettings.keyBindSneak.isKeyDown) mc.player.motionY -= speed.value
                if (!mc.player.collidedVertically && glide.value) mc.player.motionY -= glideSpeed.value
                event.setSpeed(speed.value.toDouble())
                handleVanillaKickBypass()
            }
            
            FlightMode.VANILLA -> {
                mc.player.capabilities.flySpeed = speed.value / 100f
                mc.player.capabilities.isFlying = true
                if (mc.player.capabilities.isCreativeMode) return
                mc.player.capabilities.allowFlying = true
            }

            FlightMode.PACKET -> {
                var angle: Int
                val forward = mc.gameSettings.keyBindForward.isKeyDown
                val left = mc.gameSettings.keyBindLeft.isKeyDown
                val right = mc.gameSettings.keyBindRight.isKeyDown
                val back = mc.gameSettings.keyBindBack.isKeyDown
                if (left && right) angle = if (forward) 0 else if (back) 180 else -1 else if (forward && back) angle =
                    if (left) -90 else (if (right) 90 else -1) else {
                    angle = if (left) -90 else if (right) 90 else 0
                    if (forward) angle /= 2 else if (back) angle = 180 - angle / 2
                }
                if (angle != -1 && (forward || left || right || back)) {
                    val yaw = mc.player.rotationYaw + angle
                    mc.player.motionX = EntityUtil.getRelativeX(yaw) * 0.2f
                    mc.player.motionZ = EntityUtil.getRelativeZ(yaw) * 0.2f
                }
                mc.player.motionY = 0.0
                mc.player.connection.sendPacket(
                    PositionRotation(
                        mc.player.posX + mc.player.motionX,
                        mc.player.posY + if (Minecraft.getMinecraft().gameSettings.keyBindJump.isKeyDown) 0.0622 else 0 - (if (Minecraft.getMinecraft().gameSettings.keyBindSneak.isKeyDown) 0.0622 else 0).toDouble(),
                        mc.player.posZ + mc.player.motionZ,
                        mc.player.rotationYaw,
                        mc.player.rotationPitch,
                        false
                    )
                )
                mc.player.connection.sendPacket(
                    PositionRotation(
                        mc.player.posX + mc.player.motionX,
                        mc.player.posY - 42069,
                        mc.player.posZ + mc.player.motionZ,
                        mc.player.rotationYaw,
                        mc.player.rotationPitch,
                        true
                    )
                )
            }

            FlightMode.CREATIVE -> mc.player.capabilities.isFlying = true
            FlightMode.OLDNCP -> {
                if (launchY > mc.player.posY) {
                    mc.player.motionY = -0.000000000000000000000000000000001
                }
                if (mc.gameSettings.keyBindSneak.isKeyDown) {
                    mc.player.motionY = -0.2
                }
                if (mc.gameSettings.keyBindJump.isKeyDown && mc.player.posY < launchY - 0.1) {
                    mc.player.motionY = 0.2
                }
                EntityUtil.strafe()
            }

            FlightMode.NEWNCP -> {
                mc.player.motionY = -motion.value.toDouble()
                if (mc.gameSettings.keyBindSneak.isKeyDown) mc.player.motionY = -0.5
                EntityUtil.strafe()
            }
        }
    }

    @SubscribeEvent
    fun onPacket(event: PacketEvents.Send) {
        if (fullNullCheck()) {
            return
        }
        if (mode.value == FlightMode.NEWNCP) {
            if (event.getPacket<Packet<*>>() is CPacketPlayer) {
                (event.getPacket<Packet<*>>() as CPacketPlayer).onGround = true
            }
        }
    }

    override fun onDisable() {
        when (mode.value) {
            FlightMode.VANILLA -> {
                mc.player.capabilities.isFlying = false
                mc.player.capabilities.flySpeed = 0.05f
                if (mc.player.capabilities.isCreativeMode) return
                mc.player.capabilities.allowFlying = false
            }

            FlightMode.CREATIVE -> mc.player.capabilities.isFlying = false
        }
    }

    private fun handleVanillaKickBypass() {
        if (!antiKick.value || !groundTimer.passed(1000.0)) return
        val ground = calculateGround()

        while (mc.player.posY > ground) {
            mc.connection?.sendPacket(CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, true))
            if (mc.player.posY - 8.0 < ground) break // Prevent next step
            mc.player.posY -= 8.0
        }
        mc.connection?.sendPacket(CPacketPlayer.Position(mc.player.posX, ground, mc.player.posZ, true))

        var posY = ground
        while (posY < mc.player.posY) {
            mc.connection?.sendPacket(CPacketPlayer.Position(mc.player.posX, posY, mc.player.posZ, true))
            if (posY + 8.0 > mc.player.posY) break // Prevent next step
            posY += 8.0
        }

        mc.connection?.sendPacket(CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, true))
        groundTimer.reset()
    }

    private fun calculateGround(): Double {
        val playerBoundingBox = mc.player.entityBoundingBox
        var blockHeight = 1.0

        var ground = mc.player.posY
        while (ground > 0.0) {
            val customBox = AxisAlignedBB(playerBoundingBox.maxX, ground + blockHeight, playerBoundingBox.maxZ, playerBoundingBox.minX, ground, playerBoundingBox.minZ)
            if (mc.world.checkBlockCollision(customBox)) {
                if (blockHeight <= 0.05) return ground + blockHeight
                ground += blockHeight
                blockHeight = 0.05
            }
            ground -= blockHeight
        }

        return 0.0
    }
    
    enum class FlightMode {
        VANILLA,
        STATIC,
        PACKET,
        CREATIVE,
        OLDNCP,
        NEWNCP,
        TEST
    }

    companion object {
        var INSTANCE = Flight()
    }
}
