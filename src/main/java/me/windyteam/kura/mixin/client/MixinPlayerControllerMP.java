package me.windyteam.kura.mixin.client;

import me.windyteam.kura.event.events.block.BlockEvent;
import me.windyteam.kura.module.ModuleManager;
import me.windyteam.kura.event.events.PlayerDamageBlockEvent;
import me.windyteam.kura.module.modules.player.Reach;
import me.windyteam.kura.module.ModuleManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Created by 086 on 3/10/2018.
 */
@Mixin(PlayerControllerMP.class)
public abstract class MixinPlayerControllerMP {

    @Shadow
    public int currentPlayerItem;

    @Shadow
    public abstract void syncCurrentPlayItem();

    @Inject(method = { "onPlayerDamageBlock" }, at = { @At("HEAD") }, cancellable = true)
    private void onPlayerDamageBlockHooktwo(final BlockPos pos, final EnumFacing face, final CallbackInfoReturnable<Boolean> ci) {
        final PlayerDamageBlockEvent event = new PlayerDamageBlockEvent(0, pos, face);
        MinecraftForge.EVENT_BUS.post((Event)event);
    }

    @Inject(method = {"extendedReach"}, at = {@At("RETURN")}, cancellable = true)
    private void reachHook(final CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        if (Minecraft.getMinecraft().player == null || Minecraft.getMinecraft().world == null) {
            return;
        }
        if (ModuleManager.getModuleByName("Reach").isEnabled()) {
            callbackInfoReturnable.setReturnValue(true);
            callbackInfoReturnable.cancel();
        }
    }

    @Inject(method = {"getBlockReachDistance"}, at = {@At("RETURN")}, cancellable = true)
    private void getReachDistanceHook(final CallbackInfoReturnable<Float> callbackInfoReturnable) {
        if (Minecraft.getMinecraft().player == null || Minecraft.getMinecraft().world == null) {
            return;
        }
        if (ModuleManager.getModuleByName("Reach").isEnabled()) {
            callbackInfoReturnable.setReturnValue(Reach.getReach());
            callbackInfoReturnable.cancel();
        }
    }

    @Inject(method = "onStoppedUsingItem", at = @At("HEAD"), cancellable = true)
    public void onStoppedUsingItem(EntityPlayer playerIn, CallbackInfo ci) {
        if (playerIn.getHeldItem(playerIn.getActiveHand()).getItem() instanceof ItemFood) {
            if (ModuleManager.getModuleByName("PacketEat").isEnabled()) {
                this.syncCurrentPlayItem();
                playerIn.stopActiveHand();
                ci.cancel();
            }
        }
    }

    @Inject(method = {"clickBlock"}, at = @At("HEAD"), cancellable = true)
    private void clickBlockHook2(final BlockPos pos, final EnumFacing face, final CallbackInfoReturnable<Boolean> info) {
        final BlockEvent event = new BlockEvent(3, pos, face);
        MinecraftForge.EVENT_BUS.post(event);
    }

    @Inject(method = {"onPlayerDamageBlock"}, at = @At("HEAD"), cancellable = true)
    private void onPlayerDamageBlockHook(final BlockPos pos, final EnumFacing face, final CallbackInfoReturnable<Boolean> info) {
        final BlockEvent event = new BlockEvent(4, pos, face);
        MinecraftForge.EVENT_BUS.post(event);
    }

}
