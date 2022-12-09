package me.dyzjct.kura.mixin.client;

import me.dyzjct.kura.event.events.entity.*;
import me.dyzjct.kura.event.events.player.JumpEvent;
import me.dyzjct.kura.event.events.player.UpdateWalkingPlayerEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.MoverType;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Created by 086 on 12/12/2017.
 */
@Mixin(value = EntityPlayerSP.class, priority = 10000)
public abstract class MixinEntityPlayerSP extends MixinAbstractClientPlayer {
    @Shadow
    @Final
    public NetHandlerPlayClient connection;
    @Shadow
    public boolean serverSprintState;
    @Shadow
    public boolean serverSneakState;
    @Shadow
    public double lastReportedPosX;
    @Shadow
    public double lastReportedPosY;
    @Shadow
    public double lastReportedPosZ;
    @Shadow
    public float lastReportedYaw;
    @Shadow
    public float lastReportedPitch;
    @Shadow
    public int positionUpdateTicks;
    @Shadow
    public boolean autoJumpEnabled = true;
    @Shadow
    public boolean prevOnGround;
    @Shadow
    public boolean isCurrentViewEntity() {
        return false;
    }

    public MotionUpdateEvent.Tick motionEvent;
    public MotionUpdateEvent motionOldEvent;

    @Shadow
    public MovementInput movementInput;
    @Shadow
    public Minecraft mc;

    @Redirect(method = {"move"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/AbstractClientPlayer;move(Lnet/minecraft/entity/MoverType;DDD)V"))
    public void move(AbstractClientPlayer player, MoverType moverType, double x, double y, double z) {
        MoveEvent event = new MoveEvent(0, moverType, x, y, z);
        MinecraftForge.EVENT_BUS.post(event);
        if (!event.isCanceled()) {
            super.move(event.getType(), event.getX(), event.getY(), event.getZ());
        }
    }

    @Inject(method = {"pushOutOfBlocks"}, at = @At("HEAD"), cancellable = true)
    public void pushOutOfBlocksHook(double x, double y, double z, CallbackInfoReturnable<Boolean> info) {
        PushEvent event = new PushEvent(1);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            info.setReturnValue(false);
        }
    }

    @Inject(method = "onUpdateWalkingPlayer", at = @At(value = "HEAD"), cancellable = true)
    private void onUpdateWalkingPlayer_Head(CallbackInfo callbackInfo) {
        motionOldEvent = new MotionUpdateEvent(0, this.posX, this.getEntityBoundingBox().minY, this.posZ, this.rotationYaw, this.rotationPitch, this.onGround);
        MinecraftForge.EVENT_BUS.post(motionOldEvent);
        motionEvent = new MotionUpdateEvent.Tick(0, motionOldEvent);
        MinecraftForge.EVENT_BUS.post(motionEvent);
        if (motionEvent.isCanceled()) {
            callbackInfo.cancel();
        }
    }

    @Redirect(method = "onUpdateWalkingPlayer", at = @At(value = "FIELD", target = "net/minecraft/client/entity/EntityPlayerSP.posX:D"))
    private double posXHook(EntityPlayerSP entityPlayerSP) {
        return motionEvent.getX();
    }

    @Redirect(method = "onUpdateWalkingPlayer", at = @At(value = "FIELD", target = "net/minecraft/util/math/AxisAlignedBB.minY:D"))
    private double minYHook(AxisAlignedBB axisAlignedBB) {
        return motionEvent.getY();
    }

    @Redirect(method = "onUpdateWalkingPlayer", at = @At(value = "FIELD", target = "net/minecraft/client/entity/EntityPlayerSP.posZ:D"))
    private double posZHook(EntityPlayerSP entityPlayerSP) {
        return motionEvent.getZ();
    }

    @Redirect(method = "onUpdateWalkingPlayer", at = @At(value = "FIELD", target = "net/minecraft/client/entity/EntityPlayerSP.rotationYaw:F"))
    private float rotationYawHook(EntityPlayerSP entityPlayerSP) {
        return motionEvent.getYaw();
    }

    @Redirect(method = "onUpdateWalkingPlayer", at = @At(value = "FIELD", target = "net/minecraft/client/entity/EntityPlayerSP.rotationPitch:F"))
    private float rotationPitchHook(EntityPlayerSP entityPlayerSP) {
        return motionEvent.getPitch();
    }

    @Redirect(method = "onUpdateWalkingPlayer", at = @At(value = "FIELD", target = "net/minecraft/client/entity/EntityPlayerSP.onGround:Z"))
    private boolean onGroundHook(EntityPlayerSP entityPlayerSP) {
        return motionEvent.isOnGround();
    }

    @Inject(method = {"onUpdateWalkingPlayer"}, at = {@At(value = "RETURN")})
    private void postMotion(CallbackInfo info) {
        UpdateWalkingPlayerEvent event = new UpdateWalkingPlayerEvent(1);
        MinecraftForge.EVENT_BUS.post(event);
    }

    @Inject(method = "onUpdateWalkingPlayer", at = @At(value = "RETURN"))
    private void onUpdateWalkingPlayer_Return(CallbackInfo callbackInfo) {
        MotionUpdateEvent.Tick event = new MotionUpdateEvent.Tick(1, motionEvent);
        MotionUpdateEvent oldEvent = new MotionUpdateEvent(1, motionOldEvent);
        MinecraftForge.EVENT_BUS.post(event);
        MinecraftForge.EVENT_BUS.post(oldEvent);
        event.setCanceled(motionEvent.isCanceled());
    }

    @Inject(method = {"onUpdate"}, at = @At("HEAD"), cancellable = true)
    public void onUpdate(CallbackInfo callbackInfo) {
        EventPlayerUpdate eventPlayerUpdate = new EventPlayerUpdate();
        MinecraftForge.EVENT_BUS.post(eventPlayerUpdate);
        if (eventPlayerUpdate.isCanceled()) {
            callbackInfo.cancel();
        }
    }

    @Override
    public void jump() {
        JumpEvent event = new JumpEvent();
        MinecraftForge.EVENT_BUS.post(event);
        if (!event.isCanceled()) super.jump();
    }

    @Inject(method = "swingArm", at = @At("HEAD"), cancellable = true)
    public void Swing(CallbackInfo info) {
        EntitySwingArmEvent az = new EntitySwingArmEvent();
        MinecraftForge.EVENT_BUS.post(az);
        if (az.isCanceled()) {
            info.cancel();
        }
    }
}
