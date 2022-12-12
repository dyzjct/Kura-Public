package me.dyzjct.kura.mixin.client.gui;

import me.dyzjct.kura.module.ModuleManager;
import me.dyzjct.kura.module.modules.render.Animations;
import me.dyzjct.kura.utils.animations.EaseUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin({ GuiNewChat.class })
public abstract class MixinGuiNewChat extends Gui
{
    @Shadow
    @Final
    private Minecraft mc;
    @Shadow
    @Final
    private List<ChatLine> drawnChatLines;
    @Shadow
    private int scrollPos;
    @Shadow
    private boolean isScrolled;

    @Shadow
    public abstract int getLineCount();

    @Shadow
    public abstract boolean getChatOpen();

    @Shadow
    public abstract float getChatScale();

    @Shadow
    public abstract int getChatWidth();

    /**
     * @author HuangHanBing
     * @reason L
     */
//    @Overwrite
    public void drawChat(final int updateCounter) {
        if (this.mc.gameSettings.chatVisibility != EntityPlayer.EnumChatVisibility.HIDDEN) {
            final int i = this.getLineCount();
            boolean flag = false;
            int j = 0;
            final int k = this.drawnChatLines.size();
            final float f = this.mc.gameSettings.chatOpacity * 0.9f + 0.1f;
            if (k > 0) {
                if (this.getChatOpen()) {
                    flag = true;
                }
                final float f2 = this.getChatScale();
                final int l = MathHelper.ceil(this.getChatWidth() / f2);
                GlStateManager.pushMatrix();
                GlStateManager.translate(2.0f, 20.0f, 0.0f);
                GlStateManager.scale(f2, f2, 1.0f);
                for (int i2 = 0; i2 + this.scrollPos < this.drawnChatLines.size() && i2 < i; ++i2) {
                    final ChatLine chatline = this.drawnChatLines.get(i2 + this.scrollPos);
                    if (chatline != null) {
                        final int j2 = updateCounter - chatline.getUpdatedCounter();
                        if (j2 < 200 || flag) {
                            double d0 = j2 / 200.0;
                            d0 = 1.0 - d0;
                            d0 *= 10.0;
                            d0 = MathHelper.clamp(d0, 0.0, 1.0);
                            d0 *= d0;
                            int l2 = (int)(255.0 * d0);
                            if (flag) {
                                l2 = 255;
                            }
                            l2 *= (int)f;
                            ++j;
                            if (l2 > 3) {
                                GL11.glPushMatrix();
                                final int i3 = 0;
                                final int j3 = -i2 * 9;
                                if (ModuleManager.getModuleByClass(Animations.class).isEnabled() && Animations.INSTANCE.chat.getValue() && !flag) {
                                    if (j2 <= 20) {
                                        GL11.glTranslatef((float)(-(l + 4) * EaseUtils.INSTANCE.easeInQuart(1.0 - (j2 + this.mc.timer.renderPartialTicks) / 20.0)), 0.0f, 0.0f);
                                    }
                                    if (j2 >= 180) {
                                        GL11.glTranslatef((float)(-(l + 4) * EaseUtils.INSTANCE.easeInQuart((j2 + this.mc.timer.renderPartialTicks - 180.0f) / 20.0)), 0.0f, 0.0f);
                                    }
                                }
                                drawRect(-2, j3 - 9, l + 4, j3, l2 / 2 << 24);
                                GlStateManager.enableBlend();
                                this.mc.fontRenderer.drawStringWithShadow(chatline.getChatComponent().getFormattedText(), (float)i3, (float)(j3 - 8), 16777215 + (l2 << 24));
                                GlStateManager.disableAlpha();
                                GlStateManager.disableBlend();
                                GL11.glPopMatrix();
                            }
                        }
                    }
                }
                if (flag) {
                    final int i2 = this.mc.fontRenderer.FONT_HEIGHT;
                    GlStateManager.translate(-3.0f, 0.0f, 0.0f);
                    final int l3 = k * i2 + k;
                    final int j2 = j * i2 + j;
                    final int j4 = this.scrollPos * j2 / k;
                    final int k2 = j2 * j2 / l3;
                    if (l3 != j2) {
                        final int l2 = (j4 > 0) ? 170 : 96;
                        final int l4 = this.isScrolled ? 13382451 : 3355562;
                        drawRect(0, -j4, 2, -j4 - k2, l4 + (l2 << 24));
                        drawRect(2, -j4, 1, -j4 - k2, 13421772 + (l2 << 24));
                    }
                }
                GlStateManager.popMatrix();
            }
        }
    }
}