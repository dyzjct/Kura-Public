package me.windyteam.kura.module.modules.movement;

import me.windyteam.kura.event.events.client.PacketEvents;
import me.windyteam.kura.event.events.gui.KeyEvent;
import me.windyteam.kura.module.Category;
import me.windyteam.kura.module.Module;
import me.windyteam.kura.module.Module.Info;
import me.windyteam.kura.setting.Setting;
import me.windyteam.kura.event.events.gui.KeyEvent;
import me.windyteam.kura.setting.Setting;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

@Info(name = "NoSlowDown", category = Category.MOVEMENT, description = "Prevents being slowed down when using an item or going through cobwebs")
public class NoSlowDown extends Module {
    public static KeyBinding[] keys = new KeyBinding[] { mc.gameSettings.keyBindForward, mc.gameSettings.keyBindBack, mc.gameSettings.keyBindLeft, mc.gameSettings.keyBindRight, mc.gameSettings.keyBindJump, mc.gameSettings.keyBindSprint };

    public static NoSlowDown INSTANCE = new NoSlowDown();

    public Setting<Boolean> guiMove = (Setting<Boolean>)bsetting("GuiMove", true);

    public Setting<Boolean> soulSand = (Setting<Boolean>)bsetting("SoulSand", true);

    public Setting<Boolean> strict = (Setting<Boolean>)bsetting("Strict", true);

    public Setting<Boolean> superStrict = (Setting<Boolean>)bsetting("SuperStrict", false);

    public Setting<Boolean> sneakPacket = (Setting<Boolean>)bsetting("SneakPacket", false);

    public boolean sneaking = false;

    public void onUpdate() {
        if (fullNullCheck())
            return;
        if (((Boolean)this.guiMove.getValue()).booleanValue())
            if (mc.currentScreen instanceof net.minecraft.client.gui.GuiOptions || mc.currentScreen instanceof net.minecraft.client.gui.GuiVideoSettings || mc.currentScreen instanceof net.minecraft.client.gui.GuiScreenOptionsSounds || mc.currentScreen instanceof net.minecraft.client.gui.inventory.GuiContainer || mc.currentScreen instanceof net.minecraft.client.gui.GuiIngameMenu) {
                for (KeyBinding bind : keys)
                    KeyBinding.setKeyBindState(bind.getKeyCode(), Keyboard.isKeyDown(bind.getKeyCode()));
            } else if (mc.currentScreen == null) {
                for (KeyBinding bind : keys) {
                    if (!Keyboard.isKeyDown(bind.getKeyCode()))
                        KeyBinding.setKeyBindState(bind.getKeyCode(), false);
                }
            }
        if (this.sneaking && !mc.player.isHandActive() && ((Boolean)this.sneakPacket.getValue()).booleanValue()) {
            mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            this.sneaking = false;
        }
    }

    @SubscribeEvent
    public void onWorldEvent(EntityJoinWorldEvent event) {
        if (((Boolean)this.sneakPacket.getValue()).booleanValue() && this.sneaking && !mc.player.isHandActive()) {
            mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            this.sneaking = false;
        }
    }

    @SubscribeEvent
    public void onUseItem(PlayerInteractEvent.RightClickItem event) {
        Item item = mc.player.getHeldItem(event.getHand()).getItem();
        if (((Boolean)this.sneakPacket.getValue()).booleanValue() && !this.sneaking && (
                item instanceof net.minecraft.item.ItemFood || item instanceof net.minecraft.item.ItemBow || item instanceof net.minecraft.item.ItemPotion)) {
            mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)mc.player, CPacketEntityAction.Action.START_SNEAKING));
            this.sneaking = true;
        }
    }

    @SubscribeEvent
    public void onInput(InputUpdateEvent event) {
        if (fullNullCheck())
            return;
        if (mc.player.isHandActive() && !mc.player.isRiding()) {
            (event.getMovementInput()).moveStrafe *= 5.0F;
            (event.getMovementInput()).moveForward *= 5.0F;
        }
    }

    @SubscribeEvent
    public void onKeyEvent(KeyEvent event) {
        if (fullNullCheck())
            return;
        if (((Boolean)this.guiMove.getValue()).booleanValue() && event.getStage() == 0 && !(mc.currentScreen instanceof net.minecraft.client.gui.GuiChat))
            event.info = event.pressed;
    }

    @SubscribeEvent
    public void onPacket(PacketEvents.Send event) {
        if (fullNullCheck())
            return;
        if (event.getPacket() instanceof net.minecraft.network.play.client.CPacketPlayer && ((Boolean)this.strict.getValue()).booleanValue() && mc.player.isHandActive() && !mc.player.isRiding())
            mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ)), EnumFacing.DOWN));
        if (event.getStage() == 1 && (
                event.getPacket() instanceof net.minecraft.network.play.client.CPacketPlayerTryUseItem || event.getPacket() instanceof net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock)) {
            Item item = mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem();
            if (((Boolean)this.superStrict.getValue()).booleanValue() && (item instanceof net.minecraft.item.ItemFood || item instanceof net.minecraft.item.ItemBow || item instanceof net.minecraft.item.ItemPotion))
                mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(mc.player.inventory.currentItem));
        }
    }
}
