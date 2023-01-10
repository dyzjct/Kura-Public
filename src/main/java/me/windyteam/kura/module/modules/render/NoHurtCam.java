package me.windyteam.kura.module.modules.render;

import me.windyteam.kura.module.Category;
import me.windyteam.kura.module.Module;

/**
 * @author 086
 */
@Module.Info(name = "NoHurtCam", category = Category.RENDER, description = "Disables the 'hurt' camera effect")
public class NoHurtCam extends Module {

    private static NoHurtCam INSTANCE;

    public NoHurtCam() {
        INSTANCE = this;
    }

    public static boolean shouldDisable() {
        return INSTANCE != null && INSTANCE.isEnabled();
    }

}
