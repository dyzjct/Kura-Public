package me.dyzjct.kura.module.modules.render;

import me.dyzjct.kura.module.Category;
import me.dyzjct.kura.module.Module;
import me.dyzjct.kura.setting.BooleanSetting;
import me.dyzjct.kura.setting.IntegerSetting;
import me.dyzjct.kura.utils.animations.AnimationFlag;
import me.dyzjct.kura.utils.animations.Easing;

@Module.Info(name="Animations", category=Category.RENDER)
public class Animations
        extends Module {
    public static Animations INSTANCE = new Animations();
    public AnimationFlag hotbarAnimation = new AnimationFlag(Easing.OUT_CUBIC, 200.0f);
    public BooleanSetting hotbar = this.bsetting("Hotbar", true);
    public BooleanSetting inv = this.bsetting("Inventory", true);
    public IntegerSetting invTime = this.isetting("InventoryTime", 500, 1, 1000).b(this.inv);
    public BooleanSetting chat = this.bsetting("Chat", true);

    public Animations() {
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        if (Animations.fullNullCheck()) {
            return;
        }
        float currentPos = (float)Animations.mc.player.inventory.currentItem * 20.0f;
        this.hotbarAnimation.forceUpdate(currentPos, currentPos);
    }

    public float updateHotbar() {
        float currentPos = (float)Animations.mc.player.inventory.currentItem * 20.0f;
        return this.hotbarAnimation.getAndUpdate(currentPos);
    }
}

