package me.windyteam.kura.module.modules.render

import me.windyteam.kura.event.events.render.item.RenderItemEvent
import me.windyteam.kura.module.Category
import me.windyteam.kura.module.Module
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@Module.Info(name = "ViewModel", category = Category.RENDER)
object ViewModel : Module() {
    var settings = msetting("Settings", Settings.TRANSLATE)

    @JvmField
    var noEatAnimation = bsetting("NoEatAnimation", false).m(settings, Settings.TWEAKS)

    @JvmField
    var eatX = dsetting("EatX", 1.0, -2.0, 5.0).m(settings, Settings.TWEAKS)

    @JvmField
    var eatY = dsetting("EatY", 1.0, -2.0, 5.0).m(settings, Settings.TWEAKS)

    @JvmField
    var doBob = bsetting("ItemBob", true).m(settings, Settings.TWEAKS)
    var mainX = dsetting("MainX", 1.2, -2.0, 4.0).m(settings, Settings.TRANSLATE)
    var mainY = dsetting("MainY", -0.95, -3.0, 3.0).m(settings, Settings.TRANSLATE)
    var mainZ = dsetting("MainZ", -1.45, -5.0, 5.0).m(settings, Settings.TRANSLATE)
    var offX = dsetting("OffX", 1.2, -2.0, 4.0).m(settings, Settings.TRANSLATE)
    var offY = dsetting("OffY", -0.95, -3.0, 3.0).m(settings, Settings.TRANSLATE)
    var offZ = dsetting("OffZ", -1.45, -5.0, 5.0).m(settings, Settings.TRANSLATE)
    var mainRotX = isetting("MainRotationX", 0, -36, 36).m(settings, Settings.ROTATE)
    var mainRotY = isetting("MainRotationY", 0, -36, 36).m(settings, Settings.ROTATE)
    var mainRotZ = isetting("MainRotationZ", 0, -36, 36).m(settings, Settings.ROTATE)
    var offRotX = isetting("OffRotationX", 0, -36, 36).m(settings, Settings.ROTATE)
    var offRotY = isetting("OffRotationY", 0, -36, 36).m(settings, Settings.ROTATE)
    var offRotZ = isetting("OffRotationZ", 0, -36, 36).m(settings, Settings.ROTATE)
    var mainScaleX = dsetting("MainScaleX", 1.0, 0.1, 5.0).m(settings, Settings.SCALE)
    var mainScaleY = dsetting("MainScaleY", 1.0, 0.1, 5.0).m(settings, Settings.SCALE)
    var mainScaleZ = dsetting("MainScaleZ", 1.0, 0.1, 5.0).m(settings, Settings.SCALE)
    var offScaleX = dsetting("OffScaleX", 1.0, 0.1, 5.0).m(settings, Settings.SCALE)
    var offScaleY = dsetting("OffScaleY", 1.0, 0.1, 5.0).m(settings, Settings.SCALE)
    var offScaleZ = dsetting("OffScaleZ", 1.0, 0.1, 5.0).m(settings, Settings.SCALE)

    @SubscribeEvent
    fun onItemRender(event: RenderItemEvent) {
        event.mainX = mainX.value as Double
        event.mainY = mainY.value as Double
        event.mainZ = mainZ.value as Double
        event.offX = -offX.value
        event.offY = offY.value as Double
        event.offZ = offZ.value as Double
        event.mainRotX = (mainRotX.value * 5).toDouble()
        event.mainRotY = (mainRotY.value * 5).toDouble()
        event.mainRotZ = (mainRotZ.value * 5).toDouble()
        event.offRotX = (offRotX.value * 5).toDouble()
        event.offRotY = (offRotY.value * 5).toDouble()
        event.offRotZ = (offRotZ.value * 5).toDouble()
        event.offHandScaleX = offScaleX.value as Double
        event.offHandScaleY = offScaleY.value as Double
        event.offHandScaleZ = offScaleZ.value as Double
        event.mainHandScaleX = mainScaleX.value as Double
        event.mainHandScaleY = mainScaleY.value as Double
        event.mainHandScaleZ = mainScaleZ.value as Double
    }


    private enum class Settings {
        TRANSLATE, ROTATE, SCALE, TWEAKS
    }

}