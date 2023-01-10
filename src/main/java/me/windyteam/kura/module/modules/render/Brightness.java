package me.windyteam.kura.module.modules.render;

import me.windyteam.kura.event.events.client.PacketEvents;
import me.windyteam.kura.event.events.entity.EventPlayerUpdate;
import me.windyteam.kura.mixin.client.MixinEntityRenderer;
import me.windyteam.kura.module.Category;
import me.windyteam.kura.module.Module;
import me.windyteam.kura.setting.ModeSetting;
import me.windyteam.kura.setting.Setting;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.server.SPacketEntityEffect;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Created by 086 on 12/12/2017.
 *
 * @see MixinEntityRenderer
 */
@Module.Info(name = "Brightness", description = "Makes everything brighter!", category = Category.RENDER)
public class Brightness extends Module {
    private final ModeSetting<?> mode = msetting("Mode", Mode.Gamma);
    private final Setting<Boolean> effects = bsetting("Effects", true);
    private float lastGamma;
    private World world;

    @SubscribeEvent
    public void PacketReceive(PacketEvents.Receive event) {
        if (event.getPacket() instanceof SPacketEntityEffect) {
            if (this.effects.getValue()) {
                final SPacketEntityEffect packet = event.getPacket();
                if (mc.player != null && packet.getEntityId() == mc.player.getEntityId()) {
                    if (packet.getEffectId() == 9 || packet.getEffectId() == 15) {
                        event.setCanceled(true);
                    }
                }
            }
        }
    }

    @Override
    public String getHudInfo() {
        return String.valueOf(mode.getValue());
    }

    @Override
    public void onEnable() {
        if (this.mode.getValue() == Mode.Gamma) {
            this.lastGamma = mc.gameSettings.gammaSetting;
        }
    }

    @Override
    public void onDisable() {
        if (this.mode.getValue() == Mode.Gamma) {
            mc.gameSettings.gammaSetting = this.lastGamma;
        }

        if (this.mode.getValue() == Mode.Potion && mc.player != null) {
            mc.player.removePotionEffect(MobEffects.NIGHT_VISION);
        }

        if (this.mode.getValue() == Mode.Table) {
            if (mc.world != null) {

                for (int i = 0; i <= 15; ++i) {
                    float f1 = 1.0F - (float) i / 15.0F;
                    mc.world.provider.getLightBrightnessTable()[i] = (1.0F - f1) / (f1 * 3.0F + 1.0F) + 0.0F;
                }
            }
        }
    }

    @SubscribeEvent
    public void OnPlayerUpdate(EventPlayerUpdate event) {
        if (mode.getValue().equals(Mode.Gamma)) {
            mc.gameSettings.gammaSetting = 1000;
            mc.player.removePotionEffect(MobEffects.NIGHT_VISION);
        }
        if (mode.getValue().equals(Mode.Potion)) {
            mc.gameSettings.gammaSetting = 1.0f;
            mc.player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 5210));
        }
        if (mode.getValue().equals(Mode.Table)) {
            if (this.world != mc.world) {
                if (mc.world != null) {
                    for (int i = 0; i <= 15; ++i) {
                        mc.world.provider.getLightBrightnessTable()[i] = 1.0f;
                    }
                }
                this.world = mc.world;
            }
        }
    }

    private enum Mode {
        Gamma,
        Potion,
        Table
    }

}
