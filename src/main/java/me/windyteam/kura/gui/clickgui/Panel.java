package me.windyteam.kura.gui.clickgui;

import me.windyteam.kura.gui.clickgui.component.Component;
import me.windyteam.kura.gui.clickgui.component.ModuleButton;
import me.windyteam.kura.gui.clickgui.component.SettingButton;
import me.windyteam.kura.gui.clickgui.guis.HUDEditorScreen;
import me.windyteam.kura.gui.clickgui.util.SpecialRender;
import me.windyteam.kura.manager.GuiManager;
import me.windyteam.kura.module.Category;
import me.windyteam.kura.module.HUDModule;
import me.windyteam.kura.module.IModule;
import me.windyteam.kura.module.ModuleManager;
import me.windyteam.kura.utils.Timer;
import me.windyteam.kura.utils.TimerUtils;
import me.windyteam.kura.utils.Wrapper;
import me.windyteam.kura.utils.font.CFontRenderer;
import me.windyteam.kura.utils.font.FontUtils;
import me.windyteam.kura.utils.render.RenderUtils;
import net.minecraft.client.gui.Gui;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Panel {
    public int x;
    public int y;
    public int width;
    public int height;
    public Category category;
    public boolean extended;
    public String categoryName;
    public List<ModuleButton> Elements = new ArrayList<>();
    boolean dragging;
    boolean isHUD;
    int x2;
    int y2;
    TimerUtils panelTimerUtils = new TimerUtils();
    CFontRenderer font;
    Timer panelTimer = new Timer();

    public Panel(Category category, int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.extended = true;
        this.dragging = false;
        this.category = category;
        this.isHUD = category.isHUD();
        this.font = FontUtils.LemonMilk;
        this.setup();
    }

    public void setup() {
        try {
            this.categoryName = this.category.getName();
            for (IModule m : ModuleManager.getAllIModules()) {
                if (m.category != this.category) continue;
                this.Elements.add(new ModuleButton(m, this.width, this.height, this));
            }
        } catch (Exception exception) {
            // empty catch block
        }
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
//        if (this.dragging) {
//            this.x = this.x2 + mouseX;
//            this.y = this.y2 + mouseY;
//        }
//        int panelColor = -2063597568;
//        int GradientIntensity = GuiManager.getINSTANCE().getGradientIntensity();
//        int startY = this.y + this.height + 2;
//        if (!this.Elements.isEmpty()) {
//            int zhe = 0;
//            for (ModuleButton button : this.Elements) {
//                HUDModule hud;
//                if (this.extended ? !this.panelTimerUtils.passed((long)(++zhe) * 20L) : this.panelTimerUtils.passed((long)(this.Elements.size() - ++zhe) * 20L)) continue;
//                button.solvePos();
//                button.y = startY;
//                button.setAdd((float)((startY - this.y) / button.height) * (float)GradientIntensity);
//                button.render(mouseX, mouseY, partialTicks);
//                int settingY = startY - 1;
//                startY += button.height + 1;
//                int zhe1 = 0;
//                for (Component component : this.toIsVisibleList(button.settings)) {
//                    if (button.isExtended ? !button.buttonTimerUtils.passed((long)(++zhe1) * 15L) : button.buttonTimerUtils.passed((long)(this.toIsVisibleList(button.settings).size() - ++zhe1) * 15L)) continue;
//                    if (component instanceof SettingButton && !((SettingButton)component).getValue().visible()) continue;
//                    component.solvePos();
//                    component.y = startY;
//                    component.setAdd((float)((startY - this.y) / component.height) * (float)GradientIntensity);
//                    component.render(mouseX, mouseY, partialTicks);
//                    startY += component.height;
//                }
//                if ((GuiManager.getINSTANCE().isSettingRect() || GuiManager.getINSTANCE().isSettingSide()) && button.isExtended && GuiManager.getINSTANCE().isRainbow()) {
//                    SpecialRender.drawSLine(this.x, settingY, startY, this.height, (settingY - this.y) / this.height * GradientIntensity, GradientIntensity);
//                }
//                if (GuiManager.getINSTANCE().isSettingRect() && button.isExtended) {
//                    if (GuiManager.getINSTANCE().isRainbow()) {
//                        SpecialRender.drawSLine(this.x + this.width, settingY, startY, this.height, (settingY - this.y) / this.height * GradientIntensity, GradientIntensity);
//                        Gui.drawRect((int)this.x, (int)settingY, (int)(this.x + this.width), (int)(settingY + 1), (int)GuiManager.getINSTANCE().getRainbowColorAdd((long)((settingY - this.y) / this.height) * (long)GradientIntensity));
//                        Gui.drawRect((int)this.x, (int)(startY - 1), (int)(this.x + this.width), (int)startY, (int)GuiManager.getINSTANCE().getRainbowColorAdd((long)((startY - this.y) / this.height) * (long)GradientIntensity));
//                    } else {
//                        RenderUtils.drawLine(this.x + this.width, settingY, this.x + this.width, startY, 1.0f, GuiManager.getINSTANCE().getColor());
//                        Gui.drawRect((int)this.x, (int)settingY, (int)(this.x + this.width), (int)(settingY + 1), (int)GuiManager.getINSTANCE().getColor().getRGB());
//                        Gui.drawRect((int)this.x, (int)(startY - 1), (int)(this.x + this.width), (int)startY, (int)GuiManager.getINSTANCE().getColor().getRGB());
//                    }
//                }
//                ++startY;
//                if (!button.module.isHUD || !(Wrapper.mc.currentScreen instanceof HUDEditorScreen) || !(hud = (HUDModule)button.module).isEnabled()) continue;
//                Gui.drawRect((int)hud.x, (int)hud.y, (int)(hud.x + hud.width), (int)(hud.y + hud.height), (int)panelColor);
//                hud.onRender();
//            }
//        }
        if (this.dragging) {
            this.x = this.x2 + mouseX;
            this.y = this.y2 + mouseY;
        }
        int panelColor = -2063597568;
        int GradientIntensity = GuiManager.getINSTANCE().getGradientIntensity();
        if (!this.Elements.isEmpty()) {
            int startY = this.y + this.height + 2;
            int zhe = 0;
            for (ModuleButton button : this.Elements) {
                if (this.extended ? !this.panelTimer.passed((long) zhe * 25L) : this.panelTimer.passed((long) (this.Elements.size() - ++zhe) * 25L))
                    continue;
                button.solvePos();
                button.y = startY;
                button.setAdd((float) ((startY - this.y) / button.height) * (float) GradientIntensity);
                button.render(mouseX, mouseY, partialTicks);
                int settingY = startY - 1;
                startY += button.height + 1;
                int zhe1 = 0;
                for (Component component : this.toIsVisibleList(button.settings)) {
                    ++zhe1;
                    if (!button.isExtended ? button.buttonTimer.passed((long) (this.toIsVisibleList(button.settings).size() - zhe1) * 25L) : !button.buttonTimer.passed((long) (++zhe1) * 25L))
                        continue;
                    if (component instanceof SettingButton && !((SettingButton) component).getValue().visible())
                        continue;
                    component.solvePos();
                    component.y = startY;
                    component.setAdd((float) ((startY - this.y) / component.height) * (float) GradientIntensity);
                    component.render(mouseX, mouseY, partialTicks);
                    startY += component.height;
                }
                if ((GuiManager.getINSTANCE().isSettingRect() || GuiManager.getINSTANCE().isSettingSide()) && button.isExtended) {
                    if (GuiManager.getINSTANCE().isRainbow()) {
                        SpecialRender.drawSLine(this.x, settingY, startY, this.height, 2.0f, (settingY - this.y) / this.height * GradientIntensity, GradientIntensity);
                    } else {
                        RenderUtils.drawLine(this.x, settingY, this.x, startY, 2.0f, GuiManager.getINSTANCE().getColor());
                    }
                }
                if (GuiManager.getINSTANCE().isSettingRect() && button.isExtended) {
                    if (GuiManager.getINSTANCE().isRainbow()) {
                        SpecialRender.drawSLine(this.x + this.width, settingY, startY, this.height, 2.0f, (settingY - this.y) / this.height * GradientIntensity, GradientIntensity);
                        Gui.drawRect(this.x, settingY, this.x + this.width, settingY + 1, GuiManager.getINSTANCE().getRainbowColorAdd((long) ((settingY - this.y) / this.height) * (long) GradientIntensity));
                        Gui.drawRect(this.x, startY - 1, this.x + this.width, startY, GuiManager.getINSTANCE().getRainbowColorAdd((long) ((startY - this.y) / this.height) * (long) GradientIntensity));
                    } else {
                        RenderUtils.drawLine(this.x + this.width, settingY, this.x + this.width, startY, 2.0f, GuiManager.getINSTANCE().getColor());
                        Gui.drawRect(this.x, settingY, this.x + this.width, settingY + 1, GuiManager.getINSTANCE().getColor().getRGB());
                        Gui.drawRect(this.x, startY - 1, this.x + this.width, startY, GuiManager.getINSTANCE().getColor().getRGB());
                    }
                }
                ++startY;
                if (!button.module.isHUD || !(Wrapper.mc.currentScreen instanceof HUDEditorScreen)) continue;
                HUDModule hud = (HUDModule) button.module;
                Gui.drawRect(hud.x, hud.y, hud.x + hud.width, hud.y + hud.height, panelColor);
                hud.onRender();
            }
            if (this.extended) {
                if (GuiManager.getINSTANCE().isRainbow()) {
                    SpecialRender.draw(this.x - 1, this.y + this.height, this.width + 2, startY - 1 - this.height - this.y, this.height, 2.0f, GradientIntensity);
                } else {
                    RenderUtils.setLineWidth(2.0f);
                    RenderUtils.drawRectOutline(this.x - 1, this.y + this.height, this.width + 2, startY - 1 - this.height - this.y, GuiManager.getINSTANCE().getColor());
                }
            }
        }
//        Color color = ClickGui.INSTANCE.topColor.getValue();
        int color = new Color(GuiManager.getINSTANCE().getRed(), GuiManager.getINSTANCE().getGreen(), GuiManager.getINSTANCE().getBlue(), 208).getRGB();
//        RenderUtils.drawHalfRoundedRectangle(this.x, (float)this.y + 2.0f, this.width, this.height, 5.0, RenderUtils.HalfRoundedDirection.Top, color);
//        RenderUtils.drawHalfRoundedRectangle(this.x, (float)startY - 1.5f - (float)(this.extended ? 0 : 2), this.width, this.height, 5.0, RenderUtils.HalfRoundedDirection.Bottom, color);
//        this.font.drawString(this.categoryName, (float)this.x + ((float)this.width / 2.0f - (float)this.font.getStringWidth(this.categoryName) / 2.0f), (float)this.y + (float)this.height / 2.0f - (float)this.font.getHeight() / 2.0f, -1052689);
        Gui.drawRect(this.x - 4, this.y, this.x + this.width + 4, this.y + this.height, color);
        this.font.drawString(this.category.getName(), (float) this.x + ((float) this.width / 2.0f - (float) this.font.getStringWidth(this.category.getName()) / 2.0f), (float) this.y + (float) this.height / 2.0f - (float) this.font.getHeight() / 2.0f, -1052689);
        Gui.drawRect(this.x, this.y + this.height, this.x + this.width, this.y + this.height + 1, panelColor);
    }

    public List<Component> toIsVisibleList(List<Component> toChangeList) {
        return toChangeList.stream().filter(obj -> {
            if (obj instanceof SettingButton) {
                return ((SettingButton) obj).getValue().visible();
            }
            return true;
        }).collect(Collectors.toList());
    }

    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0 && this.isHovered(mouseX, mouseY).test(this)) {
            this.x2 = this.x - mouseX;
            this.y2 = this.y - mouseY;
            this.dragging = true;
            if (!this.isHUD) {
                Collections.swap(GUIRender.panels, 0, GUIRender.panels.indexOf(this));
            } else {
                Collections.swap(HUDRender.getINSTANCE().panels, 0, HUDRender.getINSTANCE().panels.indexOf(this));
            }
            return true;
        }
        if (mouseButton == 1 && this.isHovered(mouseX, mouseY).test(this)) {
            this.extended = !this.extended;
            this.panelTimerUtils.reset();
            return true;
        }
        return false;
    }

    public void mouseReleased(int mouseX, int mouseY, int state) {
        if (state == 0) {
            this.dragging = false;
        }
        for (Component component : this.Elements) {
            component.mouseReleased(mouseX, mouseY, state);
        }
    }

    public void keyTyped(char typedChar, int keyCode) {
        for (Component component : this.Elements) {
            component.keyTyped(typedChar, keyCode);
        }
    }

    public Predicate<Panel> isHovered(int mouseX, int mouseY) {
        return c -> mouseX >= Math.min(c.x, c.x + c.width) && mouseX <= Math.max(c.x, c.x + c.width) && mouseY >= Math.min(c.y, c.y + c.height) && mouseY <= Math.max(c.y, c.y + c.height);
    }
}

