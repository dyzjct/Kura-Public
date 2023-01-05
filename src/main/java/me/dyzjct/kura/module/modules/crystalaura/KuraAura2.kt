package me.dyzjct.kura.module.modules.crystalaura

import me.dyzjct.kura.event.events.entity.MotionUpdateEvent
import me.dyzjct.kura.event.events.render.RenderEvent
import me.dyzjct.kura.module.Category
import me.dyzjct.kura.module.Module
import me.dyzjct.kura.module.Module.Info
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@Info(name = "KuraAura2", category = Category.XDDD)
class KuraAura2 : Module() {
    @SubscribeEvent
    fun onCrystalAura(event: MotionUpdateEvent.Tick){
        if (fullNullCheck())return
    }

    fun canPlaceCrystal(){

    }

    fun canBreakCrystal(){

    }

    override fun onWorldRender(event: RenderEvent?) {
        if (fullNullCheck())return
    }
}