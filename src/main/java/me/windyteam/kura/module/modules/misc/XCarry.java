// 
// Decompiled by Procyon v0.5.36
// 

package me.windyteam.kura.module.modules.misc;

import me.windyteam.kura.event.events.client.PacketEvents;
import me.windyteam.kura.module.Category;
import me.windyteam.kura.module.Module;
import me.windyteam.kura.setting.Setting;
import net.minecraft.network.play.client.CPacketCloseWindow;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Info(name = "XCarry", category = Category.MISC)
public class XCarry extends Module {
    private static XCarry INSTANCE = new XCarry();
    private final Setting<Boolean> ForceCancel = bsetting("ForceCancel", false);

    public XCarry() {
        this.setInstance();
    }

    public static XCarry getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new XCarry();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onDisable() {
        if (mc.world != null) {
            mc.player.connection.sendPacket(new CPacketCloseWindow(mc.player.inventoryContainer.windowId));
        }
    }

    @SubscribeEvent
    public void onPacket(PacketEvents.Send event) {
        if (event.getPacket() instanceof CPacketCloseWindow) {
            final CPacketCloseWindow packet = event.getPacket();
            if (packet.windowId == mc.player.inventoryContainer.windowId || ForceCancel.getValue()) {
                event.setCanceled(true);
            }
        }
    }
}
