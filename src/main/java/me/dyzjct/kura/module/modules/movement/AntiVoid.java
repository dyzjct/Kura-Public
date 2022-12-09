package me.dyzjct.kura.module.modules.movement;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.dyzjct.kura.event.events.entity.MoveEvent;
import me.dyzjct.kura.module.Category;
import me.dyzjct.kura.module.Module;
import me.dyzjct.kura.setting.ModeSetting;
import me.dyzjct.kura.setting.Setting;
import me.dyzjct.kura.utils.mc.ChatUtil;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Info(name = "AntiVoid", category = Category.MOVEMENT, description = "Fuck OFF 2b2t.org void")
public class AntiVoid extends Module {
    public ModeSetting<?> mode;
    public Setting<Boolean> display;

    public AntiVoid() {
        mode = msetting("Mode", Mode.BOUNCE);
        display = bsetting("WarnMessage", true);
    }

    @SubscribeEvent
    public void onUpdate(MoveEvent event) {
        final double yLevel = mc.player.posY;
        if (yLevel <= 0.5) {
            ChatUtil.NoSpam.sendMessage(ChatFormatting.RED + "Player " + ChatFormatting.GREEN + mc.player.getName() + ChatFormatting.RED + " is in the void!");
            if (this.mode.getValue().equals(Mode.BOUNCE)) {
                mc.player.moveVertical = 10.0f;
                mc.player.jump();
            }
            if (this.mode.getValue().equals(Mode.CANCEL)) {
                mc.player.moveVertical = 10.0f;
                mc.player.jump();
                event.setCanceled(true);
            }
        } else {
            mc.player.moveVertical = 0.0f;
        }
    }

    @Override
    public void onDisable() {
        mc.player.moveVertical = 0.0f;
    }

    @Override
    public String getHudInfo() {
        if (this.display.getValue()) {
            if (this.mode.getValue().equals(Mode.BOUNCE)) {
                return "Bounce";
            }
            if (this.mode.getValue().equals(Mode.CANCEL)) {
                return "Cancel";
            }
        }
        return null;
    }

    public enum Mode {
        BOUNCE,
        CANCEL
    }
}
