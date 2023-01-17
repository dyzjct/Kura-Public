package me.windyteam.kura.module.modules.render

import me.windyteam.kura.module.Category
import me.windyteam.kura.module.Module
import net.minecraft.client.settings.GameSettings
import net.minecraftforge.client.event.EntityViewRenderEvent.FOVModifier
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@Module.Info(name = "CustomFov", category = Category.RENDER)
class CustomFov : Module() {
    var fov = fsetting("Fov", 130f, 70f, 200f)
    override fun onUpdate() {
        mc.gameSettings.setOptionFloatValue(GameSettings.Options.FOV,fov.value)
    }
}