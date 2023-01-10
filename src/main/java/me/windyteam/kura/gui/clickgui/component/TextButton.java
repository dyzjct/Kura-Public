package me.windyteam.kura.gui.clickgui.component;

import me.windyteam.kura.gui.clickgui.Panel;
import me.windyteam.kura.setting.Setting;
import me.windyteam.kura.setting.StringSetting;
import me.windyteam.kura.utils.KeyboardUtils;
import java.awt.Color;

import me.windyteam.kura.utils.TimerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

public class TextButton extends SettingButton<String> {
    TimerUtils timerUtils = new TimerUtils();

    private String TypeDir = "";

    private boolean typing;

    public TextButton(StringSetting value, int width, int height, Panel father) {
        this.width = width;
        this.height = height;
        this.father = father;
        setValue((Setting<String>)value);
    }

    public static String removeLastChar(String str) {
        String output = "";
        if (str != null && str.length() > 0)
            output = str.substring(0, str.length() - 1);
        return output;
    }

    public void render(int mouseX, int mouseY, float partialTicks) {
        Gui.drawRect(this.x, this.y, this.x + this.width, this.y + this.height, -2063597568);
        (Minecraft.getMinecraft()).fontRenderer.drawString((String)getValue().getValue() + (this.typing ? "..." : ""), this.x + this.width / 2 - (Minecraft.getMinecraft()).fontRenderer.getStringWidth((String)getValue().getValue() + (this.typing ? "..." : "")) / 2, this.y + this.height / 2 - (Minecraft.getMinecraft()).fontRenderer.FONT_HEIGHT / 2, (new Color(191, 255, 0)).getRGB());
    }

    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (!getValue().visible() || !isHovered(mouseX, mouseY))
            return false;
        this.typing = (mouseButton == 0);
        return true;
    }

    public void keyTyped(char c, int key) {
        if (this.typing) {
            if (key == 211 || key == 14) {
                if (this.timerUtils.passed(700L)) {
                    this.TypeDir = removeLastChar(this.TypeDir);
                    this.timerUtils.reset();
                } else {
                    this.TypeDir = removeLastChar(this.TypeDir);
                }
            } else if (key == 28) {
                this.typing = false;
            } else if (!String.valueOf(c).contains("\000")) {
                this.TypeDir += c;
            } else if (KeyboardUtils.isCtrlDown() && KeyboardUtils.isDown(47)) {
                this.TypeDir += KeyboardUtils.getClipboardString();
            }
            update();
        }
    }

    public void changeValue(String newValue) {
        getValue().setValue(newValue);
    }

    public void update() {
        if (getValue().visible())
            changeValue(this.TypeDir);
    }
}
