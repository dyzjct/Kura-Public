package me.windyteam.kura.module.modules.render

import me.windyteam.kura.event.events.render.RenderEvent
import me.windyteam.kura.module.Category
import me.windyteam.kura.module.Module
import me.windyteam.kura.utils.TimerUtils
import me.windyteam.kura.utils.animations.sq
import me.windyteam.kura.utils.gl.MelonTessellator
import net.minecraft.entity.item.EntityEnderCrystal
import java.awt.Color
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

@Module.Info(name = "CrystalRender", category = Category.RENDER)
object CrystalRender : Module() {
    private val range = isetting("Range", 12, 0, 30)
    private val lineWidth = fsetting("Width", 2f, 1f, 4f)
    private val alpha = isetting("Alpha", 150, 0, 255)
    private val color = csetting("Color", Color(255, 255, 255))
    private val animationTime = isetting("AnimationTime", 500, 0, 1500)
    private val fadeSpeed = dsetting("FadeSpeed", 500.0, 0.0, 1500.0)
    private val mode = msetting("Mode", Mode.Normal)
    private val points = isetting("Points",20,1,100).m(mode,Mode.New)
    private val interval = isetting("Interval",2,1,100).m(mode,Mode.New)
    private val cryList = ConcurrentHashMap<EntityEnderCrystal, RenderInfo>()
    private val timerUtils = TimerUtils()

    override fun onWorldRender(event: RenderEvent) {
        if (fullNullCheck()) return
        for (e in CopyOnWriteArrayList(mc.world.loadedEntityList)) {
            if (e !is EntityEnderCrystal) continue
            if (mc.player.getDistanceSq(e) > range.value.sq) continue
            if (!cryList.containsKey(e)) {
                cryList[e] = RenderInfo(e, System.currentTimeMillis())
            }
        }

        when (mode.value) {
            Mode.Normal -> {
                cryList.forEach { (_: EntityEnderCrystal, renderInfo: RenderInfo) ->
                    draw(renderInfo.entity, renderInfo.time, renderInfo.time)
                }
            }

            Mode.New -> {
                var time = 0
                for (i in 1..points.value){
                    if (timerUtils.passedMs(500)){
                        cryList.forEach { (_: EntityEnderCrystal, renderInfo: RenderInfo) ->
                            draw(renderInfo.entity, renderInfo.time-time, renderInfo.time-time)
                        }
                        time += interval.value
                    }
                }
            }
        }
    }

    fun draw(entity: EntityEnderCrystal, radTime: Long, heightTime: Long) {
        val rad = System.currentTimeMillis() - radTime
        val height = System.currentTimeMillis() - heightTime
        if (rad <= animationTime.value) {
            MelonTessellator.drawCircle(
                entity,
                mc.renderPartialTicks,
                rad / fadeSpeed.value,
                height / 1000.toFloat(),
                color.value.red,
                color.value.green,
                color.value.blue,
                alpha.value,
                lineWidth.value
            )
        }
    }

    enum class Mode {
        Normal , New
    }

    class RenderInfo(var entity: EntityEnderCrystal, var time: Long)
}