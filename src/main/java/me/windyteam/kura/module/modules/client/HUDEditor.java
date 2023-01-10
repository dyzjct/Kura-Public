package me.windyteam.kura.module.modules.client;

import me.windyteam.kura.gui.clickgui.guis.HUDEditorScreen;
import me.windyteam.kura.manager.FileManager;
import me.windyteam.kura.module.Category;
import me.windyteam.kura.module.Module;
import me.windyteam.kura.gui.clickgui.guis.HUDEditorScreen;
import me.windyteam.kura.manager.FileManager;
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

