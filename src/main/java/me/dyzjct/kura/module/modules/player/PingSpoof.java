package me.dyzjct.kura.module.modules.player;

import me.dyzjct.kura.event.events.client.PacketEvents;
import me.dyzjct.kura.module.Category;
import me.dyzjct.kura.module.Module;
import me.dyzjct.kura.setting.Setting;
import net.minecraft.network.play.client.CPacketKeepAlive;
import net.minecraft.network.play.server.SPacketKeepAlive;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

@Module.Info(name = "PingSpoof", category = Category.PLAYER, description = "Cancels server side packets")
public class PingSpoof extends Module {
    private final Setting<Integer> pingSpoof = isetting("Ping", 100, 1, 10000);

    @SubscribeEvent
    public void listener(PacketEvents.Receive receive) {
        if (receive.getPacket() instanceof SPacketKeepAlive) {
            receive.setCanceled(true);
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    Objects.requireNonNull(mc.getConnection()).sendPacket(new CPacketKeepAlive(((SPacketKeepAlive) receive.getPacket()).getId()));
                }
            }, this.pingSpoof.getValue());
        }
    }

    @Override
    public String getHudInfo() {
        return String.valueOf(this.pingSpoof.getValue());
    }
}
