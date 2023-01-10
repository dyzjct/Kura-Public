package me.windyteam.kura.gui.clickgui.component;

import me.windyteam.kura.gui.clickgui.Panel;
import me.windyteam.kura.manager.GuiManager;
import me.windyteam.kura.setting.BooleanSetting;
import me.windyteam.kura.utils.font.CFontRenderer;
import java.awt.Color;

import me.windyteam.kura.manager.GuiManager;
import net.minecraft.client.gui.Gui;

public class BooleanButton
extends SettingButton<Boolean> {
    public BooleanButton(BooleanSetting value, int width, int height, Panel father) {
        this.width = width;
        this.height = height;
        this.father = father;
        this.setValue(value);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        int c;
        CFontRenderer font = GuiManager.getINSTANCE().getFont();
        int color = GuiManager.getINSTANCE().isRainbow() ? GuiManager.getINSTANCE().getRainbowColorAdd((long)this.add) : GuiManager.getINSTANCE().getRGB();
        int fontColor = 0x909090;
        Gui.drawRect((int)this.x, (int)this.y, (int)(this.x + this.width), (int)(this.y + this.height), (int)-2063597568);
        int n = c = (Boolean)this.getValue().getValue() != false ? color : fontColor;
        if (this.isHovered(mouseX, mouseY)) {
            c = GuiManager.getINSTANCE().isRainbow() ? (c & 0x7F7F7F) << 1 : new Color(255, 255, 255).getRGB();
        }
        BooleanSetting booleanValue = (BooleanSetting)this.getValue();
        font.drawString(booleanValue.getName(), this.x + 3, (int)((float)(this.y + this.height / 2) - (float)font.getHeight() / 2.0f) + 2, c);
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (!this.getValue().visible() || !this.isHovered(mouseX, mouseY)) {
            return false;
        }
        if (mouseButton == 0) {
            this.getValue().setValue((Boolean)this.getValue().getValue() == false);
        }
        return true;
    }
}

