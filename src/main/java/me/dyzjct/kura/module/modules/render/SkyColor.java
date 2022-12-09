package me.dyzjct.kura.module.modules.render;

import me.dyzjct.kura.module.Category;
import me.dyzjct.kura.module.Module;
import me.dyzjct.kura.setting.BooleanSetting;
import me.dyzjct.kura.setting.IntegerSetting;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

@Module.Info(name = "SkyColor", category = Category.RENDER)
public class SkyColor extends Module {
    public BooleanSetting rainbow = bsetting("Rainbow", false);
    public IntegerSetting red = isetting("Red", 255, 0, 255);
    public IntegerSetting green = isetting("Green", 255, 0, 255);
    public IntegerSetting blue = isetting("Blue", 255, 0, 255);


    @SubscribeEvent
    public void fog_colour(final EntityViewRenderEvent.FogColors event) {
        event.setRed(red.getValue() / 255f);
        event.setGreen(green.getValue() / 255f);
        event.setBlue(blue.getValue() / 255f);
    }

    @SubscribeEvent
    public void fog_density(final EntityViewRenderEvent.FogDensity event) {
        event.setDensity(0.0f);
        event.setCanceled(true);
    }

    @Override
    public void onUpdate() {
        if (rainbow.getValue()) {
            cycle_rainbow();
        }
    }

    public void cycle_rainbow() {

        float[] tick_color = {
                (System.currentTimeMillis() % (360 * 32)) / (360f * 32)
        };

        int color_rgb_o = Color.HSBtoRGB(tick_color[0], 0.8f, 0.8f);

        red.setValue((color_rgb_o >> 16) & 0xFF);
        green.setValue((color_rgb_o >> 8) & 0xFF);
        blue.setValue(color_rgb_o & 0xFF);

    }

}
