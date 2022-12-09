package me.dyzjct.kura.module.modules.misc;

import me.dyzjct.kura.module.Category;
import me.dyzjct.kura.module.Module;
import me.dyzjct.kura.mixin.client.MixinNetworkManager;

/**
 * @author 086
 * @see MixinNetworkManager
 */
@Module.Info(name = "NoPacketKick", category = Category.MISC, description = "Prevent large packets from kicking you")
public class NoPacketKick extends Module {
    public static NoPacketKick INSTANCE;

    public NoPacketKick() {
        NoPacketKick.INSTANCE = this;
    }

    public static boolean isEnable() {
        return NoPacketKick.INSTANCE.isEnabled();
    }
}
