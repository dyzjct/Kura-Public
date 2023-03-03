package me.windyteam.kura.module.modules.player

import me.windyteam.kura.event.events.client.PacketEvents
import me.windyteam.kura.event.events.entity.MotionUpdateEvent
import me.windyteam.kura.module.Category
import me.windyteam.kura.module.Module
import me.windyteam.kura.module.ModuleManager
import me.windyteam.kura.module.modules.combat.AutoMend
import me.windyteam.kura.setting.BooleanSetting
import me.windyteam.kura.setting.FloatSetting
import me.windyteam.kura.setting.Setting
import me.windyteam.kura.utils.TimerUtils
import me.windyteam.kura.utils.entity.EntityUtil
import me.windyteam.kura.utils.math.RandomUtil
import net.minecraft.network.play.client.CPacketPlayer
import net.minecraft.util.text.TextFormatting
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@Module.Info(name = "Timer2", category = Category.PLAYER, description = "Changes your client tick speed")
class Timer2 : Module() {
    private val tickNormal: Setting<Float> = fsetting("Speed", 1.2f, 1f, 10f)
    private val xp: FloatSetting = fsetting("WhileXP", 1.7f, 1f, 2f)
    private val packetControl: BooleanSetting = bsetting("PacketControl", false)
    private val resend: BooleanSetting = bsetting("ResendMixed", true).b(packetControl)
    private val csgo: BooleanSetting = bsetting("CSGO", false).b(packetControl)
    private var packetListReset: TimerUtils = TimerUtils()
    private var normalLookPos = 0
    private var rotationMode = 1
    private var normalPos = 0
    private var lastPitch = 0f
    private var lastYaw = 0f

    companion object {
        var INSTANCE: Timer2 = Timer2()
    }

    @SubscribeEvent
    fun onPacketSend(event: PacketEvents.Send) {
        if (fullNullCheck()) {
            return
        }
        if (ModuleManager.getModuleByClass(AutoMend::class.java).isEnabled) {
            return
        }
        if (event.packet is CPacketPlayer.Position && rotationMode == 1) {
            normalPos++
            if (normalPos > 1) {
                rotationMode = if (normalLookPos > 1) {
                    if (resend.value) {
                        3
                    } else {
                        2
                    }
                } else {
                    2
                }
            }
        } else if (event.packet is CPacketPlayer.PositionRotation && rotationMode == 2) {
            normalLookPos++
            if (normalLookPos > 1) {
                rotationMode = if (normalPos > 1) {
                    if (resend.value) {
                        3
                    } else {
                        1
                    }
                } else {
                    1
                }
            }
        }
    }

    override fun onDisable() {
        if (fullNullCheck()) {
            return
        }
        mc.timer.tickLength = 50.0f
        packetListReset.reset()
    }

    override fun onEnable() {
        if (fullNullCheck()) {
            return
        }
        mc.timer.tickLength = 50.0f
        packetListReset.reset()
        lastYaw = mc.player.rotationYaw
        lastPitch = mc.player.rotationPitch
    }

    override fun getHudInfo(): String {
        return TextFormatting.RED.toString() + "" + rotationMode + ""
    }

    @SubscribeEvent
    fun onUpdate(event: MotionUpdateEvent.Tick) {
        if (fullNullCheck()) {
            return
        }
        if (packetListReset.passed(50)) {
            normalPos = 0
            normalLookPos = 0
            rotationMode = 1
            lastYaw = mc.player.rotationYaw
            lastPitch = mc.player.rotationPitch
            packetListReset.reset()
        }
        if (packetControl.value) {
            when (rotationMode) {
                1 -> {
                    //Pos
                    if (EntityUtil.isMoving()) {
                        event.setRotation(lastYaw, lastPitch)
                    }
                }

                2 -> {
                    //PosLook
                    if (csgo.value) {
                        event.setRotation(RandomUtil.nextFloat(0f, 180f), RandomUtil.nextFloat(1f, 90f))
                    } else {
                        event.setRotation(
                            lastYaw + RandomUtil.nextFloat(1f, 3f),
                            lastPitch + RandomUtil.nextFloat(1f, 3f)
                        )
                    }
                }

                3 -> {
                    //Mixed
                    event.setRotation(lastYaw, lastPitch)
                    if (csgo.value) {
                        event.setRotation(RandomUtil.nextFloat(0f, 360f), RandomUtil.nextFloat(0f, 90f))
                    } else {
                        event.setRotation(
                            lastYaw + RandomUtil.nextFloat(1f, 3f),
                            lastPitch + RandomUtil.nextFloat(1f, 3f)
                        )
                    }
                }
            }
        }
        if (ModuleManager.getModuleByClass(AutoMend::class.java).isEnabled) {
            mc.timer.tickLength = 50.0f / xp.value
        } else {
            mc.timer.tickLength = 50.0f / tickNormal.value
        }
    }
}