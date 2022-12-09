package me.dyzjct.kura.module.modules.combat;

import me.dyzjct.kura.event.events.client.PacketEvents;
import me.dyzjct.kura.event.events.entity.EventPlayerUpdate;
import me.dyzjct.kura.module.Category;
import me.dyzjct.kura.module.Module;
import me.dyzjct.kura.setting.BooleanSetting;
import me.dyzjct.kura.utils.Timer;
import me.dyzjct.kura.utils.mc.ChatUtil;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import net.minecraft.network.play.server.SPacketSpawnPlayer;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import java.util.*;

@Module.Info(name = "AntiBot", category = Category.COMBAT)
public class AntiBot extends Module {
    public static AntiBot INSTANCE = new AntiBot();
    public static List<EntityPlayer> bots = new ArrayList<>();
    private final Map<Integer, UUID> playersMap = new HashMap<>();
    public BooleanSetting hyp = bsetting("Hypixel", false);
    public BooleanSetting flyCheck = bsetting("CheckBotFlying(Bug)", false);
    public BooleanSetting strict = bsetting("Strict", false);
    public Timer flagTimer = new Timer();

    @SubscribeEvent
    public void onPlayerUpdate(EventPlayerUpdate event) {
        if (mc.getCurrentServerData() == null) {
            return;
        }
        mc.world.playerEntities.removeIf(this::isBot);
    }

    @SubscribeEvent
    public void onUpdate(TickEvent.ClientTickEvent event) {
        if (fullNullCheck()) {
            return;
        }
        for (EntityPlayer e : mc.world.playerEntities) {
            if (flyCheck.getValue()) {
                if (!e.isElytraFlying() && !mc.world.checkBlockCollision(e.getEntityBoundingBox().offset(0.0, -1.1, 0.0)) && e != mc.player && ((e.prevPosY < e.posY && !e.onGround && flagTimer.passed(2500) && !e.isElytraFlying() && mc.world.getBlockState(e.getPosition().add(0.0, 2.0, 0.0)).getBlock().equals(Blocks.AIR)))) {
                    ChatUtil.sendMessage(e.getName() + " flagged As Bot and Got Filtered! ");
                    bots.add(e);
                    //playersMap.put(e.entityId, e.getUniqueID());
                    flagTimer.reset();
                }
            }
        }
        if (hyp.getValue()) {
            for (Entity entity : mc.world.loadedEntityList) {
                if (entity instanceof EntityPlayer) {
                    if (entity != mc.player) {
                        EntityPlayer player = (EntityPlayer) entity;
                        if (player.getName().startsWith(ChatUtil.SECTIONSIGN + "c") || !isInTablist(player)) {
                            if (player.getName().startsWith(ChatUtil.SECTIONSIGN + "c") && !isInTablist(player)) {
                                if (!bots.contains(player)) {
                                    bots.add(player);
                                }
                            }

                            if (player.isInvisible() && !bots.contains(player)) {
                                float f = (float) (mc.player.posX - player.posX);
                                float f2 = (float) (mc.player.posZ - player.posZ);
                                double horizontalReaach = MathHelper.sqrt(f * f + f2 * f2);
                                if (horizontalReaach < 1) {
                                    double vert = mc.player.posY = -player.posY;
                                    if (vert <= 5) {
                                        if (mc.player.ticksExisted % 5 == 0) {
                                            bots.add(player);
                                        }
                                    }
                                }
                            }
                        }

                    }
                }
            }
            Count();
        }
        if (strict.getValue()) {
            Count();
        }
    }

    public void Count() {
        if (!bots.isEmpty()) {
            for (int i = 0; i < bots.size(); i++) {
                if (bots.contains(bots.get(i))) {
                    if (!mc.world.playerEntities.contains(bots.get(i))) {
                        bots.remove(bots.get(i));
                    }
                }
            }
            for (EntityLivingBase entityPlayer : bots) {
                if (!entityPlayer.getName().equalsIgnoreCase(mc.player.getName())) {
                    mc.world.removeEntity(entityPlayer);
                }
            }
        }
    }

    @SubscribeEvent
    public void onPacketEvent(PacketEvents.Receive event) {
        if (mc.world == null || mc.player == null) {
            return;
        }
        if (event.getPacket() instanceof SPacketSpawnPlayer) {
            final SPacketSpawnPlayer packet = event.getPacket();
            double posX = packet.getX() / 32D;
            double posY = packet.getY() / 32D;
            double posZ = packet.getZ() / 32D;

            double diffX = mc.player.posX - posX;
            double diffY = mc.player.posY - posY;
            double diffZ = mc.player.posZ - posZ;

            double dist = Math.sqrt(diffX * diffX + diffY * diffY + diffZ * diffZ);

            if (dist <= 8 && posX != mc.player.posX && posY != mc.player.posY && posZ != mc.player.posZ) {
                playersMap.put(packet.getEntityID(), packet.getUniqueId());
            }
            if (Math.sqrt((mc.player.posX - packet.getX()) * (mc.player.posX - packet.getX()) + (mc.player.posY - packet.getY()) * (mc.player.posY - packet.getY()) + (mc.player.posZ - packet.getZ()) * (mc.player.posZ - packet.getZ())) <= 0.0 && packet.getX() != mc.player.posX && packet.getY() != mc.player.posY && packet.getZ() != mc.player.posZ) {
                this.playersMap.put(packet.getEntityID(), packet.getUniqueId());
            }
        } else if (event.getPacket() instanceof SPacketDestroyEntities) {
            SPacketDestroyEntities packet = event.getPacket();
            for (int n : packet.getEntityIDs()) {
                this.playersMap.remove(n);
            }
        }
    }

    private boolean isInTablist(EntityLivingBase player) {
        if (mc.isSingleplayer()) {
            return true;
        }
        for (NetworkPlayerInfo o : mc.getConnection().getPlayerInfoMap()) {
            if (o.getGameProfile().getName().equalsIgnoreCase(player.getName())) {
                return true;
            }
        }
        return false;
    }

    public boolean isBot(EntityPlayer entityPlayer) {
        return bots.contains(entityPlayer) || entityPlayer.getName().contains(String.valueOf(ChatUtil.SECTIONSIGN)) || entityPlayer.getUniqueID().toString().startsWith(entityPlayer.getName()) || entityPlayer.getName().contains("[NPC]") || entityPlayer.getName().contains("CIT-") || !StringUtils.stripControlCodes(entityPlayer.getGameProfile().getName()).equals(entityPlayer.getName()) || entityPlayer.getGameProfile().getId() != entityPlayer.getUniqueID() || this.playersMap.containsKey(entityPlayer.getEntityId());
    }

    @SubscribeEvent
    public void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        bots.clear();
    }

    @SubscribeEvent
    public void onJoin(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        bots.clear();
    }

    @SubscribeEvent
    public void onWorldEvent(EntityJoinWorldEvent event) {
        if (event.getEntity() == mc.player) {
            this.playersMap.clear();
            bots.clear();
        }
    }

}
