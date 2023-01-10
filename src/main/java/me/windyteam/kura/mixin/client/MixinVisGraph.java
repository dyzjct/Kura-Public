package me.windyteam.kura.mixin.client;

import me.windyteam.kura.event.events.block.EventSetOpaqueCube;
import me.windyteam.kura.module.ModuleManager;
import me.windyteam.kura.module.ModuleManager;
import net.minecraft.client.renderer.chunk.VisGraph;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.EnumSet;
import java.util.Set;

/**
 * Created by 20kdc on 14/02/2020, but really 15/02/2020 because this is basically being recycled
 */
@Mixin(VisGraph.class)
public class MixinVisGraph {

    @Inject(method = "getVisibleFacings", at = @At("HEAD"), cancellable = true)
    public void getVisibleFacings(CallbackInfoReturnable<Set<EnumFacing>> callbackInfo) {
        // WebringOfTheDamned
        // This part prevents the "block-level culling". OptiFine does this for you but vanilla doesn't.
        // We have to implement this here or else OptiFine causes trouble.
        if (ModuleManager.getModuleByName("Freecam").isEnabled())
            callbackInfo.setReturnValue(EnumSet.allOf(EnumFacing.class));
    }

    @Inject(method = "setOpaqueCube", at = @At("HEAD"), cancellable = true)
    public void setOpaqueCube(BlockPos pos, CallbackInfo info) {
        EventSetOpaqueCube event = new EventSetOpaqueCube();
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            info.cancel();
        }
    }

}
