package me.dyzjct.kura.mixin.client;

import net.minecraft.client.renderer.BlockRendererDispatcher;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = BlockRendererDispatcher.class)
public abstract class MixinBlockRendererDispatcher {

}