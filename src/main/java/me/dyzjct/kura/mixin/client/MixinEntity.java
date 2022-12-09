package me.dyzjct.kura.mixin.client;

import me.dyzjct.kura.event.events.entity.EntityEvent;
import me.dyzjct.kura.event.events.entity.PushEvent;
import me.dyzjct.kura.event.events.render.InsideBlockRenderEvent;
import me.dyzjct.kura.module.modules.player.Scaffold;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(value = {Entity.class})
public abstract class MixinEntity {
    public Minecraft mc = Minecraft.getMinecraft();
    @Shadow
    public double posX;
    @Shadow
    public double posY;
    @Shadow
    public double posZ;
    @Shadow
    public float rotationPitch;
    @Shadow
    public float rotationYaw;
    @Shadow
    public Entity ridingEntity;
    @Shadow
    public double motionX;
    @Shadow
    public double motionY;
    @Shadow
    public double motionZ;
    @Shadow
    public boolean onGround;
    @Shadow
    public boolean isAirBorne;
    @Shadow
    public boolean noClip;
    @Shadow
    public boolean isInWeb;
    @Shadow
    public float stepHeight;
    @Shadow
    public float distanceWalkedModified;
    @Shadow
    public float distanceWalkedOnStepModified;
    @Shadow
    public float width;
    @Shadow
    public Random rand;
    @Shadow
    public int nextStepDistance;
    @Shadow
    public int fire;

    @Shadow
    public boolean isRiding() {
        return this.getRidingEntity() != null;
    }

    @Shadow
    public Entity getRidingEntity() {
        return this.ridingEntity;
    }

    @Shadow
    public abstract boolean isSprinting();

    @Shadow
    public abstract AxisAlignedBB getEntityBoundingBox();

    @Shadow
    public abstract void setEntityBoundingBox(AxisAlignedBB bb);

    @Shadow
    protected abstract boolean canTriggerWalking();

    @Shadow
    public abstract boolean isInWater();

    @Shadow
    protected abstract void dealFireDamage(int amount);

    @Shadow
    public abstract boolean isWet();

    @Shadow
    public abstract void addEntityCrashInfo(CrashReportCategory category);

    @Shadow
    protected abstract void doBlockCollisions();

    @Shadow
    protected abstract void playStepSound(BlockPos pos, Block blockIn);

    @Shadow
    public abstract boolean isSneaking();

    public int getNextStepDistance() {
        return nextStepDistance;
    }

    public void setNextStepDistance(int nextStepDistance) {
        this.nextStepDistance = nextStepDistance;
    }

    public int getFire() {
        return fire;
    }

    @Shadow
    public abstract void setFire(int seconds);

    @Redirect(method = "applyEntityCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;addVelocity(DDD)V"))
    public void addVelocity(Entity entity, double x, double y, double z) {
        EntityEvent entityCollisionEvent = new EntityEvent(entity, x, y, z);
        MinecraftForge.EVENT_BUS.post(entityCollisionEvent);
        if (entityCollisionEvent.isCanceled()) return;

        entity.motionX += x;
        entity.motionY += y;
        entity.motionZ += z;

        entity.isAirBorne = true;
    }

    @Shadow
    public void move(MoverType type, double x, double y, double z) {

    }

    public void jump() {
    }

    @Redirect(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isSneaking()Z"))
    public boolean isSneaking(Entity entity) {
        return Scaffold.INSTANCE.safeWalk.getValue() || entity.isSneaking();
    }

    @Inject(method = "isEntityInsideOpaqueBlock", at = @At("HEAD"), cancellable = true)
    private void onIsEntityInsideOpaqueBlock(CallbackInfoReturnable<Boolean> cir) {
        InsideBlockRenderEvent event = new InsideBlockRenderEvent();
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            cir.setReturnValue(false);
        }
    }

    @Redirect(method = {"applyEntityCollision"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;addVelocity(DDD)V"))
    public void addVelocityHook(final Entity entity, final double x, final double y, final double z) {
        final PushEvent event = new PushEvent(entity, x, y, z, true);
        MinecraftForge.EVENT_BUS.post(event);
        if (!event.isCanceled()) {
            entity.motionX += event.x;
            entity.motionY += event.y;
            entity.motionZ += event.z;
            entity.isAirBorne = event.airbone;
        }
    }

}


