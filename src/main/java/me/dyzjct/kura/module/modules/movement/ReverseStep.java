package me.dyzjct.kura.module.modules.movement;

import me.dyzjct.kura.module.Category;
import me.dyzjct.kura.module.Module;
import me.dyzjct.kura.setting.BooleanSetting;
import me.dyzjct.kura.setting.IntegerSetting;
import me.dyzjct.kura.setting.Setting;

@Module.Info(name = "ReverseStep", category = Category.MOVEMENT, description = "null")
public class ReverseStep extends Module {

    public BooleanSetting FallSpeed = bsetting("UseFallSpeed", true);
    private final Setting<Double> height = dsetting("Height", 3, 0.5, 3).nb(FallSpeed);
    public IntegerSetting FallingSpeed = isetting("FallSpeed", 3, 1, 10);

    @Override
    public void onUpdate() {
        if (fullNullCheck() || mc.player.isInWater() || mc.player.isInLava() || mc.player.isOnLadder() || mc.gameSettings.keyBindJump.isKeyDown()) {
            return;
        }
        if (mc.player != null && mc.player.onGround && !mc.player.isInWater() && !mc.player.isOnLadder()) {
            if (FallSpeed.getValue()) {
                mc.player.motionY -= FallingSpeed.getValue();
            } else {
                for (double y = 0.0; y < this.height.getValue() + 0.5; y += 0.01) {
                    if (!mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0.0, -y, 0.0)).isEmpty()) {
                        mc.player.motionY = -15.0;
                        break;
                    }
                }
            }
        }
    }
}
