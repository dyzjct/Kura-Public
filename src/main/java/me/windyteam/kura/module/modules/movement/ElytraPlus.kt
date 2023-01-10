package me.windyteam.kura.module.modules.movement

import me.windyteam.kura.event.events.client.PacketEvents
import me.windyteam.kura.event.events.entity.EventPlayerTravel
import me.windyteam.kura.manager.RotationManager
import me.windyteam.kura.module.Category
import me.windyteam.kura.module.Module
import me.windyteam.kura.utils.TimerUtils
import me.windyteam.kura.utils.math.MathUtil
import me.windyteam.kura.utils.mc.ChatUtil
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Items
import net.minecraft.inventory.ClickType
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.network.Packet
import net.minecraft.network.play.client.CPacketEntityAction
import net.minecraft.network.play.client.CPacketPlayer
import net.minecraft.network.play.client.CPacketPlayer.PositionRotation
import net.minecraft.util.math.MathHelper
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.util.*

@Module.Info(name = "ElytraFly", description = "Allows you to fly with elytra", category = Category.MOVEMENT)
class ElytraPlus : Module() {
    private val mode = msetting("Mode", Mode.Superior)
    private val speed = fsetting("Speed", 18.0f, 0.0f, 50.0f)
    private val downSpeed = fsetting("DownSpeed", 1.8f, 0.0f, 10.0f)
    private val glideSpeed = fsetting("GlideSpeed", 1.0E-4f, 0.0f, 10.0f)
    private val upSpeed = fsetting("UpSpeed", 5.0f, 0.0f, 10.0f)
    private val accelerate = bsetting("Accelerate", true)
    private val vAccelerationTimer = isetting("AccTime", 1000, 0, 10000)
    private val rotationPitch = fsetting("RotationPitch", 45.0f, 0.0f, 90.0f)
    private val cancelInWater = bsetting("CancelInWater", true)
    private val cancelAtHeight = isetting("CancelHeight", 0, 0, 10)
    private val instantFly = bsetting("FastBoost", true)
    private val onEnableEquipElytra = bsetting("AutoEnableWhileElytra", false)
    private val pitchSpoof = bsetting("PitchSpoof", false)
    private val fastenable = bsetting("FastFly",false)
    private val accelerationTimer = TimerUtils()
    private val accelerationResetTimer = TimerUtils()
    private val instantFlyTimer = TimerUtils()
    private var sendMessage = false
    private var elytraSlot = -1
    
    @SubscribeEvent
    fun PacketEvents(event: PacketEvents.Send) {
        if (fullNullCheck()) {
            return
        }
        if (event.getPacket<Packet<*>>() is CPacketPlayer && pitchSpoof.value) {
            if (!mc.player.isElytraFlying) {
                if (fastenable.value){
                    mc.player.setFlag(7, true)
                }
                return
            }
            if (event.getPacket<Packet<*>>() is PositionRotation && pitchSpoof.value) {
                val rotation = event.getPacket<Packet<*>>() as PositionRotation
                Objects.requireNonNull(mc.connection)!!.sendPacket(
                    CPacketPlayer.Position(
                        rotation.x,
                        rotation.y,
                        rotation.z,
                        rotation.onGround
                    ) as Packet<*>
                )
                event.isCanceled = true
            } else if (event.getPacket<Packet<*>>() is CPacketPlayer.Rotation && pitchSpoof.value) {
                event.isCanceled = true
            }
        }
    }

    @SubscribeEvent
    fun Travel(event: EventPlayerTravel) {
        if (fullNullCheck()) {
            return
        }
        if (mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() !== Items.ELYTRA) {
            return
        }
        if (!mc.player.isElytraFlying) {
            if (!mc.player.onGround && instantFly.value) {
                if (!instantFlyTimer.passed(500)) {
                    return
                }
                instantFlyTimer.reset()
                mc.player.connection.sendPacket(
                    CPacketEntityAction(
                        mc.player as Entity,
                        CPacketEntityAction.Action.START_FALL_FLYING
                    ) as Packet<*>
                )
            }
            return
        }
        if (mode.value == Mode.Packet) {
            HandleNormalModeElytra(event)
        } else if (mode.value == Mode.Superior) {
            HandleImmediateModeElytra(event)
        }
    }

    override fun getHudInfo(): String {
        return mode.value.toString()
    }

    override fun onEnable() {
        elytraSlot = -1
        if (onEnableEquipElytra.value && mc.player != null && mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST)
                .getItem() !== Items.ELYTRA
        ) {
            for (i in 0..43) {
                val stacktemp = mc.player.inventory.getStackInSlot(i)
                if (!stacktemp.isEmpty() && stacktemp.getItem() === Items.ELYTRA) {
                    elytraSlot = i
                    break
                }
            }
            if (elytraSlot != -1) {
                val l_HasArmorAtChest =
                    mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() !== Items.AIR
                mc.playerController.windowClick(
                    mc.player.inventoryContainer.windowId,
                    elytraSlot,
                    0,
                    ClickType.PICKUP,
                    mc.player as EntityPlayer
                )
                mc.playerController.windowClick(
                    mc.player.inventoryContainer.windowId,
                    6,
                    0,
                    ClickType.PICKUP,
                    mc.player as EntityPlayer
                )
                if (l_HasArmorAtChest) {
                    mc.playerController.windowClick(
                        mc.player.inventoryContainer.windowId,
                        elytraSlot,
                        0,
                        ClickType.PICKUP,
                        mc.player as EntityPlayer
                    )
                }
            }
        }
    }

    override fun onDisable() {
        if (fullNullCheck()) {
            return
        }
        mc.player.setFlag(3, true)
        if (elytraSlot != -1) {
            val l_HasItem =
                !mc.player.inventory.getStackInSlot(elytraSlot).isEmpty() || mc.player.inventory.getStackInSlot(
                    elytraSlot
                ).getItem() !== Items.AIR
            mc.playerController.windowClick(
                mc.player.inventoryContainer.windowId,
                6,
                0,
                ClickType.PICKUP,
                mc.player as EntityPlayer
            )
            mc.playerController.windowClick(
                mc.player.inventoryContainer.windowId,
                elytraSlot,
                0,
                ClickType.PICKUP,
                mc.player as EntityPlayer
            )
            if (l_HasItem) {
                mc.playerController.windowClick(
                    mc.player.inventoryContainer.windowId,
                    6,
                    0,
                    ClickType.PICKUP,
                    mc.player as EntityPlayer
                )
            }
        }
    }

    fun HandleNormalModeElytra(p_Travel: EventPlayerTravel) {
        val l_YHeight = mc.player.posY
        if (l_YHeight <= cancelAtHeight.value) {
            if (!sendMessage) {
                ChatUtil.NoSpam.sendMessage("WARNING, you must scaffold up or use fireworks, as YHeight <= CancelAtHeight!")
                sendMessage = true
            }
            return
        }
        val isMoveKeyDown =
            mc.gameSettings.keyBindForward.isKeyDown || mc.gameSettings.keyBindLeft.isKeyDown || mc.gameSettings.keyBindRight.isKeyDown || mc.gameSettings.keyBindBack.isKeyDown
        val l_CancelInWater = !mc.player.isInWater && !mc.player.isInLava && cancelInWater.value
        if (!isMoveKeyDown) {
            accelerationTimer.resetTimeSkipTo(-vAccelerationTimer.value.toLong())
        } else if (mc.player.rotationPitch <= rotationPitch.value && l_CancelInWater) {
            if (accelerate.value && accelerationTimer.passed(vAccelerationTimer.value)) {
                Accelerate()
            }
            return
        }
        p_Travel.isCanceled = true
        Accelerate()
    }

    fun HandleImmediateModeElytra(p_Travel: EventPlayerTravel) {
        p_Travel.isCanceled = true
        val moveForward = mc.gameSettings.keyBindForward.isKeyDown
        val moveBackward = mc.gameSettings.keyBindBack.isKeyDown
        val moveLeft = mc.gameSettings.keyBindLeft.isKeyDown
        val moveRight = mc.gameSettings.keyBindRight.isKeyDown
        val moveUp = mc.gameSettings.keyBindJump.isKeyDown
        val moveDown = mc.gameSettings.keyBindSneak.isKeyDown
        val moveForwardFactor = if (moveForward) 1.0f else (if (moveBackward) -1 else 0).toFloat()
        var yawDeg = mc.player.rotationYaw
        if (moveLeft && (moveForward || moveBackward)) {
            yawDeg -= 40.0f * moveForwardFactor
        } else if (moveRight && (moveForward || moveBackward)) {
            yawDeg += 40.0f * moveForwardFactor
        } else if (moveLeft) {
            yawDeg -= 90.0f
        } else if (moveRight) {
            yawDeg += 90.0f
        }
        if (moveBackward) {
            yawDeg -= 180.0f
        }
        val yaw = Math.toRadians(yawDeg.toDouble()).toFloat()
        val motionAmount = Math.sqrt(mc.player.motionX * mc.player.motionX + mc.player.motionZ * mc.player.motionZ)
        if (moveUp || moveForward || moveBackward || moveLeft || moveRight) {
            if (moveUp && motionAmount > 1.0) {
                if (mc.player.motionX == 0.0 && mc.player.motionZ == 0.0) {
                    mc.player.motionY = upSpeed.value.toDouble()
                } else {
                    val calcMotionDiff = motionAmount * 0.008
                    val player = mc.player
                    player.motionY += calcMotionDiff * 3.2
                    val player2 = mc.player
                    player2.motionX -= -MathHelper.sin(yaw) * calcMotionDiff
                    val player3 = mc.player
                    player3.motionZ -= MathHelper.cos(yaw) * calcMotionDiff
                    val player4 = mc.player
                    player4.motionX *= 0.9900000095367432
                    val player5 = mc.player
                    player5.motionY *= 0.9800000190734863
                    val player6 = mc.player
                    player6.motionZ *= 0.9900000095367432
                }
            } else {
                mc.player.motionX = -MathHelper.sin(yaw) * (speed.value / 10.0f).toDouble()
                mc.player.motionY = -glideSpeed.value.toDouble()
                mc.player.motionZ = MathHelper.cos(yaw) * (speed.value / 10.0f).toDouble()
                RotationManager.addRotations(yaw, mc.player.rotationPitch)
            }
        } else {
            mc.player.motionX = 0.0
            mc.player.motionY = 0.0
            mc.player.motionZ = 0.0
        }
        if (moveDown) {
            mc.player.motionY = -downSpeed.value.toDouble()
        }
    }

    fun Accelerate() {
        if (accelerationResetTimer.passed(vAccelerationTimer.value)) {
            accelerationResetTimer.reset()
            accelerationTimer.reset()
            sendMessage = false
        }
        val speedacc = speed.value / 10.0f
        val dir = MathUtil.directionSpeed(speedacc.toDouble())
        mc.player.motionY = -glideSpeed.value.toDouble()
        if (mc.player.movementInput.moveStrafe != 0.0f || mc.player.movementInput.moveForward != 0.0f) {
            mc.player.motionX = dir[0]
            mc.player.motionZ = dir[1]
            val player = mc.player
            player.motionX -= mc.player.motionX * (Math.abs(mc.player.rotationPitch) + 90.0f) / 90.0 - mc.player.motionX
            val player2 = mc.player
            player2.motionZ -= mc.player.motionZ * (Math.abs(mc.player.rotationPitch) + 90.0f) / 90.0 - mc.player.motionZ
        } else {
            mc.player.motionX = 0.0
            mc.player.motionZ = 0.0
        }
        if (mc.gameSettings.keyBindSneak.isKeyDown) {
            mc.player.motionY = -downSpeed.value.toDouble()
        }
    }

    private enum class Mode {
        Superior, Packet
    }
}