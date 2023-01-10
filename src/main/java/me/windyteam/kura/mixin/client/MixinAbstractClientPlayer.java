package me.windyteam.kura.mixin.client;

import net.minecraft.client.entity.AbstractClientPlayer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = {AbstractClientPlayer.class}, priority = 0x7FFFFFFE)
public abstract class MixinAbstractClientPlayer extends MixinPlayer {
}

