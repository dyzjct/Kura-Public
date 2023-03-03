package me.windyteam.kura.mixin.client;

import me.windyteam.kura.event.events.render.RenderOverlayEvent;
import me.windyteam.kura.event.events.render.item.RenderItemAnimationEvent;
import me.windyteam.kura.event.events.render.item.RenderItemEvent;
import me.windyteam.kura.module.modules.render.ViewModel;
import me.windyteam.kura.utils.render.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public class MixinItemRenderer {


    @Inject(method = {"renderWaterOverlayTexture"}, at = {@At(value = "HEAD")}, cancellable = true)
    public void preRenderWaterOverlayTexture(float partialTicks, CallbackInfo ci) {
        RenderOverlayEvent event = new RenderOverlayEvent(RenderOverlayEvent.OverlayType.LIQUID);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            ci.cancel();
        }
    }

    @Inject(method = {"renderFireInFirstPerson"}, at = {@At(value = "HEAD")}, cancellable = true)
    public void preRenderFireInFirstPerson(CallbackInfo ci) {
        RenderOverlayEvent event = new RenderOverlayEvent(RenderOverlayEvent.OverlayType.FIRE);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            ci.cancel();
        }
    }

    @Inject(method = {"renderSuffocationOverlay"}, at = {@At(value = "HEAD")}, cancellable = true)
    private void onRenderSuffocationOverlay(TextureAtlasSprite sprite, CallbackInfo ci) {
        RenderOverlayEvent event = new RenderOverlayEvent(RenderOverlayEvent.OverlayType.BLOCK);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            ci.cancel();
        }
    }

    @Inject(method = {"transformSideFirstPerson"}, at = {@At("HEAD")}, cancellable = true)
    public void transformSideFirstPerson(final EnumHandSide hand, final float p_187459_2_, final CallbackInfo cancel) {
        final RenderItemEvent event = new RenderItemEvent(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0);
        MinecraftForge.EVENT_BUS.post(event);
        if (ViewModel.INSTANCE.isEnabled()) {
            final boolean bob = ViewModel.INSTANCE.isDisabled() || ViewModel.doBob.getValue();
            final int i = (hand == EnumHandSide.RIGHT) ? 1 : -1;
            GlStateManager.translate(i * 0.56f, -0.52f + (bob ? p_187459_2_ : 0.0f) * -0.6f, -0.72f);
            if (hand == EnumHandSide.RIGHT) {
                GlStateManager.translate(event.getMainX(), event.getMainY(), event.getMainZ());
                RenderUtil.rotationHelper((float) event.getMainRotX(), (float) event.getMainRotY(), (float) event.getMainRotZ());
            } else {
                GlStateManager.translate(event.getOffX(), event.getOffY(), event.getOffZ());
                RenderUtil.rotationHelper((float) event.getOffRotX(), (float) event.getOffRotY(), (float) event.getOffRotZ());
            }
            cancel.cancel();
        }
    }

    @Inject(method = {"transformEatFirstPerson"}, at = {@At(value = "HEAD")}, cancellable = true)
    private void transformEatFirstPerson(float p_187454_1_, EnumHandSide hand, ItemStack stack, CallbackInfo cancel) {
        if (ViewModel.INSTANCE.isEnabled()) {
            if (!ViewModel.noEatAnimation.getValue()) {
                float f3;
                float f = (float) Minecraft.getMinecraft().player.getItemInUseCount() - p_187454_1_ + 1.0f;
                float f2 = f / (float) stack.getMaxItemUseDuration();
                if (f2 < 0.8f) {
                    f3 = MathHelper.abs(MathHelper.cos(f / 4.0f * (float) Math.PI) * 0.1f);
                    GlStateManager.translate(0.0f, f3, 0.0f);
                }
                f3 = 1.0f - (float) Math.pow(f2, 27.0);
                int i = hand == EnumHandSide.RIGHT ? 1 : -1;
                GlStateManager.translate((double) (f3 * 0.6f * (float) i) * ViewModel.eatX.getValue(), (double) (f3 * 0.5f) * -ViewModel.eatY.getValue(), 0.0);
                GlStateManager.rotate((float) i * f3 * 90.0f, 0.0f, 1.0f, 0.0f);
                GlStateManager.rotate(f3 * 10.0f, 1.0f, 0.0f, 0.0f);
                GlStateManager.rotate((float) i * f3 * 30.0f, 0.0f, 0.0f, 1.0f);
            }
            cancel.cancel();
        }
    }

//    @Inject(method = {"transformEatFirstPerson"}, at = {@At(value = "HEAD")}, cancellable = true)
//    private void transformEatFirstPerson(float p_187454_1_, EnumHandSide hand, ItemStack stack, CallbackInfo cancel) {
//        if (ViewModel.INSTANCE.isEnabled()) {
//            if (!ViewModel.noEatAnimation.getValue()) {
//                int i = hand == EnumHandSide.RIGHT ? 1 : -1;
//                GlStateManager.translate(ViewModel.eatX.getValue(), ViewModel.eatY.getValue(), 0.0);
//            }
//            cancel.cancel();
//        }
//    }

    @Inject(method = "renderItemInFirstPerson(Lnet/minecraft/client/entity/AbstractClientPlayer;FFLnet/minecraft/util/EnumHand;FLnet/minecraft/item/ItemStack;F)V", at = @At("HEAD"), cancellable = true)
    private void onRenderItemAnimationPre(AbstractClientPlayer player, float p_187457_2_, float p_187457_3_, EnumHand hand, float p_187457_5_, ItemStack stack, float p_187457_7_, CallbackInfo ci) {
        RenderItemAnimationEvent.Render uwu = new RenderItemAnimationEvent.Render(stack, hand);
        if (uwu.isCanceled()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderItemInFirstPerson(Lnet/minecraft/client/entity/AbstractClientPlayer;FFLnet/minecraft/util/EnumHand;FLnet/minecraft/item/ItemStack;F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ItemRenderer;renderItemSide(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/renderer/block/model/ItemCameraTransforms$TransformType;Z)V"))
    private void onRenderItemTransformAnimationPre(AbstractClientPlayer player, float p_187457_2_, float p_187457_3_, EnumHand hand, float p_187457_5_, ItemStack stack, float p_187457_7_, CallbackInfo info) {
        MinecraftForge.EVENT_BUS.post(new RenderItemAnimationEvent.Transform(stack, hand, p_187457_5_));
    }
}