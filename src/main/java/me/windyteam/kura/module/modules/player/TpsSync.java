package me.windyteam.kura.module.modules.player;

import me.windyteam.kura.module.Category;
import me.windyteam.kura.module.Module;
import me.windyteam.kura.setting.Setting;
import me.windyteam.kura.module.Category;
import me.windyteam.kura.module.Module;
import me.windyteam.kura.setting.Setting;

/**
 * Created by 086 on 9/04/2018.
 */
@Module.Info(name = "TpsSync", description = "Synchronizes some actions with the server TPS", category = Category.PLAYER)
public class TpsSync extends Module {
    private static TpsSync INSTANCE;
    private final Setting<Boolean> mine = bsetting("Mine", true);
    private final Setting<Boolean> attack = bsetting("Attack", true);

    public TpsSync() {
        INSTANCE = this;
    }

    public static boolean shouldMine() {
        return INSTANCE.mine.getValue();
    }

    public static boolean shouldAttack() {
        return INSTANCE.attack.getValue() && INSTANCE.isEnabled();
    }

    public static boolean isOn() {
        return INSTANCE.isEnabled();
    }
}
