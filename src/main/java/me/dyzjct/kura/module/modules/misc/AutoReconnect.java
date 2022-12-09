package me.dyzjct.kura.module.modules.misc;

import me.dyzjct.kura.event.events.gui.GuiScreenEvent;
import me.dyzjct.kura.module.Category;
import me.dyzjct.kura.module.Module;
import me.dyzjct.kura.setting.Setting;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Info(name = "AutoReconnect", description = "Automatically reconnects after being disconnected", category = Category.MISC)
public class AutoReconnect extends Module {
    private static ServerData cServer;
    private final Setting<Integer> seconds = isetting("Seconds", 3, 0, 10);

    @SubscribeEvent
    public void displayedListener(GuiScreenEvent.Displayed displayed) {
        if (this.isEnabled() && displayed.getScreen() instanceof GuiDisconnected && (cServer != null || mc.currentServerData != null)) {
            displayed.setScreen(new MelonGuiDisconnected((GuiDisconnected) displayed.getScreen()));
        }
    }

    @SubscribeEvent
    public void closedListener(GuiScreenEvent.Closed closed) {
        if (closed.getScreen() instanceof GuiConnecting) {
            cServer = mc.currentServerData;
        }
        return;
    }

    public class MelonGuiDisconnected extends GuiDisconnected {
        long cTime;
        int millis;

        public MelonGuiDisconnected(final GuiDisconnected guiDisconnected) {
            super(guiDisconnected.parentScreen, guiDisconnected.reason, guiDisconnected.message);
            this.millis = AutoReconnect.this.seconds.getValue() * 1000;
            this.cTime = System.currentTimeMillis();
        }

        public void drawScreen(final int n, final int n2, final float n3) {
            super.drawScreen(n, n2, n3);
            final long currentTimeMillis = System.currentTimeMillis();
            this.millis -= (int) (currentTimeMillis - this.cTime);
            this.cTime = currentTimeMillis;
            final String value = "Reconnecting in " + Math.max(0.0, Math.floor(this.millis / 100.0) / 10.0) + "s";
            this.fontRenderer.drawString(value, (float) (this.width / 2 - this.fontRenderer.getStringWidth(value) / 2), (float) (this.height - 16), 16777215, true);
        }

        public void updateScreen() {
            if (this.millis <= 0) {
                this.mc.displayGuiScreen(new GuiConnecting(this.parentScreen, this.mc, (cServer == null) ? this.mc.currentServerData : cServer));
            }
        }
    }
}
