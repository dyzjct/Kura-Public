package me.windyteam.kura.mixin.client;

import me.windyteam.kura.module.modules.movement.BoatFly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({EntityBoat.class})
public abstract class MixinEntityBoat {
    public Minecraft mc;

    @Inject(method = {"applyOrientationToEntity"}, at = {@At("HEAD")}, cancellable = true)
    public void applyOrientationToEntity(final Entity entity, final CallbackInfo ci) {
        if (BoatFly.INSTANCE.isEnabled()) {
            ci.cancel();
        }
    }

    @Inject(method = {"controlBoat"}, at = {@At("HEAD")}, cancellable = true)
    public void controlBoat(final CallbackInfo ci) {
        if (BoatFly.INSTANCE.isEnabled()) {
            ci.cancel();
        }
    }

}
