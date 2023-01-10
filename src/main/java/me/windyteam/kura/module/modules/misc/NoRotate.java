package me.windyteam.kura.module.modules.misc;

import me.windyteam.kura.event.events.client.PacketEvents;
import me.windyteam.kura.module.Category;
import me.windyteam.kura.module.Module;
import me.windyteam.kura.setting.Setting;
import me.windyteam.kura.utils.Timer;
import me.windyteam.kura.utils.mc.ChatUtil;
import me.windyteam.kura.utils.mc.ChatUtil;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Info(name = "NoRotate",category = Category.MISC)
public class NoRotate
extends Module {
    private Setting<Integer> waitDelay = isetting("Delay", 2500, 0, 10000);
    private Timer timer = new Timer();
    private boolean cancelPackets = true;
    private boolean timerReset = false;

    

    @Override
    public void onLogout() {
        this.cancelPackets = false;
    }

    @Override
    public void onLogin() {
        this.timer.reset();
        this.timerReset = true;
    }

    @Override
    public void onUpdate() {
        if (this.timerReset && !this.cancelPackets && this.timer.passedMs(this.waitDelay.getValue().intValue())) {
            ChatUtil.sendMessage("<NoRotate> \u00a7cThis module might desync you!");
            this.cancelPackets = true;
            this.timerReset = false;
        }
    }

    @Override
    public void onEnable() {
        ChatUtil.sendMessage("<NoRotate> \u00a7cThis module might desync you!");
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvents.Receive event) {
        if (event.getStage() == 0 && this.cancelPackets && event.getPacket() instanceof SPacketPlayerPosLook) {
            SPacketPlayerPosLook packet = (SPacketPlayerPosLook)event.getPacket();
            packet.yaw = NoRotate.mc.player.rotationYaw;
            packet.pitch = NoRotate.mc.player.rotationPitch;
        }
    }
}

