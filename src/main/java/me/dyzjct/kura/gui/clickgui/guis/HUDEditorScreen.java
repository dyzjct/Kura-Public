package me.dyzjct.kura.gui.clickgui.guis;

import me.dyzjct.kura.gui.clickgui.HUDRender;
import me.dyzjct.kura.manager.GuiManager;
import me.dyzjct.kura.module.ModuleManager;
import me.dyzjct.kura.module.modules.client.HUDEditor;
import me.dyzjct.kura.utils.particle.ParticleSystem;
import me.dyzjct.kura.utils.Wrapper;
import java.awt.Color;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

public class HUDEditorScreen
extends GuiScreen {
    private final ParticleSystem particleSystem = new ParticleSystem(100);

    public boolean doesGuiPauseGame() {
        return false;
    }

    public void initGui() {
        if (GuiManager.getINSTANCE().getBackground().equals((Object)GuiManager.Background.Blur) || GuiManager.getINSTANCE().getBackground().equals((Object)GuiManager.Background.Both)) {
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
        if (GuiManager.getINSTANCE().getBackground().equals((Object)GuiManager.Background.Shadow) || GuiManager.getINSTANCE().getBackground().equals((Object)GuiManager.Background.Both)) {
            this.drawDefaultBackground();
        }
        if (this.mc.player == null) {
            Gui.drawRect((int)0, (int)0, (int)9999, (int)9999, (int)new Color(0, 0, 0, 255).getRGB());
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

