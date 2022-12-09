package me.dyzjct.kura.module.modules.client;

import me.dyzjct.kura.module.Category;
import me.dyzjct.kura.module.Module;
import me.dyzjct.kura.setting.BooleanSetting;
import me.dyzjct.kura.setting.IntegerSetting;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;

@Module.Info(name = "Null Aura", category = Category.CLIENT, description = "java.lang.NullPointerException")
public class NullAura
        extends Module {
    IntegerSetting level = this.isetting("Level", 1, 1, 25);
    BooleanSetting Scrash = this.bsetting("Server Crash", false);
    BooleanSetting crash = this.bsetting("Crash", false);

    @Override
    public void onInit() {
    }

    @Override
    public void onEnable() {
        try {
            for (int c = 0; c <= this.level.getValue(); c++) {
                if (c < this.level.getValue()) {
                    throw new NullPointerException();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (this.Scrash.getValue()) {
            for (int c = 0; c <= this.level.getValue(); c++) {
                if (c < this.level.getValue()) {
                    mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(null));
                }
            }
        }
        if (this.crash.getValue()) {
            this.crash.setValue(false);
            throw new NullPointerException("null");
        }
        this.disable();
    }

    @Override
    public void onConfigLoad() {
        this.crash.setValue(false);
    }

    @Override
    public void onConfigSave() {
        this.crash.setValue(false);
    }
}

