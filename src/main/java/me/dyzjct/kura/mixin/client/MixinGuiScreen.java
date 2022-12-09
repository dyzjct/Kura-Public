package me.dyzjct.kura.mixin.client;

import me.dyzjct.kura.event.events.render.EventRenderTooltip;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Created by 086 on 24/12/2017.
 */
@Mixin(GuiScreen.class)
public class MixinGuiScreen {

    @Shadow
    public Minecraft mc;
    @Shadow
    public int width;
    @Shadow
    public int height;

    @Inject(method = "renderToolTip", at = @At("HEAD"), cancellable = true)
    public void renderToolTip(ItemStack stack, int x, int y, CallbackInfo p_Info) {
        EventRenderTooltip l_Event = new EventRenderTooltip(stack, x, y);
        MinecraftForge.EVENT_BUS.post(l_Event);
        if (l_Event.isCanceled())
            p_Info.cancel();
    }
}