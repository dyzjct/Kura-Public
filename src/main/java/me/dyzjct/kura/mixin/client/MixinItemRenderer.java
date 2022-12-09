package me.dyzjct.kura.mixin.client;

import me.dyzjct.kura.event.events.render.RenderOverlayEvent;
import me.dyzjct.kura.event.events.render.TransformSideFirstPersonEvent;
import me.dyzjct.kura.event.events.render.item.RenderItemAnimationEvent;
import me.dyzjct.kura.event.events.render.item.RenderItemEvent;
import me.dyzjct.kura.module.ModuleManager;
import me.dyzjct.kura.module.modules.render.ViewModel;
import me.dyzjct.kura.utils.NTMiku.RenderUtil;
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
import net.minecraftforge.fml.common.eventhandler.Event;
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

    @Inject(method = { "transformSideFirstPerson" }, at = { @At("HEAD") }, cancellable = true)
    public void transformSideFirstPerson(final EnumHandSide hand, final float p_187459_2_, final CallbackInfo cancel) {
        final RenderItemEvent event = new RenderItemEvent(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0);
        MinecraftForge.EVENT_BUS.post((Event)event);
        if (ViewModel.getInstance().isEnabled()) {
            final boolean bob = ViewModel.getInstance().isDisabled() || (boolean)ViewModel.getInstance().doBob.getValue();
            final int i = (hand == EnumHandSide.RIGHT) ? 1 : -1;
            GlStateManager.translate(i * 0.56f, -0.52f + (bob ? p_187459_2_ : 0.0f) * -0.6f, -0.72f);
            if (hand == EnumHandSide.RIGHT) {
                GlStateManager.translate(event.getMainX(), event.getMainY(), event.getMainZ());
                RenderUtil.rotationHelper((float)event.getMainRotX(), (float)event.getMainRotY(), (float)event.getMainRotZ());
            }
            else {
                GlStateManager.translate(event.getOffX(), event.getOffY(), event.getOffZ());
                RenderUtil.rotationHelper((float)event.getOffRotX(), (float)event.getOffRotY(), (float)event.getOffRotZ());
            }
            cancel.cancel();
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

//    @Inject(method = "transformSideFirstPerson", at = @At("HEAD"))
//    public void transformSideFirstPerson(EnumHandSide hand, float p_187459_2_, CallbackInfo callbackInfo) {
//        TransformSideFirstPersonEvent event = new TransformSideFirstPersonEvent(hand);
//        MinecraftForge.EVENT_BUS.post(event);
//    }
//
//    @Inject(method = "transformEatFirstPerson", at = @At("HEAD"), cancellable = true)
//    public void transformEatFirstPerson(float p_187454_1_, EnumHandSide hand, ItemStack stack, CallbackInfo callbackInfo) {
//        TransformSideFirstPersonEvent event = new TransformSideFirstPersonEvent(hand);
//        MinecraftForge.EVENT_BUS.post(event);
//        if (ModuleManager.getModuleByName("ViewModel").isEnabled() && ((ViewModel) ModuleManager.getModuleByName("ViewModel")).cancelEating.getValue()) {
//            callbackInfo.cancel();
//        }
//    }

//    @Inject(method={"transformSideFirstPerson"}, at={@At(value="HEAD")}, cancellable=true)
//    public void transformSideFirstPerson(EnumHandSide hand, float p_187459_2_, CallbackInfo cancel) {
//        RenderItemEvent event = new RenderItemEvent(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
//        MinecraftForge.EVENT_BUS.post((Event)event);
//        if (ViewModel.getInstance().isEnabled()) {
//            boolean bob = ViewModel.getInstance().isDisabled() || ViewModel.getInstance().doBob.getValue() != false;
//            int i = hand == EnumHandSide.RIGHT ? 1 : -1;
//            GlStateManager.translate((float)((float)i * 0.56f), (float)(-0.52f + (bob ? p_187459_2_ : 0.0f) * -0.6f), (float)-0.72f);
//            if (hand == EnumHandSide.RIGHT) {
//                GlStateManager.translate((double)event.getMainX(), (double)event.getMainY(), (double)event.getMainZ());
//            } else {
//                GlStateManager.translate((double)event.getOffX(), (double)event.getOffY(), (double)event.getOffZ());
//            }
//            cancel.cancel();
//        }
//    }
    @Inject(method={"transformEatFirstPerson"}, at={@At(value="HEAD")}, cancellable=true)
    private void transformEatFirstPerson(float p_187454_1_, EnumHandSide hand, ItemStack stack, CallbackInfo cancel) {
        if (ViewModel.getInstance().isEnabled()) {
            if (!ViewModel.getInstance().noEatAnimation.getValue()) {
                float f3;
                float f = (float) Minecraft.getMinecraft().player.getItemInUseCount() - p_187454_1_ + 1.0f;
                float f2 = f / (float)stack.getMaxItemUseDuration();
                if (f2 < 0.8f) {
                    f3 = MathHelper.abs((float)(MathHelper.cos((float)(f / 4.0f * (float)Math.PI)) * 0.1f));
                    GlStateManager.translate((float)0.0f, (float)f3, (float)0.0f);
                }
                f3 = 1.0f - (float)Math.pow(f2, 27.0);
                int i = hand == EnumHandSide.RIGHT ? 1 : -1;
                GlStateManager.translate((double)((double)(f3 * 0.6f * (float)i) * ViewModel.getInstance().eatX.getValue()), (double)((double)(f3 * 0.5f) * -ViewModel.getInstance().eatY.getValue().doubleValue()), (double)0.0);
                GlStateManager.rotate((float)((float)i * f3 * 90.0f), (float)0.0f, (float)1.0f, (float)0.0f);
                GlStateManager.rotate((float)(f3 * 10.0f), (float)1.0f, (float)0.0f, (float)0.0f);
                GlStateManager.rotate((float)((float)i * f3 * 30.0f), (float)0.0f, (float)0.0f, (float)1.0f);
            }
            cancel.cancel();
        }
    }

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