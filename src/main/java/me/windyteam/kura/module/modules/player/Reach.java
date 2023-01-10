package me.windyteam.kura.module.modules.player;

import me.windyteam.kura.module.Category;
import me.windyteam.kura.module.Module;
import me.windyteam.kura.setting.Setting;
import me.windyteam.kura.module.Category;
import me.windyteam.kura.module.Module;
import me.windyteam.kura.setting.Setting;

@Module.Info(name = "Reach", category = Category.PLAYER)
public class Reach extends Module {
    public Setting<Float> range = fsetting("Range", 6, 1, 10);
    public static Reach INSTANCE;

    public static boolean isEnable() {
        return INSTANCE.isEnabled();
    }

    @Override
    public String getHudInfo() {
        return this.range.getValue().toString();
    }

    public static float getReach() {
        return INSTANCE.range.getValue();
    }

    public Reach() {
        INSTANCE = this;
    }
}
