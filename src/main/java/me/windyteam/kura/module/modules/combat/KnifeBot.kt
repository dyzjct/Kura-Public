package me.windyteam.kura.module.modules.combat

import me.windyteam.kura.event.events.entity.MotionUpdateEvent
import me.windyteam.kura.event.events.render.RenderEvent
import me.windyteam.kura.module.Category
import me.windyteam.kura.module.Module
import me.windyteam.kura.module.ModuleManager
import me.windyteam.kura.module.modules.crystalaura.KuraAura
import me.windyteam.kura.setting.Setting
import me.windyteam.kura.utils.Timer
import me.windyteam.kura.utils.entity.EntityUtil
import me.windyteam.kura.utils.getTarget
import me.windyteam.kura.utils.math.DamageUtil
import me.windyteam.kura.utils.math.RotationUtil
import me.windyteam.kura.utils.math.deneb.LagCompensator
import me.windyteam.kura.utils.render.RenderUtil
import me.windyteam.kura.utils.render.RenderUtilv12
import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.awt.Color

@Module.Info(name = "Knife-Bot", category = Category.COMBAT)
class KnifeBot : Module() {
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
    var target:EntityPlayer? = null
    override fun onWorldRender(event: RenderEvent) {
        if (fullNullCheck()) return
        if (target != null) {
            if (render.value === RenderMode.OLD) {
                RenderUtilv12.drawEntityBoxESP(
                    target,
                    Color(255,255,255),
                    true,
                    Color(255, 255, 255, 130),
                    0.7f,
                    true,
                    true,
                    35
                )
            } else if (render.value === RenderMode.JELLO) {
                if (!ModuleManager.getModuleByClass(KuraAura::class.java).isEnabled || KuraAura.renderEnt == null){
                    RenderUtil.drawFade(target, Color(255,255,255))
                }
            }
        }
    }

    @SubscribeEvent
    fun onTick(event: MotionUpdateEvent.Tick?) {
        if (fullNullCheck()) return
        if (!rotate.value) {
            doKnifeBot()
        }
        target = getTarget(range.value)
    }

    @SubscribeEvent
    fun onUpdateWalkingPlayerEvent(event: MotionUpdateEvent.Tick) {
        if (fullNullCheck()) return
        if (event.stage == 0 && rotate.value) {
            doKnifeBot()
        }
    }

    private fun doKnifeBot() {
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
        if (target == null) return
        if (rotate.value) {
            RotationUtil.lookAtEntity(target)
        }
        EntityUtil.attackEntity(target, packet.value, true)
        timer.reset()
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

    companion object { @JvmStatic var INSTANCE: KnifeBot? = KnifeBot() }
}