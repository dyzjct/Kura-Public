package me.dyzjct.kura.module.modules.client

import me.dyzjct.kura.event.events.entity.MotionUpdateEvent
import me.dyzjct.kura.module.Category
import me.dyzjct.kura.module.Module
import me.dyzjct.kura.module.Module.Info
import me.dyzjct.kura.utils.NTMiku.RenderUtil
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@Info(name = "Overlay", category = Category.CLIENT)
class Overlay : Module(){
    val mark = ResourceLocation("assets/pngs/three.png")
    var imageX = fsetting("x", 606.0f, 0.0f, 1000.0f)
    var imageY = fsetting("y", 218.0f, 0.0f, 1000.0f)
    var imageWidth = fsetting("width", 318.9f, 0.0f, 4380.0f)
    var imageHeight = fsetting("height", 345.9f, 0.0f, 5700.0f)

    fun renderLogo(){
        mc.renderEngine.bindTexture(mark);
        RenderUtil.drawCompleteImage(imageX.value,imageY.value,imageWidth.value,imageHeight.value)
    }
    @SubscribeEvent
    fun onTick(event: MotionUpdateEvent.Tick?){
        renderLogo()
    }
}