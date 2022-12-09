package me.dyzjct.kura.module.modules.combat;

import me.dyzjct.kura.Kura;
import me.dyzjct.kura.event.events.client.PacketEvents;
import me.dyzjct.kura.gui.Notification;
import me.dyzjct.kura.manager.FriendManager;
import me.dyzjct.kura.module.Category;
import me.dyzjct.kura.module.Module;
import me.dyzjct.kura.setting.Setting;
import me.dyzjct.kura.utils.chat.ColourTextFormatting;
import me.dyzjct.kura.utils.mc.ChatUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;

import static me.dyzjct.kura.utils.chat.ColourTextFormatting.toTextMap;

@Module.Info(name = "TotemPopCounter", description = "Counts how many times players pop", category = Category.COMBAT)
public class TotemPopCounter extends Module {
    private final Setting<Announce> announceSetting = msetting("Announce", Announce.CLIENT);
    private final Setting<Boolean> countFriends = bsetting("CountFriends", true);
    private final Setting<Boolean> countSelf = bsetting("CountSelf", true);
    private final Setting<Boolean> resetDeaths = bsetting("ResetDeath", true);
    private final Setting<Boolean> resetSelfDeaths = bsetting("ResetSelfDeath", true);
    private final Setting<Boolean> thanksTo = bsetting("ThanksTo", false);
    private final Setting<ColourTextFormatting.ColourCode> colourCode = msetting("ColorName", ColourTextFormatting.ColourCode.AQUA);
    private final Setting<ColourTextFormatting.ColourCode> colourCode1 = msetting("ColorNumber", ColourTextFormatting.ColourCode.AQUA);
    private HashMap<String, Integer> playerList = new HashMap<>();
    private boolean isDead = false;

    @SubscribeEvent
    public void useTotem(NMSL event) {
        if (fullNullCheck()) {
            return;
        }
        if (playerList == null) {
            playerList = new HashMap<>();
        }
        if (playerList.get(event.getEntity().getName()) == null) {
            playerList.put(event.getEntity().getName(), 1);
            sendMessage(formatName(event.getEntity().getName()) + " popped " + formatNumber(1) + " totem" + ending());
        } else if (!(playerList.get(event.getEntity().getName()) == null)) {
            int popCounter = playerList.get(event.getEntity().getName());
            popCounter += 1;
            playerList.put(event.getEntity().getName(), popCounter);
            sendMessage(formatName(event.getEntity().getName()) + " popped " + formatNumber(popCounter) + " totems" + ending());
        }
    }

    @SubscribeEvent
    public void popListener(PacketEvents.Receive event) {
        if (mc.player == null) {
            return;
        }
        if (event.getPacket() instanceof SPacketEntityStatus) {
            SPacketEntityStatus packet = event.getPacket();
            if (packet.getOpCode() == 35) {
                Entity entity = packet.getEntity(mc.world);
                if (friendCheck(entity.getName()) || selfCheck(entity.getName())) {
                    MinecraftForge.EVENT_BUS.post(new NMSL(entity));
                }
            }
        }
    }

    @Override
    public void onUpdate() {
        if (!isDead
                && resetSelfDeaths.getValue()
                && 0 >= mc.player.getHealth()) {
            sendMessage(formatName(mc.player.getName()) + " died and " + grammar(mc.player.getName()) + " pop list was reset!");
            isDead = true;
            playerList.clear();
            return;
        }
        if (isDead && 0 < mc.player.getHealth()) {
            isDead = false;
        }

        for (EntityPlayer player : mc.world.playerEntities) {
            if (
                    resetDeaths.getValue()
                            && 0 >= player.getHealth()
                            && friendCheck(player.getName())
                            && selfCheck(player.getName())
                            && playerList.containsKey(player.getName())) {
                sendMessage(formatName(player.getName()) + " died after popping " + formatNumber(playerList.get(player.getName())) + " totems" + ending());
                playerList.remove(player.getName(), playerList.get(player.getName()));
            }
        }
    }

    private boolean friendCheck(String name) {
        if (isDead) {
            return false;
        }
        if (FriendManager.isFriend(name)) {
            return countFriends.getValue();
        }
        return true;
    }

    private boolean selfCheck(String name) {
        if (isDead) {
            return false;
        }
        if (countSelf.getValue() && name.equalsIgnoreCase(mc.player.getName())) {
            return true;
        } else return countSelf.getValue() || !name.equalsIgnoreCase(mc.player.getName());
    }

    private boolean isSelf(String name) {
        return name.equalsIgnoreCase(mc.player.getName());
    }

    private String formatName(String name) {
        String extraText = "";
        if (FriendManager.isFriend(name) && !isPublic()) {
            extraText = "Your friend, ";
        } else if (FriendManager.isFriend(name) && isPublic()) {
            extraText = "My friend, ";
        }
        if (isSelf(name)) {
            extraText = "";
            name = "I";
        }

        if (announceSetting.getValue().equals(Announce.EVERYONE)) {
            return extraText + name;
        }
        return extraText + setToText(colourCode.getValue()) + name + TextFormatting.RESET;
    }

    private String grammar(String name) {
        if (isSelf(name)) {
            return "my";
        } else {
            return "their";
        }
    }

    private String ending() {
        if (thanksTo.getValue()) {
            return " thanks to " + Kura.MOD_NAME + "!";
        } else {
            return "!";
        }
    }

    private boolean isPublic() {
        return announceSetting.getValue().equals(Announce.EVERYONE);
    }

    private String formatNumber(int message) {
        if (announceSetting.getValue().equals(Announce.EVERYONE)) {
            return "" + message;
        }
        return setToText(colourCode1.getValue()) + "" + message + TextFormatting.RESET;
    }

    private void sendMessage(String message) {
        switch (announceSetting.getValue()) {
            case CLIENT:
                ChatUtil.sendMessage(message);
                return;
            case EVERYONE:
                ChatUtil.sendServerMessage(message);
                return;
            default:
        }
        ChatUtil.sendClientMessage(message, Notification.Type.INFO);
    }

    private TextFormatting setToText(ColourTextFormatting.ColourCode colourCode) {
        return toTextMap.get(colourCode);
    }

    private enum Announce {CLIENT, EVERYONE}

    public static class NMSL extends Event {
        public Entity entity;

        public NMSL(Entity entity) {
            super();
            this.entity = entity;
        }

        public Entity getEntity() {
            return entity;
        }
    }
}