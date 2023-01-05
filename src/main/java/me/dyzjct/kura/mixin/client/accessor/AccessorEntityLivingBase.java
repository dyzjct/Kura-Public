package me.dyzjct.kura.mixin.client.accessor;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.datasync.DataParameter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EntityLivingBase.class)
public interface AccessorEntityLivingBase {
    @Accessor("HEALTH")
    static DataParameter<Float> melonGetHealthDataKey() {
        throw new AssertionError();
    }

    @Invoker("onItemUseFinish")
    void melonInvokeOnItemUseFinish();
}