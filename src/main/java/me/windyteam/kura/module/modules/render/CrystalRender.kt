package me.windyteam.kura.module.modules.render

import kura.utils.Wrapper.world
import me.windyteam.kura.event.events.entity.MotionUpdateEvent
import me.windyteam.kura.event.events.render.RenderEvent
import me.windyteam.kura.module.Category
import me.windyteam.kura.module.Module
import me.windyteam.kura.utils.animations.sq
import me.windyteam.kura.utils.gl.MelonTessellator
import net.minecraft.entity.item.EntityEnderCrystal
import java.awt.Color
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

@Module.Info(name = "CrystalRender", category = Category.RENDER)
object CrystalRender : Module() {
    private val range = isetting("Range", 12, 0, 30)
    private val lineWidth = fsetting("Width",2f,1f,4f)
    private val alpha = isetting("Alpha",150,0,255)
    private val color = csetting("Color", Color(255,255,255))
    private val animation = bsetting("Animation",true)
    private val animationTime = isetting("AnimationTime",500,0,1500)
    private val fadeSpeed = dsetting("FadeSpeed",500.0,0.0,1500.0)
    private val cryList = ConcurrentHashMap<EntityEnderCrystal, RenderInfo>()

    fun onTick(event: MotionUpdateEvent){
        if (cryList.isNotEmpty()) {
            cryList.forEach {
                if (!world!!.loadedEntityList.contains(it.key)) {
                    cryList.remove(it.key, it.value)
                }
            }
        }
    }
    override fun onWorldRender(event: RenderEvent) {
        if (fullNullCheck()) return
        for (e in CopyOnWriteArrayList(mc.world.loadedEntityList)) {
            if (e !is EntityEnderCrystal) continue
            if (mc.player.getDistanceSq(e) > range.value.sq) continue
            if (!cryList.containsKey(e)) {
                cryList[e] = RenderInfo(e, System.currentTimeMillis())
            }
        }
        if (cryList.isNotEmpty()) {
            cryList.forEach { (_: EntityEnderCrystal, renderInfo: RenderInfo) ->
                if (renderInfo.entity.isEntityAlive) {
                    if (mc.player.getDistanceSq(renderInfo.entity) <= range.value.sq){
                        if (animation.value){
                            val rad = System.currentTimeMillis() - renderInfo.time
                            val height = System.currentTimeMillis() - renderInfo.time
                            if (rad<=animationTime.value){
                                MelonTessellator.drawCircle(renderInfo.entity, mc.renderPartialTicks, rad/fadeSpeed.value, height/1000.toFloat(), color.value.red, color.value.green, color.value.blue, alpha.value,lineWidth.value)
                            } else{
                                renderInfo.time = System.currentTimeMillis()
                            }
                        } else{
                            MelonTessellator.drawCircle(renderInfo.entity, mc.renderPartialTicks, 0.4, 0f, color.value.red, color.value.green, color.value.blue, alpha.value,lineWidth.value)
                            MelonTessellator.drawCircle(renderInfo.entity, mc.renderPartialTicks, 0.6, 0f, color.value.red, color.value.green, color.value.blue, alpha.value,lineWidth.value)
                        }
                    }
                }
            }
        }
    }

    class RenderInfo(var entity: EntityEnderCrystal, var time: Long)
}