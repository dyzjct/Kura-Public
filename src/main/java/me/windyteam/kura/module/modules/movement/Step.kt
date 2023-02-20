package me.windyteam.kura.module.modules.movement

import me.windyteam.kura.module.Category
import me.windyteam.kura.module.Module
import me.windyteam.kura.module.ModuleManager
import me.windyteam.kura.module.modules.player.Timer
import me.windyteam.kura.setting.Setting
import net.minecraft.network.play.client.CPacketPlayer
import java.math.BigDecimal
import java.math.RoundingMode

@Module.Info(name = "Step", category = Category.MOVEMENT)
class Step : Module() {
    private val mode = msetting("Mode", Mode.NCP)
    private val reverse: Setting<Boolean> = bsetting("ReverseStep", true)

    private val heights: Setting<Double> = dsetting("Height", 2.0, 0.0, 5.0)
    private var stepped = false

    override fun onUpdate() {
        if (mc.world == null || mc.player == null || mc.player.isInWater || mc.player.isInLava || mc.player.isOnLadder || mc.gameSettings.keyBindJump.isKeyDown) {
            return
        }
        if (mc.player != null && mc.player.onGround && !mc.player.isInWater && !mc.player.isOnLadder && reverse.value) {
            var n = 0.0
            while (n < this.heights.value + 0.5) {
                if (mc.world.getCollisionBoxes(mc.player, mc.player.entityBoundingBox.offset(0.0, -n, 0.0)).isNotEmpty()
                ) {
                    mc.player.motionY = -10.0
                    break
                }
                n += 0.01
            }
        }
        if (mode.value === Mode.NCP) {
            stepped = false
            val forward = forward(0.1)
            var b = false
            var b2 = false
            var b3 = false
            var b4 = false
            if (mc.world.getCollisionBoxes(mc.player, mc.player.entityBoundingBox.offset(forward[0], 3.1, forward[1]))
                    .isEmpty()
            ) {
                mc.world.getCollisionBoxes(mc.player, mc.player.entityBoundingBox.offset(forward[0], 2.9, forward[1]))
            }
            if (mc.world.getCollisionBoxes(mc.player, mc.player.entityBoundingBox.offset(forward[0], 2.6, forward[1]))
                    .isEmpty() && mc.world.getCollisionBoxes(
                    mc.player, mc.player.entityBoundingBox.offset(
                        forward[0], 2.4, forward[1]
                    )
                ).isNotEmpty()
            ) {
                b = true
            }
            if (mc.world.getCollisionBoxes(mc.player, mc.player.entityBoundingBox.offset(forward[0], 2.1, forward[1]))
                    .isEmpty() && mc.world.getCollisionBoxes(
                    mc.player, mc.player.entityBoundingBox.offset(
                        forward[0], 1.9, forward[1]
                    )
                ).isNotEmpty()
            ) {
                b2 = true
            }
            if (mc.world.getCollisionBoxes(mc.player, mc.player.entityBoundingBox.offset(forward[0], 1.6, forward[1]))
                    .isEmpty() && mc.world.getCollisionBoxes(
                    mc.player, mc.player.entityBoundingBox.offset(
                        forward[0], 1.4, forward[1]
                    )
                ).isNotEmpty()
            ) {
                b3 = true
            }
            if (mc.world.getCollisionBoxes(mc.player, mc.player.entityBoundingBox.offset(forward[0], 1.0, forward[1]))
                    .isEmpty() && mc.world.getCollisionBoxes(
                    mc.player, mc.player.entityBoundingBox.offset(
                        forward[0], 0.6, forward[1]
                    )
                ).isNotEmpty()
            ) {
                b4 = true
            }
            if (mc.player.collidedHorizontally && (mc.player.moveForward != 0.0f || mc.player.moveStrafing != 0.0f) && mc.player.onGround) {
                if (b4 && this.heights.value >= 1.0) {
                    stepped = true
                    val array = doubleArrayOf(0.42, 0.753, 1.0)
                    for (v in array) {
                        mc.player.connection.sendPacket(
                            CPacketPlayer.Position(
                                mc.player.posX,
                                mc.player.posY + v,
                                mc.player.posZ,
                                mc.player.onGround
                            )
                        )
                    }
                    mc.player.setPosition(mc.player.posX, mc.player.posY + 1.0, mc.player.posZ)
                }
                if (b3 && this.heights.value >= 1.5) {
                    stepped = true
                    val array2 = doubleArrayOf(0.42, 0.75, 1.0, 1.16, 1.23, 1.2, 1.5)
                    for (v in array2) {
                        mc.player.connection.sendPacket(
                            CPacketPlayer.Position(
                                mc.player.posX,
                                mc.player.posY + v,
                                mc.player.posZ,
                                mc.player.onGround
                            )
                        )
                    }
                    mc.player.setPosition(mc.player.posX, mc.player.posY + 1.5, mc.player.posZ)
                }
                if (b2 && this.heights.value >= 2.0) {
                    stepped = true
                    val array3 = doubleArrayOf(0.42, 0.78, 0.63, 0.51, 0.9, 1.21, 1.45, 1.43, 2.0)
                    for (v in array3) {
                        mc.player.connection.sendPacket(
                            CPacketPlayer.Position(
                                mc.player.posX,
                                mc.player.posY + v,
                                mc.player.posZ,
                                mc.player.onGround
                            )
                        )
                    }
                    mc.player.setPosition(mc.player.posX, mc.player.posY + 2.0, mc.player.posZ)
                }
                if (b && this.heights.value >= 2.5) {
                    stepped = true
                    val array4 =
                        doubleArrayOf(0.425, 0.821, 0.699, 0.599, 1.022, 1.372, 1.652, 1.869, 2.019, 1.907, 2.5)
                    for (v in array4) {
                        mc.player.connection.sendPacket(
                            CPacketPlayer.Position(
                                mc.player.posX,
                                mc.player.posY + v,
                                mc.player.posZ,
                                mc.player.onGround
                            )
                        )
                    }
                    mc.player.setPosition(mc.player.posX, mc.player.posY + 2.5, mc.player.posZ)
                }
            }
        } else if (mode.value == Mode.VANILLA) {
            mc.player.stepHeight = (this.heights.value as Double).toFloat()
        }
    }

    override fun getHudInfo(): String {
        return if (mode.value == Mode.NCP) "NCP" else "Vanilla"
    }

    override fun onDisable() {
        mc.player.stepHeight = 0.5f
    }

    enum class Mode {
        VANILLA, NCP
    }

    companion object {
        var INSTANCE = Step()
        fun forward(n: Double): DoubleArray {
            var moveForward = mc.player.movementInput.moveForward
            var moveStrafe = mc.player.movementInput.moveStrafe
            var n2 =
                mc.player.prevRotationYaw + (mc.player.rotationYaw - mc.player.prevRotationYaw) * mc.renderPartialTicks
            if (moveForward != 0.0f) {
                if (moveStrafe > 0.0f) {
                    n2 += (if (moveForward > 0.0f) -45 else 45).toFloat()
                } else if (moveStrafe < 0.0f) {
                    n2 += (if (moveForward > 0.0f) 45 else -45).toFloat()
                }
                moveStrafe = 0.0f
                if (moveForward > 0.0f) {
                    moveForward = 1.0f
                } else if (moveForward < 0.0f) {
                    moveForward = -1.0f
                }
            }
            val sin = Math.sin(Math.toRadians((n2 + 90.0f).toDouble()))
            val cos = Math.cos(Math.toRadians((n2 + 90.0f).toDouble()))
            return doubleArrayOf(
                moveForward * n * cos + moveStrafe * n * sin,
                moveForward * n * sin - moveStrafe * n * cos
            )
        }
    }
}