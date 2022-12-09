package me.dyzjct.kura.module.modules.player;

import me.dyzjct.kura.event.events.client.PacketEvents;
import me.dyzjct.kura.module.Category;
import me.dyzjct.kura.module.Module;
import me.dyzjct.kura.setting.BooleanSetting;
import me.dyzjct.kura.setting.ModeSetting;
import me.dyzjct.kura.setting.Setting;
import net.minecraft.network.play.client.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author S-B99
 * Updated by NobleSix
 */
@Module.Info(name = "PacketCancel", description = "Cancels specific packets used for various actions", category = Category.PLAYER)
public class PacketCancel extends Module {
    //BASIC
    private final Setting<Boolean> all = bsetting("All", false);
    private final Setting<Page> p = msetting("Page", Page.DUPE);
    //DUPE PAGE
    private final Setting<Boolean> packetInput = bsetting("Input", false).bn((BooleanSetting) all).m((ModeSetting) p, Page.DUPE);
    private final Setting<Boolean> packetPlayer = bsetting("Player", false).bn((BooleanSetting) all).m((ModeSetting) p, Page.DUPE);
    private final Setting<Boolean> packetEntityAction = bsetting("EntityAction", false).bn((BooleanSetting) all).m((ModeSetting) p, Page.DUPE);
    private final Setting<Boolean> packetUseEntity = bsetting("UseEntity", false).bn((BooleanSetting) all).m((ModeSetting) p, Page.DUPE);
    private final Setting<Boolean> packetVehicleMove = bsetting("VehicleMove", false).bn((BooleanSetting) all).m((ModeSetting) p, Page.DUPE);
    //PAGE ONE
    private final Setting<Boolean> packetAnimation = bsetting("Animation", false).bn((BooleanSetting) all).m((ModeSetting) p, Page.ONE);
    private final Setting<Boolean> packetChatMessage = bsetting("ChatMessage", false).bn((BooleanSetting) all).m((ModeSetting) p, Page.ONE);
    private final Setting<Boolean> packetClickWindow = bsetting("ClickWindow", false).bn((BooleanSetting) all).m((ModeSetting) p, Page.ONE);
    private final Setting<Boolean> packetClientSettings = bsetting("ClientSettings", false).bn((BooleanSetting) all).m((ModeSetting) p, Page.ONE);
    private final Setting<Boolean> packetClientStatus = bsetting("ClientStatus", false).bn((BooleanSetting) all).m((ModeSetting) p, Page.ONE);
    private final Setting<Boolean> packetCloseWindow = bsetting("CloseWindow", false).bn((BooleanSetting) all).m((ModeSetting) p, Page.ONE);
    private final Setting<Boolean> packetConfirmTeleport = bsetting("ConfirmTeleport", false).bn((BooleanSetting) all).m((ModeSetting) p, Page.ONE);
    private final Setting<Boolean> packetConfirmTransaction = bsetting("ConfirmTransc", false).bn((BooleanSetting) all).m((ModeSetting) p, Page.ONE);
    private final Setting<Boolean> packetCreativeInventoryAction = bsetting("CmInvAction", false).bn((BooleanSetting) all).m((ModeSetting) p, Page.ONE);
    private final Setting<Boolean> packetCustomPayload = bsetting("CustomPayLoad", false).bn((BooleanSetting) all).m((ModeSetting) p, Page.ONE);
    private final Setting<Boolean> packetEnchantItem = bsetting("EnchantItem", false).bn((BooleanSetting) all).m((ModeSetting) p, Page.ONE);
    private final Setting<Boolean> packetHeldItemChange = bsetting("HeldItemChange", false).bn((BooleanSetting) all).m((ModeSetting) p, Page.ONE);
    //PAGE TWO
    private final Setting<Boolean> packetKeepAlive = bsetting("KeepAlive", false).bn((BooleanSetting) all).m((ModeSetting) p, Page.TWO);
    private final Setting<Boolean> packetPlayerAbilities = bsetting("PlayerAbilities", false).bn((BooleanSetting) all).m((ModeSetting) p, Page.TWO);
    private final Setting<Boolean> packetPlayerDigging = bsetting("PlayerDigging", false).bn((BooleanSetting) all).m((ModeSetting) p, Page.TWO);
    private final Setting<Boolean> packetPlayerTryUseItem = bsetting("PlayerTryUse", false).bn((BooleanSetting) all).m((ModeSetting) p, Page.TWO);
    private final Setting<Boolean> packetPlayerTryUseItemOnBlock = bsetting("PlayerTryUseOnBlock", false).bn((BooleanSetting) all).m((ModeSetting) p, Page.TWO);
    private final Setting<Boolean> packetRecipeInfo = bsetting("RecipeInfo", false).bn((BooleanSetting) all).m((ModeSetting) p, Page.TWO);
    private final Setting<Boolean> packetResourcePackStatus = bsetting("RsrcPackStatus", false).bn((BooleanSetting) all).m((ModeSetting) p, Page.TWO);
    private final Setting<Boolean> packetSeenAdvancements = bsetting("SeenAdvances", false).bn((BooleanSetting) all).m((ModeSetting) p, Page.TWO);
    private final Setting<Boolean> packetSpectate = bsetting("Spectate", false).bn((BooleanSetting) all).m((ModeSetting) p, Page.TWO);
    private final Setting<Boolean> packetSteerBoat = bsetting("SteerBoat", false).bn((BooleanSetting) all).m((ModeSetting) p, Page.TWO);
    private final Setting<Boolean> packetTabComplete = bsetting("TabComplete", false).bn((BooleanSetting) all).m((ModeSetting) p, Page.TWO);
    private final Setting<Boolean> packetUpdateSign = bsetting("UpdateSign", false).bn((BooleanSetting) all).m((ModeSetting) p, Page.TWO);
    private int numPackets;

    @SubscribeEvent
    public void send(PacketEvents.Send event) {
        if (
                (all.getValue())
                        ||
                        (packetInput.getValue() && event.getPacket() instanceof CPacketInput)
                        ||
                        (packetPlayer.getValue() && event.getPacket() instanceof CPacketPlayer)
                        ||
                        (packetEntityAction.getValue() && event.getPacket() instanceof CPacketEntityAction)
                        ||
                        (packetUseEntity.getValue() && event.getPacket() instanceof CPacketUseEntity)
                        ||
                        (packetVehicleMove.getValue() && event.getPacket() instanceof CPacketVehicleMove)//1
                        ||
                        (packetAnimation.getValue() && event.getPacket() instanceof CPacketAnimation)
                        ||
                        (packetChatMessage.getValue() && event.getPacket() instanceof CPacketChatMessage)
                        ||
                        (packetClickWindow.getValue() && event.getPacket() instanceof CPacketClickWindow)
                        ||
                        (packetClientSettings.getValue() && event.getPacket() instanceof CPacketClientSettings)
                        ||
                        (packetClientStatus.getValue() && event.getPacket() instanceof CPacketClientStatus)
                        ||
                        (packetCloseWindow.getValue() && event.getPacket() instanceof CPacketCloseWindow)
                        ||
                        (packetConfirmTeleport.getValue() && event.getPacket() instanceof CPacketConfirmTeleport)
                        ||
                        (packetConfirmTransaction.getValue() && event.getPacket() instanceof CPacketConfirmTransaction)
                        ||
                        (packetCreativeInventoryAction.getValue() && event.getPacket() instanceof CPacketCreativeInventoryAction)
                        ||
                        (packetCustomPayload.getValue() && event.getPacket() instanceof CPacketCustomPayload)
                        ||
                        (packetEnchantItem.getValue() && event.getPacket() instanceof CPacketEnchantItem)
                        ||
                        (packetHeldItemChange.getValue() && event.getPacket() instanceof CPacketHeldItemChange)//2
                        ||
                        (packetKeepAlive.getValue() && event.getPacket() instanceof CPacketKeepAlive)
                        ||
                        (packetPlayerAbilities.getValue() && event.getPacket() instanceof CPacketPlayerAbilities)
                        ||
                        (packetPlayerDigging.getValue() && event.getPacket() instanceof CPacketPlayerDigging)
                        ||
                        (packetPlayerTryUseItem.getValue() && event.getPacket() instanceof CPacketPlayerTryUseItem)
                        ||
                        (packetPlayerTryUseItemOnBlock.getValue() && event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock)
                        ||
                        (packetRecipeInfo.getValue() && event.getPacket() instanceof CPacketRecipeInfo)
                        ||
                        (packetResourcePackStatus.getValue() && event.getPacket() instanceof CPacketResourcePackStatus)
                        ||
                        (packetSeenAdvancements.getValue() && event.getPacket() instanceof CPacketSeenAdvancements)
                        ||
                        (packetSpectate.getValue() && event.getPacket() instanceof CPacketSpectate)
                        ||
                        (packetSteerBoat.getValue() && event.getPacket() instanceof CPacketSteerBoat)
                        ||
                        (packetTabComplete.getValue() && event.getPacket() instanceof CPacketTabComplete)
                        ||
                        (packetUpdateSign.getValue() && event.getPacket() instanceof CPacketUpdateSign)
        )
            event.setCanceled(true);
        numPackets++;
    }

    public void onDisable() {
        numPackets = 0;
    }

    @Override
    public String getHudInfo() {
        return Integer.toString(numPackets);
    }

    private enum Page {DUPE, ONE, TWO}
}
