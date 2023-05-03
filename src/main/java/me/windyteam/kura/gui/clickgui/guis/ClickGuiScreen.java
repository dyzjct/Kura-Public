package me.windyteam.kura.gui.clickgui.guis;

import me.windyteam.kura.gui.clickgui.GUIRender;
import me.windyteam.kura.manager.GuiManager;
import me.windyteam.kura.module.ModuleManager;
import me.windyteam.kura.module.modules.client.ClickGui;
import me.windyteam.kura.module.modules.client.Colors;
import me.windyteam.kura.utils.Wrapper;
import me.windyteam.kura.utils.fakeMeteor.MeteorSystem;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

public class ClickGuiScreen
        extends GuiScreen {
    private final MeteorSystem meteorSystem = new MeteorSystem(30);

    public boolean doesGuiPauseGame() {
        return false;
    }

    public void initGui() {
        if (Colors.background.getValue().equals(Colors.
                Background.Blur) || Colors.background.getValue().equals(Colors.Background.Both)) {
            if (Wrapper.getMinecraft().entityRenderer.getShaderGroup() != null) {
                Wrapper.getMinecraft().entityRenderer.getShaderGroup().deleteShaderGroup();
            }
            Wrapper.getMinecraft().entityRenderer.loadShader(new ResourceLocation("shaders/post/blur.json"));
        }
    }

    public void onGuiClosed() {
        if (Wrapper.getMinecraft().entityRenderer.getShaderGroup() != null) {
            Wrapper.getMinecraft().entityRenderer.getShaderGroup().deleteShaderGroup();
        }
        if (ModuleManager.getModuleByClass(ClickGui.class).isEnabled()) {
            ModuleManager.getModuleByClass(ClickGui.class).disable();
        }
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (Colors.background.getValue().equals(Colors.Background.Shadow) || Colors.background.getValue().equals(Colors.Background.Both)) {
            this.drawDefaultBackground();
        }
        if (this.mc.player == null) {
            Gui.drawRect(0, 0, 9999, 9999, new Color(0, 0, 0, 255).getRGB());
        }
        if (GuiManager.getINSTANCE().isParticle()) {
            this.meteorSystem.setRainbow(GuiManager.INSTANCE.isRainbow());
            this.meteorSystem.tick();
            this.meteorSystem.render();
        }
        GUIRender.getINSTANCE().drawScreen(mouseX, mouseY, partialTicks);
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        GUIRender.getINSTANCE().mouseClicked(mouseX, mouseY, mouseButton);
    }

    public void keyTyped(char typedChar, int keyCode) {
        GUIRender.getINSTANCE().keyTyped(typedChar, keyCode);
    }

    public void mouseReleased(int mouseX, int mouseY, int state) {
        GUIRender.getINSTANCE().mouseReleased(mouseX, mouseY, state);
    }
}

