package me.dyzjct.kura.module.modules.client;

import me.dyzjct.kura.gui.clickgui.guis.HUDEditorScreen;
import me.dyzjct.kura.manager.FileManager;
import me.dyzjct.kura.module.Category;
import me.dyzjct.kura.module.Module;
import net.minecraft.client.gui.GuiScreen;

@Module.Info(name="HUDEditor", category=Category.CLIENT, keyCode=41, visible=false)
public class HUDEditor
extends Module {
    public static HUDEditor INSTANCE;
    HUDEditorScreen screen;

    @Override
    public void onInit() {
        INSTANCE = this;
        this.setGUIScreen(new HUDEditorScreen());
    }

    @Override
    public void onEnable() {
        if (HUDEditor.mc.player != null && !(HUDEditor.mc.currentScreen instanceof HUDEditorScreen)) {
            mc.displayGuiScreen((GuiScreen)this.screen);
        }
    }

    @Override
    public void onDisable() {
        if (HUDEditor.mc.currentScreen instanceof HUDEditorScreen) {
            mc.displayGuiScreen(null);
        }
        FileManager.saveAll();
    }

    private void setGUIScreen(HUDEditorScreen screen) {
        this.screen = screen;
    }
}

