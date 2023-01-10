package me.windyteam.kura.mixin.client;

import net.minecraft.client.gui.FontRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Hoosiers 12/07/2020
 */

@Mixin(FontRenderer.class)
public class MixinFontRenderer {

    @Redirect(method = "drawStringWithShadow", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;drawString(Ljava/lang/String;FFIZ)I"))
    public int drawCustomFontStringWithShadow(FontRenderer fontRenderer, String text, float x, float y, int color, boolean dropShadow) {
        return fontRenderer.drawString(text, x, y, color, true);
    }
}