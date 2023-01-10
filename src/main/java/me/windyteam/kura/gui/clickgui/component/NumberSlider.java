package me.windyteam.kura.gui.clickgui.component;

import me.windyteam.kura.gui.clickgui.Panel;
import me.windyteam.kura.manager.GuiManager;
import me.windyteam.kura.setting.DoubleSetting;
import me.windyteam.kura.setting.FloatSetting;
import me.windyteam.kura.setting.IntegerSetting;
import me.windyteam.kura.setting.Setting;
import me.windyteam.kura.utils.font.CFontRenderer;
import java.awt.Color;

import me.windyteam.kura.manager.GuiManager;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

public class NumberSlider<T>
extends SettingButton<T> {
    boolean sliding = false;

    public NumberSlider(Setting<T> value, int width, int height, Panel father) {
        this.width = width;
        this.height = height;
        this.father = father;
        this.setValue(value);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        IntegerSetting intValue;
        FloatSetting floatValue;
        DoubleSetting doubleValue;
        CFontRenderer font = GuiManager.getINSTANCE().getFont();
        if (!this.getValue().visible()) {
            this.sliding = false;
        }
        int color = GuiManager.getINSTANCE().isRainbow() ? GuiManager.getINSTANCE().getRainbowColorAdd((long)this.add, 192) : GuiManager.getINSTANCE().getRGB();
        int fontColor = new Color(255, 255, 255).getRGB();
        Gui.drawRect((int)this.x, (int)this.y, (int)(this.x + this.width), (int)(this.y + this.height), (int)-2063597568);
        double iwidth = 0.0;
        String displayvalue = "0";
        int sliderWidth = this.width - 2;
        if (this.getValue() instanceof DoubleSetting) {
            doubleValue = (DoubleSetting)this.getValue();
            displayvalue = String.format("%.1f", doubleValue.getValue());
            double percentBar = ((Double)doubleValue.getValue() - doubleValue.getMin()) / (doubleValue.getMax() - doubleValue.getMin());
            iwidth = (double)sliderWidth * percentBar;
        } else if (this.getValue() instanceof FloatSetting) {
            floatValue = (FloatSetting)this.getValue();
            displayvalue = String.format("%.1f", floatValue.getValue());
            double percentBar = (((Float)floatValue.getValue()).floatValue() - floatValue.getMin().floatValue()) / (floatValue.getMax().floatValue() - floatValue.getMin().floatValue());
            iwidth = (double)sliderWidth * percentBar;
        } else if (this.getValue() instanceof IntegerSetting) {
            intValue = (IntegerSetting)this.getValue();
            displayvalue = String.valueOf(intValue.getValue());
            double percentBar = (double)((Integer)intValue.getValue() - intValue.getMin()) / (double)(intValue.getMax() - intValue.getMin());
            iwidth = (double)sliderWidth * percentBar;
        }
        Gui.drawRect((int)(this.x + 1), (int)(this.y + 1), (int)(this.x + 1 + (int)iwidth), (int)(this.y + this.height), (int)color);
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        if (this.sliding) {
            if (this.getValue() instanceof DoubleSetting) {
                doubleValue = (DoubleSetting)this.getValue();
                double diff = doubleValue.getMax() - doubleValue.getMin();
                double val = doubleValue.getMin() + MathHelper.clamp((double)(((double)mouseX - (double)(this.x + 1)) / (double)sliderWidth), (double)0.0, (double)1.0) * diff;
                doubleValue.setValue(val);
            } else if (this.getValue() instanceof FloatSetting) {
                floatValue = (FloatSetting)this.getValue();
                double diff = floatValue.getMax().floatValue() - floatValue.getMin().floatValue();
                double val = (double)floatValue.getMin().floatValue() + MathHelper.clamp((double)(((double)mouseX - (double)(this.x + 1)) / (double)sliderWidth), (double)0.0, (double)1.0) * diff;
                floatValue.setValue(Float.valueOf((float)val));
            } else if (this.getValue() instanceof IntegerSetting) {
                intValue = (IntegerSetting)this.getValue();
                double diff = intValue.getMax() - intValue.getMin();
                double val = (double)intValue.getMin().intValue() + MathHelper.clamp((double)(((double)mouseX - (double)(this.x + 1)) / (double)sliderWidth), (double)0.0, (double)1.0) * diff;
                intValue.setValue((int)val);
            }
        }
        font.drawString(this.getValue().getName(), this.x + 3, (int)((float)(this.y + this.height / 2) - (float)font.getHeight() / 2.0f) + 2, fontColor);
        font.drawString(String.valueOf(displayvalue), this.x + this.width - 1 - font.getStringWidth(String.valueOf(displayvalue)), (int)((float)(this.y + this.height / 2) - (float)font.getHeight() / 2.0f) + 2, this.isHovered(mouseX, mouseY) ? Color.WHITE.getRGB() : 0x909090);
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (!this.getValue().visible() || !this.isHovered(mouseX, mouseY)) {
            return false;
        }
        if (mouseButton == 0) {
            this.sliding = true;
            return true;
        }
        return false;
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        this.sliding = false;
    }
}

