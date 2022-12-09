package me.dyzjct.kura.module.modules.render;

import me.dyzjct.kura.module.Category;
import me.dyzjct.kura.module.Module;

/**
 * @author 086
 */
@Module.Info(name = "Notification", category = Category.RENDER, description = "Notify(?")
public class Notification extends Module {

    private static Notification INSTANCE;

    public Notification() {
        INSTANCE = this;
    }

    public static boolean shouldDisable() {
        return INSTANCE != null && INSTANCE.isEnabled();
    }

}
