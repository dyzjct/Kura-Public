package me.windyteam.kura.module.modules.player;

import me.windyteam.kura.event.events.client.PacketEvents;
import me.windyteam.kura.event.events.world.WorldEvent;
import me.windyteam.kura.module.Category;
import me.windyteam.kura.module.Module;
import me.windyteam.kura.module.ModuleManager;
import me.windyteam.kura.module.modules.movement.PacketFlyRewrite;
import me.windyteam.kura.setting.BooleanSetting;
import me.windyteam.kura.utils.mc.ChatUtil;
import me.windyteam.kura.event.events.world.WorldEvent;
import me.windyteam.kura.module.Category;
import me.windyteam.kura.module.Module;
import me.windyteam.kura.module.ModuleManager;
import me.windyteam.kura.module.modules.movement.PacketFlyRewrite;
import me.windyteam.kura.setting.BooleanSetting;
import me.windyteam.kura.utils.mc.ChatUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketKeepAlive;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerAbilities;
import net.minecraft.network.play.server.SPacketPlayerAbilities;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.network.play.server.SPacketRespawn;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.List;

@Module.Info(name = "Disabler", category = Category.PLAYER)
public class Disabler extends Module {
    public List<Packet<?>> KeepAlive = new ArrayList<>();
    public BooleanSetting debug = bsetting("Debug", false);
    public BooleanSetting PacketKeepAlive = bsetting("PacketKeepAlive", false);
    public BooleanSetting PacketKeepAliveCancel = bsetting("PacketKeepAliveCancel", false);
    public BooleanSetting caction = bsetting("CPacketActionCancel", false);
    public BooleanSetting cab = bsetting("CPacketAbilities", false);
    public BooleanSetting sab = bsetting("SPacketAbilitiesCancel", false);
    public BooleanSetting cplayer = bsetting("CPacketPlayer", false);
    public BooleanSetting posrot = bsetting("PositionRotation", false);
    public BooleanSetting packetflyTest = bsetting("PacketFlyTest" , false);
    public int C06List = 0;
    public double xCoord;
    public double yCoord;
    public double zCoord;

    @Override
    public void onDisable() {
        if (fullNullCheck()) {
            return;
        }
        KeepAlive.clear();
        C06List = 0;
    }

    @Override
    public void onEnable() {
        if (fullNullCheck()) {
            return;
        }
        KeepAlive.clear();
        C06List = 0;
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (fullNullCheck()) {
            return;
        }
        try {
            if (PacketKeepAlive.getValue()) {
                mc.player.connection.sendPacket(new CPacketKeepAlive(0));
                if (mc.player.ticksExisted % 100 == 0) {
                    mc.player.connection.sendPacket(KeepAlive.get(0));
                    KeepAlive.remove(KeepAlive.get(0));
                    KeepAlive.clear();
                }
            }
        } catch (Exception ignored) {
        }
    }

    @SubscribeEvent
    public void WorldEvent(WorldEvent event) {
        if (fullNullCheck()) {
            return;
        }
        KeepAlive.clear();
        C06List = 0;
    }

    @SubscribeEvent
    public void onPacket(PacketEvents.Send event) {
        if (fullNullCheck()) {
            return;
        }
        try {
            if (event.getPacket() instanceof CPacketPlayer.PositionRotation && posrot.getValue()) {
                if (C06List > 0) {
                    if (((CPacketPlayer.PositionRotation) event.getPacket()).x == xCoord && ((CPacketPlayer.PositionRotation) event.getPacket()).y == yCoord && ((CPacketPlayer.PositionRotation) event.getPacket()).z == zCoord) {
                        mc.getConnection().sendPacket(new CPacketPlayer.Position(((CPacketPlayer.PositionRotation) event.getPacket()).x, ((CPacketPlayer.PositionRotation) event.getPacket()).y, ((CPacketPlayer.PositionRotation) event.getPacket()).z, ((CPacketPlayer.PositionRotation) event.getPacket()).onGround));
                        if (debug.getValue()) {
                            ChatUtil.sendDisablerDebugMessage("CPosition Sent");
                        }
                        event.setCanceled(true);
                        if (debug.getValue()) {
                            ChatUtil.sendDisablerDebugMessage("CPosRotation Canceled");
                        }
                    }
                }
                C06List++;
            }
            if (event.getPacket() instanceof CPacketPlayer) {
                if (mc.player.ticksExisted % 10 == 0 && cplayer.getValue()) {
                    mc.getConnection().getNetworkManager().sendPacket(new CPacketPlayerAbilities());
                    if (debug.getValue()) {
                        ChatUtil.sendDisablerDebugMessage("PacketPlayerAbilities Sent");
                    }
                }
                if (ModuleManager.getModuleByClass(PacketFlyRewrite.class).isEnabled() && packetflyTest.getValue() && event.getStage() == 0){
                    event.setCanceled(true);
                    //mc.getConnection().getNetworkManager().sendPacket(new CPacketPlayer.Position(((CPacketPlayer) event.getPacket()).x, -1e+159, ((CPacketPlayer) event.getPacket()).z + 10, true));
                    //mc.getConnection().getNetworkManager().sendPacket(new CPacketPlayer.Position(((CPacketPlayer) event.getPacket()).x, ((CPacketPlayer) event.getPacket()).y, ((CPacketPlayer) event.getPacket()).z, true));
                    if (debug.getValue()){
                        ChatUtil.sendDisablerDebugMessage("CPacketPlayer Canceled For PacketFly");
                    }
                }
            }
            if (event.getPacket() instanceof CPacketKeepAlive) {
                KeepAlive.add(event.getPacket());
                event.setCanceled(PacketKeepAliveCancel.getValue());
            }
            if (event.getPacket() instanceof CPacketPlayerAbilities && cab.getValue()) {
                ((CPacketPlayerAbilities) event.getPacket()).setFlying(true);
                ((CPacketPlayerAbilities) event.getPacket()).setAllowFlying(true);
                ((CPacketPlayerAbilities) event.getPacket()).setInvulnerable(true);
                ((CPacketPlayerAbilities) event.getPacket()).setCreativeMode(true);
                if (debug.getValue()) {
                    ChatUtil.sendDisablerDebugMessage("PacketPlayerAbilities Received");
                }
            }
            if (event.getPacket() instanceof CPacketEntityAction && caction.getValue()) {
                if (((CPacketEntityAction) event.getPacket()).getAction().toString().contains("SNEAK")) {
                    event.setCanceled(true);
                }
                if (debug.getValue()) {
                    ChatUtil.sendDisablerDebugMessage("Packet C0B");
                }
            }
        } catch (Exception ignored) {
        }
    }

    @SubscribeEvent
    public void onReceivePacket(PacketEvents.Receive event) {
        if (fullNullCheck()) {
            return;
        }
        try {
            if (event.getPacket() instanceof SPacketPlayerAbilities && !mc.player.isDead && sab.getValue()) {
                event.setCanceled(true);
                if (debug.getValue()) {
                    ChatUtil.sendDisablerDebugMessage("SPacketPlayerAbilities Canceled");
                }
            }
            if (event.getPacket() instanceof SPacketRespawn && posrot.getValue()) {
                C06List = 0;
                if (debug.getValue()) {
                    ChatUtil.sendDisablerDebugMessage("PacketList Cleared!");
                }
            }
            if (event.getPacket() instanceof SPacketPlayerPosLook && posrot.getValue()) {
                xCoord = ((SPacketPlayerPosLook) event.getPacket()).getX();
                yCoord = ((SPacketPlayerPosLook) event.getPacket()).getY();
                zCoord = ((SPacketPlayerPosLook) event.getPacket()).getZ();
                if (debug.getValue()) {
                    ChatUtil.sendDisablerDebugMessage("SPosLook Received!");
                }
            }
        } catch (Exception ignored) {
        }
    }
}
