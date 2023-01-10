package me.windyteam.kura.mixin.client;

import me.windyteam.kura.event.events.render.RenderLeftArmEvent;
import me.windyteam.kura.event.events.render.RenderRightArmEvent;
import me.windyteam.kura.module.ModuleManager;
import me.windyteam.kura.module.modules.render.HandColor;
import me.windyteam.kura.utils.render.RenderUtil;
import me.windyteam.kura.module.ModuleManager;
import me.windyteam.kura.module.modules.render.HandColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

/**
 * Created by 086 on 19/12/2017.
 */
@Mixin(RenderPlayer.class)
public class MixinRenderPlayer {
    @Inject(method = "renderEntityName", at = @At("HEAD"), cancellable = true)
    public void renderLivingLabel(AbstractClientPlayer entityIn, double x, double y, double z, String name, double distanceSq, CallbackInfo info) {
        if (ModuleManager.getModuleByName("Nametags").isEnabled()) {
            info.cancel();
        }
    }

    @Inject(method = {"renderRightArm"}, at = {@At(value = "FIELD", target = "Lnet/minecraft/client/model/ModelPlayer;swingProgress:F", opcode = 181)}, cancellable = true)
    public void renderRightArmBegin(final AbstractClientPlayer clientPlayer, final CallbackInfo ci) {
        if (clientPlayer.equals(Minecraft.getMinecraft().player)) {
            RenderRightArmEvent event = new RenderRightArmEvent();
            MinecraftForge.EVENT_BUS.post(event);
        }
        if (clientPlayer == Minecraft.getMinecraft().player && HandColor.INSTANCE.isEnabled()) {
            GL11.glPushAttrib(1048575);
            GL11.glDisable(3008);
            GL11.glDisable(3553);
            GL11.glDisable(2896);
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            GL11.glLineWidth(1.5f);
            GL11.glEnable(2960);
            GL11.glEnable(10754);
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0f, 240.0f);
            if (HandColor.INSTANCE.rainbow.getValue()) {
                final Color rainbowColor = HandColor.INSTANCE.colorSync.getValue() ? HandColor.INSTANCE.getCurrentColor() : new Color(RenderUtil.getRainbow(HandColor.INSTANCE.speed.getValue() * 100, 0, HandColor.INSTANCE.saturation.getValue() / 100.0f, HandColor.INSTANCE.brightness.getValue() / 100.0f));
                GL11.glColor4f(rainbowColor.getRed() / 255.0f, rainbowColor.getGreen() / 255.0f, rainbowColor.getBlue() / 255.0f, HandColor.INSTANCE.alpha.getValue() / 255.0f);
            } else {
                final Color color = HandColor.INSTANCE.colorSync.getValue() ? new Color(HandColor.INSTANCE.getCurrentColor().getRed(), HandColor.INSTANCE.getCurrentColor().getBlue(), HandColor.INSTANCE.getCurrentColor().getGreen(), HandColor.INSTANCE.alpha.getValue()) : new Color(HandColor.INSTANCE.red.getValue(), HandColor.INSTANCE.green.getValue(), HandColor.INSTANCE.blue.getValue(), HandColor.INSTANCE.alpha.getValue());
                GL11.glColor4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
            }
        }
    }

    @Inject(method = {"renderRightArm"}, at = {@At("RETURN")}, cancellable = true)
    public void renderRightArmReturn(final AbstractClientPlayer clientPlayer, final CallbackInfo ci) {
        if (clientPlayer.equals(Minecraft.getMinecraft().player)) {
            RenderRightArmEvent event = new RenderRightArmEvent();
            MinecraftForge.EVENT_BUS.post(event);
        }
        if (clientPlayer == Minecraft.getMinecraft().player && HandColor.INSTANCE.isEnabled()) {
            GL11.glEnable(3042);
            GL11.glEnable(2896);
            GL11.glEnable(3553);
            GL11.glEnable(3008);
            GL11.glPopAttrib();
        }
    }

    @Inject(method = {"renderLeftArm"}, at = {@At(value = "FIELD", target = "Lnet/minecraft/client/model/ModelPlayer;swingProgress:F", opcode = 181)}, cancellable = true)
    public void renderLeftArmBegin(final AbstractClientPlayer clientPlayer, final CallbackInfo ci) {
        if (clientPlayer.equals(Minecraft.getMinecraft().player)) {
            RenderLeftArmEvent event = new RenderLeftArmEvent();
            MinecraftForge.EVENT_BUS.post(event);
        }
        if (clientPlayer == Minecraft.getMinecraft().player && HandColor.INSTANCE.isEnabled()) {
            GL11.glPushAttrib(1048575);
            GL11.glDisable(3008);
            GL11.glDisable(3553);
            GL11.glDisable(2896);
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            GL11.glLineWidth(1.5f);
            GL11.glEnable(2960);
            GL11.glEnable(10754);
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0f, 240.0f);
            final Color rainbowColor = HandColor.INSTANCE.colorSync.getValue() ? HandColor.INSTANCE.getCurrentColor() : new Color(RenderUtil.getRainbow(HandColor.INSTANCE.speed.getValue() * 100, 0, HandColor.INSTANCE.saturation.getValue() / 100.0f, HandColor.INSTANCE.brightness.getValue() / 100.0f));
            GL11.glColor4f(rainbowColor.getRed() / 255.0f, rainbowColor.getGreen() / 255.0f, rainbowColor.getBlue() / 255.0f, HandColor.INSTANCE.alpha.getValue() / 255.0f);
        }
    }

    @Inject(method = {"renderLeftArm"}, at = {@At("RETURN")}, cancellable = true)
    public void renderLeftArmReturn(final AbstractClientPlayer clientPlayer, final CallbackInfo ci) {
        if (clientPlayer.equals(Minecraft.getMinecraft().player)) {
            RenderLeftArmEvent event = new RenderLeftArmEvent();
            MinecraftForge.EVENT_BUS.post(event);
        }
        if (clientPlayer == Minecraft.getMinecraft().player && HandColor.INSTANCE.isEnabled()) {
            GL11.glEnable(3042);
            GL11.glEnable(2896);
            GL11.glEnable(3553);
            GL11.glEnable(3008);
            GL11.glPopAttrib();
        }
    }

}
