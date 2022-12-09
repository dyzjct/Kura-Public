package me.dyzjct.kura.mixin.client;

import net.minecraft.client.settings.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = {KeyBinding.class})
public class MixinKeyBinding {
    @Shadow
    public boolean pressed;

}

