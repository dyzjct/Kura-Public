package me.windyteam.kura.module.modules.client;

import me.windyteam.kura.event.events.gui.GuiScreenEvent;
import me.windyteam.kura.module.Category;
import me.windyteam.kura.module.Module;
import me.windyteam.kura.setting.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

@Module.Info(name = "Colors", category = Category.CLIENT, visible = false)
public class Colors extends Module {
    public static Colors INSTANCE;
    public ColorSetting color = csetting("Color", new Color(210, 100, 165));
    public BooleanSetting rainbow = this.bsetting("Rainbow", false);
    public BooleanSetting particle = this.bsetting("Particle", true);
    public BooleanSetting blur = bsetting("Blur", false);
    public BooleanSetting chat = bsetting("ToggleChat", false);
    public FloatSetting rainbowSpeed = this.fsetting("RainbowSpeed", 5.0f, 0.0f, 30.0f).b(this.rainbow);
    public IntegerSetting rainbowHue = this.isetting("RainbowHue", 1, 0, 1).b(this.rainbow);
    public FloatSetting rainbowSaturation = this.fsetting("Saturation", 0.65f, 0.0f, 1.0f).b(this.rainbow);
    public FloatSetting rainbowBrightness = this.fsetting("Brightness", 1.0f, 0.0f, 1.0f).b(this.rainbow);
    //public FloatSetting rainbowWidth = this.fsetting("RainbowWidth",10,1,20).b(this.rainbow);
    public IntegerSetting GradientIntensity = this.isetting("GIntensity", 50, 0, 500).b(this.rainbow);
    public ModeSetting<Background> background = this.msetting("Background", Background.SHADOW);
    public ModeSetting<SettingViewType> setting = this.msetting("Setting", SettingViewType.SIDE);
    //public ModeSetting<?> moduleListMode = this.msetting("ModuleListMode" , ModuleListMode.RAINBOW);
    public ColorSetting fadeColor = csetting("FadeColor", new Color(100, 181, 210));

    public static Colors getINSTANCE() {
        return INSTANCE;
    }

    @Override
    public void onInit() {
        INSTANCE = this;
    }

    @SubscribeEvent
    public void onGuiScreenEvent(GuiScreenEvent.Displayed event) {
        if (fullNullCheck()) {
            return;
        }
        if (blur.getValue() && !mc.entityRenderer.isShaderActive() && event.getScreen() != null) {
            mc.entityRenderer.loadShader(new ResourceLocation("shader/blur/blur.json"));
        } else if (mc.entityRenderer.isShaderActive() && event.getScreen() == null) {
            mc.entityRenderer.stopUseShader();
        }
    }

    public enum Background {
        SHADOW,
        BLUR,
        BOTH,
        NONE
    }

    public enum SettingViewType {
        RECT,
        SIDE,
        NONE
    }
}

