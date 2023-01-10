package me.windyteam.kura.gui.clickgui.component;

import me.windyteam.kura.gui.clickgui.Panel;
import me.windyteam.kura.manager.GuiManager;
import me.windyteam.kura.module.HUDModule;
import me.windyteam.kura.module.IModule;
import me.windyteam.kura.module.modules.client.Colors;
import me.windyteam.kura.setting.*;
import me.windyteam.kura.utils.TimerUtils;
import me.windyteam.kura.utils.font.CFontRenderer;
import net.minecraft.client.gui.Gui;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ModuleButton
extends Component {
    public List<Component> settings = new ArrayList<Component>();
    public IModule module;
    public TimerUtils buttonTimerUtils = new TimerUtils();
    int x2;
    int y2;
    int fade = 0;
    boolean dragging;

    public ModuleButton(IModule module, int width, int height, Panel father) {
        this.module = module;
        this.width = width;
        this.height = height;
        this.father = father;
        this.setup();
    }

    public void setup() {
        try {
            for (Setting value : this.module.getSettingList()) {
                if (value instanceof BooleanSetting) {
                    this.settings.add(new BooleanButton((BooleanSetting)value, this.width, this.height, this.father));
                }
                if (value instanceof BindSetting) {
                    this.settings.add(new BindButton(this.module, this.width, this.height, this.father));
                }
                if (value instanceof IntegerSetting || value instanceof FloatSetting || value instanceof DoubleSetting) {
                    this.settings.add(new NumberSlider(value, this.width, this.height, this.father));
                }
                if (value instanceof ModeSetting) {
                    this.settings.add(new ModeButton((ModeSetting)value, this.width, this.height, this.father));
                }
                if (value instanceof StringSetting) {
                    this.settings.add(new TextButton((StringSetting)value, this.width, this.height, this.father));
                }
                if (!(value instanceof ColorSetting)) continue;
                this.settings.add(new ColorPicker((ColorSetting)value, this.father, this.width, this.height, 50));
            }
            this.settings.add(new BindButton(this.module, this.width, this.height, this.father));
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        int newColor;
        CFontRenderer font = GuiManager.getINSTANCE().getFont();
        if (this.dragging) {
            ((HUDModule)this.module).onDragging(mouseX, mouseY);
        }
        this.solveHUDPos(mouseX, mouseY);
        int color = GuiManager.getINSTANCE().isRainbow() ? GuiManager.getINSTANCE().getRainbowColorAdd((long)this.add) : GuiManager.getINSTANCE().getRGB();
        int fontColor = new Color(255, 255, 255).getRGB();
        if (this.isHovered(mouseX, mouseY)) {
            int n;
            color = (color & 0x7F7F7F) << 1;
            int n2 = ((Color)Colors.getINSTANCE().fadeColor.getValue()).getRed();
            int n3 = ((Color)Colors.getINSTANCE().fadeColor.getValue()).getGreen();
            int n4 = ((Color)Colors.getINSTANCE().fadeColor.getValue()).getBlue();
            if (this.fade < 255) {
                int n5 = this.fade;
                n = n5;
                this.fade = n5 + 1;
            } else {
                n = 255;
            }
            newColor = new Color(n2, n3, n4, n).getRGB();
        } else {
            this.fade = 0;
            newColor = new Color(0, 0, 0, 255).getRGB();
        }
        Gui.drawRect((int)this.x, (int)(this.y - 1), (int)(this.x + this.width), (int)(this.y + this.height + 1), (int)newColor);
        int finalColor = color;
        font.drawString(this.module.getName(), (float)this.x + ((float)this.width / 2.0f - (float)font.getStringWidth(this.module.getName()) / 2.0f), (int)((float)(this.y + this.height / 2) - (float)font.getHeight() / 2.0f) + 2, this.module.isEnabled() ? finalColor : fontColor);
        GL11.glPushMatrix();
        GL11.glTranslated((double)((double)(this.x + this.width) - (double)this.height / 2.0 - 3.0), (double)((double)this.y + (double)this.height / 2.0), (double)0.0);
        GL11.glPopMatrix();
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (this.module.isHUD && mouseButton == 0 && this.isHoveredHUD(mouseX, mouseY)) {
            this.x2 = this.module.x - mouseX;
            this.y2 = this.module.y - mouseY;
            this.dragging = true;
            return true;
        }
        if (!this.isHovered(mouseX, mouseY)) {
            return false;
        }
        if (mouseButton == 0) {
            this.module.toggle();
        } else if (mouseButton == 1) {
            this.isExtended = !this.isExtended;
            this.buttonTimerUtils.reset();
        }
        return true;
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        if (state == 0 && this.module.isHUD) {
            ((HUDModule)this.module).onMouseRelease();
            this.dragging = false;
        }
        for (Component setting : this.settings) {
            setting.mouseReleased(mouseX, mouseY, state);
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        super.keyTyped(typedChar, keyCode);
        for (Component setting : this.settings) {
            setting.keyTyped(typedChar, keyCode);
        }
    }

    public void solveHUDPos(int mouseX, int mouseY) {
        if (this.module.isHUD && this.dragging) {
            this.module.x = this.x2 + mouseX;
            this.module.y = this.y2 + mouseY;
        }
        if (this.module.isHUD && !this.dragging) {
            if (Math.min(this.module.x, this.module.x + this.module.width) < 0) {
                int n = this.module.x = this.module.x < this.module.x + this.module.width ? 0 : -this.module.width;
            }
            if (Math.max(this.module.x, this.module.x + this.module.width) > this.mc.displayWidth / 2) {
                int n = this.module.x = this.module.x < this.module.x + this.module.width ? this.mc.displayWidth / 2 - this.module.width : this.mc.displayWidth / 2;
            }
            if (Math.min(this.module.y, this.module.y + this.module.height) < 0) {
                int n = this.module.y = this.module.y < this.module.y + this.module.height ? 0 : -this.module.height;
            }
            if (Math.max(this.module.y, this.module.y + this.module.height) > this.mc.displayHeight / 2) {
                this.module.y = this.module.y < this.module.y + this.module.height ? this.mc.displayHeight / 2 - this.module.height : this.mc.displayHeight / 2;
            }
        }
    }

    public boolean isHoveredHUD(int mouseX, int mouseY) {
        return mouseX >= Math.min(this.module.x, this.module.x + this.module.width) && mouseX <= Math.max(this.module.x, this.module.x + this.module.width) && mouseY >= Math.min(this.module.y, this.module.y + this.module.height) && mouseY <= Math.max(this.module.y, this.module.y + this.module.height);
    }
}

