package me.windyteam.kura.module.modules.misc;

import me.windyteam.kura.module.Category;
import me.windyteam.kura.module.Module;
import me.windyteam.kura.setting.Setting;

@Module.Info(name = "ExtraTab", category = Category.MISC, description = "Just ExtraTab")
public class ExtraTab extends Module {
    private static ExtraTab INSTANCE;

    static {
        ExtraTab.INSTANCE = new ExtraTab();
    }

    public Setting<Integer> size = isetting("Size", 250, 1, 1000);

    public ExtraTab() {
        this.setInstance();
    }

    public static ExtraTab getINSTANCE() {
        if (ExtraTab.INSTANCE == null) {
            ExtraTab.INSTANCE = new ExtraTab();
        }
        return ExtraTab.INSTANCE;
    }

    private void setInstance() {
        ExtraTab.INSTANCE = this;
    }
}
