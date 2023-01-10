package me.windyteam.kura.module.modules.misc;

import me.windyteam.kura.module.Category;
import me.windyteam.kura.module.Module;
import me.windyteam.kura.setting.IntegerSetting;
import me.windyteam.kura.utils.Timer;
import me.windyteam.kura.utils.mc.ChatUtil;
import me.windyteam.kura.utils.mc.ChatUtil;
import net.minecraft.network.play.client.CPacketChatMessage;

import java.util.Random;

@Module.Info(name = "EasyKitsCrasher", description = "Stupid EasyKitsPlugin", category = Category.MISC)
public class EasyKitsCrasher extends Module {
    public IntegerSetting delay = isetting("Delay", 250, 0, 10000);
    public int ez;
    Timer timer = new Timer();

    @Override
    public void onEnable() {
        timer.reset();
    }

    @Override
    public void onDisable() {
        timer.reset();
    }

    @Override
    public String getHudInfo() {
        return "[ " + ez + " ]";
    }

    @Override
    public void onUpdate() {
        if (fullNullCheck()) {
            return;
        }
        if (timer.passed(delay.getValue())) {
            Random r = new Random();
            String nmsl = "/kit create " + r.nextInt(1999999999);
            mc.player.connection.sendPacket(new CPacketChatMessage(nmsl));
            ChatUtil.sendMessage(nmsl);
            ez++;
            timer.reset();
        }
    }
}
