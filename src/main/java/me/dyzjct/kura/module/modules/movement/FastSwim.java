package me.dyzjct.kura.module.modules.movement;

import me.dyzjct.kura.event.events.entity.MoveEvent;
import me.dyzjct.kura.module.Category;
import me.dyzjct.kura.module.Module;
import me.dyzjct.kura.setting.Setting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Info(name = "FastSwim", category = Category.MOVEMENT)
public class FastSwim extends Module {
    public Setting<Double> waterHorizontal = dsetting("WaterHorizontal", 3, 1, 20);
    public Setting<Double> waterVertical = dsetting("WaterVertical", 3, 1, 20);
    public Setting<Double> lavaHorizontal = dsetting("LavaHorizontal", 3, 1, 20);
    public Setting<Double> lavaVertical = dsetting("LavaVertical", 3, 1, 20);

    @SubscribeEvent
    public void onMove(final MoveEvent event) {
        if (fullNullCheck()) {
            return;
        }
        if (mc.player.isInLava() && !mc.player.onGround) {
            event.setX(event.getX() * this.lavaHorizontal.getValue());
            event.setZ(event.getZ() * this.lavaHorizontal.getValue());
            event.setY(event.getY() * this.lavaVertical.getValue());
        } else if (mc.player.isInWater() && !mc.player.onGround) {
            event.setX(event.getX() * this.waterHorizontal.getValue());
            event.setZ(event.getZ() * this.waterHorizontal.getValue());
            event.setY(event.getY() * this.waterVertical.getValue());
        }
    }
}
