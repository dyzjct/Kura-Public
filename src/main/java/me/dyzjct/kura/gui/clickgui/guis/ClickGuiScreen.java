package me.dyzjct.kura.gui.clickgui.guis;

import me.dyzjct.kura.gui.clickgui.GUIRender;
import me.dyzjct.kura.manager.GuiManager;
import me.dyzjct.kura.module.ModuleManager;
import me.dyzjct.kura.module.modules.client.ClickGui;
import me.dyzjct.kura.utils.FakeMeteor.MeteorSystem;
import me.dyzjct.kura.utils.Wrapper;
import java.awt.Color;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

public class ClickGuiScreen
extends GuiScreen {
    private final MeteorSystem meteorSystem = new MeteorSystem(30);

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
        if (ModuleManager.getModuleByClass(ClickGui.class).isEnabled()) {
            ModuleManager.getModuleByClass(ClickGui.class).disable();
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

