package me.dyzjct.kura.module.modules.render;

import me.dyzjct.kura.module.Category;
import me.dyzjct.kura.module.Module;
import me.dyzjct.kura.setting.BooleanSetting;
import me.dyzjct.kura.setting.DoubleSetting;

@Module.Info(name="CameraClip", category=Category.RENDER)
public class CameraClip
extends Module {
    private static CameraClip INSTANCE;
    public BooleanSetting extend = this.bsetting("Extend", false);
    public DoubleSetting distance = this.dsetting("Distance", 0.0, 0.0, 100.0);

    @Override
    public void onInit() {
        INSTANCE = this;
    }

    public static CameraClip getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CameraClip();
        }
        return INSTANCE;
    }
}

