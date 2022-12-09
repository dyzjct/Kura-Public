package me.dyzjct.kura.mixin.client;

import me.dyzjct.kura.event.events.render.RenderEnchantmentTableEvent;
import me.dyzjct.kura.module.modules.render.NoRender;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntityEnchantmentTableRenderer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntityEnchantmentTable;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={TileEntityEnchantmentTableRenderer.class})
public class MixinTileEntityEnchantmentTableRenderer {
    @Inject(method={"render"}, at={@At(value="INVOKE")}, cancellable=true)
    private void renderEnchantingTableBook(TileEntityEnchantmentTable te, double x, double y, double z, float partialTicks, int destroyStage, float alpha, CallbackInfo info) {
        RenderEnchantmentTableEvent event = new RenderEnchantmentTableEvent();
        IBlockState blockState = Blocks.SNOW_LAYER.defaultBlockState.withProperty(BlockSnow.LAYERS, 7);
        MinecraftForge.EVENT_BUS.post(event);
        if(event.isCanceled()){
            Minecraft.getMinecraft().world.setBlockState(te.getPos(), blockState);
            Minecraft.getMinecraft().world.markTileEntityForRemoval(te);
        }
    }

    @Inject(method = "render", at = @At("INVOKE"), cancellable = true)
    public void onRenderTileEntityPre(TileEntityEnchantmentTable te, double x, double y, double z, float partialTicks, int destroyStage, float alpha, CallbackInfo ci) {
        if (NoRender.INSTANCE.isEnabled()) {
            if (NoRender.INSTANCE.tryReplaceEnchantingTable(te)) {
                ci.cancel();
            }
        }
    }
}
