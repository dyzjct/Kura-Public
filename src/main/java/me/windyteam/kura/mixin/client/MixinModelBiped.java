package me.windyteam.kura.mixin.client;

import me.windyteam.kura.event.events.entity.MotionUpdateEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelBiped.class)
@SideOnly(Side.CLIENT)
public class MixinModelBiped {

    @Shadow
    public ModelRenderer bipedRightArm;

    public int heldItemRight;

    @Shadow
    public ModelRenderer bipedHead;

    @Inject(method = "setRotationAngles", at = @At(value = "FIELD", target = "Lnet/minecraft/client/model/ModelBiped;swingProgress:F"))
    private void revertSwordAnimation(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn, CallbackInfo callbackInfo) {
        MotionUpdateEvent.Tick event = MotionUpdateEvent.Tick.INSTANCETick;
        if (heldItemRight == 3) {
            this.bipedRightArm.rotateAngleY = 0F;
        }
        if (entityIn instanceof EntityPlayer && entityIn.equals(Minecraft.getMinecraft().player) && event != null) {
            bipedHead.rotateAngleX = event.getPitch() / (180F / (float) Math.PI);
        }
    }
}