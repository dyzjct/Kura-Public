package me.windyteam.kura.gui.alt.utils;

import me.windyteam.kura.utils.gl.RenderUtils;
import me.windyteam.kura.utils.gl.RenderUtils;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class UserNameField extends GuiTextField {
    public UserNameField(int componentId, FontRenderer fontrendererObj, int x, int y, int par5Width, int par6Height) {
        super(componentId, fontrendererObj, x, y, par5Width, par6Height);
    }


    @Override
    public void drawTextBox() {
        if (this.getVisible()) {
            if (this.getEnableBackgroundDrawing()) {
                RenderUtils.drawRoundedRectangle(this.x - 1, this.y - 1, this.width+2, this.height+2, (int)(this.height/2F)-3F, new Color(160, 160, 160));
                RenderUtils.drawRoundedRectangle(this.x, this.y, this.width, this.height, (int)(this.height/2F)-3F, new Color(0, 0, 0));
            }

            int i = this.isEnabled ? this.enabledColor : this.disabledColor;
            int j = this.cursorPosition - this.lineScrollOffset;
            int k = this.selectionEnd - this.lineScrollOffset;
            String s = this.fontRenderer.trimStringToWidth(this.text.substring(this.lineScrollOffset), this.getWidth());
            boolean flag = j >= 0 && j <= s.length();
            boolean flag1 = this.isFocused && this.cursorCounter / 6 % 2 == 0 && flag;
            int l = this.enableBackgroundDrawing ? this.x + 4 : this.x;
            int i1 = this.enableBackgroundDrawing ? this.y + (this.height - 8) / 2 : this.y;
            int j1 = l;

            if (k > s.length())
            {
                k = s.length();
            }

            if (!s.isEmpty())
            {
                String s1 = flag ? s.substring(0, j) : s;
                j1 = this.fontRenderer.drawStringWithShadow(s1, (float)l, (float)i1, i);
            }

            boolean flag2 = this.cursorPosition < this.text.length() || this.text.length() >= this.getMaxStringLength();
            int k1 = j1;

            if (!flag)
            {
                k1 = j > 0 ? l + this.width : l;
            }
            else if (flag2)
            {
                k1 = j1 - 1;
                --j1;
            }

            if (!s.isEmpty() && flag && j < s.length())
            {
                j1 = this.fontRenderer.drawStringWithShadow(s.substring(j), (float)j1, (float)i1, i);
            }

            if (flag1)
            {
                if (flag2)
                {
                    Gui.drawRect(k1, i1 - 1, k1 + 1, i1 + 1 + this.fontRenderer.FONT_HEIGHT, -3092272);
                }
                else
                {
                    this.fontRenderer.drawStringWithShadow("_", (float)k1, (float)i1, i);
                }
            }

            if (k != j)
            {
                int l1 = l + this.fontRenderer.getStringWidth(s.substring(0, k));
                this.drawCursorVertical(k1, i1 - 1, l1 - 1, i1 + 1 + this.fontRenderer.FONT_HEIGHT);
            }
        }
    }


    private void drawCursorVertical(int par1, int par2, int par3, int par4) {
        if (par1 < par3) {
            final int var5 = par1;
            par1 = par3;
            par3 = var5;
        }
        if (par2 < par4) {
            final int var5 = par2;
            par2 = par4;
            par4 = var5;
        }
        final Tessellator var6 = Tessellator.getInstance();
        final BufferBuilder var7 = var6.getBuffer();
        GL11.glColor4f(0.0f, 0.0f, 255.0f, 255.0f);
        GL11.glDisable(3553);
        GL11.glEnable(3058);
        GL11.glLogicOp(5387);
        var7.begin(7, var7.getVertexFormat());
        var7.pos(par1, par4, 0.0);
        var7.pos(par3, par4, 0.0);
        var7.pos(par3, par2, 0.0);
        var7.pos(par1, par2, 0.0);
        var7.finishDrawing();
        GL11.glDisable(3058);
        GL11.glEnable(3553);
    }
}
