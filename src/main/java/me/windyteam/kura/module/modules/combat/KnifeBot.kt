package me.windyteam.kura.module.modules.combat

import me.windyteam.kura.event.events.entity.MotionUpdateEvent
import me.windyteam.kura.event.events.render.RenderEvent
import me.windyteam.kura.module.Category
import me.windyteam.kura.module.Module
import me.windyteam.kura.module.modules.crystalaura.AutoCrystal
import me.windyteam.kura.module.modules.player.PacketMine
import me.windyteam.kura.setting.Setting
import me.windyteam.kura.utils.Timer
import me.windyteam.kura.utils.block.BlockInteractionHelper
import me.windyteam.kura.utils.entity.EntityUtil
import me.windyteam.kura.utils.getTarget
import me.windyteam.kura.utils.math.DamageUtil
import me.windyteam.kura.utils.math.deneb.LagCompensator
import me.windyteam.kura.utils.render.RenderUtil
import me.windyteam.kura.utils.render.RenderUtilv12
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.network.play.client.CPacketPlayerDigging
import net.minecraft.network.play.client.CPacketUseEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.awt.Color

@Module.Info(name = "Knife-Bot", category = Category.COMBAT)
object KnifeBot : Module() {
    private val timer = Timer()
    private val setting = msetting("Settings", Settings.COMBAT)
    private val render = msetting("RenderMode", RenderMode.JELLO).m(setting, Settings.RENDER)
    private var range: Setting<Float> = fsetting("Range", 6.0f, 0.1f, 7.0f).m(setting, Settings.COMBAT)
    private var delay: Setting<Boolean> = bsetting("HitDelay", true).m(setting, Settings.COMBAT)
    private var rotate: Setting<Boolean> = bsetting("Rotate", true).m(setting, Settings.COMBAT)
    private var onlySharp: Setting<Boolean> = bsetting("SwordOnly", true).m(setting, Settings.COMBAT)
    private var tps: Setting<Boolean> = bsetting("TPSSync", true).m(setting, Settings.COMBAT)
    private var packet: Setting<Boolean> = bsetting("Packet", false).m(setting, Settings.COMBAT)
    private var info: Setting<Boolean> = bsetting("Info", true).m(setting, Settings.RENDER)
    var target: EntityPlayer? = null

    override fun onWorldRender(event: RenderEvent) {
        if (fullNullCheck()) return
        target = getTarget(range.value)
        if (target != null) {
            if (render.value === RenderMode.OLD) {
                RenderUtilv12.drawEntityBoxESP(
                    target, Color(255, 255, 255), true, Color(255, 255, 255, 130), 0.7f, true, true, 35
                )
            } else if (render.value === RenderMode.JELLO) {
                if (EntityUtil.holdingWeapon(mc.player) && AutoCrystal.renderEnt == null) {
                    RenderUtil.drawFade(target, Color(0, 255, 0))
                } else if (AutoCrystal.renderEnt != null) {
                    RenderUtil.drawFade(target, Color(255, 0, 0))
                }
            }
        }
    }

    @SubscribeEvent
    fun onTick(event: MotionUpdateEvent.Tick) {
        if (fullNullCheck()) return
        var wait = 0
        if (onlySharp.value && !EntityUtil.holdingWeapon(mc.player)) {
            target = null
            return
        }
        if (delay.value) (DamageUtil.getCooldownByWeapon(mc.player) * if (tps.value) 21.0f - LagCompensator.INSTANCE.tickRate else 1.0f).toInt()
            .also { wait = it } else 0
        if (!timer.passedMs(wait.toLong())) {
            return
        }
        target?.let {
            if (rotate.value) {
                event.setRotation(BlockInteractionHelper.getLegitRotations(it.positionVector)[0],BlockInteractionHelper.getLegitRotations(it.positionVector)[1])
            }
            EntityUtil.attackEntity(it, packet.value, true)
            timer.reset()
        }
    }


    override fun getHudInfo(): String? {
        return if (info.value && target is EntityPlayer) {
            target!!.name
        } else null
    }

    private enum class RenderMode {
        OLD, JELLO, OFF
    }

    enum class Settings {
        COMBAT, RENDER
    }

}