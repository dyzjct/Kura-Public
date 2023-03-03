package me.windyteam.kura.mixin.client;

import me.windyteam.kura.event.events.render.item.RenderItemEvent;
import me.windyteam.kura.module.modules.render.ViewModel;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ RenderItem.class })
public class MixinRenderItem
{
    @Shadow
    private void renderModel(final IBakedModel model, final int color, final ItemStack stack) {
    }

    @Inject(method = { "renderItemModel" }, at = { @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderItem;renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/renderer/block/model/IBakedModel;)V", shift = At.Shift.BEFORE) })
    private void renderItemModel(final ItemStack stack, final IBakedModel bakedModel, final ItemCameraTransforms.TransformType transform, final boolean leftHanded, final CallbackInfo ci) {
        final RenderItemEvent event = new RenderItemEvent(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,  0.0, 0.0, 0.0, 0.0);
//        final RenderItemEvent event = new RenderItemEvent(1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0);
        MinecraftForge.EVENT_BUS.post((Event)event);
        if (ViewModel.INSTANCE.isEnabled()) {
            if (!leftHanded) {
                GlStateManager.scale(event.getMainHandScaleX(), event.getMainHandScaleY(), event.getMainHandScaleZ());
            }
            else {
                GlStateManager.scale(event.getOffHandScaleX(), event.getOffHandScaleY(), event.getOffHandScaleZ());
            }
        }
    }
}
