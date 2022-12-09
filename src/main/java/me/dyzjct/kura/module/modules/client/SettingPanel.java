package me.dyzjct.kura.module.modules.client;

import me.dyzjct.kura.gui.settingpanel.XG42SettingPanel;
import me.dyzjct.kura.manager.FileManager;
import me.dyzjct.kura.module.Category;
import me.dyzjct.kura.module.Module;

@Module.Info(name="SettingPanel", category=Category.CLIENT, keyCode=54, visible=false)
public class SettingPanel
extends Module {
    public static SettingPanel INSTANCE;
    XG42SettingPanel screen;

    @Override
    public void onInit() {
        INSTANCE = this;
        this.setGUIScreen(new XG42SettingPanel());
    }

    @Override
    public void onEnable() {
        if (SettingPanel.mc.player != null && !(SettingPanel.mc.currentScreen instanceof XG42SettingPanel)) {
            mc.displayGuiScreen(this.screen);
        }
    }

    @Override
    public void onDisable() {
        if (SettingPanel.mc.currentScreen instanceof XG42SettingPanel) {
            mc.displayGuiScreen(null);
        }
        FileManager.saveAll();
    }

    public void setGUIScreen(XG42SettingPanel screen) {
        this.screen = screen;
    }
}

