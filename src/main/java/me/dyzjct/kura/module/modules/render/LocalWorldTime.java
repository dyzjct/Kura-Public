package me.dyzjct.kura.module.modules.render;

import me.dyzjct.kura.event.events.client.PacketEvents;
import me.dyzjct.kura.event.events.render.RenderEvent;
import me.dyzjct.kura.module.Category;
import me.dyzjct.kura.module.Module;
import me.dyzjct.kura.setting.IntegerSetting;
import me.dyzjct.kura.setting.ModeSetting;
import net.minecraft.network.play.server.SPacketTimeUpdate;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Info(name = "Local World Time", category = Category.RENDER, description = "Custom Your MC Local Time")
public class LocalWorldTime extends Module {

    ModeSetting<Mode> mode = msetting("Mode", Mode.DTime).setOnChange(newValue -> {
        if (newValue == Mode.Loop) {
            reset = true;
        }
    });
    ModeSetting<WorldTime> DTimeMode = msetting("DTime", WorldTime.NIGHT).m(mode, Mode.DTime);
    IntegerSetting customTime = isetting("Tick", 1000, 0, 24000).m(mode, Mode.Custom);
    IntegerSetting speedLoop = isetting("Speed", 1, 1, 250).m(mode, Mode.Loop);
    long current = 0;
    long mcRealTime = 0;
    boolean reset = false;

    private enum Mode {
        Custom,
        Loop,
        DTime
    }

    private enum WorldTime {
        DAY(1000),
        NOON(6000),
        SUNSET(12000),
        NIGHT(13000),
        MIDNIGHT(18000),
        SUNRISE(23000);
        private final long time;

        WorldTime(long time) {
            this.time = time;
        }

        public long getTime() {
            return time;
        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvents.Receive event) {
        if (event.getPacket() instanceof SPacketTimeUpdate) {
            SPacketTimeUpdate packet = event.getPacket();
            mcRealTime = packet.getWorldTime();
            event.setCanceled(true);
        }
    }

    @Override
    public void onDisable() {
        mc.world.setWorldTime(mcRealTime);
    }

    @Override
    public void onWorldRender(RenderEvent event) {
        updateTime();
    }

    @Override
    public void onUpdate() {
        updateTime();
    }

    public void updateTime() {
        if (mc.world == null) return;

        if (reset) {
            current = mc.world.getWorldTime();
            reset = false;
        }

        switch (mode.getValue()) {
            case Custom:
                mc.world.setWorldTime(customTime.getValue());
                break;
            case Loop:
                this.current = this.current < 240000 ? this.current + speedLoop.getValue() : 0;
                mc.world.setWorldTime(current);
                break;
            case DTime:
                mc.world.setWorldTime(DTimeMode.getValue().getTime());
                break;
        }
    }
}
