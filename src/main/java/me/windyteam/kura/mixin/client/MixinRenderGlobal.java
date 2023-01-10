package me.windyteam.kura.mixin.client;

import me.windyteam.kura.event.events.block.BlockBreakEvent;
import me.windyteam.kura.module.ModuleManager;
import me.windyteam.kura.module.ModuleManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Created by 086 on 11/04/2018.
 */
@Mixin(RenderGlobal.class)
public class MixinRenderGlobal {
    @Final
    @Shadow
    public
    Minecraft mc;

    @Inject(method = "drawBlockDamageTexture", at = @At("HEAD"), cancellable = true)
    public void drawBlockDamageTexture(Tessellator tessellatorIn, BufferBuilder bufferBuilderIn, Entity entityIn, float partialTicks, CallbackInfo callbackInfo) {
        if (ModuleManager.getModuleByName("BreakESP").isEnabled()) {
            callbackInfo.cancel();
        }
    }
    @Inject(method = "sendBlockBreakProgress", at = @At("HEAD"), cancellable = true)
    public void onSendingBlockBreakProgressPre(int p_180441_1_, BlockPos p_180441_2_, int p_180441_3_, CallbackInfo ci) {
        BlockBreakEvent event = new BlockBreakEvent(p_180441_1_, p_180441_2_, p_180441_3_);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            ci.cancel();
        }
    }
}
