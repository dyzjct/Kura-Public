package me.windyteam.kura.module.modules.player;

import me.windyteam.kura.module.Category;
import me.windyteam.kura.module.Module;
import me.windyteam.kura.setting.Setting;
import me.windyteam.kura.module.Category;
import me.windyteam.kura.module.Module;
import me.windyteam.kura.setting.Setting;

@Module.Info(name = "LowOffHand", category = Category.PLAYER)
public class LowOffHand extends Module {
    private final Setting<Float> offhandHeight = fsetting("Height",0.5f,0.1f,1);

    @Override
    public void onUpdate() {
        LowOffHand.mc.entityRenderer.itemRenderer.equippedProgressOffHand = this.offhandHeight.getValue();
    }

    @Override
    public String getHudInfo() {
        return "[" + this.offhandHeight.getValue().toString() + "]";
    }
}
