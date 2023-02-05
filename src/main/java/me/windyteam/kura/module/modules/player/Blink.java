package me.windyteam.kura.module.modules.player;

import me.windyteam.kura.event.events.client.PacketEvents;
import me.windyteam.kura.module.Category;
import me.windyteam.kura.module.Module;
import me.windyteam.kura.setting.Setting;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.LinkedList;
import java.util.Queue;

@SuppressWarnings("all")
@Module.Info(name = "Blink", category = Category.PLAYER, description = "Cancels server side packets")
public class Blink extends Module {
    public Setting<modes> mode = msetting("Mode", modes.All);
    Queue<Packet> packets = new LinkedList<>();
    private EntityOtherPlayerMP clonedPlayer;

    @Override
    public void onDisable() {
        while (!this.packets.isEmpty()) {
            mc.player.connection.sendPacket(this.packets.poll());
        }
        if (mc.player != null) {
            mc.world.removeEntityFromWorld(-1600);
            this.clonedPlayer = null;
        }
    }

    @Override
    public void onEnable() {
        if (mc.player != null) {
            (this.clonedPlayer = new EntityOtherPlayerMP(mc.world, mc.getSession().getProfile())).copyLocationAndAnglesFrom(mc.player);
            this.clonedPlayer.rotationYawHead = mc.player.rotationYawHead;
            mc.world.addEntityToWorld(-1600, this.clonedPlayer);
        }
    }

    @SubscribeEvent
    public void lambda$new$0(PacketEvents.Send send) {
        if (this.isEnabled() && ((this.mode.getValue() == modes.All && !(send.getPacket() instanceof CPacketPlayerTryUseItem)) || send.getPacket() instanceof CPacketPlayer)) {
            send.setCanceled(true);
            this.packets.add(send.getPacket());
        }
    }

    @Override
    public String getHudInfo() {
        return String.valueOf(this.packets.size());
    }

    public enum modes {
        CPacketPlayer,
        All

    }
}
