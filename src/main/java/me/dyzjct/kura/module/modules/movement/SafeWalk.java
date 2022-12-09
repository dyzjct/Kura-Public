package me.dyzjct.kura.module.modules.movement;

import me.dyzjct.kura.event.events.entity.MoveEvent;
import me.dyzjct.kura.module.Category;
import me.dyzjct.kura.module.Module;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Info(name = "SafeWalk", category = Category.MOVEMENT)
public class SafeWalk extends Module {
    @SubscribeEvent
    public void onMove(MoveEvent event) {
        double x2 = event.getX();
        double y2 = event.getY();
        double z2 = event.getZ();
        if (mc.player.onGround) {
            double increment = 0.05;
            while (x2 != 0.0) {
                if (!mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(x2, -1.0, 0.0)).isEmpty())
                    break;
                if (x2 < increment && x2 >= -increment) {
                    x2 = 0.0;
                    continue;
                }
                if (x2 > 0.0) {
                    x2 -= increment;
                    continue;
                }
                x2 += increment;
            }
            while (z2 != 0.0) {
                if (!mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0.0, -1.0, z2)).isEmpty())
                    break;
                if (z2 < increment && z2 >= -increment) {
                    z2 = 0.0;
                    continue;
                }
                if (z2 > 0.0) {
                    z2 -= increment;
                    continue;
                }
                z2 += increment;
            }
            while (x2 != 0.0 && z2 != 0.0 && mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(x2, -1.0, z2)).isEmpty()) {
                x2 = x2 < increment && x2 >= -increment ? 0.0 : (x2 > 0.0 ? x2 - increment : x2 + increment);
                if (z2 < increment && z2 >= -increment) {
                    z2 = 0.0;
                    continue;
                }
                if (z2 > 0.0) {
                    z2 -= increment;
                    continue;
                }
                z2 += increment;
            }
        }
        event.setX(x2);
        event.setY(y2);
        event.setZ(z2);
    }

}
