package me.windyteam.kura.mixin.client;

import org.spongepowered.asm.mixin.*;
import net.minecraft.client.renderer.*;
import org.spongepowered.asm.mixin.gen.*;

@Mixin({ EntityRenderer.class })
public interface IEntityRenderer
{
    @Invoker("setupCameraTransform")
    void invokeSetupCameraTransform(final float p0, final int p1);

    @Invoker("renderHand")
    void invokeRenderHand(final float p0, final int p1);
}
