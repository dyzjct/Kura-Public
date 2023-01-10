package me.windyteam.kura.module.modules.render;

import me.windyteam.kura.module.Category;
import me.windyteam.kura.module.Module;
import me.windyteam.kura.setting.FloatSetting;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Info(name = "CustomFov" , category = Category.RENDER)
public class CustomFov extends Module{
    public FloatSetting fov = fsetting("Fov" , 130f, 70f, 200f);
    @SubscribeEvent
    public void onFov(EntityViewRenderEvent.FOVModifier event) {
        event.setFOV(fov.getValue());
    }
}
