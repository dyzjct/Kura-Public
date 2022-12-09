package me.dyzjct.kura.module.modules.movement;

import me.dyzjct.kura.module.Category;
import me.dyzjct.kura.module.Module;
import me.dyzjct.kura.setting.BooleanSetting;
import me.dyzjct.kura.utils.entity.EntityUtil;

/**
 * Created by 086 on 23/08/2017.
 * Updated by S-B99 on 06/03/20
 */
@Module.Info(name = "Sprint", description = "Automatically makes the player sprint", category = Category.MOVEMENT)
public class Sprint extends Module {
    public BooleanSetting Legit = bsetting("Legit", false);

    @Override
    public void onUpdate() {
        if (fullNullCheck() || mc.player.isElytraFlying() || mc.player.capabilities.isFlying) {
            return;
        }
        if (Legit.getValue()) {
            try {
                mc.player.setSprinting(!mc.player.collidedHorizontally && mc.player.moveForward > 0);
            } catch (Exception ignored) {
            }
        } else {
            if (EntityUtil.isMoving()) {
                mc.player.setSprinting(true);
            }
        }
    }
}
