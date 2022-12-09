package me.dyzjct.kura.module.modules.client;

import me.dyzjct.kura.module.Category;
import me.dyzjct.kura.module.Module;
import me.dyzjct.kura.setting.ColorSetting;
import me.dyzjct.kura.setting.DoubleSetting;

import java.awt.Color;

@Module.Info(name="SettingPanelColor", category=Category.CLIENT, visible=false)
public class SettingPanelColor
extends Module {
    public final DoubleSetting Scale = this.dsetting("Scale", 1.0, 0.0, 5.0).v(az -> false);
    public final ColorSetting Enable = this.csetting("EnableColor", new Color(40, 40, 40));
    public final ColorSetting Font = this.csetting("FontColor", new Color(255, 255, 255));
    public final ColorSetting Secondary_Foreground = this.csetting("2_Foreground", new Color(30, 30, 30));
    public final ColorSetting Secondary_Outline = this.csetting("2_Outline", new Color(10, 10, 10));
    public final ColorSetting Tertiary_Foreground = this.csetting("3_Foreground", new Color(20, 20, 20));
    public final ColorSetting Tertiary_Outline = this.csetting("3_Outline", new Color(15, 15, 15));
    public final ColorSetting Background = this.csetting("Background", new Color(20, 20, 20));
    public final ColorSetting Foreground = this.csetting("Foreground", new Color(255, 255, 255));
    public static SettingPanelColor INSTANCE;

    @Override
    public void onInit() {
        INSTANCE = this;
    }
}

