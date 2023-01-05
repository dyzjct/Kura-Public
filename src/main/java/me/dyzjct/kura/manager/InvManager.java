package me.dyzjct.kura.manager;

import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.CPacketHeldItemChange;

public class InvManager {
    public int currentPlayerItem;
    private static int recoverySlot = -1;

    Minecraft mc = Minecraft.getMinecraft();
    public void update() {
        if (recoverySlot != -1) {
            mc.player.connection.sendPacket(new CPacketHeldItemChange(recoverySlot == 8 ? 7 : recoverySlot + 1));
            mc.player.connection.sendPacket(new CPacketHeldItemChange(recoverySlot));
            mc.player.inventory.currentItem = recoverySlot;
            int i = mc.player.inventory.currentItem;
            if (i != this.currentPlayerItem) {
                this.currentPlayerItem = i;
                mc.player.connection.sendPacket(new CPacketHeldItemChange(this.currentPlayerItem));
            }
            recoverySlot = -1;
        }
    }

    public static void recoverSilent(int slot) {
        recoverySlot = slot;
    }
}

