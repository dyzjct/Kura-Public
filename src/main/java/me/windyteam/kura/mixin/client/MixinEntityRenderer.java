package me.windyteam.kura.mixin.client;

import me.windyteam.kura.event.events.render.PerspectiveEvent;
import me.windyteam.kura.event.events.render.RenderLiquidVisionEvent;
import me.windyteam.kura.event.events.render.RenderTotemPopEvent;
import me.windyteam.kura.module.ModuleManager;
import me.windyteam.kura.module.modules.render.CameraClip;
import me.windyteam.kura.module.modules.render.NoHurtCam;
import me.windyteam.kura.module.modules.render.NoRender;
import me.windyteam.kura.module.ModuleManager;
import me.windyteam.kura.module.modules.render.CameraClip;
import me.windyteam.kura.module.modules.render.NoHurtCam;
import me.windyteam.kura.module.modules.render.NoRender;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.util.glu.Project;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = {EntityRenderer.class}, priority = 8888)
public abstract class MixinEntityRenderer {
    @Shadow
    @Final
    public Minecraft mc = Minecraft.getMinecraft();

    @Redirect(method = {"setupFog"}, at = @At(value = "INVOKE", target = "net/minecraft/client/renderer/ActiveRenderInfo.getBlockStateAtEntityViewpoint(Lnet/minecraft/world/World;Lnet/minecraft/entity/Entity;F)Lnet/minecraft/block/state/IBlockState;"))
    public IBlockState onSettingUpFogWhileInLiquid(World worldIn, Entity entityIn, float p_186703_2_) {
        IBlockState iBlockState = ActiveRenderInfo.getBlockStateAtEntityViewpoint(this.mc.world, entityIn, p_186703_2_);
        RenderLiquidVisionEvent event = new RenderLiquidVisionEvent();
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled() && (iBlockState.getMaterial() == Material.LAVA || iBlockState.getMaterial() == Material.WATER)) {
            return Blocks.AIR.getDefaultState();
        }
        return iBlockState;
    }

    @Inject(method = "displayItemActivation", at = @At(value = "HEAD"), cancellable = true)
    public void onDisplayItemActivationPre(ItemStack stack, CallbackInfo ci) {
        RenderTotemPopEvent event = new RenderTotemPopEvent();
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) ci.cancel();
    }

    @Redirect(method = { "setupCameraTransform" },  at = @At(value = "INVOKE",  target = "Lorg/lwjgl/util/glu/Project;gluPerspective(FFFF)V"))
    private void onSetupCameraTransform(final float f,  final float f2,  final float f3,  final float f4) {
        final PerspectiveEvent perspectiveEvent = new PerspectiveEvent(this.mc.displayWidth / (float)this.mc.displayHeight);
        MinecraftForge.EVENT_BUS.post(perspectiveEvent);
        Project.gluPerspective(f,  perspectiveEvent.getAspect(),  f3,  f4);
    }

    @Redirect(method = { "renderWorldPass" },  at = @At(value = "INVOKE",  target = "Lorg/lwjgl/util/glu/Project;gluPerspective(FFFF)V"))
    private void onRenderWorldPass(final float f,  final float f2,  final float f3,  final float f4) {
        final PerspectiveEvent perspectiveEvent = new PerspectiveEvent(this.mc.displayWidth / (float)this.mc.displayHeight);
        MinecraftForge.EVENT_BUS.post(perspectiveEvent);
        Project.gluPerspective(f,  perspectiveEvent.getAspect(),  f3,  f4);
    }

    @Redirect(method = { "renderCloudsCheck" },  at = @At(value = "INVOKE",  target = "Lorg/lwjgl/util/glu/Project;gluPerspective(FFFF)V"))
    private void onRenderCloudsCheck(final float f,  final float f2,  final float f3,  final float f4) {
        final PerspectiveEvent perspectiveEvent = new PerspectiveEvent(this.mc.displayWidth / (float)this.mc.displayHeight);
        MinecraftForge.EVENT_BUS.post(perspectiveEvent);
        Project.gluPerspective(f,  perspectiveEvent.getAspect(),  f3,  f4);
    }

    @Inject(method = "renderItemActivation", at = @At(value = "HEAD"), cancellable = true)
    public void onRenderItemActivationPre(int p_190563_1_, int p_190563_2_, float p_190563_3_, CallbackInfo ci) {
        RenderTotemPopEvent event = new RenderTotemPopEvent();
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) ci.cancel();
    }

    @Inject(method = {"updateLightmap"}, at = {@At("HEAD")}, cancellable = true)
    private void updateLightmap(float partialTicks, CallbackInfo info) {
        if (NoRender.getInstance().isEnabled() && ((NoRender.getInstance()).skylight.getValue() == NoRender.Skylight.ENTITY || (NoRender.getInstance()).skylight.getValue() == NoRender.Skylight.ALL))
            info.cancel();
    }

    @Inject(method = "hurtCameraEffect", at = @At("HEAD"), cancellable = true)
    public void hurtCameraEffect(float ticks, CallbackInfo info) {
        if (NoHurtCam.shouldDisable()) info.cancel();
    }

    @Redirect(method = "getMouseOver", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/WorldClient;getEntitiesInAABBexcluding(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/AxisAlignedBB;Lcom/google/common/base/Predicate;)Ljava/util/List;"))
    public List<Entity> getEntitiesInAABBexcluding(WorldClient worldClient, Entity entityIn, AxisAlignedBB boundingBox, com.google.common.base.Predicate<? super Entity> predicate) {
        if (ModuleManager.getModuleByName("NoEntityTrace").isEnabled()) {
            return new ArrayList<>();
        } else {
            return worldClient.getEntitiesInAABBexcluding(entityIn, boundingBox, predicate::test);
        }
    }

    @ModifyVariable(method = {"orientCamera"}, ordinal = 3, at = @At(value = "STORE", ordinal = 0), require = 1)
    public double changeCameraDistanceHook(final double range) {
        return (CameraClip.getInstance().isEnabled() && CameraClip.getInstance().extend.getValue()) ? CameraClip.getInstance().distance.getValue() : range;
    }

    @ModifyVariable(method = {"orientCamera"}, ordinal = 7, at = @At(value = "STORE", ordinal = 0), require = 1)
    public double orientCameraHook(final double range) {
        return (CameraClip.getInstance().isEnabled() && CameraClip.getInstance().extend.getValue()) ? CameraClip.getInstance().distance.getValue() : ((CameraClip.getInstance().isEnabled() && !CameraClip.getInstance().extend.getValue()) ? 4.0 : range);
    }

}

