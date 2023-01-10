package me.windyteam.kura.module.modules.render;

import me.windyteam.kura.event.events.client.ConnectEvent;
import me.windyteam.kura.event.events.client.DisconnectEvent;
import me.windyteam.kura.event.events.render.RenderEvent;
import me.windyteam.kura.module.Category;
import me.windyteam.kura.module.Module;
import me.windyteam.kura.setting.BooleanSetting;
import me.windyteam.kura.setting.IntegerSetting;
import me.windyteam.kura.setting.Setting;
import me.windyteam.kura.utils.GeometryMasks;
import me.windyteam.kura.utils.Timer;
import me.windyteam.kura.utils.color.GSColor;
import me.windyteam.kura.utils.mc.ChatUtil;
import me.windyteam.kura.utils.render.RenderUtil;
import me.windyteam.kura.utils.mc.ChatUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Rewrote by Py on 13/05/2021
 */

@Module.Info(name = "LogOutSpots", category = Category.RENDER, description = "Draw EntityPlayer LogOut Spots")
public class LogoutSpots extends Module {
    public Setting<Integer> red = isetting("Red", 255, 0, 255);
    public Setting<Integer> green = isetting("Green", 198, 0, 255);
    public Setting<Integer> blue = isetting("Blue", 203, 0, 255);
    public Setting<Integer> alpha = isetting("Alpha", 70, 0, 255);
    public IntegerSetting range = isetting("Range", 100, 10, 260);
    public BooleanSetting chatMsg = bsetting("Chat", true);
    public BooleanSetting nameTag = bsetting("Nametags", true);
    public IntegerSetting lineWidth = isetting("Width", 1, 1, 10);
    public Map<Entity, String> loggedPlayers = new ConcurrentHashMap<>();
    public Set<EntityPlayer> worldPlayers = ConcurrentHashMap.newKeySet();
    public Timer timer = new Timer();

    public void onUpdate() {
        mc.world.playerEntities.stream()
                .filter(entityPlayer -> entityPlayer != mc.player)
                .filter(entityPlayer -> entityPlayer.getDistance(mc.player) <= range.getValue())
                .forEach(entityPlayer -> worldPlayers.add(entityPlayer));
    }

    public void onWorldRender(RenderEvent event) {
        if (mc.player != null && mc.world != null) {
            loggedPlayers.forEach(this::startFunction);
        }
    }

    public void onEnable() {
        loggedPlayers.clear();
        worldPlayers = ConcurrentHashMap.newKeySet();
    }

    public void onDisable() {
        worldPlayers.clear();
    }

    private void startFunction(Entity entity, String string) {
        if (entity.getDistance(mc.player) > range.getValue()) {
            return;
        }
        GSColor color = new GSColor(red.getValue(), green.getValue(), blue.getValue(), alpha.getValue());
        int posX = (int) entity.posX;
        int posY = (int) entity.posY;
        int posZ = (int) entity.posZ;

        String[] nameTagMessage = new String[2];
        nameTagMessage[0] = entity.getName() + " (" + string + ")";
        nameTagMessage[1] = "(" + posX + "," + posY + "," + posZ + ")";

        GlStateManager.pushMatrix();

        if (nameTag.getValue()) {
            RenderUtil.drawNametag(entity, nameTagMessage, color, 0);
        }

        RenderUtil.drawBoundingBox(entity.getRenderBoundingBox(), lineWidth.getValue(), color);
        RenderUtil.drawBox(entity.getRenderBoundingBox(), true, -0.4, new GSColor(color, 50), GeometryMasks.Quad.ALL);

        GlStateManager.popMatrix();
    }

    @SubscribeEvent
    public void Join(ConnectEvent event) {
        if (mc.world != null) {
            loggedPlayers.keySet().removeIf((entity) -> {
                if (entity.getName().equalsIgnoreCase(event.getName())) {
                    if (chatMsg.getValue()) {
                        ChatUtil.sendMessage(event.getName() + " reconnected!");
                    }
                    return true;
                }
                return false;
            });
        }
    }

    @SubscribeEvent
    public void Leave(DisconnectEvent event) {
        if (mc.world != null) {
            worldPlayers.removeIf(entity -> {
                if (entity.getName().equalsIgnoreCase(event.getName())) {
                    String date = new SimpleDateFormat("k:mm").format(new Date());
                    loggedPlayers.put(entity, date);

                    if (chatMsg.getValue() && timer.getPassedTimeMs() / 50L >= 5) {
                        String location = "(" + (int) entity.posX + "," + (int) entity.posY + "," + (int) entity.posZ + ")";
                        ChatUtil.sendMessage(event.getName() + " disconnected at " + location + "!");
                        timer.reset();
                    }
                    return true;
                }
                return false;
            });
        }
    }
}
