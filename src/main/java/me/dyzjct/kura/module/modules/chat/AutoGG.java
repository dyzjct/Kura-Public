package me.dyzjct.kura.module.modules.chat;

import me.dyzjct.kura.Kura;
import me.dyzjct.kura.event.events.client.PacketEvents;
import me.dyzjct.kura.module.Category;
import me.dyzjct.kura.module.Module;
import me.dyzjct.kura.setting.Setting;
import me.dyzjct.kura.utils.chat.ChatTextUtils;
import me.dyzjct.kura.utils.entity.EntityUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created 15 November 2019 by hub
 * Updated 24 November 2019 by hub
 */
@Module.Info(name = "AutoGG", category = Category.CHAT, description = "Announce killed Players")
public class AutoGG extends Module {
    public static AutoGG INSTANCE = new AutoGG();

    private ConcurrentHashMap<String, Integer> targetedPlayers = null;

    private final Setting<String> text = ssetting("Text", "RIP ");
    private final Setting<Boolean> clientName = bsetting("ClientName", false);
    private final Setting<Integer> timeoutTicks = isetting("TimeOutTicks", 20, 1, 50);

    @SubscribeEvent
    public void DeathEvent(LivingDeathEvent event) {

        if (mc.player == null) {
            return;
        }

        if (targetedPlayers == null) {
            targetedPlayers = new ConcurrentHashMap<>();
        }

        EntityLivingBase entity = event.getEntityLiving();

        if (entity == null) {
            return;
        }

        // skip non player entities
        if (!EntityUtil.isPlayer(entity)) {
            return;
        }

        EntityPlayer player = (EntityPlayer) entity;

        // skip if player is alive
        if (player.getHealth() > 0) {
            return;
        }

        String name = player.getName();
        if (shouldAnnounce(name)) {
            doAnnounce(name);
        }

    }

    @SubscribeEvent
    public void sendListener(PacketEvents.Send event) {

        if (mc.player == null) {
            return;
        }

        if (targetedPlayers == null) {
            targetedPlayers = new ConcurrentHashMap<>();
        }

        // return if packet is not of type CPacketUseEntity
        if (!(event.getPacket() instanceof CPacketUseEntity)) {
            return;
        }
        CPacketUseEntity cPacketUseEntity = event.getPacket();

        // return if action is not of type CPacketUseEntity.Action.ATTACK
        if (!(cPacketUseEntity.getAction().equals(CPacketUseEntity.Action.ATTACK))) {
            return;
        }

        // return if targeted Entity is not a player
        Entity targetEntity = cPacketUseEntity.getEntityFromWorld(mc.world);
        if (!EntityUtil.isPlayer(targetEntity)) {
            return;
        }

        addTargetedPlayer(targetEntity.getName());

    }

    @Override
    public void onEnable() {
        targetedPlayers = new ConcurrentHashMap<>();
    }

    @Override
    public void onDisable() {
        targetedPlayers = null;
    }

    @Override
    public void onUpdate() {
        if (isDisabled() || mc.player == null) {
            return;
        }
        if (targetedPlayers == null) {
            targetedPlayers = new ConcurrentHashMap<>();
        }
        for (Entity entity : mc.world.getLoadedEntityList()) {
            // skip non player entities
            if (!EntityUtil.isPlayer(entity)) {
                continue;
            }
            EntityPlayer player = (EntityPlayer) entity;
            // skip if player is alive
            if (player.getHealth() > 0) {
                continue;
            }
            String name = player.getName();
            if (shouldAnnounce(name)) {
                doAnnounce(name);
                break;
            }
        }

        targetedPlayers.forEach((name, timeout) -> {
            if (timeout <= 0) {
                targetedPlayers.remove(name);
            } else {
                targetedPlayers.put(name, timeout - 1);
            }
        });

    }

    private boolean shouldAnnounce(String name) {
        return targetedPlayers.containsKey(name);
    }

    private void doAnnounce(String name) {

        targetedPlayers.remove(name);

        StringBuilder message = new StringBuilder();
        message.append(text.getValue());
        message.append(name);
        message.append("!");

        if (clientName.getValue()) {
            message.append(" ");
            message.append(Kura.MOD_NAME);
            message.append(" OWNS YOU!");
        }

        String messageSanitized = message.toString().replaceAll(ChatTextUtils.SECTIONSIGN, "");

        if (messageSanitized.length() > 255) {
            messageSanitized = messageSanitized.substring(0, 255);
        }

        mc.player.connection.sendPacket(new CPacketChatMessage(messageSanitized));

    }

    public void addTargetedPlayer(String name) {

        // skip if self is the target
        if (Objects.equals(name, mc.player.getName())) {
            return;
        }

        if (targetedPlayers == null) {
            targetedPlayers = new ConcurrentHashMap<>();
        }

        targetedPlayers.put(name, timeoutTicks.getValue());

    }

}
