package me.windyteam.kura.module.modules.render

import me.windyteam.kura.event.events.render.PerspectiveEvent
import me.windyteam.kura.module.Category
import me.windyteam.kura.module.Module
import me.windyteam.kura.setting.Setting
import me.windyteam.kura.utils.mc.ChatUtil
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@Module.Info(name = "Aspect", category = Category.RENDER)
class Aspect : Module() {
    private var aspect: Setting<Float> = fsetting("Alpha", 1.0f, 0.1f, 5.0f)
    @SubscribeEvent
    fun onPerspectiveEvent(perspectiveEvent: PerspectiveEvent) {
        perspectiveEvent.aspect = aspect.value
    }
}