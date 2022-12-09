package me.dyzjct.kura.module.modules.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.dyzjct.kura.manager.FriendManager;
import me.dyzjct.kura.module.Category;
import me.dyzjct.kura.module.Module;
import me.dyzjct.kura.setting.Setting;
import me.dyzjct.kura.utils.mc.ChatUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 26 October 2019 by hub
 * Updated 12 January 2020 by hub
 * Updated by polymer on 23/02/20
 */
@Module.Info(name = "VisualRange", description = "Shows players who enter and leave range in chat", category = Category.COMBAT)
public class VisualRange extends Module {
    private final Setting<Boolean> leaving = bsetting("Leave", true);

    private List<String> knownPlayers;

    @Override
    public void onUpdate() {
        if (mc.player == null) return;

        List<String> tickPlayerList = new ArrayList<>();

        for (Entity entity : mc.world.getLoadedEntityList()) {
            if (entity instanceof EntityPlayer) tickPlayerList.add(entity.getName());
        }

        if (tickPlayerList.size() > 0) {
            for (String playerName : tickPlayerList) {
                if (playerName.equals(mc.player.getName())) continue;

                if (!knownPlayers.contains(playerName)) {
                    knownPlayers.add(playerName);

                    if (FriendManager.isFriend(playerName)) {
                        sendNotification(ChatFormatting.GREEN.toString() + playerName + ChatFormatting.RESET.toString() + " entered the Battlefield!");
                    } else {
                        sendNotification(ChatFormatting.RED.toString() + playerName + ChatFormatting.RESET.toString() + " entered the Battlefield!");
                    }
                    return;
                }
            }
        }

        if (knownPlayers.size() > 0) {
            for (String playerName : knownPlayers) {
                if (!tickPlayerList.contains(playerName)) {
                    knownPlayers.remove(playerName);

                    if (leaving.getValue()) {
                        if (FriendManager.isFriend(playerName)) {
                            sendNotification(ChatFormatting.GREEN.toString() + playerName + ChatFormatting.RESET.toString() + " left the Battlefield!");
                        } else {
                            sendNotification(ChatFormatting.RED.toString() + playerName + ChatFormatting.RESET.toString() + " left the Battlefield!");
                        }
                    }

                    return;
                }
            }
        }

    }

    private void sendNotification(String s) {
        ChatUtil.sendMessage(s);
    }

    @Override
    public void onEnable() {
        this.knownPlayers = new ArrayList<>();
    }
}
