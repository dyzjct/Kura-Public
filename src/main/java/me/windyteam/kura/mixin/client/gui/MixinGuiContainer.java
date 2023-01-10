package me.windyteam.kura.mixin.client.gui;

import me.windyteam.kura.module.ModuleManager;
import me.windyteam.kura.module.modules.render.Animations;
import me.windyteam.kura.utils.TimerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiContainer.class)
public abstract class MixinGuiContainer extends MixinGuiScreen {
    @Shadow
    protected int xSize;
    @Shadow
    protected int ySize;
    @Shadow
    protected int guiLeft;
    @Shadow
    protected int guiTop;
    public TimerUtils updateDelay = new TimerUtils();

    private long guiOpenTime = -1;

    private boolean translated = false;

    @Inject(method = "initGui", at = @At("RETURN"))
    private void initGuiReturn(CallbackInfo callbackInfo) {
        guiOpenTime = System.currentTimeMillis();
    }

    @Inject(method = "drawScreen", at = @At("HEAD"))
    private void drawScreenHead(CallbackInfo callbackInfo) {
        updateDelay.reset();
        Minecraft mc = Minecraft.getMinecraft();
        mc.currentScreen.drawWorldBackground(0);

        if (ModuleManager.getModuleByClass(Animations.class).isEnabled() && Animations.INSTANCE.inv.getValue()) {
            double pct = Math.max(Animations.INSTANCE.invTime.getValue() - (System.currentTimeMillis() - guiOpenTime), 0) / ((double) Animations.INSTANCE.invTime.getValue());
            if (pct != 0) {
                GL11.glPushMatrix();

                double scale = 1 - pct;
                GL11.glScaled(scale, scale, scale);
                if (updateDelay.passed(Animations.INSTANCE.invTime.getValue() / 2f)) {
                    GL11.glTranslated(((guiLeft + (xSize * 0.5 * pct)) / scale) - guiLeft, ((guiTop + (ySize * 0.5d * pct)) / scale) - guiTop, 0);
                } else {
                    GL11.glTranslated(0, ((guiTop + (ySize * 0.5d * pct)) / scale) - guiTop, ((guiLeft - (xSize * 0.5 * pct)) / scale) + guiLeft);
                }
                translated = true;
            }
        }
    }

    @Inject(method = "drawScreen", at = @At("RETURN"))
    private void drawScreenReturn(CallbackInfo callbackInfo) {
        if (translated) {
            GL11.glPopMatrix();
            translated = false;
        }
    }
}
