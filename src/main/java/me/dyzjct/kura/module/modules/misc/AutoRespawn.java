package me.dyzjct.kura.module.modules.misc;

import me.dyzjct.kura.event.events.gui.GuiScreenEvent;
import me.dyzjct.kura.module.Category;
import me.dyzjct.kura.module.Module;
import me.dyzjct.kura.setting.Setting;
import me.dyzjct.kura.utils.mc.ChatUtil;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Created by 086 on 9/04/2018.
 * Updated 16 November 2019 by hub
 */
@Module.Info(name = "AutoRespawn", description = "Automatically respawn after dying", category = Category.MISC)
public class AutoRespawn extends Module {

    private final Setting<Boolean> respawn = bsetting("Respawn", true);
    private final Setting<Boolean> deathCoords = bsetting("DeadCoords", true);
    private final Setting<Boolean> antiGlitchScreen = bsetting("AntiGlitchScreen", true);

    @SubscribeEvent
    public void listener(GuiScreenEvent.Displayed event) {

        if (!(event.getScreen() instanceof GuiGameOver)) {
            return;
        }

        if (deathCoords.getValue() && mc.player.getHealth() <= 0) {
            ChatUtil.NoSpam.sendMessage(String.format("You died at x %d y %d z %d", (int) mc.player.posX, (int) mc.player.posY, (int) mc.player.posZ));
        }

        if (respawn.getValue() || (antiGlitchScreen.getValue() && mc.player.getHealth() > 0)) {
            mc.player.respawnPlayer();
            mc.displayGuiScreen(null);
        }

    }

}
