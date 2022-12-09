package me.dyzjct.kura.module.modules.movement;

import me.dyzjct.kura.event.events.client.PacketEvents;
import me.dyzjct.kura.event.events.entity.EntityEvent;
import me.dyzjct.kura.event.events.entity.PushEvent;
import me.dyzjct.kura.mixin.client.MixinBlockLiquid;
import me.dyzjct.kura.module.Category;
import me.dyzjct.kura.module.Module;
import me.dyzjct.kura.setting.Setting;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Created by 086 on 16/11/2017.
 *
 * @see MixinBlockLiquid
 */
@Module.Info(name = "Velocity", description = "Modify knockback impact", category = Category.MOVEMENT)
public class Velocity extends Module {

    private final Setting<Boolean> noPush = bsetting("NoPush", true);
    private final Setting<Float> horizontal = fsetting("Horizontal", 0, 0, 100);
    private final Setting<Float> vertical = fsetting("Vertical", 0, 0, 100);

    @SubscribeEvent
    public void nmsl(EntityEvent event) {
        if (fullNullCheck()) {
            return;
        }
        if (event.getEntity() == mc.player) {
            if ((horizontal.getValue() == 0 && vertical.getValue() == 0) || noPush.getValue()) {
                event.setCanceled(true);
                return;
            }
            event.setX(-event.getX() * horizontal.getValue());
            event.setY(0);
            event.setZ(-event.getZ() * horizontal.getValue());
        }
    }

    @Override
    public String getHudInfo() {
        return "[ " + "H" + horizontal.getValue() + "%" + " |" + "V" + vertical.getValue() + "%" + " ]";
    }

    @SubscribeEvent
    public void packet(PacketEvents.Receive event) {
        if (fullNullCheck()) {
            return;
        }
        if (event.getPacket() instanceof SPacketEntityVelocity) {
            SPacketEntityVelocity velocity = event.getPacket();
            if (velocity.getEntityID() == mc.player.entityId) {
                if (horizontal.getValue() == 0 && vertical.getValue() == 0) event.setCanceled(true);
                velocity.motionX *= horizontal.getValue() / 100f;
                velocity.motionY *= vertical.getValue() / 100f;
                velocity.motionZ *= horizontal.getValue() / 100f;
            }
        } else if (event.getPacket() instanceof SPacketExplosion) {
            if (horizontal.getValue() == 0 && vertical.getValue() == 0) event.setCanceled(true);
            SPacketExplosion velocity = event.getPacket();
            velocity.motionX *= horizontal.getValue() / 100f;
            velocity.motionY *= vertical.getValue() / 100f;
            velocity.motionZ *= horizontal.getValue() / 100f;
        }
    }

    @SubscribeEvent
    public void NoPush(PushEvent event) {
        if (fullNullCheck()) {
            return;
        }
        if (noPush.getValue()) {
            event.setCanceled(true);
        }
    }

}
