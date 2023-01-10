package me.windyteam.kura.utils.packet;

import me.windyteam.kura.mixin.client.mc.INetworkManager;
import me.windyteam.kura.mixin.client.mc.INetworkManager;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;

import java.util.ArrayList;
import java.util.List;

public class PacketUtil {
    public static Minecraft mc = Minecraft.getMinecraft();
    public static List<Packet<?>> packets = new ArrayList<>();

    public static void sendPacketNoEvent(Packet<?> packet) {
        try {
            packets.add(packet);
            ((INetworkManager) mc.getConnection().getNetworkManager()).sendPacketNoEvent(packet);
        } catch (Exception ignored) {
        }
    }
}
