package me.windyteam.kura.gui.clickgui.guis;

import me.windyteam.kura.gui.clickgui.HUDRender;
import me.windyteam.kura.manager.GuiManager;
import me.windyteam.kura.module.ModuleManager;
import me.windyteam.kura.module.modules.client.Colors;
import me.windyteam.kura.module.modules.client.HUDEditor;
import me.windyteam.kura.utils.Wrapper;
import me.windyteam.kura.utils.particle.ParticleSystem;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

public class HUDEditorScreen
        extends GuiScreen {
    private final ParticleSystem particleSystem = new ParticleSystem(100);

    public boolean doesGuiPauseGame() {
        return false;
    }

    public void initGui() {
        if (Colors.background.getValue().equals(GuiManager.Background.Blur) || Colors.background.getValue().equals(GuiManager.Background.Both)) {
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
        if (ModuleManager.getModuleByClass(HUDEditor.class).isEnabled()) {
            ModuleManager.getModuleByClass(HUDEditor.class).disable();
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
            this.particleSystem.tick(10);
            this.particleSystem.render();
        }
        HUDRender.getINSTANCE().drawScreen(mouseX, mouseY, partialTicks);
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        HUDRender.getINSTANCE().mouseClicked(mouseX, mouseY, mouseButton);
    }

    public void keyTyped(char typedChar, int keyCode) {
        HUDRender.getINSTANCE().keyTyped(typedChar, keyCode);
    }

    public void mouseReleased(int mouseX, int mouseY, int state) {
        HUDRender.getINSTANCE().mouseReleased(mouseX, mouseY, state);
    }
}

