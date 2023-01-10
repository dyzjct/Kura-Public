package me.windyteam.kura.mixin.client;

import me.windyteam.kura.event.events.entity.EventPlayerTravel;
import me.windyteam.kura.event.events.player.PlayerApplyCollisionEvent;
import me.windyteam.kura.event.events.entity.EventPlayerTravel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = {EntityPlayer.class})
public abstract class MixinPlayer extends MixinEntity {

    @Inject(method = "travel", at = @At("HEAD"), cancellable = true)
    public void travel(float strafe, float vertical, float forward, CallbackInfo info) {
        EventPlayerTravel event_packet = new EventPlayerTravel(strafe, vertical, forward);
        MinecraftForge.EVENT_BUS.post(event_packet);
        if (event_packet.isCanceled()) {
            move(MoverType.SELF, motionX, motionY, motionZ);
            info.cancel();
        }
    }

    @Inject(method = {"applyEntityCollision"}, at = {@At(value = "HEAD")}, cancellable = true)
    public void applyEntityCollision(Entity entity, CallbackInfo ci) {
        PlayerApplyCollisionEvent event = new PlayerApplyCollisionEvent(entity);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            ci.cancel();
        }
    }

    @Inject(method = {"isEntityInsideOpaqueBlock"}, at = {@At("HEAD")}, cancellable = true)
    private void isEntityInsideOpaqueBlockHook(final CallbackInfoReturnable<Boolean> info) {
        info.setReturnValue(false);
    }

}

