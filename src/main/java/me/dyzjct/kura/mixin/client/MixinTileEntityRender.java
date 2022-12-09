package me.dyzjct.kura.mixin.client;

import me.dyzjct.kura.module.modules.render.NoRender;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TileEntityRendererDispatcher.class)
public class MixinTileEntityRender {

    @Inject(method = "render(Lnet/minecraft/tileentity/TileEntity;FI)V", at = @At("INVOKE"), cancellable = true)
    public void onRenderTileEntityPre(TileEntity entity, float partialTicks, int destroyStage, CallbackInfo ci) {
        if (NoRender.INSTANCE.isEnabled()) {
            if (NoRender.INSTANCE.tryReplaceEnchantingTable(entity) || NoRender.INSTANCE.tryReplaceEnderChest(entity)) {
                ci.cancel();
            }
        }
    }
}
