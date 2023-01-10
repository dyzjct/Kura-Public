package me.windyteam.kura.mixin.client;

import net.minecraft.util.MovementInput;
import net.minecraft.util.MovementInputFromOptions;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = MovementInputFromOptions.class, priority = Integer.MAX_VALUE)
public abstract class MixinMovementInputFromOptions extends MovementInput {

}
