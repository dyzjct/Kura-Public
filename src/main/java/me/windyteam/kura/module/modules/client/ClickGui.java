package me.windyteam.kura.module.modules.client;

import me.windyteam.kura.gui.clickgui.GUIRender;
import me.windyteam.kura.gui.clickgui.guis.ClickGuiScreen;
import me.windyteam.kura.manager.FileManager;
import me.windyteam.kura.module.Category;
import me.windyteam.kura.module.Module;
import me.windyteam.kura.module.modules.render.Wireframe;
import me.windyteam.kura.gui.clickgui.GUIRender;
import me.windyteam.kura.gui.clickgui.guis.ClickGuiScreen;
import me.windyteam.kura.manager.FileManager;

@Module.Info(name = "ClickGUI", category = Category.CLIENT, keyCode = 22, visible = false)
public class ClickGui
        extends Module {
    public static ClickGui INSTANCE;
    ClickGuiScreen screen;

// NULL
    public static Colors getInstance() {
        return null;
    }
//

    @Override
    public void onInit() {
        INSTANCE = this;
        this.setGUIScreen(new ClickGuiScreen());
    }

    @Override
    public void onEnable() {
        if (!fullNullCheck() && !(mc.currentScreen instanceof ClickGuiScreen)) {
            GUIRender.getINSTANCE().initGui();
            mc.displayGuiScreen(this.screen);
        }
    }

    @Override
    public void onDisable() {
        if (!fullNullCheck() && mc.currentScreen instanceof ClickGuiScreen) {
            mc.displayGuiScreen(null);
        }
        FileManager.saveAll();
    }

    private void setGUIScreen(ClickGuiScreen screen) {
        this.screen = screen;
    }
}

