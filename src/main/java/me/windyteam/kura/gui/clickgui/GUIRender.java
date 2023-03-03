package me.windyteam.kura.gui.clickgui;

import me.windyteam.kura.gui.clickgui.component.Component;
import me.windyteam.kura.gui.clickgui.component.ModuleButton;
import me.windyteam.kura.gui.clickgui.component.SettingButton;
import me.windyteam.kura.module.Category;
import me.windyteam.kura.module.modules.client.ClickGui;
import me.windyteam.kura.utils.render.FadeUtil;
import me.windyteam.kura.utils.render.gui.ScalaCalc;
import java.util.ArrayList;

import me.windyteam.kura.gui.clickgui.component.Component;
import me.windyteam.kura.gui.clickgui.component.ModuleButton;
import me.windyteam.kura.gui.clickgui.component.SettingButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class GUIRender
extends GuiScreen {
    public static ArrayList<Panel> panels = new ArrayList();
    public static boolean shade = false;
    static GUIRender INSTANCE;
    private final ScalaCalc oc = new ScalaCalc().setAnimationTime(350L).setFadeMode(FadeUtil.FadeMode.FADE_EPS_IN);

    public GUIRender() {
        INSTANCE = this;
        int startX = 5;
        for (Category category : Category.values()) {
            if (category.isHUD() || category == Category.HIDDEN) continue;
            panels.add(new Panel(category, startX, 5, 110, 15));
            startX += 120;
        }
    }

    public static GUIRender getINSTANCE() {
        return INSTANCE;
    }

    public static Panel getPanelByName(String name) {
        Panel getPane = null;
        if (panels != null) {
            for (Panel panel : panels) {
                if (!panel.category.getName().equals(name)) continue;
                getPane = panel;
            }
        }
        return getPane;
    }

    public void initGui() {
        super.initGui();
        this.oc.reset();
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.mouseDrag();
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        GL11.glPushMatrix();
        this.oc.drawA(sr);
        panels.forEach(panel -> panel.drawScreen(mouseX, mouseY, partialTicks));
        this.oc.drawB(sr);
        GL11.glPopMatrix();
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        for (Panel panel : panels) {
            if (panel.mouseClicked(mouseX, mouseY, mouseButton)) {
                return;
            }
            if (!panel.extended) continue;
            for (ModuleButton part : panel.Elements) {
                if (part.mouseClicked(mouseX, mouseY, mouseButton)) {
                    return;
                }
                if (!part.isExtended) continue;
                for (Component component : part.settings) {
                    if (component instanceof SettingButton && !((SettingButton)component).getValue().visible() || !component.mouseClicked(mouseX, mouseY, mouseButton)) continue;
                    return;
                }
            }
        }
    }

    public void keyTyped(char typedChar, int keyCode) {
        if (keyCode == 1) {
            ClickGui.INSTANCE.disable();
        }
        panels.forEach(panel -> panel.keyTyped(typedChar, keyCode));
    }

    public void mouseReleased(int mouseX, int mouseY, int state) {
        panels.forEach(panel -> panel.mouseReleased(mouseX, mouseY, state));
    }

    public void mouseDrag() {
        int dWheel = Mouse.getDWheel();
        if (dWheel < 0) {
            panels.forEach(component -> component.y -= 10);
        } else if (dWheel > 0) {
            panels.forEach(component -> component.y += 10);
        }
    }
}

