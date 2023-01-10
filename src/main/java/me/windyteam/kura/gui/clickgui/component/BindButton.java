package me.windyteam.kura.gui.clickgui.component;

import me.windyteam.kura.gui.clickgui.Panel;
import me.windyteam.kura.manager.GuiManager;
import me.windyteam.kura.module.IModule;
import me.windyteam.kura.module.ModuleManager;
import me.windyteam.kura.setting.BindSetting;
import me.windyteam.kura.setting.Setting;
import me.windyteam.kura.utils.font.CFontRenderer;
import java.awt.Color;

import me.windyteam.kura.manager.GuiManager;
import net.minecraft.client.gui.Gui;
import org.lwjgl.input.Keyboard;

public class BindButton<T>
extends Component {
    public Setting<T> value;
    IModule module;
    boolean accepting = false;

    public BindButton(IModule module, int width, int height, Panel father) {
        this.module = module;
        this.width = width;
        this.height = height;
        this.father = father;
    }

    public BindButton(BindSetting value, int width, int height, Panel father) {
        this.width = width;
        this.height = height;
        this.father = father;
        this.setValue((Setting<T>) value);
    }

    public void setValue(Setting<T> value) {
        this.value = value;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        int c;
        CFontRenderer font = GuiManager.getINSTANCE().getFont();
        int color = GuiManager.getINSTANCE().isRainbow() ? GuiManager.getINSTANCE().getRainbowColorAdd((long)this.add) : GuiManager.getINSTANCE().getRGB();
        int fontColor = new Color(255, 255, 255).getRGB();
        Gui.drawRect((int)this.x, (int)this.y, (int)(this.x + this.width), (int)(this.y + this.height), (int)-2063597568);
        int n = c = this.accepting ? color : fontColor;
        if (this.isHovered(mouseX, mouseY)) {
            c = (c & 0x7F7F7F) << 1;
        }
        try {
            font.drawString(this.accepting ? "Bind | ..." : "Bind | " + (this.module.keyCode == 0 ? "NONE" : Keyboard.getKeyName((int)this.module.getBind())), this.x + 3, (int)((float)(this.y + this.height / 2) - (float)font.getHeight() / 2.0f) + 2, c);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        super.keyTyped(typedChar, keyCode);
        if (this.accepting) {
            if (keyCode == ModuleManager.getModuleByName("ClickGUI").getBind()) {
                this.module.setBind(0);
            } else {
                this.module.setBind(keyCode);
            }
            this.accepting = false;
        }
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (!this.isHovered(mouseX, mouseY)) {
            return false;
        }
        if (mouseButton == 0) {
            this.accepting = true;
        }
        return true;
    }
}

